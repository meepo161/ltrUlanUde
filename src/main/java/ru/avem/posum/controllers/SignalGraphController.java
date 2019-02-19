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
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField valueTextField;

    private WindowsManager wm;
    private ControllerManager cm;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private double[] data;
    private double maxValue;
    private RingBuffer ringBuffer;
    private volatile XYChart.Series<Number, Number> graphSeries;
    private volatile boolean isDone;

    public  void initializeView(CrateModel.Moudules moduleType, int selectedSlot, int channel) {
        cm.setClosed(false);
        clear();

        initializeModuleType(selectedSlot);

        new Thread(() -> {
            while (!cm.isClosed()) {
                showData(moduleType, channel - 1);
                while (!isDone && !cm.isClosed()) {
                    sleep(10);
                }
            }
            isDone = true;
        }).start();
    }

    private void clear() {
        ObservableList<XYChart.Series<Number, Number>> graphData = graph.getData();
        graphData.clear();
        graphSeries = new XYChart.Series<>();
        graphData.add(graphSeries);
    }

    private void initializeModuleType(int selectedSlot) {
        if (isDefineLTR24Slot(selectedSlot)) {
            data = new double[50000];
            ringBuffer = new RingBuffer(data.length * 100);
            setGraphBounds(-5, 10, 1, false);
        } else if (isDefineLTR212Slot(selectedSlot)) {
            data = new double[2048];
            ringBuffer = new RingBuffer(data.length * 10);
            setGraphBounds(-0.1, 0.1, 0.01, false);
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

    private void showData(CrateModel.Moudules moduleType, int channelIndex) {
        switch (moduleType) {
            case LTR24:
                if (!ltr24.isBusy()) {
                    ltr24.receiveData(data);
                    ringBuffer.put(data);
                    fillSeries(moduleType, channelIndex);
                }
                break;
            case LTR212:
                if (!ltr212.isBusy()) {
                    ltr212.receiveData(data);
                    ringBuffer.put(data);
                    fillSeries(moduleType, channelIndex);
                }
                break;
        }
    }

    private void fillSeries(CrateModel.Moudules moduleType, int channelIndex) {
        List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
        int scale = 1;
        double[] buffer = new double[2048];

        if (moduleType.name().equals("LTR24")) {
            buffer = new double[150000];
            scale = 10;
        }

        ringBuffer.take(buffer, buffer.length);
        maxValue = -999;

        for (int i = channelIndex; i < buffer.length; i += 4 * scale) {
            intermediateList.add(new XYChart.Data<>((double) i / (buffer.length / 4), buffer[i]));
            if (buffer[i] > maxValue) {
                maxValue = (double) Math.round(buffer[i] * 100) / 100;
            }
        }

        isDone = false;
        Platform.runLater(() -> {
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            valueTextField.setText(Double.toString(maxValue));
            isDone = true;
        });
    }

    @FXML
    private void handleClear() {
        clear();
    }

    @FXML
    private void handleCalibrate() {
        cm.setCalibrationStopped();
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
