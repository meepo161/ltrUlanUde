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
    private TextField amplitudeOfChannelN1;
    @FXML
    private TextField amplitudeOfChannelN2;
    @FXML
    private TextField amplitudeOfChannelN3;
    @FXML
    private TextField amplitudeOfChannelN4;
    @FXML
    private TextField amplitudeOfChannelN5;
    @FXML
    private TextField amplitudeOfChannelN6;
    @FXML
    private TextField amplitudeOfChannelN7;
    @FXML
    private TextField amplitudeOfChannelN8;
    @FXML
    private ComboBox<String> calibrationComboBox;
    @FXML
    private Label calibrationLabel;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
    @FXML
    private CheckBox checkChannelN5;
    @FXML
    private CheckBox checkChannelN6;
    @FXML
    private CheckBox checkChannelN7;
    @FXML
    private CheckBox checkChannelN8;
    @FXML
    private ComboBox<String> dacModeComboBox;
    @FXML
    private Label dacModeLabel;
    @FXML
    private TextField descriptionOfChannelN1;
    @FXML
    private TextField descriptionOfChannelN2;
    @FXML
    private TextField descriptionOfChannelN3;
    @FXML
    private TextField descriptionOfChannelN4;
    @FXML
    private TextField descriptionOfChannelN5;
    @FXML
    private TextField descriptionOfChannelN6;
    @FXML
    private TextField descriptionOfChannelN7;
    @FXML
    private TextField descriptionOfChannelN8;
    @FXML
    private TextField frequencyOfChannelN1;
    @FXML
    private TextField frequencyOfChannelN2;
    @FXML
    private TextField frequencyOfChannelN3;
    @FXML
    private TextField frequencyOfChannelN4;
    @FXML
    private TextField frequencyOfChannelN5;
    @FXML
    private TextField frequencyOfChannelN6;
    @FXML
    private TextField frequencyOfChannelN7;
    @FXML
    private TextField frequencyOfChannelN8;
    @FXML
    private Button generateSignalButton;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField phaseOfChannelN1;
    @FXML
    private TextField phaseOfChannelN2;
    @FXML
    private TextField phaseOfChannelN3;
    @FXML
    private TextField phaseOfChannelN4;
    @FXML
    private TextField phaseOfChannelN5;
    @FXML
    private TextField phaseOfChannelN6;
    @FXML
    private TextField phaseOfChannelN7;
    @FXML
    private TextField phaseOfChannelN8;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private ComboBox<String> signalTypeComboBox;
    @FXML
    private Label signalTypeLabel;
    @FXML
    private Button stopSignalButton;
    @FXML
    private StatusBar statusBar;

    private ControllerManager cm;
    private LTR34ChannelsSettings ltr34ChannelsSettings;
    private LTR34ModuleSettings ltr34ModuleSettings;
    private LTR34SettingsModel ltr34SettingsModel;
    private StatusBarLine statusBarLine;
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
    private void initialize() {
        ltr34SettingsModel = new LTR34SettingsModel();
        ltr34ChannelsSettings = new LTR34ChannelsSettings(this);
        ltr34ModuleSettings = new LTR34ModuleSettings(this);
        statusBarLine = new StatusBarLine();
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

    TextField getAmplitudeOfChannelN1() {
        return amplitudeOfChannelN1;
    }

    TextField getAmplitudeOfChannelN2() {
        return amplitudeOfChannelN2;
    }

    TextField getAmplitudeOfChannelN3() {
        return amplitudeOfChannelN3;
    }

    TextField getAmplitudeOfChannelN4() {
        return amplitudeOfChannelN4;
    }

    TextField getAmplitudeOfChannelN5() {
        return amplitudeOfChannelN5;
    }

    TextField getAmplitudeOfChannelN6() {
        return amplitudeOfChannelN6;
    }

    TextField getAmplitudeOfChannelN7() {
        return amplitudeOfChannelN7;
    }

    TextField getAmplitudeOfChannelN8() {
        return amplitudeOfChannelN8;
    }

    ComboBox<String> getCalibrationComboBox() {
        return calibrationComboBox;
    }

    Label getCalibrationLabel() {
        return calibrationLabel;
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

    CheckBox getCheckChannelN5() {
        return checkChannelN5;
    }

    CheckBox getCheckChannelN6() {
        return checkChannelN6;
    }

    CheckBox getCheckChannelN7() {
        return checkChannelN7;
    }

    CheckBox getCheckChannelN8() {
        return checkChannelN8;
    }

    ComboBox<String> getDacModeComboBox() {
        return dacModeComboBox;
    }

    Label getDacModeLabel() {
        return dacModeLabel;
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

    TextField getDescriptionOfChannelN5() {
        return descriptionOfChannelN5;
    }

    TextField getDescriptionOfChannelN6() {
        return descriptionOfChannelN6;
    }

    TextField getDescriptionOfChannelN7() {
        return descriptionOfChannelN7;
    }

    TextField getDescriptionOfChannelN8() {
        return descriptionOfChannelN8;
    }

    TextField getFrequencyOfChannelN1() {
        return frequencyOfChannelN1;
    }

    TextField getFrequencyOfChannelN2() {
        return frequencyOfChannelN2;
    }

    TextField getFrequencyOfChannelN3() {
        return frequencyOfChannelN3;
    }

    TextField getFrequencyOfChannelN4() {
        return frequencyOfChannelN4;
    }

    TextField getFrequencyOfChannelN5() {
        return frequencyOfChannelN5;
    }

    TextField getFrequencyOfChannelN6() {
        return frequencyOfChannelN6;
    }

    TextField getFrequencyOfChannelN7() {
        return frequencyOfChannelN7;
    }

    TextField getFrequencyOfChannelN8() {
        return frequencyOfChannelN8;
    }

    Button getGenerateSignalButton() {
        return generateSignalButton;
    }

    LineChart<Number, Number> getGraph() {
        return graph;
    }

    TextField getPhaseOfChannelN1() {
        return phaseOfChannelN1;
    }

    TextField getPhaseOfChannelN2() {
        return phaseOfChannelN2;
    }

    TextField getPhaseOfChannelN3() {
        return phaseOfChannelN3;
    }

    TextField getPhaseOfChannelN4() {
        return phaseOfChannelN4;
    }

    TextField getPhaseOfChannelN5() {
        return phaseOfChannelN5;
    }

    TextField getPhaseOfChannelN6() {
        return phaseOfChannelN6;
    }

    TextField getPhaseOfChannelN7() {
        return phaseOfChannelN7;
    }

    TextField getPhaseOfChannelN8() {
        return phaseOfChannelN8;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    ComboBox<String> getSignalTypeComboBox() {
        return signalTypeComboBox;
    }

    Label getSignalTypeLabel() {
        return signalTypeLabel;
    }

    Button getStopSignalButton() {
        return stopSignalButton;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    LTR34SettingsModel getLtr34SettingsModel() {
        return ltr34SettingsModel;
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
