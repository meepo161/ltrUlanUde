package ru.avem.posum.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.ProtocolRepository;
import ru.avem.posum.db.models.Protocol;
import ru.avem.posum.hardware.Crate;

public class SettingsController implements BaseController {
    @FXML
    private Button chooseCrateButton;
    @FXML
    private Button setupModuleButton;
    @FXML
    private ListView<String> cratesListView;
    @FXML
    private ListView<String> modulesListView;
    @FXML
    private TextArea commentsTextArea;
    @FXML
    private TextField experimentNameTextField;
    @FXML
    private TextField sampleNameTextField;
    @FXML
    private TextField sampleSerialNumberTextField;
    @FXML
    private TextField documentNumberTextField;
    @FXML
    private TextField experimentTimeTextField;
    @FXML
    private TextField experimentDateTextField;
    @FXML
    private TextField experimentTypeTextField;
    @FXML
    private TextField leadEngineerTextField;

    private String experimentName;
    private String sampleName;
    private String sampleSerialNumber;
    private String documentNumber;
    private String experimentType;
    private String experimentTime;
    private String experimentDate;
    private String leadEngineer;
    private String comments;

    private WindowsManager wm;
    private ControllerManager cm;

    private Crate crate = new Crate();
    private int selectedCrate;
    private int selectedModule;

    @FXML
    private void initialize() {
        initCrate();
    }

    private void initCrate() {
        cratesListView.setItems(crate.getCratesNames());
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            modulesListView.setItems(crate.getModulesNames(selectedCrate));
        }));
    }

    public void handleChooseCrate() {
        cratesListView.setDisable(true);
        chooseCrateButton.setDisable(true);
        modulesListView.setDisable(false);
        setupModuleButton.setDisable(false);
    }

    public void handleSetupModule() {
        ObservableList<String> modulesNames;
        modulesNames = crate.getModulesNames(selectedCrate);
        selectedModule = modulesListView.getSelectionModel().getSelectedIndex();
        String module = modulesNames.get(selectedModule);
        showModuleSettings(module);
    }

    public void handleSaveSetup() {

    }

    public void handleSaveExperimentGeneralSettings() {
        parseGeneralSettingsData();
        Protocol protocol = new Protocol(experimentName, sampleName, sampleSerialNumber, documentNumber, experimentType, experimentTime, experimentDate, leadEngineer, comments);
        ProtocolRepository.insertProtocol(protocol);
    }

    private void parseGeneralSettingsData() {
        experimentName = experimentNameTextField.getText();
        sampleName = sampleNameTextField.getText();
        sampleSerialNumber = sampleSerialNumberTextField.getText();
        documentNumber = documentNumberTextField.getText();
        experimentType = experimentTypeTextField.getText();
        experimentTime = experimentTimeTextField.getText();
        experimentDate = experimentDateTextField.getText();
        leadEngineer = leadEngineerTextField.getText();
        comments = commentsTextArea.getText();
    }

    public void handleBackButton() {
        cm.loadItemsForTableView();
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void showModuleSettings(String module) {
        switch (module) {
            case "LTR24":
                wm.setScene(WindowsManager.Scenes.LTR24_SCENE);
                break;
            case "LTR34":
                wm.setScene(WindowsManager.Scenes.LTR34_SCENE);
                break;
            case "LTR212":
                wm.setScene(WindowsManager.Scenes.LTR212_SCENE);
                break;
        }
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public int getSelectedCrate() {
        return selectedCrate;
    }

    public int getSelectedModule() {
        return selectedModule;
    }

    public Crate getCrate() {
        return crate;
    }
}
