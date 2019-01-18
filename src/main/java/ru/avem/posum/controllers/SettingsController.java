package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.avem.posum.Main;

public class SettingsController {
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

    private Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleChooseCrate() {

    }

    public void handleSetupModule() {

    }

    public void handleSaveSetup() {

    }

    public void handleSaveExperimentGeneralSettings() {

    }

    public void handleBackButton() {
        Main.getPrimaryStage().setScene(main.getMainScene());
    }
}
