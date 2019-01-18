package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.avem.posum.WindowsManager;

public class SettingsController implements BaseController {
    @FXML
    private DatePicker experimentDate_DatePicker;
    @FXML
    private Spinner experimentTime_Spinner;
    @FXML
    private TextArea comments_TextArea;
    @FXML
    private TextField experimentName_TextField;
    @FXML
    private TextField sampleName_TextField;
    @FXML
    private TextField sampleSerialNumber_TextField;
    @FXML
    private TextField documentNumber_TextField;
    @FXML
    private TextField experimentType_TextField;
    @FXML
    private TextField leadEngineer_TextField;

    private WindowsManager wm;

    public void handleChooseCrate() {

    }

    public void handleSetupModule() {

    }

    public void handleSaveSetup() {

    }

    public void handleSaveExperimentGeneralSettings() {

    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
