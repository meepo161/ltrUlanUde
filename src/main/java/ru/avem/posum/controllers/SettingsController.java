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
    private Button chooseCrateButton;
    @FXML
    private Button setupModuleButton;
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
    private TextField testProgramNameTextField;
    @FXML
    private TextField sampleNameTextField;
    @FXML
    private TextField sampleSerialNumberTextField;
    @FXML
    private TextField documentNumberTextField;
    @FXML
    private TextField testProgramTimeTextField;
    @FXML
    private TextField testProgramDateTextField;
    @FXML
    private TextField testProgramTypeTextField;
    @FXML
    private TextField leadEngineerTextField;

    private int slot;
    private String crate;
    private boolean editMode;
    private int selectedCrate;
    private WindowsManager wm;
    private boolean didBackSpacePressed;
    private int selectedModule;
    private ControllerManager cm;
    private TestProgram testProgram;
    private ObservableList<String> crates;
    private ObservableList<String> modulesNames;
    private CrateModel crateModel = new CrateModel();
    private List<Pair<Label, TextField>> requiredFields = new ArrayList<>();
    private List<TextField> textFields = new ArrayList<>();
    private SettingsModel settingsModel = new SettingsModel();
    private StatusBarLine statusBarLine = new StatusBarLine();

    @FXML
    private void initialize() {
        fillListOfTextFields();
        fillListOfRequiredSymbols();
        initRequiredFieldsSymbols();
        setTextFormat(testProgramTimeTextField, 8, ":");
        setTextFormat(testProgramDateTextField, 10, ".");
        crates = crateModel.getCratesNames();
        cratesListView.setItems(crates);
        showCrateModules();
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

    @FXML
    public void listenBackSpaceKey(KeyEvent keyEvent) {
        didBackSpacePressed = keyEvent.getCode() == KeyCode.BACK_SPACE;
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

    public void handleSaveTestProgramSettings() {
        boolean isRequiredSettingsSet = checkHardwareSettings() && checkRequiredTextFields() && checkTimeAndDateFormat();

        if (isRequiredSettingsSet) {
            new Thread(this::saveSettings).start();
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
                    statusBarLine.setStatus("Перед сохранением настроек заполните обязательные поля", statusBar);
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

        if (!time.matches("^[\\d]{2}:[\\d]{2}:[\\d]{2}")) {
            statusBarLine.setStatus("Неверно задано время испытаний (необходимый формат - чч:мм:сс)", statusBar);
            isTextFormatCorrect = false;
        }

        if (!date.matches("^[\\d]{2}\\.[\\d]{2}\\.[\\d]{4}")) {
            statusBarLine.setStatus("Неверно задана дата испытаний (необходимый формат - дд.мм.гггг)", statusBar);
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

    private void saveSettings() {
        Platform.runLater(() -> {
            toggleProgressIndicatorState(false);
        });

        for (Pair<Label, TextField> pair : requiredFields) {
            pair.getKey().setVisible(false);
        }

        settingsModel.saveGeneralSettings(parseGeneralSettingsData(), editMode);
        settingsModel.saveHardwareSettings(editMode);

        Platform.runLater(this::handleBackButton);
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private HashMap<String, String> parseGeneralSettingsData() {
        HashMap<String, String> generalSettings = new HashMap<>();

        generalSettings.put("Test Program Name", testProgramNameTextField.getText());
        generalSettings.put("Sample Name", sampleNameTextField.getText());
        generalSettings.put("Sample Serial Number", sampleSerialNumberTextField.getText());
        generalSettings.put("Document Number", documentNumberTextField.getText());
        generalSettings.put("Test Program Type", testProgramTypeTextField.getText());
        generalSettings.put("Test Program Time", testProgramTimeTextField.getText());
        generalSettings.put("Test Program Date", testProgramDateTextField.getText());
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
    }

    public void hideRequiredFieldsSymbols() {
        for (Pair<Label, TextField> pair : requiredFields) {
            pair.getKey().setVisible(false);
        }
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
