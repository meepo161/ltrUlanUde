package ru.avem.posum.controllers.settings.LTR27;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.controllers.calibration.LTR27CalibrationManager;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.settings.LTR27SettingsModel;
import ru.avem.posum.models.signal.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import javax.rmi.CORBA.Util;

public class LTR27SettingsController implements BaseController {
    @FXML
    private Label averageLabel;
    @FXML
    private TextField averageTextField;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox calibrationCheckBox;
    @FXML
    private Button calibrateSubmoduleOneButton;
    @FXML
    private Button calibrateSubmoduleTwoButton;
    @FXML
    private Button calibrateSubmoduleThreeButton;
    @FXML
    private Button calibrateSubmoduleFourButton;
    @FXML
    private Button calibrateSubmoduleFiveButton;
    @FXML
    private Button calibrateSubmoduleSixButton;
    @FXML
    private Button calibrateSubmoduleSevenButton;
    @FXML
    private Button calibrateSubmoduleEightButton;
    @FXML
    private Label checkIcon;
    @FXML
    private Button enableAllButton;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private Button initializeButton;
    @FXML
    private Label frequencyLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox<String> rarefactionComboBox;
    @FXML
    private Label rarefactionLabel;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private CheckBox submoduleOneCheckBox;
    @FXML
    private Label subModuleOneChannelOneLabel;
    @FXML
    private Label subModuleOneChannelTwoLabel;
    @FXML
    private TextField subModuleOneChannelOneTextField;
    @FXML
    private TextField subModuleOneChannelTwoTextField;
    @FXML
    private CheckBox submoduleTwoCheckBox;
    @FXML
    private Label subModuleTwoChannelOneLabel;
    @FXML
    private Label subModuleTwoChannelTwoLabel;
    @FXML
    private TextField subModuleTwoChannelOneTextField;
    @FXML
    private TextField subModuleTwoChannelTwoTextField;
    @FXML
    private CheckBox submoduleThreeCheckBox;
    @FXML
    private Label subModuleThreeChannelOneLabel;
    @FXML
    private Label subModuleThreeChannelTwoLabel;
    @FXML
    private TextField subModuleThreeChannelOneTextField;
    @FXML
    private TextField subModuleThreeChannelTwoTextField;
    @FXML
    private CheckBox submoduleFourCheckBox;
    @FXML
    private Label subModuleFourChannelOneLabel;
    @FXML
    private Label subModuleFourChannelTwoLabel;
    @FXML
    private TextField subModuleFourChannelOneTextField;
    @FXML
    private TextField subModuleFourChannelTwoTextField;
    @FXML
    private CheckBox submoduleFiveCheckBox;
    @FXML
    private Label subModuleFiveChannelOneLabel;
    @FXML
    private Label subModuleFiveChannelTwoLabel;
    @FXML
    private TextField subModuleFiveChannelOneTextField;
    @FXML
    private TextField subModuleFiveChannelTwoTextField;
    @FXML
    private CheckBox submoduleSixCheckBox;
    @FXML
    private Label subModuleSixChannelOneLabel;
    @FXML
    private Label subModuleSixChannelTwoLabel;
    @FXML
    private TextField subModuleSixChannelOneTextField;
    @FXML
    private TextField subModuleSixChannelTwoTextField;
    @FXML
    private CheckBox submoduleSevenCheckBox;
    @FXML
    private Label subModuleSevenChannelOneLabel;
    @FXML
    private Label subModuleSevenChannelTwoLabel;
    @FXML
    private TextField subModuleSevenChannelOneTextField;
    @FXML
    private TextField subModuleSevenChannelTwoTextField;
    @FXML
    private CheckBox submoduleEightCheckBox;
    @FXML
    private Label subModuleEightChannelOneLabel;
    @FXML
    private Label subModuleEightChannelTwoLabel;
    @FXML
    private TextField subModuleEightChannelOneTextField;
    @FXML
    private TextField subModuleEightChannelTwoTextField;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private LTR27CalibrationManager lcm;
    private LTR27SettingsModel ltr27SettingsModel;
    private LTR27SubmodulesSettings ltr27SubmodulesSettings;
    private SignalModel signalModel = new SignalModel();
    private boolean stoped;
    private String[][] submodulesDescription;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar,
                warningIcon);
        ltr27SettingsModel = new LTR27SettingsModel(this);
        ltr27SubmodulesSettings = new LTR27SubmodulesSettings(this);
        ltr27SubmodulesSettings.initializeView();
        listen(calibrationCheckBox);
    }

    private void listen(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(observable -> {
            setUnits(!checkBox.isSelected());
        });
    }

    private void setUnits(boolean isDefaultUnits) {
        if (isDefaultUnits) {
            ltr27SubmodulesSettings.setSubmodulesUnits();
        } else {
            ltr27SubmodulesSettings.setCalibratedUnits();
        }
    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr27SettingsModel.setModuleName(moduleName);
        ltr27SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr27SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr27SubmodulesSettings.setSubmodulesNames();
        ltr27SubmodulesSettings.setSubmodulesUnits();
        lcm.loadCalibrationSettings(ltr27SettingsModel.getModuleInstance());
        setSettings();
        loadInitialStateOfUi();
    }

    public void handleInitialize() {
        statusBarLine.setStatusOfProgress("Инициализация модуля");

        new Thread(() -> {
            initializeButton.setDisable(true);
            backButton.setDisable(true);
            boolean isSuccessful = ltr27SettingsModel.initModule(frequencyComboBox.getSelectionModel().getSelectedIndex());

            if (isSuccessful) {
                stoped = false;
                ltr27SettingsModel.receiveData();
                ltr27SubmodulesSettings.showValues();
                ltr27SubmodulesSettings.toggleCheckBoxesState(false);
                ltr27SubmodulesSettings.loadUiElementsState();
                toggleUiElements(true);
                backButton.setDisable(false);
            } else {
                loadInitialStateOfUi();
            }

            statusBarLine.setStatus(ltr27SettingsModel.getModuleInstance().getStatus(), isSuccessful);
        }).start();

    }

    private void toggleUiElements(boolean isInit) {
        frequencyComboBox.setDisable(isInit);
        frequencyLabel.setDisable(isInit);
        enableAllButton.setDisable(!isInit);
        initializeButton.setDisable(isInit);
    }

    public void handleCalibrateSubmoduleOne() {
        int submoduleIndex = 0;
        String title = createTitle(submoduleIndex);
        showCalibrationView(title, submoduleIndex);
    }

    private String createTitle(int submoduleIndex) {
        return String.format("Градуировка субмодуля %s (слот %d)",
                ltr27SubmodulesSettings.getSubmodulesNames()[submoduleIndex], submoduleIndex + 1);
    }

    public void handleCalibrateSubmoduleTwo() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 1;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleThree() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 2;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleFour() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 3;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleFive() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 4;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleSix() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 5;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleSeven() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 6;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    public void handleCalibrateSubmoduleEight() {
        showStatusOfCalibrationLoading();

        new Thread(() -> {
            int submoduleIndex = 7;
            String title = createTitle(submoduleIndex);
            showCalibrationView(title, submoduleIndex);
        }).start();
    }

    private void showStatusOfCalibrationLoading() {
        statusBarLine.setStatusOfProgress("Загрузка грудуировок");
    }

    private void showCalibrationView(String title, int submoduleIndex) {
        calibrationCheckBox.setSelected(false);
        lcm.initCalibrationView(title, submoduleIndex);
        statusBarLine.clear();
        statusBarLine.toggleProgressIndicator(true);
        Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.LTR27_CALIBRATION_SCENE));
    }

    public void handleEnableAll() {
        ltr27SubmodulesSettings.enableAll();
    }

    public void handleBack() {
        statusBarLine.setStatusOfProgress("Загрузка списка модулей");

        new Thread(() -> {
            stoped = true;
            saveSettings();
            disableUiElements();
            ltr27SettingsModel.stop();
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clear();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE));
        }).start();
    }

    private void disableUiElements() {
        ltr27SubmodulesSettings.toggleCheckBoxesState(true);
        ltr27SubmodulesSettings.disableSubmodulesUiElements();
        toggleUi(true);
    }

    private void toggleUi(boolean isDisable) {
        frequencyComboBox.setDisable(isDisable);
        rarefactionLabel.setDisable(isDisable);
        rarefactionComboBox.setDisable(isDisable);
        averageLabel.setDisable(isDisable);
        averageTextField.setDisable(isDisable);
        calibrationCheckBox.setDisable(isDisable);
        enableAllButton.setDisable(isDisable);
        initializeButton.setDisable(isDisable);
        backButton.setDisable(isDisable);
    }

    private void saveSettings() {
        ltr27SettingsModel.getModuleInstance().setCheckedChannels(ltr27SubmodulesSettings.getCheckedSubmodules());
        ltr27SettingsModel.getModuleInstance().getSettingsOfModule().put(ADC.Settings.FREQUENCY, frequencyComboBox.getSelectionModel().getSelectedIndex());
        lcm.saveCalibrationSettings(ltr27SettingsModel.getModuleInstance());
    }

    private void setSettings() {
        int frequencyIndex = ltr27SettingsModel.getModuleInstance().getSettingsOfModule().get(ADC.Settings.FREQUENCY);
        frequencyComboBox.getSelectionModel().select(frequencyIndex);
        boolean[] checkedSubmodules = ltr27SettingsModel.getModuleInstance().getCheckedChannels();
        ltr27SubmodulesSettings.setCheckedSubmodules(checkedSubmodules);
    }

    private void loadInitialStateOfUi() {
        ltr27SubmodulesSettings.disableSubmodulesUiElements();
        toggleUi(true);
        frequencyLabel.setDisable(false);
        frequencyComboBox.setDisable(false);
        backButton.setDisable(false);
        initializeButton.setDisable(false);
    }

    public Label getAverageLabel() {
        return averageLabel;
    }

    public TextField getAverageTextField() {
        return averageTextField;
    }

    public CheckBox getCalibrationCheckBox() {
        return calibrationCheckBox;
    }

    public Button getCalibrateSubmoduleOneButton() {
        return calibrateSubmoduleOneButton;
    }

    public Button getCalibrateSubmoduleTwoButton() {
        return calibrateSubmoduleTwoButton;
    }

    public Button getCalibrateSubmoduleThreeButton() {
        return calibrateSubmoduleThreeButton;
    }

    public Button getCalibrateSubmoduleFourButton() {
        return calibrateSubmoduleFourButton;
    }

    public Button getCalibrateSubmoduleFiveButton() {
        return calibrateSubmoduleFiveButton;
    }

    public Button getCalibrateSubmoduleSixButton() {
        return calibrateSubmoduleSixButton;
    }

    public Button getCalibrateSubmoduleSevenButton() {
        return calibrateSubmoduleSevenButton;
    }

    public Button getCalibrateSubmoduleEightButton() {
        return calibrateSubmoduleEightButton;
    }

    public double[] getData() {
        return ltr27SettingsModel.getData();
    }

    public ComboBox<String> getFrequencyComboBox() {
        return frequencyComboBox;
    }

    public ComboBox<String> getRarefactionComboBox() {
        return rarefactionComboBox;
    }

    public Label getRarefactionLabel() {
        return rarefactionLabel;
    }

    public String[][] getSubmodulesDescriptions() {
        return ltr27SettingsModel.getDescriptions();
    }

    public CheckBox getSubmoduleOneCheckBox() {
        return submoduleOneCheckBox;
    }

    public Label getSubModuleOneChannelOneLabel() {
        return subModuleOneChannelOneLabel;
    }

    public Label getSubModuleOneChannelTwoLabel() {
        return subModuleOneChannelTwoLabel;
    }

    public TextField getSubModuleOneChannelOneTextField() {
        return subModuleOneChannelOneTextField;
    }

    public TextField getSubModuleOneChannelTwoTextField() {
        return subModuleOneChannelTwoTextField;
    }

    public CheckBox getSubmoduleTwoCheckBox() {
        return submoduleTwoCheckBox;
    }

    public Label getSubModuleTwoChannelOneLabel() {
        return subModuleTwoChannelOneLabel;
    }

    public Label getSubModuleTwoChannelTwoLabel() {
        return subModuleTwoChannelTwoLabel;
    }

    public TextField getSubModuleTwoChannelOneTextField() {
        return subModuleTwoChannelOneTextField;
    }

    public TextField getSubModuleTwoChannelTwoTextField() {
        return subModuleTwoChannelTwoTextField;
    }

    public CheckBox getSubmoduleThreeCheckBox() {
        return submoduleThreeCheckBox;
    }

    public Label getSubModuleThreeChannelOneLabel() {
        return subModuleThreeChannelOneLabel;
    }

    public Label getSubModuleThreeChannelTwoLabel() {
        return subModuleThreeChannelTwoLabel;
    }

    public TextField getSubModuleThreeChannelOneTextField() {
        return subModuleThreeChannelOneTextField;
    }

    public TextField getSubModuleThreeChannelTwoTextField() {
        return subModuleThreeChannelTwoTextField;
    }

    public CheckBox getSubmoduleFourCheckBox() {
        return submoduleFourCheckBox;
    }

    public Label getSubModuleFourChannelOneLabel() {
        return subModuleFourChannelOneLabel;
    }

    public Label getSubModuleFourChannelTwoLabel() {
        return subModuleFourChannelTwoLabel;
    }

    public TextField getSubModuleFourChannelOneTextField() {
        return subModuleFourChannelOneTextField;
    }

    public TextField getSubModuleFourChannelTwoTextField() {
        return subModuleFourChannelTwoTextField;
    }

    public CheckBox getSubmoduleFiveCheckBox() {
        return submoduleFiveCheckBox;
    }

    public Label getSubModuleFiveChannelOneLabel() {
        return subModuleFiveChannelOneLabel;
    }

    public Label getSubModuleFiveChannelTwoLabel() {
        return subModuleFiveChannelTwoLabel;
    }

    public TextField getSubModuleFiveChannelOneTextField() {
        return subModuleFiveChannelOneTextField;
    }

    public TextField getSubModuleFiveChannelTwoTextField() {
        return subModuleFiveChannelTwoTextField;
    }

    public CheckBox getSubmoduleSixCheckBox() {
        return submoduleSixCheckBox;
    }

    public Label getSubModuleSixChannelOneLabel() {
        return subModuleSixChannelOneLabel;
    }

    public Label getSubModuleSixChannelTwoLabel() {
        return subModuleSixChannelTwoLabel;
    }

    public TextField getSubModuleSixChannelOneTextField() {
        return subModuleSixChannelOneTextField;
    }

    public TextField getSubModuleSixChannelTwoTextField() {
        return subModuleSixChannelTwoTextField;
    }

    public CheckBox getSubmoduleSevenCheckBox() {
        return submoduleSevenCheckBox;
    }

    public Label getSubModuleSevenChannelOneLabel() {
        return subModuleSevenChannelOneLabel;
    }

    public Label getSubModuleSevenChannelTwoLabel() {
        return subModuleSevenChannelTwoLabel;
    }

    public TextField getSubModuleSevenChannelOneTextField() {
        return subModuleSevenChannelOneTextField;
    }

    public TextField getSubModuleSevenChannelTwoTextField() {
        return subModuleSevenChannelTwoTextField;
    }

    public CheckBox getSubmoduleEightCheckBox() {
        return submoduleEightCheckBox;
    }

    public Label getSubModuleEightChannelOneLabel() {
        return subModuleEightChannelOneLabel;
    }

    public Label getSubModuleEightChannelTwoLabel() {
        return subModuleEightChannelTwoLabel;
    }

    public TextField getSubModuleEightChannelOneTextField() {
        return subModuleEightChannelOneTextField;
    }

    public TextField getSubModuleEightChannelTwoTextField() {
        return subModuleEightChannelTwoTextField;
    }

    public LTR27SubmodulesSettings getSubmoduleSettings() {
        return ltr27SubmodulesSettings;
    }

    public boolean isStopped() {
        return stoped || cm.isClosed();
    }

    public void setLTR27CalibrationManager(LTR27CalibrationManager lcm) {
        this.lcm = lcm;
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
