package ru.avem.posum.controllers.settings.LTR34;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.communication.CommunicationModel;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.models.settings.LTR34SettingsModel;
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
    private TextField dcOfChannelN1;
    @FXML
    private TextField dcOfChannelN2;
    @FXML
    private TextField dcOfChannelN3;
    @FXML
    private TextField dcOfChannelN4;
    @FXML
    private TextField dcOfChannelN5;
    @FXML
    private TextField dcOfChannelN6;
    @FXML
    private TextField dcOfChannelN7;
    @FXML
    private TextField dcOfChannelN8;
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
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private LTR34ChannelsSettings ltr34ChannelsSettings;
    private LTR34ModuleSettings ltr34ModuleSettings;
    private LTR34SettingsModel ltr34SettingsModel;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;


    // Загружает настройки модуля
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
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar,
                warningIcon);
    }

    // Обрабатывает нажатие на кнопку "Генерировать"
    @FXML
    public void handleGenerateSignal() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Запуск генерации сигнала");
        ltr34ChannelsSettings.disableUiElementsState();
        ltr34ModuleSettings.disableUiElementsState();

        new Thread(() -> {
            ltr34ChannelsSettings.saveSettings();
            ltr34ModuleSettings.saveSettings();
            ltr34SettingsModel.initModule();

            if (ltr34SettingsModel.getLTR34Instance().checkStatus()) {
                ltr34SettingsModel.calculateSignal(signalTypeComboBox.getSelectionModel().getSelectedIndex());
                generate();
                showGraph();
                CommunicationModel.INSTANCE.getMU110Controller().onKM1();//TODO тесты
            } else {
                ltr34ChannelsSettings.enableUiElements();
                ltr34ModuleSettings.enableUiElements();
            }

            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.setStatus(ltr34SettingsModel.getLTR34Instance().getStatus(),
                    ltr34SettingsModel.getLTR34Instance().checkStatus());
        }).start();
    }

    // Генерирует сигнал
    private void generate() {
        LTR34 ltr34 = ltr34SettingsModel.getLTR34Instance();
        ltr34.generate(ltr34SettingsModel.getSignal());
        ltr34.start();
        ltr34SettingsModel.setStopped(false);
        boolean isAutogenerationMode = dacModeComboBox.getSelectionModel().getSelectedIndex() == 1;

        if (!isAutogenerationMode) {
            new Thread(() -> {
                while (!ltr34SettingsModel.isStopped() && ltr34.checkStatus()) {
                    ltr34.generate(ltr34SettingsModel.getSignal());
                    checkConnection();
                    Utils.sleep(1000);
                }
            }).start();
        } else {
            new Thread(() -> {
                while (!ltr34SettingsModel.isStopped() && ltr34.checkStatus()) {
                    checkConnection();
                    Utils.sleep(1000);
                }
            }).start();
        }
    }

    // Проверяет соединение с модулем
    private void checkConnection() {
        LTR34 ltr34 = ltr34SettingsModel.getLTR34Instance();
        ltr34.checkConnection();
        if (!ltr34.checkStatus()) {
            ltr34SettingsModel.setStopped(true);
            statusBarLine.setStatus(ltr34.getStatus(), false);
            Platform.runLater(() -> graph.getData().clear());
            ltr34ChannelsSettings.enableUiElements();
            ltr34ModuleSettings.enableUiElements();
        }
    }

    // Отображает график сигнала
    private void showGraph() {
        Platform.runLater(() -> {
            graph.setDisable(false);
            stopSignalButton.setDisable(false);
            stopSignalButton.requestFocus();
            drawGraph();
        });
    }

    // Выводит график
    private void drawGraph() {
        for (int channelIndex = 0; channelIndex < ltr34SettingsModel.getLTR34Instance().getChannelsCount();
             channelIndex++) {
            if (ltr34ChannelsSettings.getCheckBoxes().get(channelIndex).isSelected()) {
                graph.getData().add(ltr34SettingsModel.createSeries(channelIndex));
            }
        }
    }

    // Останавливает генерацию сигнала
    @FXML
    public void handleStopSignal() {
        ltr34SettingsModel.stopModule();
        Platform.runLater(() -> graph.getData().clear());
        ltr34ChannelsSettings.enableUiElements();
        ltr34ModuleSettings.enableUiElements();
        CommunicationModel.INSTANCE.getMU110Controller().offAllKms();
    }

    // Возвращает пользователя в окно выбора модуля
    @FXML
    public void handleBackButton() {
        new Thread(() -> {
            ltr34ChannelsSettings.saveSettings();
            ltr34ModuleSettings.saveSettings();
            ltr34SettingsModel.stopModule();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        statusBarLine.clear();
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

    public TextField getDcOfChannelN1() {
        return dcOfChannelN1;
    }

    public TextField getDcOfChannelN2() {
        return dcOfChannelN2;
    }

    public TextField getDcOfChannelN3() {
        return dcOfChannelN3;
    }

    public TextField getDcOfChannelN4() {
        return dcOfChannelN4;
    }

    public TextField getDcOfChannelN5() {
        return dcOfChannelN5;
    }

    public TextField getDcOfChannelN6() {
        return dcOfChannelN6;
    }

    public TextField getDcOfChannelN7() {
        return dcOfChannelN7;
    }

    public TextField getDcOfChannelN8() {
        return dcOfChannelN8;
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
