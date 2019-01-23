package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public class SignalGraphController implements BaseController {
    @FXML
    private LineChart signalGraph;
    @FXML
    private TextField valueTextField;

//    private double[] buffer = new double[data.length];
    private double averageValue;

    private WindowsManager wm;
    private ControllerManager cm;

    public void handleCalibrate() {

    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
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
