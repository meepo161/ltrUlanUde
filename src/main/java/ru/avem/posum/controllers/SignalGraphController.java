package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField valueTextField;

    private ADC adc;
    private int channel;
    private double[] data;
    private double maxValue;
    private CrateModel.Moudules moduleType;
    private RingBuffer ringBuffer;
    private double[] buffer;
    private volatile boolean isDone;
    List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private volatile XYChart.Series<Number, Number> graphSeries;
    private WindowsManager wm;
    private ControllerManager cm;

    public void initializeView(ADC adc, int selectedSlot, int channel) {
        setFields(adc, channel);
        changeState(false);
        clearGraphData();
        initializeModuleType(selectedSlot);
        startShow();
    }

    private void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
    }

    private void changeState(boolean isClosed) {
        cm.setClosed(isClosed);
    }

    private void clearGraphData() {
        Platform.runLater(() -> {
            ObservableList<XYChart.Series<Number, Number>> graphData = graph.getData();
            graphData.clear();
            graphSeries = new XYChart.Series<>();
            graphData.add(graphSeries);
        });
    }

    private void initializeModuleType(int selectedSlot) {
        if (isDefineLTR24Slot(selectedSlot)) {
            data = new double[39064];
            ringBuffer = new RingBuffer(data.length * 100);
            setGraphBounds(-5, 10, 1, false);
            moduleType = CrateModel.Moudules.LTR24;
        } else if (isDefineLTR212Slot(selectedSlot)) {
            data = new double[2048];
            ringBuffer = new RingBuffer(data.length * 10);
            setGraphBounds(-0.1, 0.1, 0.01, true);
            moduleType = CrateModel.Moudules.LTR212;
        }
    }

    private boolean isDefineLTR24Slot(int slot) {
//        for (Pair<Integer, LTR24> module : cm.getCrateModelInstance().getLtr24ModulesList()) {
//            if (module.getValue().getSlot() == slot) {
//                adc = module.getValue();
//                return true;
//            }
//        }
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
//        for (Pair<Integer, LTR212> module : cm.getCrateModelInstance().getLtr212ModulesList()) {
//            if (module.getValue().getSlot() == slot) {
//                adc = module.getValue();
//                return true;
//            }
//        }
        return false;
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
            getADCData();
            processData();
        }
    }

    private void getADCData() {
//        adc.receiveData(data);
        ringBuffer.put(data);
    }

    private void processData() {
        calculateData();
        showData();
    }

    private void calculateData() {
        maxValue = -999;
        final int CHANNELS = 4;

        createBuffer();
        fillBuffer();
        clearSeriesData();

        for (int i = channel; i < buffer.length; i += CHANNELS) {
            calculateMaxValue(buffer[i]);
            addPointToGraph(buffer, i);
        }
    }

    private double[] createBuffer() {
        if (moduleType.name().equals("LTR24")) {
            buffer = new double[39064];
        } else {
            buffer = new double[2048];
        }

        return buffer;
    }

    private void fillBuffer() {
        ringBuffer.take(buffer, buffer.length);
    }

    private void clearSeriesData() {
        intermediateList.clear();
    }

    private void calculateMaxValue(double value) {
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
        clearGraphData();
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(adc, channel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
//        String module = cm.getCrateModelInstance().getModulesNames().get(cm.getSelectedModuleIndex());
//        wm.setModuleScene(module, cm.getSelectedModuleIndex());
        cm.loadItemsForModulesTableView();
        changeState(true);
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
