package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField valueTextField;

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

    public void initializeView(CrateModel.Moudules moduleType, int selectedSlot, int channel) {
        setFields(moduleType, channel);
        clearGraphData();
        initializeModuleType(selectedSlot);
        checkCalibration();
        startShow();
    }

    private void setFields(CrateModel.Moudules moduleType, int channel) {
        this.moduleType = moduleType;
        this.channel = channel;
        cm.setClosed(false);
    }

    private void clearGraphData() {
        ObservableList<XYChart.Series<Number, Number>> graphData = graph.getData();
        graphData.clear();
        graphSeries = new XYChart.Series<>();
        graphData.add(graphSeries);
    }

    private void initializeModuleType(int selectedSlot) {
        if (isDefineLTR24Slot(selectedSlot)) {
            data = new double[39064];
            ringBuffer = new RingBuffer(data.length * 100);
            setGraphBounds(-5, 10, 1, false);
        } else if (isDefineLTR212Slot(selectedSlot)) {
            data = new double[2048];
            ringBuffer = new RingBuffer(data.length * 10);
            setGraphBounds(-0.1, 0.1, 0.01, true);
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

    private void setGraphBounds(double lowerBound, double upperBound, double tickUnit, boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
        yAxis.setAutoRanging(isAutoRangeEnabled);
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
        maxValue = -999;
        final int CHANNELS = 4;
        double[] buffer = new double[2048];

        if (moduleType.name().equals("LTR24")) {
            buffer = new double[39064];
        }

        ringBuffer.take(buffer, buffer.length);

        for (int i = channel; i < buffer.length; i += CHANNELS) {
            if (buffer[i] > maxValue) {
                maxValue = (double) Math.round(buffer[i] * 100) / 100;
            }

            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
        }

        if (calibrationState.equals("setted")) {
            linearApproximation.approximate(maxValue);
            maxValue = (double) Math.round(linearApproximation.getApproximatedValue() * 100) / 100;
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
            valueTextField.setText(Double.toString(maxValue));
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
    private void handleClear() {
        clearGraphData();
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
        wm.setModuleScene(module, cm.getSelectedModule());
        cm.loadItemsForModulesTableView();
        cm.setClosed(true);
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
