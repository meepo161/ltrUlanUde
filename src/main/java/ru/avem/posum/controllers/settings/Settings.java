package ru.avem.posum.controllers.settings;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.models.settings.SettingsModel;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings implements BaseController {
    @FXML
    private Button backButton;
    @FXML
    private Button backButtonGeneralTab;
    @FXML
    private Label checkIcon;
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
    private Button saveSettingsButton;
    @FXML
    private Button setupModuleButton;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private TextField testProgramNameTextField;
    @FXML
    private TextField testProgramTimeTextField;
    @FXML
    private TextField testProgramDateTextField;
    @FXML
    private TextField testProgramTypeTextField;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private boolean didBackSpacePressed;
    private boolean editMode;
    private HardwareSettings hardwareSettings;
    private List<Pair<Label, TextField>> requiredFields = new ArrayList<>();
    private SettingsModel settingsModel = new SettingsModel();
    private StatusBarLine statusBarLine;
    private TestProgram testProgram;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, true, progressIndicator, statusBar,
                warningIcon);
        initRequiredFieldsSymbols();
        initTimeAndDateFields();
        hardwareSettings = new HardwareSettings(this);
        initHardwareSettings();
    }

    private void initRequiredFieldsSymbols() {
        requiredFields.addAll(Arrays.asList(
                new Pair<>(requiredFieldN1, testProgramNameTextField),
                new Pair<>(requiredFieldN2, sampleNameTextField),
                new Pair<>(requiredFieldN3, testProgramTypeTextField),
                new Pair<>(requiredFieldN4, testProgramTimeTextField),
                new Pair<>(requiredFieldN5, testProgramDateTextField)
        ));

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

    private void initHardwareSettings() {
        hardwareSettings.showCrates();
        hardwareSettings.showModules();
        addDoubleClickListener(cratesListView, true);
        addDoubleClickListener(modulesListView, false);
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

    @FXML
    public void handleChooseCrate() {
        if (cratesListView.getSelectionModel().getSelectedIndex() == -1) {
            statusBarLine.setStatus("Выделите крейт", false);
        } else {
            statusBarLine.toggleProgressIndicator(false);
            statusBarLine.setStatusOfProgress("Инициализация крейта");
            cratesListView.setDisable(true);
            chooseCrateButton.setDisable(true);
            saveSettingsButton.setDisable(true);
            backButton.setDisable(true);
            backButtonGeneralTab.setDisable(true);

            new Thread(() -> {
                cm.createListModulesControllers(hardwareSettings.getModulesNames());
                hardwareSettings.initialize();

                if (editMode) {
                    settingsModel.loadChannelsSettings(testProgram, hardwareSettings.getCrate(),
                            hardwareSettings.getSelectedCrate());
                }

                statusBarLine.toggleProgressIndicator(true);
                statusBarLine.clear();
            }).start();
        }
    }

    @FXML
    public void handleSetupModule() {
        if (modulesListView.getSelectionModel().getSelectedIndex() == -1) {
            statusBarLine.setStatus("Выберите модуль", false);
        } else {
            statusBarLine.setStatusOfProgress("Загрузка настроек модуля");

            new Thread(() -> {
                for (int i = 0; i < hardwareSettings.getModulesNames().size(); i++) {
                    if (modulesListView.getSelectionModel().isSelected(i)) {
                        hardwareSettings.loadModuleSettings();
                        statusBarLine.toggleProgressIndicator(true);
                        statusBarLine.clear();
                        Platform.runLater(() -> wm.setModuleScene(hardwareSettings.getModuleName(),
                                hardwareSettings.getSelectedModuleIndex()));
                        break;
                    }
                }
            }).start();
        }
    }

    @FXML
    public void handleSaveTestProgramSettings() {
        boolean isRequiredSettingsSet = checkTimeAndDateFormat() & hardwareSettings.checkHardwareSettings()
                & checkRequiredTextFields();

        if (isRequiredSettingsSet) {
            new Thread(this::save).start();
        }
    }

    private boolean checkTimeAndDateFormat() {
        String time = testProgramTimeTextField.getText();
        String date = testProgramDateTextField.getText();
        boolean isTextFormatCorrect = true;

        if (!time.matches("^[\\d]{2,3}:[0-5][\\d]:[0-5][\\d]")) {
            statusBarLine.setStatus("Неверно задано время испытаний", false);
            isTextFormatCorrect = false;
        }

        if (!date.matches("(^[0-2][\\d]|^[3][0,1])\\.(0[\\d]|1[0-2])\\.[2][\\d]{3}")) {
            statusBarLine.setStatus("Неверно задана дата испытаний", false);
            isTextFormatCorrect = false;
        }

        return isTextFormatCorrect;
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
                statusBarLine.setStatus("Перед сохранением настроек заполните обязательные поля основной информации",
                        false);
            } else {
                filledFields++;
            }

            if (filledFields == requiredFields.size()) {
                isRequiredFieldsFilled = true;
            }
        }

        return isRequiredFieldsFilled;
    }

    private void save() {
        statusBarLine.setStatusOfProgress("Сохранение программы испытаний");
        toggleUiElements();
        saveSettings();
        Platform.runLater(this::handleBackButton);
    }

    private void toggleUiElements() {
        setupModuleButton.setDisable(true);
        saveSettingsButton.setDisable(true);
        backButton.setDisable(true);
        backButtonGeneralTab.setDisable(true);
        hideRequiredFieldsSymbols();
    }

    public void hideRequiredFieldsSymbols() {
        for (Pair<Label, TextField> pair : requiredFields) {
            pair.getKey().setVisible(false);
        }
    }

    private void saveSettings() {
        settingsModel.saveGeneralSettings(parseGeneralSettingsData(), editMode);
        settingsModel.saveHardwareSettings(editMode);
    }

    private HashMap<String, String> parseGeneralSettingsData() {
        HashMap<String, String> generalSettings = new HashMap<>();

        generalSettings.put("Crate Serial Number", hardwareSettings.getCrateSerialNumber());
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

    @FXML
    public void handleBackButton() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Загрузка списка программ испытаний");

        new Thread(() -> {
            TestProgramRepository.updateTestProgramIndexes();
            cm.loadItemsForMainTableView();
            cm.stopAllModules();
            hardwareSettings.clear();
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clear();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.MAIN_SCENE));
        }).start();
    }

    public void showTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;

        loadGeneralSettings(testProgram);
        initHardwareSettings();
        hardwareSettings.selectCrate();
    }

    private void loadGeneralSettings(TestProgram testProgram) {
        testProgramNameTextField.setText(testProgram.getName());
        sampleNameTextField.setText(testProgram.getSampleName());
        sampleSerialNumberTextField.setText(testProgram.getSampleSerialNumber());
        documentNumberTextField.setText(testProgram.getDocumentNumber());
        testProgramTypeTextField.setText(testProgram.getTestProgramType());
        testProgramTimeTextField.setText(testProgram.getTestProgramTime());
        testProgramDateTextField.setText(testProgram.getTestProgramDate());
        leadEngineerTextField.setText(testProgram.getLeadEngineer());
        commentsTextArea.setText(testProgram.getComments());
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

        hardwareSettings.clear();
        hardwareSettings.showCrates();
        hardwareSettings.toggleUiElements(false);
    }

    @FXML
    public void listenBackSpaceKey(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        didBackSpacePressed = keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    }

    public void refreshModulesList() {
        modulesListView.setItems(hardwareSettings.getModulesNames());
    }

    @FXML
    public void selectGeneralSettingsTab() {
        mainTabPane.getSelectionModel().select(0);
        Platform.runLater(() -> mainTabPane.requestFocus());
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getBackButtonGeneralTab() {
        return backButtonGeneralTab;
    }

    public Label getCheckIcon() {
        return checkIcon;
    }

    public Button getChooseCrateButton() {
        return chooseCrateButton;
    }

    public ControllerManager getCm() {
        return cm;
    }

    public ListView<String> getCratesListView() {
        return cratesListView;
    }

    public HardwareSettings getHardwareSettings() {
        return hardwareSettings;
    }

    public ListView<String> getModulesListView() {
        return modulesListView;
    }

    public Button getSaveSettingsButton() {
        return saveSettingsButton;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    public Button getSetupModuleButton() {
        return setupModuleButton;
    }

    public StatusBarLine getStatusBarLine() {
        return statusBarLine;
    }

    public TestProgram getTestProgram() {
        return testProgram;
    }

    public Label getWarningIcon() {
        return warningIcon;
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
