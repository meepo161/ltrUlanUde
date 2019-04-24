package ru.avem.posum.controllers.LTR24;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.LTR24SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class LTR24Settings implements BaseController {
    @FXML
    private CheckBox applyForAllChannels;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
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
        statusBarLine = new StatusBarLine();

    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr24SettingsModel.setModuleName(moduleName);
        ltr24SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr24SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr24ChannelsSettings.setSettings();
        ltr24ModuleSettings.setSettings();
    }

    @FXML
    public void handleInitialize() {
        toggleProgressIndicatorState(false);
        ltr24ChannelsSettings.toggleChannelsUiElementsState(true);
        ltr24ModuleSettings.toggleUiElementsState(true);

        new Thread(() -> {
            ltr24ChannelsSettings.saveSettings();
            ltr24ModuleSettings.saveSettings();
            ltr24SettingsModel.initModule();

            if (ltr24SettingsModel.getLTR24Instance().getStatus().equals("Операция успешно выполнена")) {
                ltr24SettingsModel.setConnectionOpen(true);
            } else {
                Platform.runLater(() -> {
                    ltr24SettingsModel.setConnectionOpen(false);
                    ltr24ChannelsSettings.enableUiElements();
                    ltr24ModuleSettings.toggleUiElementsState(false);
                });
            }

            toggleProgressIndicatorState(true);
            Platform.runLater(() -> statusBarLine.setStatus(ltr24SettingsModel.getLTR24Instance().getStatus(), statusBar));
        }).start();
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    @FXML
    public void handleBackButton() {
        new Thread(() -> {
            ltr24ChannelsSettings.enableUiElements();
            ltr24ChannelsSettings.saveSettings();
            ltr24ModuleSettings.saveSettings();
            closeConnection();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void closeConnection() {
        if (ltr24SettingsModel.isConnectionOpen()) {
            ltr24SettingsModel.getLTR24Instance().closeConnection();
            ltr24SettingsModel.setConnectionOpen(false);
        }
    }

    private void changeScene(WindowsManager.Scenes settingsScene) {
        wm.setScene(settingsScene);
    }

    public void handleValueOfChannelN1() {
        showChannelValue(0);
    }

    private void showChannelValue(int channel) {
        ltr24SettingsModel.getLTR24Instance().defineFrequency();
        ltr24SettingsModel.getLTR24Instance().start(ltr24SettingsModel.getSlot());
        cm.giveChannelInfo(channel, CrateModel.LTR24, ltr24SettingsModel.getLTR24Instance().getSlot());
        cm.initializeSignalGraphView();
        cm.checkCalibration();
        changeScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
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

    public CheckBox getApplyForAllChannels() {
        return applyForAllChannels;
    }

    public CheckBox getCheckChannelN1() {
        return checkChannelN1;
    }

    public CheckBox getCheckChannelN2() {
        return checkChannelN2;
    }

    public CheckBox getCheckChannelN3() {
        return checkChannelN3;
    }

    public CheckBox getCheckChannelN4() {
        return checkChannelN4;
    }

    public TextField getDescriptionOfChannelN1() {
        return descriptionOfChannelN1;
    }

    public TextField getDescriptionOfChannelN2() {
        return descriptionOfChannelN2;
    }

    public TextField getDescriptionOfChannelN3() {
        return descriptionOfChannelN3;
    }

    public TextField getDescriptionOfChannelN4() {
        return descriptionOfChannelN4;
    }

    public ComboBox<String> getFrequencyComboBox() {
        return frequencyComboBox;
    }

    public Button getInitializeButton() {
        return initializeButton;
    }

    public ComboBox<String> getMeasuringRangeOfChannelN1() {
        return measuringRangeOfChannelN1;
    }

    public ComboBox<String> getMeasuringRangeOfChannelN2() {
        return measuringRangeOfChannelN2;
    }

    public ComboBox<String> getMeasuringRangeOfChannelN3() {
        return measuringRangeOfChannelN3;
    }

    public ComboBox<String> getMeasuringRangeOfChannelN4() {
        return measuringRangeOfChannelN4;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public Label getSceneTitleLabel() {
        return sceneTitleLabel;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public ComboBox<String> getTypeOfChannelN1() {
        return typeOfChannelN1;
    }

    public ComboBox<String> getTypeOfChannelN2() {
        return typeOfChannelN2;
    }

    public ComboBox<String> getTypeOfChannelN3() {
        return typeOfChannelN3;
    }

    public ComboBox<String> getTypeOfChannelN4() {
        return typeOfChannelN4;
    }

    public Button getValueOnChannelN1() {
        return valueOnChannelN1;
    }

    public Button getValueOnChannelN2() {
        return valueOnChannelN2;
    }

    public Button getValueOnChannelN3() {
        return valueOnChannelN3;
    }

    public Button getValueOnChannelN4() {
        return valueOnChannelN4;
    }

    public ControllerManager getCm() {
        return cm;
    }

    public LTR24SettingsModel getLtr24SettingsModel() {
        return ltr24SettingsModel;
    }

    public StatusBarLine getStatusBarLine() {
        return statusBarLine;
    }

    public WindowsManager getWm() {
        return wm;
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
