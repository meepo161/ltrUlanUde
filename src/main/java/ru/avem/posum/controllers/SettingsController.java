package ru.avem.posum.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.LTR212ModuleRepository;
import ru.avem.posum.db.LTR24ModuleRepository;
import ru.avem.posum.db.LTR34ModuleRepository;
import ru.avem.posum.db.TestProgrammRepository;
import ru.avem.posum.db.models.LTR212Module;
import ru.avem.posum.db.models.LTR24Module;
import ru.avem.posum.db.models.LTR34Module;
import ru.avem.posum.db.models.TestProgramm;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.utils.StatusBarLine;

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
    private StatusBar statusBar;
    @FXML
    private TextArea commentsTextArea;
    @FXML
    private TextField testProgrammNameTextField;
    @FXML
    private TextField sampleNameTextField;
    @FXML
    private TextField sampleSerialNumberTextField;
    @FXML
    private TextField documentNumberTextField;
    @FXML
    private TextField testProgrammTimeTextField;
    @FXML
    private TextField testProgrammDateTextField;
    @FXML
    private TextField testProgrammTypeTextField;
    @FXML
    private TextField leadEngineerTextField;

    private WindowsManager wm;
    private ControllerManager cm;
    private CrateModel crateModel = new CrateModel();
    private int selectedCrate;
    private int selectedModule;
    private ObservableList<String> modulesNames;
    private ObservableList<String> crates;
    private String crate;
    private int slot;
    private TestProgramm testProgramm;
    private boolean editMode;
    private StatusBarLine statusBarLine = new StatusBarLine();

    @FXML
    private void initialize() {
        initCrate();
    }

    private void initCrate() {
        crates = crateModel.getCratesNames();
        cratesListView.setItems(crates);
        showCrateModules();
    }

    private void showCrateModules() {
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            modulesListView.setItems(crateModel.fillModulesNames(selectedCrate));
            modulesNames = crateModel.getModulesNames();
            cm.createListModulesControllers(modulesNames);
        }));
    }

    public void loadDefaultSettings() {
        testProgrammNameTextField.setText("");
        sampleNameTextField.setText("");
        sampleSerialNumberTextField.setText("");
        documentNumberTextField.setText("");
        testProgrammTypeTextField.setText("");
        testProgrammTimeTextField.setText("");
        testProgrammDateTextField.setText("");
        leadEngineerTextField.setText("");
        commentsTextArea.setText("");

        cratesListView.getSelectionModel().clearSelection();
        modulesListView.getSelectionModel().clearSelection();

        cratesListView.setDisable(false);
        modulesListView.setDisable(true);
        chooseCrateButton.setDisable(false);
        setupModuleButton.setDisable(true);
    }

    public void handleChooseCrate() {
        createModulesInstances();

        for (int i = 0; i < crates.size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                cratesListView.setDisable(true);
                chooseCrateButton.setDisable(true);
                modulesListView.setDisable(false);
                setupModuleButton.setDisable(false);
            }
        }
    }

    private void createModulesInstances() {
        crate = crateModel.getCrates()[0][selectedCrate]; // серийный номер крейта

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    ltr24.setCrate(crate);
                    ltr24.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR24> ltr24Pair = new Pair<>(i, ltr24);
                    crateModel.getLtr24ModulesList().add(ltr24Pair);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = new LTR34();
                    ltr34.setCrate(crate);
                    ltr34.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
                    crateModel.getLtr34ModulesList().add(ltr34Pair);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    ltr212.setCrate(crate);
                    ltr212.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
                    crateModel.getLtr212ModulesList().add(ltr212Pair);
                    break;
            }
        }
    }

    private int parseSlotNumber(String module) {
        return Integer.parseInt(module.split("Слот ")[1].split("\\)")[0]); // номер слота
    }

    public void handleSetupModule() {
        for (int i = 0; i < modulesNames.size(); i++) {
            if (modulesListView.getSelectionModel().isSelected(i)) {
                selectedModule = modulesListView.getSelectionModel().getSelectedIndex();
                String module = modulesNames.get(selectedModule);

                slot = parseSlotNumber(module);

                showModuleSettings(module);
                break;
            }
        }
    }

    private void showModuleSettings(String module) {
        String moduleName = (module + " ").substring(0, 6).trim();
        wm.setModuleScene(moduleName, selectedModule);
    }

    public void handleSaveTestProgrammSettings() {
        if (!chooseCrateButton.isDisabled()) {
            statusBarLine.setStatus("Ошибка сохранения настроек: необходимо выбрать крейт.", statusBar);
        } else {
            saveSettings();
        }
    }

    private void saveSettings() {
        /* сохранение общих данных */
        String testProgrammName = testProgrammNameTextField.getText();
        String sampleName = sampleNameTextField.getText();
        String sampleSerialNumber = sampleSerialNumberTextField.getText();
        String documentNumber = documentNumberTextField.getText();
        String testProgrammType = testProgrammTypeTextField.getText();
        String testProgrammTime = testProgrammTimeTextField.getText();
        String testProgrammDate = testProgrammDateTextField.getText();
        String leadEngineer = leadEngineerTextField.getText();
        String comments = commentsTextArea.getText();

        if (editMode) {
            testProgramm.setTestProgrammName(testProgrammName);
            testProgramm.setSampleName(sampleName);
            testProgramm.setSampleSerialNumber(sampleSerialNumber);
            testProgramm.setDocumentNumber(documentNumber);
            testProgramm.setExperimentType(testProgrammType);
            testProgramm.setExperimentTime(testProgrammTime);
            testProgramm.setExperimentDate(testProgrammDate);
            testProgramm.setLeadEngineer(leadEngineer);
            testProgramm.setComments(comments);
            TestProgrammRepository.updateTestProgramm(testProgramm);
        } else {
            testProgramm = new TestProgramm(crate, testProgrammName, sampleName, sampleSerialNumber, documentNumber, testProgrammType, testProgrammTime, testProgrammDate, leadEngineer, comments);
            TestProgrammRepository.insertTestProgramm(testProgramm);
        }

        /* сохранение настроек оборудования */
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = crateModel.getLtr24ModulesList().get(ltr24Index++).getValue();
                    LTR24Module ltr24Module = new LTR24Module(testProgramm.getId(), ltr24.getCheckedChannels(), ltr24.getChannelsTypes(), ltr24.getMeasuringRanges(), ltr24.getChannelsDescription(), ltr24.getCrate(), ltr24.getSlot());
                    LTR24ModuleRepository.insertLTR24Module(ltr24Module);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = crateModel.getLtr34ModulesList().get(ltr212Index++).getValue();
                    LTR34Module ltr34Module = new LTR34Module(testProgramm.getId(), ltr34.getCheckedChannels(), ltr34.getChannelsParameters(), ltr34.getCrate(), ltr34.getSlot());
                    LTR34ModuleRepository.insertLTR34Module(ltr34Module);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = crateModel.getLtr212ModulesList().get(ltr34Index++).getValue();
                    LTR212Module ltr212Module = new LTR212Module(testProgramm.getId(), ltr212.getCheckedChannels(), ltr212.getChannelsTypes(), ltr212.getMeasuringRanges(), ltr212.getChannelsDescription(), ltr212.getCrate(), ltr212.getSlot());
                    LTR212ModuleRepository.insertLTR212Module(ltr212Module);
                    break;
            }
        }

        handleBackButton();
    }

    public void handleBackButton() {
//        stopModules();
        TestProgrammRepository.updateTestProgrammId();
        cm.loadItemsForMainTableView();
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    /*private void stopModules() {
        for (LTR24 ltr24 : crateModel.getLtr24ModulesList()) {
            ltr24.closeConnection();
        }

        for (LTR34 ltr34 : crateModel.getLtr34ModulesList()) {
            ltr34.closeConnection();
        }

        for (LTR212 ltr212 : crateModel.getLtr212ModulesList()) {
            ltr212.closeConnection();
        }
    }*/

    public void refreshModulesList() {
        modulesListView.setItems(modulesNames);
    }

    public void loadTestProgramm(TestProgramm testProgramm) {
        loadGeneralSettings(testProgramm);
        loadHardwareSettings(testProgramm);

        this.testProgramm = testProgramm;
    }

    private void loadGeneralSettings(TestProgramm testProgramm) {
        testProgrammNameTextField.setText(testProgramm.getTestProgrammName());
        sampleNameTextField.setText(testProgramm.getSampleName());
        sampleSerialNumberTextField.setText(testProgramm.getSampleSerialNumber());
        documentNumberTextField.setText(testProgramm.getDocumentNumber());
        testProgrammTypeTextField.setText(testProgramm.getExperimentType());
        testProgrammTimeTextField.setText(testProgramm.getExperimentTime());
        testProgrammDateTextField.setText(testProgramm.getExperimentDate());
        leadEngineerTextField.setText(testProgramm.getLeadEngineer());
        commentsTextArea.setText(testProgramm.getComments());
    }

    private void loadHardwareSettings(TestProgramm testProgramm) {
        selectCrate(testProgramm);
        loadChannelsSettings(testProgramm);
    }

    private void selectCrate(TestProgramm testProgramm) {
        for (int i = 0; i < crateModel.getCratesNames().size(); i++) {
            String crateName = crateModel.getCratesNames().get(i);
            String crateSN = testProgramm.getCrate();
            int notCrate = 0;

            if (crateName.contains(crateSN)) {
                selectedCrate = i;
            } else {
                notCrate++;
            }

            if (notCrate == crateModel.getCratesNames().size()) {
                statusBarLine.setStatus("Ошибка загрузки настроек: крейт с указанным серийным номером не найден.", statusBar);
            }

            cratesListView.getSelectionModel().select(selectedCrate);
        }

    }

    private void loadChannelsSettings(TestProgramm testProgramm) {

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
