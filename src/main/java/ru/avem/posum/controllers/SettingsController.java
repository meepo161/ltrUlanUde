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
import ru.avem.posum.hardware.CrateModel;

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

    private CrateModel crateModel = new CrateModel();
    private int selectedCrate;
    private int selectedModule;
    private int slot;
    private Protocol protocol;
    private boolean editMode;

    @FXML
    private void initialize() {
        initCrate();
    }

    private void initCrate() {
        cratesListView.setItems(crateModel.getCratesNames());
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            modulesListView.setItems(crateModel.fillModulesNames(selectedCrate));
            cm.createListModulesControllers(crateModel.getModulesNames(selectedCrate));
        }));
    }

    public void handleChooseCrate() {
        ObservableList<String> crates = crateModel.getCratesNames();

        for (int i = 0; i < crates.size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                cratesListView.setDisable(true);
                chooseCrateButton.setDisable(true);
                modulesListView.setDisable(false);
                setupModuleButton.setDisable(false);
            }
        }
    }

    public void handleSetupModule() {
        ObservableList<String> modulesNames = crateModel.getModulesNames(selectedCrate);

        for (int i = 0; i < modulesNames.size(); i++) {
            if (modulesListView.getSelectionModel().isSelected(i)) {
                selectedModule = modulesListView.getSelectionModel().getSelectedIndex();
                String module = modulesNames.get(selectedModule);

                slot = Integer.parseInt(module.split("Слот ")[1].split("\\)")[0]);

                showModuleSettings(module);

                cm.refreshLTR24Settings();
                break;
            }
        }
    }

    public void handleSaveExperimentGeneralSettings() {
        parseGeneralSettingsData();

        if (editMode) {
            protocol.setExperimentName(experimentName);
            protocol.setSampleName(sampleName);
            protocol.setSampleSerialNumber(sampleSerialNumber);
            protocol.setDocumentNumber(documentNumber);
            protocol.setExperimentType(experimentType);
            protocol.setExperimentTime(experimentTime);
            protocol.setExperimentDate(experimentDate);
            protocol.setLeadEngineer(leadEngineer);
            protocol.setComments(comments);
            ProtocolRepository.updateProtocol(protocol);
        } else {
            protocol = new Protocol(experimentName, sampleName, sampleSerialNumber, documentNumber, experimentType, experimentTime, experimentDate, leadEngineer, comments);
            ProtocolRepository.insertProtocol(protocol);
        }
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
        cm.loadItemsForMainTableView();
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    private void showModuleSettings(String module) {
        String moduleName = (module + " ").substring(0, 6).trim();
        wm.setModuleScene(moduleName, selectedModule);
    }

    public void refreshModulesList() {
        modulesListView.setItems(crateModel.getModulesNames(selectedCrate));
    }

    public void clearSettingsView() {
        experimentNameTextField.setText("");
        sampleNameTextField.setText("");
        sampleSerialNumberTextField.setText("");
        documentNumberTextField.setText("");
        experimentTypeTextField.setText("");
        experimentTimeTextField.setText("");
        experimentDateTextField.setText("");
        leadEngineerTextField.setText("");
        commentsTextArea.setText("");
    }

    public void setupProtocol(Protocol protocol) {
        experimentNameTextField.setText(protocol.getExperimentName());
        sampleNameTextField.setText(protocol.getSampleName());
        sampleSerialNumberTextField.setText(protocol.getSampleSerialNumber());
        documentNumberTextField.setText(protocol.getDocumentNumber());
        experimentTypeTextField.setText(protocol.getExperimentType());
        experimentTimeTextField.setText(protocol.getExperimentTime());
        experimentDateTextField.setText(protocol.getExperimentDate());
        leadEngineerTextField.setText(protocol.getLeadEngineer());
        commentsTextArea.setText(protocol.getComments());

        this.protocol = protocol;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
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

    public int getSlot() {
        return slot;
    }

    public CrateModel getCrateModel() {
        return crateModel;
    }

    public void handleSaveSetup() {

    }
}
