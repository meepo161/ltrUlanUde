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
import ru.avem.posum.models.CrateModel;
import ru.avem.posum.models.HardwareModel;

public class SettingsController implements BaseController {
    @FXML
    private Button chooseCrateButton;
    @FXML
    private Button saveSetupButton;
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
    private CrateModel crateModel = new CrateModel();

    private int selectedCrate;

    @FXML
    private void initialize() {
        HardwareModel.getInstance().initModules();
        initCrate();
    }

    private void initCrate() {
        cratesListView.setItems(HardwareModel.getInstance().getCrate().getCratesNames());
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            modulesListView.setItems(HardwareModel.getInstance().getCrate().getModulesNames(cratesListView.getSelectionModel().getSelectedIndex()));
        }));
    }

    public void handleChooseCrate() {
        selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();

        crateModel.setSelectedCrate(HardwareModel.getInstance().getCrate().getCratesNames().get(selectedCrate));
        toggleUiElements();
    }

    private void toggleUiElements() {
        cratesListView.setDisable(true);
        chooseCrateButton.setDisable(true);
        modulesListView.setDisable(false);
        setupModuleButton.setDisable(false);
    }

    public void handleSetupModule() {
        ObservableList<String> modulesNames;
        modulesNames = HardwareModel.getInstance().getCrate().getModulesNames(selectedCrate);
        String module = modulesNames.get(modulesListView.getSelectionModel().getSelectedIndex());
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
}
