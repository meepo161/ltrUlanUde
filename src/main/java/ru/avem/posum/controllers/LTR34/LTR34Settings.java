package ru.avem.posum.controllers.LTR34;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.LTR34SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class LTR34Settings implements BaseController {
    @FXML
    protected TextField amplitudeOfChannelN1;
    @FXML
    protected TextField amplitudeOfChannelN2;
    @FXML
    protected TextField amplitudeOfChannelN3;
    @FXML
    protected TextField amplitudeOfChannelN4;
    @FXML
    protected TextField amplitudeOfChannelN5;
    @FXML
    protected TextField amplitudeOfChannelN6;
    @FXML
    protected TextField amplitudeOfChannelN7;
    @FXML
    protected TextField amplitudeOfChannelN8;
    @FXML
    protected CheckBox checkChannelN1;
    @FXML
    protected CheckBox checkChannelN2;
    @FXML
    protected CheckBox checkChannelN3;
    @FXML
    protected CheckBox checkChannelN4;
    @FXML
    protected CheckBox checkChannelN5;
    @FXML
    protected CheckBox checkChannelN6;
    @FXML
    protected CheckBox checkChannelN7;
    @FXML
    protected CheckBox checkChannelN8;
    @FXML
    protected ComboBox<String> calibrationComboBox;
    @FXML
    protected ComboBox<String> dacModeComboBox;
    @FXML
    protected TextField descriptionOfChannelN1;
    @FXML
    protected TextField descriptionOfChannelN2;
    @FXML
    protected TextField descriptionOfChannelN3;
    @FXML
    protected TextField descriptionOfChannelN4;
    @FXML
    protected TextField descriptionOfChannelN5;
    @FXML
    protected TextField descriptionOfChannelN6;
    @FXML
    protected TextField descriptionOfChannelN7;
    @FXML
    protected TextField descriptionOfChannelN8;
    @FXML
    protected TextField frequencyOfChannelN1;
    @FXML
    protected TextField frequencyOfChannelN2;
    @FXML
    protected TextField frequencyOfChannelN3;
    @FXML
    protected TextField frequencyOfChannelN4;
    @FXML
    protected TextField frequencyOfChannelN5;
    @FXML
    protected TextField frequencyOfChannelN6;
    @FXML
    protected TextField frequencyOfChannelN7;
    @FXML
    protected TextField frequencyOfChannelN8;
    @FXML
    protected Button generateSignalButton;
    @FXML
    protected LineChart<Number, Number> graph;
    @FXML
    protected TextField phaseOfChannelN1;
    @FXML
    protected TextField phaseOfChannelN2;
    @FXML
    protected TextField phaseOfChannelN3;
    @FXML
    protected TextField phaseOfChannelN4;
    @FXML
    protected TextField phaseOfChannelN5;
    @FXML
    protected TextField phaseOfChannelN6;
    @FXML
    protected TextField phaseOfChannelN7;
    @FXML
    protected TextField phaseOfChannelN8;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    protected ComboBox<String> signalTypeComboBox;
    @FXML
    protected Button stopSignalButton;
    @FXML
    private StatusBar statusBar;

    private ControllerManager cm;
    private LTR34ChannelsSettings ltr34ChannelsSettings = new LTR34ChannelsSettings();
    private LTR34ModuleSettings ltr34ModuleSettings = new LTR34ModuleSettings();
    LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private WindowsManager wm;

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr34SettingsModel.setModuleName(moduleName);
        ltr34SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr34SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr34ChannelsSettings.setSettings();
        ltr34ModuleSettings.setSettings();
    }

    @FXML
    public void handleGenerateSignal() {
        toggleProgressIndicatorState(false);
        ltr34ChannelsSettings.disableUiElementsState();
        ltr34ModuleSettings.disableUiElementsState();

        new Thread(() -> {
            ltr34ChannelsSettings.saveSettings();
            ltr34ModuleSettings.saveSettings();
            ltr34SettingsModel.initModule();

            if (ltr34SettingsModel.getLTR34Instance().getStatus().equals("Операция успешно выполнена")) {
                ltr34SettingsModel.calculateSignal(signalTypeComboBox.getSelectionModel().getSelectedIndex());
                ltr34SettingsModel.generate(dacModeComboBox.getSelectionModel().getSelectedIndex() == 1);
                showGraph();
            } else {
                ltr34ModuleSettings.enableUiElements();
            }

            toggleProgressIndicatorState(true);
            Platform.runLater(() -> statusBarLine.setStatus(ltr34SettingsModel.getLTR34Instance().getStatus(), statusBar));
        }).start();
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void showGraph() {
        Platform.runLater(() -> {
            graph.setDisable(false);
            stopSignalButton.setDisable(false);
            stopSignalButton.requestFocus();
            drawGraph();
        });
    }

    private void drawGraph() {
        for (int channelIndex = 0; channelIndex < ltr34SettingsModel.getLTR34Instance().getChannelsCount(); channelIndex++) {
            if (ltr34ChannelsSettings.getCheckBoxes().get(channelIndex).isSelected()) {
                graph.getData().add(ltr34SettingsModel.createSeries(channelIndex));
            }
        }
    }

    @FXML
    public void handleStopSignal() {
        ltr34SettingsModel.stopModule();
        graph.getData().clear();
        ltr34ChannelsSettings.enableUiElements();
        ltr34ModuleSettings.enableUiElements();
    }

    @FXML
    public void handleBackButton() {
        new Thread(() -> {
//            ltr34SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
            ltr34ChannelsSettings.saveSettings();
            ltr34ModuleSettings.saveSettings();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
