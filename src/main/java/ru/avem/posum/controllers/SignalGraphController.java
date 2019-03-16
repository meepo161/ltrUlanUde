package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.models.ReceivedSignal;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private CheckBox autoScaleCheckBox;
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
    private TextField zeroShiftTextField;
    @FXML
    private Label titleLabel;

    private ADC adc;
    private double amplitude;
    private double averageCount = 1;
    private int averageIterator;
    private double averageValue;
    private double[] buffer;
    private double bufferedAmplitude;
    private double bufferedPhase;
    private double buffereZeroShift;
    private int channel;
    private ControllerManager cm;
    private double[] data;
    private double frequency;
    private volatile XYChart.Series<Number, Number> graphSeries;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private volatile boolean isDone;
    private double lowerBound;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleType;
    private double phase;
    private RingBuffer ringBuffer;
    private int slot;
    private double tickUnit;
    private double upperBound;
    private WindowsManager wm;
    private double zeroShift;

    public void initializeView(String moduleType, int slot, int channel) {
        setFields(moduleType, slot, channel);
        setTitleLabel();
        setApplicationState(false);
        listenAverageCheckBox();
        listenAutoScaleCheckBox();
        initGraph();
        initAverage();
        initModule();
        startShow();
    }

    private void setTitleLabel() {
        titleLabel.setText("Текущая нагрузка на " + (channel + 1) + " канале" + " (" + moduleType + " слот " + slot + ")");
    }

    private void setFields(String moduleType, int slot, int channel) {
        this.moduleType = moduleType;
        this.slot = slot;
        this.channel = channel;
    }

    private void setApplicationState(boolean isClosed) {
        cm.setClosed(isClosed);
    }

    private void listenAverageCheckBox() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
                averageTextField.setText("");
                averageCount = 1;
            }
        });
    }

    private void initGraph() {
        Platform.runLater(() -> {
            ObservableList<XYChart.Series<Number, Number>> graphData = graph.getData();
            graphData.clear();
            graphSeries = new XYChart.Series<>();
            graphData.add(graphSeries);
        });
    }

    private void initModule() {
        getModuleInstance();
        addInitModuleInstructions();
        runInstructions();
        addDefineBoundsInstructions();
        runInstructions();
        setGraphBounds(lowerBound, upperBound, tickUnit, false);
    }

    private void getModuleInstance() {
        for (Pair<Integer, Module> module : cm.getCrateModelInstance().getModulesList()) {
            if (module.getKey() == slot) {
                adc = (ADC) module.getValue();
            }
        }
    }

    private void addInitModuleInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::initLTR24Module);
        instructions.put(CrateModel.LTR212, this::initLTR212Module);
    }

    private void initLTR24Module() {
        ltr24 = (LTR24) adc;
        data = new double[39064];
        buffer = new double[39064];
        ringBuffer = new RingBuffer(data.length * 100);
    }

    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        data = new double[2048];
        buffer = new double[2048];
        ringBuffer = new RingBuffer(data.length * 10);
    }

    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    private void addDefineBoundsInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::defineLTR24Bounds);
        instructions.put(CrateModel.LTR212, this::defineLTR212MeasuringRange);
    }

    private void defineLTR24Bounds() {
        if (cm.getICPMode()) {
            defineICPModeRanges();
        } else {
            defineDifferentialModeRanges();
        }
    }

    private void defineICPModeRanges() {
        switch (ltr24.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(0, 1, 0.1);
                break;
            case 1:
                setBounds(0, 5, 0.5);
                break;
            default:
                setBounds(0, 5, 0.5);
        }
    }

    private void defineDifferentialModeRanges() {
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

    private void defineLTR212MeasuringRange() {
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

    private void listenAutoScaleCheckBox() {
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
        changeAverageUiElementsState();
    }

    private void setDigitFilter() {
        averageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            averageTextField.setText(newValue.replaceAll("[^1-9][\\d]{2,3}", ""));
            if (!newValue.matches("^[1-9]|\\d{2,3}|$")) {
                averageTextField.setText(oldValue);
            }

            if (!averageTextField.getText().isEmpty()) {
                averageCount = Double.parseDouble(averageTextField.getText());
            }
        });
    }

    private void changeAverageUiElementsState() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
            }
        });
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
        if (!adc.isBusy()) {
            addGettingDataInstructions();
            runInstructions();
            processData();
        }
    }

    private void addGettingDataInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::getLTR24Data);
        instructions.put(CrateModel.LTR212, this::getLTR212Data);
    }

    private void getLTR24Data() {
        ltr24.receive(data);
        ringBuffer.put(data);
    }

    private void getLTR212Data() {
        ltr212.receive(data);
        ringBuffer.put(data);
    }

    private void processData() {
        calculateData();
        showData();
    }

    private void calculateData() {
        fillBuffer();
        clearSeriesData();
        addSeriesData();
        calculateParameters();
    }

    private void fillBuffer() {
        ringBuffer.take(buffer, buffer.length);
    }

    private void clearSeriesData() {
        intermediateList.clear();
    }

    private void addSeriesData() {
        final int CHANNELS = 4;
        for (int i = channel; i < buffer.length; i += CHANNELS) {
            addPointToGraph(buffer, i);
        }
    }

    private void addPointToGraph(double[] buffer, int i) {
        intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
    }

    private void calculateParameters() {
        ReceivedSignal receivedSignal = new ReceivedSignal();
        receivedSignal.calculateBaseParameters(buffer, channel);

        if (averageIterator < averageCount) {
            bufferedAmplitude += receivedSignal.getAmplitude();
            buffereZeroShift += receivedSignal.getZeroShift();
            bufferedPhase += receivedSignal.getPhase();
            averageIterator++;
        } else {
            amplitude = bufferedAmplitude / averageCount;
            zeroShift = buffereZeroShift / averageCount;
            phase = buffereZeroShift / averageCount;
            averageIterator = 0;
            bufferedAmplitude = 0;
            buffereZeroShift = 0;
            bufferedPhase = 0;
        }
    }

    private void showData() {
        isDone = false;
        Platform.runLater(() -> {
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            amplitudeTextField.setText(String.format("%.3f", amplitude));
            frequencyTextField.setText(String.format("%.3f", frequency));
            phaseTextField.setText(String.format("%.3f", phase));
            zeroShiftTextField.setText(String.format("%.3f", zeroShift));
            isDone = true;
        });
    }

    private void pause() {
        while (!isDone && !cm.isClosed()) {
            sleep(10);
        }
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(adc, moduleType, channel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
        setApplicationState(true);
        disableAutoRange();
        disableAverage();
    }

    private void disableAutoRange() {
        setGraphBounds(lowerBound, upperBound, tickUnit, false);
        autoScaleCheckBox.setSelected(false);
    }

    private void disableAverage() {
        averageTextField.setText("");
        averageCheckBox.setSelected(false);
    }

    public double getZeroShift() {
        return zeroShift;
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
