package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField valueTextField;

    private ADC adc;
    private double[] buffer;
    private int channel;
    private ControllerManager cm;
    private double[] data;
    private volatile XYChart.Series<Number, Number> graphSeries;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private volatile boolean isDone;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private double maxValue;
    private String moduleType;
    private RingBuffer ringBuffer;
    private int slot;
    private WindowsManager wm;

    public void initializeView(String moduleType, int slot, int channel) {
        setFields(moduleType, slot, channel);
        setTitleLabel();
        setApplicationState(false);
        initGraph();
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
        setGraphBounds(-5, 10, 1, false);
    }

    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        data = new double[2048];
        buffer = new double[2048];
        ringBuffer = new RingBuffer(data.length * 10);
        setGraphBounds(-0.1, 0.1, 0.01, true);
    }

    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    private void setGraphBounds(double lowerBound, double upperBound, double tickUnit, boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
        yAxis.setAutoRanging(isAutoRangeEnabled);
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
        maxValue = -999;
        final int CHANNELS = 4;

        fillBuffer();
        clearSeriesData();
        calculate(CHANNELS);
    }

    private void fillBuffer() {
        ringBuffer.take(buffer, buffer.length);
    }

    private void clearSeriesData() {
        intermediateList.clear();
    }

    private void calculate(int CHANNELS) {
        for (int i = channel; i < buffer.length; i += CHANNELS) {
            defineMaxValue(buffer[i]);
            addPointToGraph(buffer, i);
        }
    }

    private void defineMaxValue(double value) {
        if (value > maxValue) {
            maxValue = (double) Math.round(value * 100) / 100;
        }
    }

    private void addPointToGraph(double[] buffer, int i) {
        intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
    }

    private void showData() {
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

    @FXML
    private void handleClear() {
        initGraph();
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(adc, channel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
        setApplicationState(true);
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
