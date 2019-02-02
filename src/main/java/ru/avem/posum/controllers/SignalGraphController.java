package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.List;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField valueTextField;

    private WindowsManager wm;
    private ControllerManager cm;

    private int slot;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private double[] buffer = new double[4096];
    private RingBuffer ringBuffer = new RingBuffer(buffer.length * 10);
    private int seconds;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private CrateModel.Moudules moduleType;
    private int channel;
    private boolean isDone = true;

    @FXML
    private void initialize() {
        graphSeries.setName("Канал 1");
        graph.getData().add(graphSeries);
    }

    private Runnable startShow = () -> {
        chooseLTR24Slot();
        chooseLTR212Slot();
        while (!cm.isClosed()) {
            showData(moduleType);
        }
    };

    private void showData(CrateModel.Moudules moduleType) {
        switch (moduleType) {
            case LTR24:
                ltr24.fillArray(ltr24.getSlot(), buffer);
                ringBuffer.put(buffer);
                fillSeries();
                setGraphBounds(-5, 10, 1);

                break;
            case LTR212:
                ltr212.fillArray(ltr212.getSlot(), buffer);
                System.out.println(buffer[0]);
                ringBuffer.put(buffer);
                fillSeries();
                setGraphBounds(-0.1, 0.1, 0.01);
                break;
        }
    }

    private void setGraphBounds(double lowerBound, double upperBound, double tickUnit) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
    }

    public void fillSeries() {
        List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
        double[] data = new double[buffer.length];

        ringBuffer.take(data, data.length);

        for (int i = channel; i < data.length; i += 4) {
            intermediateList.add(new XYChart.Data<>((double) i / (data.length / 4), data[i]));
        }

        while (!isDone) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }

        Platform.runLater(() -> {
            isDone = false;
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            isDone = true;
        });
    }

    private void chooseLTR24Slot() {
        for (LTR24 module : cm.getCrateModelInstance().getLtr24ModulesList()) {
            if (module.getSlot() == slot) {
                ltr24 = module;
            }
        }
    }

    private void chooseLTR212Slot() {
        for (LTR212 module : cm.getCrateModelInstance().getLtr212ModulesList()) {
            if (module.getSlot() == slot) {
                ltr212 = module;
            }
        }
    }

    public void showValue(CrateModel.Moudules moduleType, int selectedSlot, int channel) {
        this.moduleType = moduleType;
        this.slot = selectedSlot;
        this.channel = channel - 1;
        cm.setClosed(false);
        handleClear();
        new Thread(startShow).start();
        isDone = true;
    }

    public void handleCalibrate() {

    }

    public void handleBackButton() {
        cm.setClosed(true);
        String module = cm.getCrateModelInstance().getModulesNames(cm.getSelectedCrate()).get(cm.getSelectedModule());
        wm.setModuleScene(module, cm.getSelectedModule());
        cm.loadItemsForModulesTableView();
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public void handleClear() {
        graphSeries = new XYChart.Series<>();
        graph.getData().clear();
        graph.getData().add(graphSeries);
        seconds = 0;
    }
}
