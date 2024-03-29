package ru.avem.posum.controllers.settings.LTR24;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.settings.LTR24SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class LTR24Settings implements BaseController {
    @FXML
    private CheckBox applyForAllChannels;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
    @FXML
    private Label checkIcon;
    @FXML
    private TextField descriptionOfChannelN1;
    @FXML
    private TextField descriptionOfChannelN2;
    @FXML
    private TextField descriptionOfChannelN3;
    @FXML
    private TextField descriptionOfChannelN4;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private Button initializeButton;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN1;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN2;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN3;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN4;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private Button valueOnChannelN1;
    @FXML
    private Button valueOnChannelN2;
    @FXML
    private Button valueOnChannelN3;
    @FXML
    private Button valueOnChannelN4;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private LTR24ChannelsSettings ltr24ChannelsSettings;
    private LTR24ModuleSettings ltr24ModuleSettings;
    private LTR24SettingsModel ltr24SettingsModel;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        ltr24SettingsModel = new LTR24SettingsModel();
        ltr24ChannelsSettings = new LTR24ChannelsSettings(this);
        ltr24ModuleSettings = new LTR24ModuleSettings(this);
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar,
                warningIcon);

    }

    // Загружает настройки модуля
    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr24SettingsModel.setModuleName(moduleName);
        ltr24SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr24SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr24ChannelsSettings.setSettings();
        ltr24ModuleSettings.setSettings();
    }

    // Выполняет инициализацию модуля
    @FXML
    public void handleInitialize() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Инициализация модуля");
        ltr24ChannelsSettings.disableUiElementsState();
        ltr24ModuleSettings.toggleUiElementsState(true);

        new Thread(() -> {
            ltr24ChannelsSettings.saveSettings();
            ltr24ModuleSettings.saveSettings();
            ltr24SettingsModel.getLTR24Instance().openConnection();
            ltr24SettingsModel.initModule();

            if (!ltr24SettingsModel.getLTR24Instance().checkStatus()) {
                Platform.runLater(() -> {
                    ltr24ChannelsSettings.enableUiElements();
                    ltr24ModuleSettings.toggleUiElementsState(false);
                });
            }

            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.setStatus(ltr24SettingsModel.getLTR24Instance().getStatus(),
                    ltr24SettingsModel.getLTR24Instance().checkStatus());
            backButton.setDisable(false);
            ltr24ChannelsSettings.enableValueOnChannelButtonsState();
            backButton.setDisable(false);
        }).start();
    }

    // Возвращает пользователя в окно выбора модуля
    @FXML
    public void handleBackButton() {
        new Thread(() -> {
            ltr24ChannelsSettings.enableUiElements();
            ltr24ChannelsSettings.saveSettings();
            ltr24ModuleSettings.toggleUiElementsState(false);
            ltr24ModuleSettings.saveSettings();
            ltr24SettingsModel.getLTR24Instance().closeConnection();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        statusBarLine.clear();
        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void changeScene(WindowsManager.Scenes settingsScene) {
        Platform.runLater(() -> wm.setScene(settingsScene));
    }

    public void handleValueOfChannelN1() {
        showChannelValue(0);
    }

    // Отображает график текущей нагрузки на канале
    private void showChannelValue(int channel) {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Подготовка данных для отображения");
        ltr24ChannelsSettings.disableValueOnChannelButtonsState();
        backButton.setDisable(true);

        new Thread(() -> {
            ltr24ChannelsSettings.saveMeasuringRangeOfChannel(channel);
            ltr24SettingsModel.getLTR24Instance().defineFrequency();
            ltr24SettingsModel.getLTR24Instance().start(ltr24SettingsModel.getSlot());

            cm.giveChannelInfo(channel, Crate.LTR24, ltr24SettingsModel.getLTR24Instance().getSlot());
            cm.initializeSignalGraphView();
            cm.checkCalibration();

            Utils.sleep(2500); // пауза для отрисовки ненулевого сигнала
            ltr24ChannelsSettings.enableValueOnChannelButtonsState();
            backButton.setDisable(false);
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clear();
            changeScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
        }).start();
    }

    public void handleValueOfChannelN2() {
        showChannelValue(1);
    }

    public void handleValueOfChannelN3() {
        showChannelValue(2);
    }

    public void handleValueOfChannelN4() {
        showChannelValue(3);
    }

    CheckBox getApplyForAllChannels() {
        return applyForAllChannels;
    }

    public Button getBackButton() {
        return backButton;
    }

    CheckBox getCheckChannelN1() {
        return checkChannelN1;
    }

    CheckBox getCheckChannelN2() {
        return checkChannelN2;
    }

    CheckBox getCheckChannelN3() {
        return checkChannelN3;
    }

    CheckBox getCheckChannelN4() {
        return checkChannelN4;
    }

    TextField getDescriptionOfChannelN1() {
        return descriptionOfChannelN1;
    }

    TextField getDescriptionOfChannelN2() {
        return descriptionOfChannelN2;
    }

    TextField getDescriptionOfChannelN3() {
        return descriptionOfChannelN3;
    }

    TextField getDescriptionOfChannelN4() {
        return descriptionOfChannelN4;
    }

    ComboBox<String> getFrequencyComboBox() {
        return frequencyComboBox;
    }

    Button getInitializeButton() {
        return initializeButton;
    }

    ComboBox<String> getMeasuringRangeOfChannelN1() {
        return measuringRangeOfChannelN1;
    }

    ComboBox<String> getMeasuringRangeOfChannelN2() {
        return measuringRangeOfChannelN2;
    }

    ComboBox<String> getMeasuringRangeOfChannelN3() {
        return measuringRangeOfChannelN3;
    }

    ComboBox<String> getMeasuringRangeOfChannelN4() {
        return measuringRangeOfChannelN4;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    ComboBox<String> getTypeOfChannelN1() {
        return typeOfChannelN1;
    }

    ComboBox<String> getTypeOfChannelN2() {
        return typeOfChannelN2;
    }

    ComboBox<String> getTypeOfChannelN3() {
        return typeOfChannelN3;
    }

    ComboBox<String> getTypeOfChannelN4() {
        return typeOfChannelN4;
    }

    Button getValueOnChannelN1() {
        return valueOnChannelN1;
    }

    Button getValueOnChannelN2() {
        return valueOnChannelN2;
    }

    Button getValueOnChannelN3() {
        return valueOnChannelN3;
    }

    Button getValueOnChannelN4() {
        return valueOnChannelN4;
    }

    LTR24SettingsModel getLtr24SettingsModel() {
        return ltr24SettingsModel;
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
