package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.LTR24ModuleRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR24Module;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.SettingsModel;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ProgressIndicator progressIndicator;
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

    private int slot;
    private String crate;
    private boolean editMode;
    private int selectedCrate;
    private WindowsManager wm;
    private int selectedModule;
    private ControllerManager cm;
    private TestProgram testProgram;
    private ObservableList<String> crates;
    private ObservableList<String> modulesNames;
    private CrateModel crateModel = new CrateModel();
    private SettingsModel settingsModel = new SettingsModel();
    private StatusBarLine statusBarLine = new StatusBarLine();

    @FXML
    private void initialize() {
        crates = crateModel.getCratesNames();
        cratesListView.setItems(crates);
        showCrateModules();
    }

    private void showCrateModules() {
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            modulesListView.setItems(crateModel.fillModulesNames(selectedCrate));
            addDoubleClickListener(cratesListView, true);
            addDoubleClickListener(modulesListView, false);
            modulesNames = crateModel.getModulesNames();
            cm.createListModulesControllers(modulesNames);
        }));
    }

    private void addDoubleClickListener(ListView<String> listView, boolean isCrate) {
        listView.setCellFactory(tv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!cell.isEmpty())) {
                    if (isCrate) {
                        handleChooseCrate();
                    } else {
                        handleSetupModule();
                    }
                }
            });
            return cell;
        });
    }

    public void handleChooseCrate() {
        settingsModel.createModulesInstances(crateModel);
        crate = settingsModel.getCrate();

        for (int i = 0; i < crates.size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                toggleUiElements(true, false);
            }
        }
    }

    private void toggleUiElements(boolean crate, boolean module) {
        cratesListView.setDisable(crate);
        chooseCrateButton.setDisable(crate);
        modulesListView.setDisable(module);
        setupModuleButton.setDisable(module);
    }

    public void handleSetupModule() {
        for (int i = 0; i < modulesNames.size(); i++) {
            if (modulesListView.getSelectionModel().isSelected(i)) {
                selectedModule = modulesListView.getSelectionModel().getSelectedIndex();
                String module = modulesNames.get(selectedModule);

                slot = settingsModel.parseSlotNumber(module);

                showModuleSettings(module);
                break;
            }
        }
    }

    private void showModuleSettings(String module) {
        String moduleName = (module + " ").substring(0, 6).trim();
        loadModuleSettingsView(moduleName);

        wm.setModuleScene(moduleName, selectedModule);
    }

    private void loadModuleSettingsView(String moduleName) {
        switch (moduleName) {
            case CrateModel.LTR24:
                cm.loadLTR24Settings(selectedModule);
                break;
            case CrateModel.LTR34:
                cm.loadLTR34Settings(selectedModule);
                break;
            case CrateModel.LTR212:
                cm.loadLTR212Settings(selectedModule);
                break;
        }
    }

    public void handleSaveTestProgrammSettings() {
        if (!chooseCrateButton.isDisabled()) {
            statusBarLine.setStatus("Ошибка сохранения настроек: необходимо выбрать крейт.", statusBar);
        } else {
            toggleProgressIndicatorState(false);
            new Thread(this::saveSettings).start();
        }
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private void saveSettings() {
        settingsModel.saveGeneralSettings(parseGeneralSettingsData(), editMode);
        settingsModel.saveHardwareSettings();

        Platform.runLater(this::handleBackButton);
    }

    private HashMap<String, String> parseGeneralSettingsData() {
        HashMap<String, String> generalSettings = new HashMap<>();

        generalSettings.put("Test Program Name", testProgrammNameTextField.getText());
        generalSettings.put("Sample Name", sampleNameTextField.getText());
        generalSettings.put("Sample Serial Number", sampleSerialNumberTextField.getText());
        generalSettings.put("Document Number", documentNumberTextField.getText());
        generalSettings.put("Test Program Type", testProgrammTypeTextField.getText());
        generalSettings.put("Test Program Time", testProgrammTimeTextField.getText());
        generalSettings.put("Test Program Date", testProgrammDateTextField.getText());
        generalSettings.put("Lead Engineer", leadEngineerTextField.getText());
        generalSettings.put("Comments", commentsTextArea.getText());
        generalSettings.put("Crate Serial Number", crate);

        return generalSettings;
    }

    public void handleBackButton() {
        new Thread(() -> {
            TestProgramRepository.updateTestProgramId();
            cm.loadItemsForMainTableView();

        }).start();
        toggleProgressIndicatorState(true);
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void showTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;

        loadGeneralSettings(testProgram);
        selectCrate();
        settingsModel.loadChannelsSettings(testProgram, crateModel, modulesNames, selectedCrate);
    }

    private void loadGeneralSettings(TestProgram testProgram) {
        testProgrammNameTextField.setText(testProgram.getTestProgramName());
        sampleNameTextField.setText(testProgram.getSampleName());
        sampleSerialNumberTextField.setText(testProgram.getSampleSerialNumber());
        documentNumberTextField.setText(testProgram.getDocumentNumber());
        testProgrammTypeTextField.setText(testProgram.getTestProgramType());
        testProgrammTimeTextField.setText(testProgram.getTestProgramTime());
        testProgrammDateTextField.setText(testProgram.getTestProgramDate());
        leadEngineerTextField.setText(testProgram.getLeadEngineer());
        commentsTextArea.setText(testProgram.getComments());
    }

    private void selectCrate() {
        for (int i = 0; i < crateModel.getCratesNames().size(); i++) {
            String crateName = crateModel.getCratesNames().get(i);
            crate = testProgram.getCrate(); // серийный номер крейта
            int notCrate = 0;

            if (crateName.contains(crate)) {
                selectedCrate = i;
                cratesListView.setDisable(true);
                modulesListView.setDisable(false);
                chooseCrateButton.setDisable(true);
                setupModuleButton.setDisable(false);
            } else {
                notCrate++;
            }

            if (notCrate == crateModel.getCratesNames().size()) {
                statusBarLine.setStatus("Ошибка загрузки настроек: крейт с указанным серийным номером не найден.", statusBar);
            }

            cratesListView.getSelectionModel().select(selectedCrate);
            modulesListView.getSelectionModel().clearSelection();
        }
    }


    private List<LTR24Module> fillLTR24ModulesList(TestProgram testProgram) {
        List<LTR24Module> ltr24Modules = new ArrayList<>();

        for (LTR24Module module : LTR24ModuleRepository.getAllLTR24Modules()) {
            if (module.getTestProgrammId() == testProgram.getTestProgramId()) {
                ltr24Modules.add(module);
            }
        }
        return ltr24Modules;
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

        toggleUiElements(false, true);
    }

    public void refreshModulesList() {
        modulesListView.setItems(modulesNames);
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
