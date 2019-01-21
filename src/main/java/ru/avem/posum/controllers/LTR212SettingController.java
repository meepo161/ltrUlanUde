package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public class LTR212SettingController implements BaseController {
    @FXML
    private Button backButton;

    private WindowsManager wm;
    private ControllerManager cm;

    @FXML
    private void initialize() {

    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }
}