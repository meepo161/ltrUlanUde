package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.utils.LinearApproximation;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private CheckBox autoScaleCheckBox;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private CheckBox averageCheckBox;
    @FXML
    private TextField averageTextField;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField phaseTextField;
    @FXML
    private TextField shiftTextField;

    private LTR24 ltr24;
    private int channel;
    private double[] data;
    private LTR212 ltr212;
    private double maxValue;
    private WindowsManager wm;
    private ControllerManager cm;
    private RingBuffer ringBuffer;
    private volatile boolean isDone;
    private CrateModel.Moudules moduleType;
    private String calibrationState;
    private String[] calibrationSettings;
    private String moduleCalibrationSettings;
    private LinearApproximation linearApproximation;
    private volatile XYChart.Series<Number, Number> graphSeries;
    private double lowerBound;
    private double upperBound;
    private double tickUnit;
    private double averageCount;
    private int averageIterator;
    private double bufferedAverageValue;
    private double averageValue;

    public void initializeView(CrateModel.Moudules moduleType, int selectedSlot, int channel) {
        setFields(moduleType, channel);
        initializeModuleType(selectedSlot);
        getGraphBounds();
        setGraphBounds(lowerBound, upperBound, tickUnit, false);
        initAverage();
        toggleAutoScale();
        clearGraphData();
        checkCalibration();
        startShow();
    }

    private void setFields(CrateModel.Moudules moduleType, int channel) {
        this.moduleType = moduleType;
        this.channel = channel;
        cm.setClosed(false);
    }

    private void initializeModuleType(int selectedSlot) {
        if (isDefineLTR24Slot(selectedSlot)) {
            data = new double[39064];
            ringBuffer = new RingBuffer(data.length * 100);
        } else if (isDefineLTR212Slot(selectedSlot)) {
            data = new double[2048];
            ringBuffer = new RingBuffer(data.length * 10);
        }
    }

    private boolean isDefineLTR24Slot(int slot) {
        for (Pair<Integer, LTR24> module : cm.getCrateModelInstance().getLtr24ModulesList()) {
            if (module.getValue().getSlot() == slot) {
                ltr24 = module.getValue();
                return true;
            }
        }
        return false;
    }

    private void getGraphBounds() {
        if (moduleType == CrateModel.Moudules.LTR24) {
            getLTR24Bounds();
        } else {
            getLTR212Bounds();
        }
    }

    private void getLTR24Bounds() {
        switch (ltr24.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(-2, 2, 0.4);
                break;
            case 1:
                setBounds(-10, 10, 2);
                break;
            default:
                setBounds(-10, 10, 2);
        }
    }

    private void setBounds(double lowerBound, double upperBound, double tickUnit) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tickUnit = tickUnit;
    }

    private void getLTR212Bounds() {
        switch (ltr212.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(-0.01, 0.01, 0.002);
                break;
            case 1:
                setBounds(-0.02, 0.02, 0.004);
                break;
            case 2:
                setBounds(-0.04, 0.04, 0.008);
                break;
            case 3:
                setBounds(-0.08, 0.08, 0.016);
                break;
            case 4:
                setBounds(0, 0.01, 0.001);
                break;
            case 5:
                setBounds(0, 0.02, 0.002);
                break;
            case 6:
                setBounds(0, 0.04, 0.004);
                break;
            case 7:
                setBounds(0, 0.08, 0.008);
                break;
            default:
                setBounds(-10, 10, 1);
        }
    }

    private void clearGraphData() {
        ObservableList<XYChart.Series<Number, Number>> graphData = graph.getData();
        graphData.clear();
        graphSeries = new XYChart.Series<>();
        graphData.add(graphSeries);
    }

    private void toggleAutoScale() {
        autoScaleCheckBox.selectedProperty().addListener(observable -> {
            if (autoScaleCheckBox.isSelected()) {
                setGraphBounds(lowerBound, upperBound, tickUnit, true);
            } else {
                setGraphBounds(lowerBound, upperBound, tickUnit, false);
            }
        });
    }

    private void setGraphBounds(double lowerBound, double upperBound, double tickUnit, boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(isAutoRangeEnabled);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
    }

    private void initAverage() {
        setDigitFilter();
        toggleAverageUiElements();
        calculateAverage();
    }

    private void setDigitFilter() {
        averageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            averageTextField.setText(newValue.replaceAll("[^1-9]{1,3}", ""));
            if (!newValue.matches("^[1-9]{1,3}|$")) {
                averageTextField.setText(oldValue);
                averageValue = 0;
                averageCount = 1;
            }
        });
    }

    private void toggleAverageUiElements() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
            }
        });
    }

    private void calculateAverage() {
        averageTextField.textProperty().addListener(observable -> {
            if (averageCheckBox.isSelected() & !averageTextField.getText().isEmpty()) {
                averageCount = Double.parseDouble(averageTextField.getText());
            } else if (averageTextField.getText().isEmpty()) {
                averageCount = 0;
            }
        });
    }

    private boolean isDefineLTR212Slot(int slot) {
        for (Pair<Integer, LTR212> module : cm.getCrateModelInstance().getLtr212ModulesList()) {
            if (module.getValue().getSlot() == slot) {
                ltr212 = module.getValue();
                return true;
            }
        }
        return false;
    }

    private void checkCalibration() {
        if (moduleType == CrateModel.Moudules.LTR24) {
            ltr24 = cm.getLTR24Instance();
            moduleCalibrationSettings = ltr24.getCalibrationSettings()[channel];
            loadCalibrationSettings();
        } else {
            ltr212 = cm.getLTR212Instance();
            moduleCalibrationSettings = ltr212.getCalibrationSettings()[channel];
            loadCalibrationSettings();
        }
    }

    private void loadCalibrationSettings() {
        parseCalibrationSettings();

        if (calibrationState.equals("setted")) {
            setNewYaxisLabel();
            approximate();
        }
    }

    private void parseCalibrationSettings() {
        calibrationSettings = moduleCalibrationSettings.split(", ", 6);
        calibrationState = calibrationSettings[0];
    }

    private void setNewYaxisLabel() {
        String yAxisLabel = calibrationSettings[5];
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();

        yAxis.setLabel(yAxisLabel);
    }

    private void approximate() {
        linearApproximation = new LinearApproximation(getPoints());
        linearApproximation.createEquationSystem();
        linearApproximation.calculateRoots();

        linearApproximation.approximate(maxValue);
    }

    private List<XYChart.Data<Double, Double>> getPoints() {
        List<XYChart.Data<Double, Double>> points = new ArrayList<>();
        XYChart.Data<Double, Double> firstPoint = new XYChart.Data<>(Double.parseDouble(calibrationSettings[1]), Double.parseDouble(calibrationSettings[2]));
        XYChart.Data<Double, Double> secondPoint = new XYChart.Data<>(Double.parseDouble(calibrationSettings[3]), Double.parseDouble(calibrationSettings[4]));

        points.add(firstPoint);
        points.add(secondPoint);

        return points;
    }

    private void startShow() {
        new Thread(() -> {
            while (!cm.isClosed()) {
                prepareDataForShow();
                pause();
            }
            isDone = true;
        }).start();
    }

    private void prepareDataForShow() {
        switch (moduleType) {
            case LTR24:
                if (!ltr24.isBusy()) {
                    getLTR24Data();
                    processData();
                }
                break;
            case LTR212:
                if (!ltr212.isBusy()) {
                    getLTR212Data();
                    processData();
                }
                break;
        }
    }

    private void getLTR24Data() {
        ltr24.receiveData(data);
        ringBuffer.put(data);
    }

    private void processData() {
        List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();

        calculateData(intermediateList);
        showData(intermediateList);
    }

    private void calculateData(List<XYChart.Data<Number, Number>> intermediateList) {
        final int CHANNELS = 4;
        double[] buffer = new double[2048];

        if (moduleType.name().equals("LTR24")) {
            buffer = new double[39064];
        }

        ringBuffer.take(buffer, buffer.length);

        maxValue = -999;
        for (int i = channel; i < buffer.length; i += CHANNELS) {
            if (buffer[i] > maxValue) {
                maxValue = buffer[i];
            }

            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
        }

        if (averageIterator < averageCount) {
            bufferedAverageValue += maxValue;
            averageIterator++;
        } else {
            if (averageCount != 0) {
                averageValue = bufferedAverageValue / averageCount;
                averageIterator = 0;
                bufferedAverageValue = 0;
            } else {
                averageValue = maxValue;
            }
        }

        if (calibrationState.equals("setted")) {
            linearApproximation.approximate(maxValue);
            maxValue = linearApproximation.getApproximatedValue();
            intermediateList.clear();

            for (int i = channel; i < buffer.length; i += CHANNELS) {
                intermediateList.add(new XYChart.Data<>((double) i / buffer.length, maxValue));
            }
        }
    }

    private void showData(List<XYChart.Data<Number, Number>> intermediateList) {
        isDone = false;
        Platform.runLater(() -> {
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            shiftTextField.setText(String.format("%f", averageValue));
            isDone = true;
        });
    }

    private void pause() {
        while (!isDone && !cm.isClosed()) {
            sleep(10);
        }
    }

    private void getLTR212Data() {
        ltr212.receiveData(data);
        ringBuffer.put(data);
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(moduleType, channel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        String module = cm.getCrateModelInstance().getModulesNames().get(cm.getSelectedModule());
        disableAverage();
        disableAutoScale();
        wm.setModuleScene(module, cm.getSelectedModule());
        cm.loadItemsForModulesTableView();
        cm.setClosed(true);
    }

    private void disableAverage() {
        averageCheckBox.setSelected(false);
        averageTextField.setText("");
    }

    @FXML
    private void disableAutoScale() {
        autoScaleCheckBox.setSelected(false);
        toggleAutoScale();
    }

    public LTR24 getLtr24() {
        return ltr24;
    }

    public LTR212 getLtr212() {
        return ltr212;
    }

    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
