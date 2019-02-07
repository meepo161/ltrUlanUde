package ru.avem.posum.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.CrateRepository;
import ru.avem.posum.db.ProtocolRepository;
import ru.avem.posum.db.models.Crate;
import ru.avem.posum.db.models.TestProgramm;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.LTR34;

import java.util.ArrayList;
import java.util.List;

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

    private WindowsManager wm;
    private ControllerManager cm;
    private String experimentName;
    private String sampleName;
    private String sampleSerialNumber;
    private String documentNumber;
    private String experimentType;
    private String experimentTime;
    private String experimentDate;
    private String leadEngineer;
    private String comments;
    private CrateModel crateModel = new CrateModel();
    private int selectedCrate;
    private int selectedModule;
    private int slot;
    private TestProgramm testProgramm;
    private Crate crate;
    private boolean editMode;

    @FXML
    private void initialize() {
        initCrate();
    }

    public void loadDefaultSettings() {
        experimentNameTextField.setText("");
        sampleNameTextField.setText("");
        sampleSerialNumberTextField.setText("");
        documentNumberTextField.setText("");
        experimentTypeTextField.setText("");
        experimentTimeTextField.setText("");
        experimentDateTextField.setText("");
        leadEngineerTextField.setText("");
        commentsTextArea.setText("");

        cratesListView.getSelectionModel().clearSelection();
        modulesListView.getSelectionModel().clearSelection();

        cratesListView.setDisable(false);
        modulesListView.setDisable(true);
        chooseCrateButton.setDisable(false);
        setupModuleButton.setDisable(true);
    }

    private void initCrate() {
        cratesListView.setItems(crateModel.getCratesNames());
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            modulesListView.setItems(crateModel.fillModulesNames(selectedCrate));
            cm.createListModulesControllers(crateModel.getModulesNames());
        }));
    }

    public void handleChooseCrate() {
        ObservableList<String> crates = crateModel.getCratesNames();

        for (int i = 0; i < crates.size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                cratesListView.setDisable(true);
                chooseCrateButton.setDisable(true);
                modulesListView.setDisable(false);
                saveSetupButton.setDisable(false);
                setupModuleButton.setDisable(false);
            }
        }
    }

    public void handleSetupModule() {
        ObservableList<String> modulesNames = crateModel.getModulesNames();

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

    private void showModuleSettings(String module) {
        String moduleName = (module + " ").substring(0, 6).trim();
        wm.setModuleScene(moduleName, selectedModule);
    }

    public void handleSaveExperimentGeneralSettings() {
        parseGeneralSettingsData();

        if (editMode) {
            testProgramm.setExperimentName(experimentName);
            testProgramm.setSampleName(sampleName);
            testProgramm.setSampleSerialNumber(sampleSerialNumber);
            testProgramm.setDocumentNumber(documentNumber);
            testProgramm.setExperimentType(experimentType);
            testProgramm.setExperimentTime(experimentTime);
            testProgramm.setExperimentDate(experimentDate);
            testProgramm.setLeadEngineer(leadEngineer);
            testProgramm.setComments(comments);
            ProtocolRepository.updateProtocol(testProgramm);
        } else {
            testProgramm = new TestProgramm(experimentName, sampleName, sampleSerialNumber, documentNumber, experimentType, experimentTime, experimentDate, leadEngineer, comments);
            ProtocolRepository.insertProtocol(testProgramm);
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
        stopModules();

        cm.loadItemsForMainTableView();
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    private void stopModules() {
        for (LTR24 ltr24 : crateModel.getLtr24ModulesList()) {
            ltr24.closeConnection();
        }

        for (LTR34 ltr34 : crateModel.getLtr34ModulesList()) {
            ltr34.closeConnection();
        }

        for (LTR212 ltr212 : crateModel.getLtr212ModulesList()) {
            ltr212.closeConnection();
        }
    }

    public void refreshModulesList() {
        modulesListView.setItems(crateModel.getModulesNames());
    }

    public void setupProtocol(TestProgramm testProgramm) {
        experimentNameTextField.setText(testProgramm.getExperimentName());
        sampleNameTextField.setText(testProgramm.getSampleName());
        sampleSerialNumberTextField.setText(testProgramm.getSampleSerialNumber());
        documentNumberTextField.setText(testProgramm.getDocumentNumber());
        experimentTypeTextField.setText(testProgramm.getExperimentType());
        experimentTimeTextField.setText(testProgramm.getExperimentTime());
        experimentDateTextField.setText(testProgramm.getExperimentDate());
        leadEngineerTextField.setText(testProgramm.getLeadEngineer());
        commentsTextArea.setText(testProgramm.getComments());

        this.testProgramm = testProgramm;
    }

    public void handleSaveSetup() {
        List<String> LTR24List = new ArrayList<>();
        List<String> LTR34List = new ArrayList<>();
        List<String> LTR212List = new ArrayList<>();

        for (String module : crateModel.getModulesNames()) {
            switch (module.split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24List.add(module);
                    break;
                case CrateModel.LTR34 :
                    LTR34List.add(module);
                    break;
                case CrateModel.LTR212:
                    LTR212List.add(module);
                    break;
            }
        }

        crate = new Crate(crateModel.getCrates()[0][selectedCrate], LTR24List, LTR34List, LTR212List);
        CrateRepository.insertCrate(crate);
    }

    public CrateModel getCrateModel() {
        return crateModel;
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

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
