package ru.avem.posum.controllers.Settings.LTR212;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Settings.LTR212SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.io.File;

public class LTR212Settings implements BaseController {
    @FXML
    private CheckBox applyForAllChannels;
    @FXML
    private Button backButton;
    @FXML
    private Label checkIcon;
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
    private Button initializeButton;
    @FXML
    private CheckBox firCheckBox;
    @FXML
    private Button firPathButton;
    @FXML
    private TextField firPathTextField;
    @FXML
    private CheckBox iirCheckBox;
    @FXML
    private Button iirPathButton;
    @FXML
    private TextField iirPathTextField;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN1;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN2;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN3;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN4;
    @FXML
    private ComboBox<String> moduleModesComboBox;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> referenceVoltageComboBox;
    @FXML
    private CheckBox referenceVoltageTypeCheckBox;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private CheckBox factoryCalibrationCheckBox;
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
    private LTR212ChannelsSettings ltr212ChannelsSettings;
    private LTR212ModuleSettings ltr212ModuleSettings;
    private LTR212SettingsModel ltr212SettingsModel;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        ltr212SettingsModel = new LTR212SettingsModel();
        ltr212ChannelsSettings = new LTR212ChannelsSettings(this);
        ltr212ModuleSettings = new LTR212ModuleSettings(this);
        statusBarLine = new StatusBarLine();
    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr212SettingsModel.setModuleName(moduleName);
        ltr212SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr212SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr212ChannelsSettings.setSettings();
        ltr212ModuleSettings.setSettings();
    }

    public void handleInitialize() {
        toggleProgressIndicatorState(false);
        statusBarLine.setStatus("Инициализация модуля", statusBar);
        ltr212ChannelsSettings.disableUiElementsState();
        ltr212ModuleSettings.toggleUiElementsState(true);

        new Thread(() -> {
            ltr212ChannelsSettings.saveSettings();
            ltr212ModuleSettings.saveSettings();
            ltr212SettingsModel.initModule();

            if (ltr212SettingsModel.getLTR212Instance().getStatus().equals("Операция успешно выполнена")) {
                ltr212SettingsModel.setConnectionOpen(true);
            } else {
                Platform.runLater(() -> {
                    ltr212SettingsModel.setConnectionOpen(false);
                    ltr212ChannelsSettings.enableUiElements();
                    ltr212ModuleSettings.toggleUiElementsState(false);
                });
            }

            toggleProgressIndicatorState(true);
            Platform.runLater(() -> statusBarLine.setStatus
                    (ltr212SettingsModel.getLTR212Instance().getStatus(), statusBar, checkIcon, warningIcon));
        }).start();
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
            statusBarLine.clearStatusBar(statusBar);
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    public void handleBackButton() {
        new Thread(() -> {
            ltr212ChannelsSettings.enableUiElements();
            ltr212ChannelsSettings.saveSettings();
            ltr212ModuleSettings.enableUiElements();
            ltr212ModuleSettings.saveSettings();
            closeConnection();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        statusBarLine.clearStatusBar(statusBar);
        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void closeConnection() {
        if (ltr212SettingsModel.isConnectionOpen()) {
            ltr212SettingsModel.getLTR212Instance().closeConnection();
            ltr212SettingsModel.setConnectionOpen(false);
        }
    }

    private void changeScene(WindowsManager.Scenes settingsScene) {
        Platform.runLater(() -> wm.setScene(settingsScene));
    }

    public void handleValueOfChannelN1() {
        showChannelValue(0);
    }

    private void showChannelValue(int channel) {
        toggleProgressIndicatorState(false);
        Platform.runLater(() -> statusBarLine.setStatus("Подготовка данных для отображения", statusBar));
        ltr212ChannelsSettings.toggleValueOnChannelButtons(true);
        backButton.setDisable(true);

        new Thread(() -> {
            ltr212SettingsModel.getLTR212Instance().defineFrequency();
            ltr212SettingsModel.getLTR212Instance().start(ltr212SettingsModel.getSlot());
            cm.giveChannelInfo(channel, Crate.LTR212, ltr212SettingsModel.getLTR212Instance().getSlot());
            cm.initializeSignalGraphView();
            cm.checkCalibration();

            toggleProgressIndicatorState(true);
            ltr212ChannelsSettings.toggleValueOnChannelButtons(false);
            backButton.setDisable(false);
            changeScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
        }).start();
    }

    @FXML
    public void handleValueOfChannelN2() {
        showChannelValue(1);
    }

    @FXML
    public void handleValueOfChannelN3() {
        showChannelValue(2);
    }

    @FXML
    public void handleValueOfChannelN4() {
        showChannelValue(3);
    }

    @FXML
    public void handleChoosingIIRFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла фильтра");
        File selectedDirectory = fileChooser.showOpenDialog(new Stage());
        if (selectedDirectory == null) {
            iirPathTextField.setText("Не выбран файл фильтра");
        } else {
            iirPathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void handleChoosingFIRFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла фильтра");
        File selectedDirectory = fileChooser.showOpenDialog(new Stage());
        if (selectedDirectory == null) {
            firPathTextField.setText("Не выбран файл фильтра");
        } else {
            firPathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    CheckBox getApplyForAllChannels() {
        return applyForAllChannels;
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

    CheckBox getFirCheckBox() {
        return firCheckBox;
    }

    Button getFirPathButton() {
        return firPathButton;
    }

    TextField getFirPathTextField() {
        return firPathTextField;
    }

    CheckBox getIirCheckBox() {
        return iirCheckBox;
    }

    Button getIirPathButton() {
        return iirPathButton;
    }

    TextField getIirPathTextField() {
        return iirPathTextField;
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

    ComboBox<String> getModuleModesComboBox() {
        return moduleModesComboBox;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    LTR212SettingsModel getLtr212SettingsModel() {
        return ltr212SettingsModel;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    ComboBox<String> getReferenceVoltageComboBox() {
        return referenceVoltageComboBox;
    }

    CheckBox getReferenceVoltageTypeCheckBox() {
        return referenceVoltageTypeCheckBox;
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

    CheckBox getFactoryCalibrationCheckBox() {
        return factoryCalibrationCheckBox;
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

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}