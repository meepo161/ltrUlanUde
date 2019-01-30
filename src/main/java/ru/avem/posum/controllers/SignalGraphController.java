package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;

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
    private double[] buffer = new double[128];
    private double averageValue;
    private int seconds;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private CrateModel.Moudules moduleType;
    private int channel;

    @FXML
    private void initialize() {
        graphSeries.setName("Канал 1");
        graph.getData().add(graphSeries);
    }

    private Runnable startShow = () -> {
        while (!cm.isClosed()) {
            showData(moduleType);
        }
    };

    private void showData(CrateModel.Moudules moduleType) {
        switch (moduleType) {
            case LTR24:
                chooseLTR24Slot();
                ltr24.fillArray(ltr24.getSlot(), buffer);
                break;
            case LTR34:
                break;
            case LTR212:
                chooseLTR212Slot();
                ltr212.fillArray(ltr212.getSlot(), buffer);
                break;
        }

        averageValue = 0;
        for (int i = 0; i < buffer.length; i += 4) {
            averageValue += buffer[channel] / (buffer.length / 4);

        }

        Platform.runLater(() -> {
            graphSeries.getData().add(new XYChart.Data<>(seconds++, averageValue));
            valueTextField.setText(String.valueOf(Math.ceil(averageValue * 100) / 100));
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
        graph.getData().clear();
        graphSeries = new XYChart.Series<>();
        intermediateList = new ArrayList<>();
        graph.getData().add(graphSeries);
        seconds = 0;
    }
}
