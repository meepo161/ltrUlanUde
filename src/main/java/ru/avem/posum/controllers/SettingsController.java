package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.SettingsModel;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingsController implements BaseController {
    @FXML
    private Button backButton;
    @FXML
    private Button chooseCrateButton;
    @FXML
    private TextArea commentsTextArea;
    @FXML
    private TextField documentNumberTextField;
    @FXML
    private ListView<String> cratesListView;
    @FXML
    private TextField leadEngineerTextField;
    @FXML
    private ListView<String> modulesListView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label requiredFieldN1;
    @FXML
    private Label requiredFieldN2;
    @FXML
    private Label requiredFieldN3;
    @FXML
    private Label requiredFieldN4;
    @FXML
    private Label requiredFieldN5;
    @FXML
    private TextField sampleNameTextField;
    @FXML
    private TextField sampleSerialNumberTextField;
    @FXML
    private Button saveTestProgramSettingsButton;
    @FXML
    private Button setupModuleButton;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TextField testProgramNameTextField;
    @FXML
    private TextField testProgramTimeTextField;
    @FXML
    private TextField testProgramDateTextField;
    @FXML
    private TextField testProgramTypeTextField;

    private ControllerManager cm;
    private String crate;
    private CrateModel crateModel = new CrateModel();
    private ObservableList<String> crates;
    private boolean didBackSpacePressed;
    private boolean editMode;
    private String moduleName;
    private ObservableList<String> modulesNames;
    private List<Pair<Label, TextField>> requiredFields = new ArrayList<>();
    private int selectedCrate;
    private int selectedModuleIndex;
    private SettingsModel settingsModel = new SettingsModel();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private TestProgram testProgram;
    private List<TextField> textFields = new ArrayList<>();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        fillListOfTextFields();
        fillListOfRequiredSymbols();
        initRequiredFieldsSymbols();
        initTimeAndDateFields();
        showCrates();
        showModules();
    }

    private void fillListOfTextFields() {
        textFields.addAll(Arrays.asList(
                testProgramNameTextField,
                sampleNameTextField,
                sampleSerialNumberTextField,
                documentNumberTextField,
                testProgramTimeTextField,
                testProgramDateTextField,
                testProgramTypeTextField,
                leadEngineerTextField
        ));
    }

    private void fillListOfRequiredSymbols() {
        requiredFields.addAll(Arrays.asList(
                new Pair<>(requiredFieldN1, testProgramNameTextField),
                new Pair<>(requiredFieldN2, sampleNameTextField),
                new Pair<>(requiredFieldN3, testProgramTypeTextField),
                new Pair<>(requiredFieldN4, testProgramTimeTextField),
                new Pair<>(requiredFieldN5, testProgramDateTextField)
        ));
    }

    private void initRequiredFieldsSymbols() {
        for (Pair<Label, TextField> pair : requiredFields) {
            pair.getKey().setTextFill(Color.web("#D30303"));
            pair.getKey().setVisible(false);
        }
    }

    private void initTimeAndDateFields() {
        setTextFormat(testProgramTimeTextField, 8, ":");
        setTextFormat(testProgramDateTextField, 10, ".");
    }

    private void setTextFormat(TextField textField, int limitOfNumbers, String separator) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = textField.getText();

            textField.setText(text.replaceAll("[^\\d" + separator + "]", ""));
            addColons(textField, text, separator);

            if (text.length() > limitOfNumbers) {
                textField.setText(oldValue);
            }
        });
    }

    private void addColons(TextField textField, String text, String separator) {
        int charactersCounter = text.length();

        if (!didBackSpacePressed) {
            if (charactersCounter == 2 || charactersCounter == 5) {
                textField.setText(text + separator);
            }
        }
    }

    private void showCrates() {
        crates = crateModel.getCratesNames();
        cratesListView.setItems(crates);
    }

    private void showModules() {
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            crate = crateModel.getCrates()[0][selectedCrate];
            modulesListView.setItems(crateModel.getModulesNames(selectedCrate));
            modulesNames = crateModel.getModulesNames(selectedCrate);
            addDoubleClickListener(cratesListView, true);
            addDoubleClickListener(modulesListView, false);
            cm.createListModulesControllers(modulesNames);
        }));
    }

    private void addDoubleClickListener(ListView<String> listView, boolean forCrate) {
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
                    if (forCrate) {
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
        checkSelection();
        initSettingsModel();
    }

    private void initSettingsModel() {
        settingsModel.setControllerManager(cm);
        settingsModel.createModulesInstances(modulesNames);
    }

    private void checkSelection() {
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
                saveSelectedModuleIndex();
                showModuleSettings();
                break;
            }
        }
    }

    private void saveSelectedModuleIndex() {
        selectedModuleIndex = modulesListView.getSelectionModel().getSelectedIndex();
    }

    private void showModuleSettings() {
        parseModuleName();
        loadModuleSettings();
        setScene();
    }

    private void parseModuleName() {
        moduleName = modulesNames.get(selectedModuleIndex);
    }

    private void loadModuleSettings() {
        cm.loadModuleSettings(selectedModuleIndex, moduleName);
    }

    private void setScene() {
        wm.setModuleScene(moduleName, selectedModuleIndex);
    }

    public void handleSaveTestProgramSettings() {
        boolean isRequiredSettingsSet = checkHardwareSettings() && checkRequiredTextFields() && checkTimeAndDateFormat();

        if (isRequiredSettingsSet) {
            new Thread(this::save).start();
        }
    }

    private boolean checkRequiredTextFields() {
        int filledFields = 0;
        boolean isRequiredFieldsFilled = false;

        for (int i = 0; i < requiredFields.size(); i++) {
            TextField textField = requiredFields.get(i).getValue();
            Label label = requiredFields.get(i).getKey();

            if (textField.getText().isEmpty()) {
                label.setVisible(true);
                isRequiredFieldsFilled = false;

                Platform.runLater(() -> {
                    statusBarLine.setStatus("Перед сохранением настроек заполните обязательные поля основной информации", statusBar);
                });
            } else {
                filledFields++;
            }

            if (filledFields == requiredFields.size()) {
                isRequiredFieldsFilled = true;
            }
        }

        return isRequiredFieldsFilled;
    }

    private boolean checkTimeAndDateFormat() {
        String time = testProgramTimeTextField.getText();
        String date = testProgramDateTextField.getText();
        boolean isTextFormatCorrect = true;

        if (!time.matches("^[\\d]{2,3}:[0-5][\\d]:[0-5][\\d]")) {
            statusBarLine.setStatus("Неверно задано время испытаний", statusBar);
            isTextFormatCorrect = false;
        }

        if (!date.matches("(^[0-2][\\d]|^[3][0,1])\\.(0[\\d]|1[0-2])\\.[2][\\d]{3}")) {
            statusBarLine.setStatus("Неверно задана дата испытаний", statusBar);
            isTextFormatCorrect = false;
        }

        return isTextFormatCorrect;
    }

    private boolean checkHardwareSettings() {
        boolean isCrateChosen = false;

        if (!chooseCrateButton.isDisabled()) {
            statusBarLine.setStatus("Ошибка сохранения настроек: необходимо выбрать крейт", statusBar);
        } else {
            isCrateChosen = true;
        }

        return isCrateChosen;
    }

    private void save() {
        toggleUiElements();
        saveSettings();
        Platform.runLater(this::handleBackButton);
    }

    private void toggleUiElements() {
        toggleButtons(true);
        hideRequiredFieldsSymbols();

        Platform.runLater(() -> {
            toggleProgressIndicatorState(false);
        });
    }

    public void toggleButtons(boolean isDisable) {
        saveTestProgramSettingsButton.setDisable(isDisable);
        setupModuleButton.setDisable(true);
        backButton.setDisable(isDisable);
    }

    public void hideRequiredFieldsSymbols() {
        for (Pair<Label, TextField> pair : requiredFields) {
            pair.getKey().setVisible(false);
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
        settingsModel.saveHardwareSettings(editMode);
    }

    private HashMap<String, String> parseGeneralSettingsData() {
        HashMap<String, String> generalSettings = new HashMap<>();

        generalSettings.put("Crate Serial Number", crate);
        generalSettings.put("Test Program Name", testProgramNameTextField.getText());
        generalSettings.put("Sample Name", sampleNameTextField.getText());
        generalSettings.put("Sample Serial Number", sampleSerialNumberTextField.getText());
        generalSettings.put("Document Number", documentNumberTextField.getText());
        generalSettings.put("Test Program Type", testProgramTypeTextField.getText());
        generalSettings.put("Test Program Time", testProgramTimeTextField.getText());
        generalSettings.put("Test Program Date", testProgramDateTextField.getText());
        generalSettings.put("Lead Engineer", leadEngineerTextField.getText());
        generalSettings.put("Comments", commentsTextArea.getText());

        return generalSettings;
    }

    public void handleBackButton() {
        new Thread(() -> {
            TestProgramRepository.updateTestProgramIndexes();
            cm.loadItemsForMainTableView();
        }).start();
        toggleProgressIndicatorState(true);
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void showTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;

        loadGeneralSettings(testProgram);
        selectCrate();
        settingsModel.loadChannelsSettings(testProgram, crateModel, selectedCrate);
    }

    private void loadGeneralSettings(TestProgram testProgram) {
        testProgramNameTextField.setText(testProgram.getTestProgramName());
        sampleNameTextField.setText(testProgram.getSampleName());
        sampleSerialNumberTextField.setText(testProgram.getSampleSerialNumber());
        documentNumberTextField.setText(testProgram.getDocumentNumber());
        testProgramTypeTextField.setText(testProgram.getTestProgramType());
        testProgramTimeTextField.setText(testProgram.getTestProgramTime());
        testProgramDateTextField.setText(testProgram.getTestProgramDate());
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

    public void loadDefaultSettings() {
        testProgramNameTextField.setText("");
        sampleNameTextField.setText("");
        sampleSerialNumberTextField.setText("");
        documentNumberTextField.setText("");
        testProgramTypeTextField.setText("");
        testProgramTimeTextField.setText("");
        testProgramDateTextField.setText("");
        leadEngineerTextField.setText("");
        commentsTextArea.setText("");

        cratesListView.getSelectionModel().clearSelection();
        modulesListView.getSelectionModel().clearSelection();
        modulesListView.getItems().clear();

        toggleUiElements(false, true);
        toggleButtons(false);
    }

    @FXML
    public void listenBackSpaceKey(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        didBackSpacePressed = keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    }

    public void refreshModulesList() {
        modulesListView.setItems(modulesNames);
    }

    public String getCrate() {
        return crate;
    }

    public CrateModel getCrateModel() {
        return crateModel;
    }

    public int getSelectedCrate() {
        return selectedCrate;
    }

    public int getSelectedModuleIndex() {
        return selectedModuleIndex;
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
}
