package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public class SignalGraphController implements BaseController {
    @FXML
    private Button backButton;

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
