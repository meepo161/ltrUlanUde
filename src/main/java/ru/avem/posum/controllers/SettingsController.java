package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

import java.util.ArrayList;
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

    private WindowsManager wm;
    private ControllerManager cm;
    private int slot;
    private String crate;
    private boolean editMode;
    private int selectedCrate;
    private int selectedModule;
    private TestProgramm testProgramm;
    private ObservableList<String> crates;
    private ObservableList<String> modulesNames;
    private CrateModel crateModel = new CrateModel();
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
        createModulesInstances();

        for (int i = 0; i < crates.size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                toggleUiElements(true, false);
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

                slot = parseSlotNumber(module);

                showModuleSettings(module);
                break;
            }
        }
    }

    private void showModuleSettings(String module) {
        String moduleName = (module + " ").substring(0, 6).trim();
        loadModuleSettings(moduleName);

        wm.setModuleScene(moduleName, selectedModule);
    }

    private void loadModuleSettings(String moduleName) {
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
        saveGeneralSettings();
        saveHardwareSettings();

        Platform.runLater(() -> {
            handleBackButton();
        });
    }

    private void saveGeneralSettings() {
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
            testProgramm.setTestProgrammType(testProgrammType);
            testProgramm.setTestProgrammTime(testProgrammTime);
            testProgramm.setTestProgrammDate(testProgrammDate);
            testProgramm.setLeadEngineer(leadEngineer);
            testProgramm.setComments(comments);
            TestProgrammRepository.updateTestProgramm(testProgramm);
        } else {
            testProgramm = new TestProgramm(crate, testProgrammName, sampleName, sampleSerialNumber, documentNumber, testProgrammType, testProgrammTime, testProgrammDate, leadEngineer, comments);
            TestProgrammRepository.insertTestProgramm(testProgramm);
        }
    }

    private void saveHardwareSettings() {
        /* сохранение настроек оборудования */
        int testProgrammId = testProgramm.getTestProgrammId();
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (String modulesName : modulesNames) {
            switch (modulesName.split(" ")[0]) {
                case CrateModel.LTR24:
                    updateLTR24Settings(testProgrammId, ltr24Index++);
                    break;
                case CrateModel.LTR34:
                    updateLTR34Settings(testProgrammId, ltr34Index++);
                    break;
                case CrateModel.LTR212:
                    updateLTR212Settings(testProgrammId, ltr212Index++);
                    break;
            }
        }
    }

    private void updateLTR24Settings(int testProgrammId, int ltr24Index) {
        LTR24 ltr24 = crateModel.getLtr24ModulesList().get(ltr24Index).getValue();
        boolean[] checkedChannels = ltr24.getCheckedChannels();

        if (editMode) {
            for (LTR24Module ltr24Module : LTR24ModuleRepository.getAllLTR24Modules()) {
                if (ltr24Module.getTestProgrammId() == testProgrammId && ltr24Module.getSlot() == ltr24.getSlot()) {
                    String channels = "";
                    String types = "";
                    String ranges = "";
                    String descriptions = "";

                    for (int i = 0; i < checkedChannels.length; i++) {
                        if (checkedChannels[i]) {
                            channels += 1 + ", ";
                            types += ltr24.getChannelsTypes()[i] + ", ";
                            ranges += ltr24.getMeasuringRanges()[i] + ", ";
                            descriptions += ltr24.getChannelsDescription()[i] + ", ";
                        } else {
                            channels += 0 + ", ";
                            types += 0 + ", ";
                            ranges += 0 + ", ";
                            descriptions += ", ";
                        }
                    }
                    ltr24Module.setCheckedChannels(channels);
                    ltr24Module.setChannelsTypes(types);
                    ltr24Module.setMeasuringRanges(ranges);
                    ltr24Module.setChannelsDescription(descriptions);
                    ltr24Module.setCrate(ltr24.getCrate());
                    ltr24Module.setSlot(ltr24.getSlot());

                    LTR24ModuleRepository.updateLTR24Module(ltr24Module);
                }
            }
        } else {
            LTR24Module ltr24Module = new LTR24Module(testProgramm.getId(), ltr24.getCheckedChannels(), ltr24.getChannelsTypes(), ltr24.getMeasuringRanges(), ltr24.getChannelsDescription(), ltr24.getCrate(), ltr24.getSlot());
            LTR24ModuleRepository.insertLTR24Module(ltr24Module);
        }
    }

    private void updateLTR212Settings(int testProgrammId, int ltr212Index) {
        LTR212 ltr212 = crateModel.getLtr212ModulesList().get(ltr212Index).getValue();
        boolean[] checkedChannels = ltr212.getCheckedChannels();

        if (editMode) {
            for (LTR212Module ltr212Module : LTR212ModuleRepository.getAllLTR212Modules()) {
                if (ltr212Module.getTestProgrammId() == testProgrammId && ltr212Module.getSlot() == ltr212.getSlot()) {
                    String channels = "";
                    String types = "";
                    String ranges = "";
                    String descriptions = "";

                    for (int i = 0; i < checkedChannels.length; i++) {
                        if (checkedChannels[i]) {
                            channels += 1 + ", ";
                            types += ltr212.getChannelsTypes()[i] + ", ";
                            ranges += ltr212.getMeasuringRanges()[i] + ", ";
                            descriptions += ltr212.getChannelsDescription()[i] + ", ";
                        } else {
                            channels += 0 + ", ";
                            types += 0 + ", ";
                            ranges += 0 + ", ";
                            descriptions += ", ";
                        }
                    }
                    ltr212Module.setCheckedChannels(channels);
                    ltr212Module.setChannelsTypes(types);
                    ltr212Module.setMeasuringRanges(ranges);
                    ltr212Module.setChannelsDescription(descriptions);
                    ltr212Module.setCrate(ltr212.getCrate());
                    ltr212Module.setSlot(ltr212.getSlot());

                    LTR212ModuleRepository.updateLTR212Module(ltr212Module);
                }
            }
        } else {
            LTR212Module ltr212Module = new LTR212Module(testProgramm.getId(), ltr212.getCheckedChannels(), ltr212.getChannelsTypes(), ltr212.getMeasuringRanges(), ltr212.getChannelsDescription(), ltr212.getCrate(), ltr212.getSlot());
            LTR212ModuleRepository.insertLTR212Module(ltr212Module);
        }
    }

    private void updateLTR34Settings(int testProgrammId, int ltr34Index) {
        LTR34 ltr34 = crateModel.getLtr34ModulesList().get(ltr34Index).getValue();
        boolean[] checkedChannels = ltr34.getCheckedChannels();

        if (editMode) {
            for (LTR34Module ltr34Module : LTR34ModuleRepository.getAllLTR34Modules()) {
                if (ltr34Module.getTestProgrammId() == testProgrammId && ltr34Module.getSlot() == ltr34.getSlot()) {
                    String channels = "";
                    String frequencies = "";
                    String amplitudes = "";

                    for (int i = 0; i < checkedChannels.length; i++) {
                        if (checkedChannels[i]) {
                            channels += 1 + ", ";
                            frequencies += ltr34.getChannelsParameters()[0][i] + ", ";
                            amplitudes += ltr34.getChannelsParameters()[1][i] + ", ";
                        } else {
                            channels += 0 + ", ";
                            frequencies += 0 + ", ";
                            amplitudes += 0 + ", ";
                        }
                    }
                    ltr34Module.setCheckedChannels(channels);
                    ltr34Module.setChannelsFrequency(frequencies);
                    ltr34Module.setChannelsAmplitude(amplitudes);
                    ltr34Module.setCrate(ltr34.getCrate());
                    ltr34Module.setSlot(ltr34.getSlot());

                    LTR34ModuleRepository.updateLTR34Module(ltr34Module);
                }
            }
        } else {
            LTR34Module ltr34Module = new LTR34Module(testProgramm.getId(), ltr34.getCheckedChannels(), ltr34.getChannelsParameters(), ltr34.getCrate(), ltr34.getSlot());
            LTR34ModuleRepository.insertLTR34Module(ltr34Module);
        }
    }

    public void handleBackButton() {
        new Thread(() -> {
            TestProgrammRepository.updateTestProgrammId();
            cm.loadItemsForMainTableView();

        }).start();
        toggleProgressIndicatorState(true);
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void showTestProgramm(TestProgramm testProgramm) {
        loadGeneralSettings(testProgramm);
        loadHardwareSettings(testProgramm);

        this.testProgramm = testProgramm;
    }

    private void loadGeneralSettings(TestProgramm testProgramm) {
        testProgrammNameTextField.setText(testProgramm.getTestProgrammName());
        sampleNameTextField.setText(testProgramm.getSampleName());
        sampleSerialNumberTextField.setText(testProgramm.getSampleSerialNumber());
        documentNumberTextField.setText(testProgramm.getDocumentNumber());
        testProgrammTypeTextField.setText(testProgramm.getTestProgrammType());
        testProgrammTimeTextField.setText(testProgramm.getTestProgrammTime());
        testProgrammDateTextField.setText(testProgramm.getTestProgrammDate());
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
            crate = testProgramm.getCrate(); // серийный номер крейта
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

    private void loadChannelsSettings(TestProgramm testProgramm) {
        crateModel.fillModulesNames(selectedCrate);
        modulesNames = crateModel.getModulesNames();
        List<LTR24Module> ltr24Modules = fillLTR24ModulesList(testProgramm);
        List<LTR34Module> ltr34Modules = fillLTR34ModulesList(testProgramm);
        List<LTR212Module> ltr212Modules = fillLTR212ModulesList(testProgramm);

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    setLTR24ChannelsSettings(ltr24, ltr24Modules, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR24> ltr24Pair = new Pair<>(i, ltr24);
                    crateModel.getLtr24ModulesList().add(ltr24Pair);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = new LTR34();
                    setLTR34ChannelsSettings(ltr34, ltr34Modules, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
                    crateModel.getLtr34ModulesList().add(ltr34Pair);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    setLTR212ChannelsSettings(ltr212, ltr212Modules, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
                    crateModel.getLtr212ModulesList().add(ltr212Pair);
                    break;
            }
        }
    }

    private List<LTR24Module> fillLTR24ModulesList(TestProgramm testProgramm) {
        List<LTR24Module> ltr24Modules = new ArrayList<>();

        for (LTR24Module module : LTR24ModuleRepository.getAllLTR24Modules()) {
            if (module.getTestProgrammId() == testProgramm.getTestProgrammId()) {
                ltr24Modules.add(module);
            }
        }
        return ltr24Modules;
    }

    private List<LTR34Module> fillLTR34ModulesList(TestProgramm testProgramm) {
        List<LTR34Module> ltr34Modules = new ArrayList<>();

        for (LTR34Module module : LTR34ModuleRepository.getAllLTR34Modules()) {
            if (module.getTestProgrammId() == testProgramm.getTestProgrammId()) {
                ltr34Modules.add(module);
            }
        }
        return ltr34Modules;
    }

    private List<LTR212Module> fillLTR212ModulesList(TestProgramm testProgramm) {
        List<LTR212Module> ltr212Modules = new ArrayList<>();

        for (LTR212Module module : LTR212ModuleRepository.getAllLTR212Modules()) {
            if (module.getTestProgrammId() == testProgramm.getTestProgrammId()) {
                ltr212Modules.add(module);
            }
        }
        return ltr212Modules;
    }

    private void setLTR24ChannelsSettings(LTR24 ltr24, List<LTR24Module> ltr24Modules, int slot) {
        boolean[] checkedChannels = ltr24.getCheckedChannels();
        int[] channelsTypes = ltr24.getChannelsTypes();
        int[] measuringRanges = ltr24.getMeasuringRanges();
        String[] channelsDescription = ltr24.getChannelsDescription();

        for (LTR24Module ltr24Module : ltr24Modules) {
            if (slot == ltr24Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr24Module.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr24Module.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr24Module.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr24Module.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                ltr24.setCrate(ltr24Module.getCrate());
                ltr24.setSlot(ltr24Module.getSlot());
            }
        }
    }

    private void setLTR34ChannelsSettings(LTR34 ltr34, List<LTR34Module> ltr34Modules, int slot) {
        boolean[] checkedChannels = ltr34.getCheckedChannels();
        int[][] channelsParameters = ltr34.getChannelsParameters();

        for (LTR34Module ltr34Module : ltr34Modules) {
            if (slot == ltr34Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr34Module.getCheckedChannels().split(", ", 9)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsParameters[0][i] = Integer.parseInt(ltr34Module.getChannelsFrequency().split(", ", 9)[i]);
                        channelsParameters[1][i] = Integer.parseInt(ltr34Module.getChannelsAmplitude().split(", ", 9)[i]);
                    }
                }

                ltr34.setCrate(ltr34Module.getCrate());
                ltr34.setSlot(ltr34Module.getSlot());
            }
        }
    }

    private void setLTR212ChannelsSettings(LTR212 ltr212, List<LTR212Module> ltr212Modules, int slot) {
        boolean[] checkedChannels = ltr212.getCheckedChannels();
        int[] channelsTypes = ltr212.getChannelsTypes();
        int[] measuringRanges = ltr212.getMeasuringRanges();
        String[] channelsDescription = ltr212.getChannelsDescription();

        for (LTR212Module ltr212Module : ltr212Modules) {
            if (slot == ltr212Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr212Module.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr212Module.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr212Module.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr212Module.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                ltr212.setCrate(ltr212Module.getCrate());
                ltr212.setSlot(ltr212Module.getSlot());
            }
        }
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
