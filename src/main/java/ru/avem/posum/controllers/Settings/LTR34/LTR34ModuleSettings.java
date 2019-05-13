package ru.avem.posum.controllers.Settings.LTR34;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import ru.avem.posum.hardware.DAC;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

class LTR34ModuleSettings extends LTR34Settings {
    private ComboBox<String> calibrationComboBox;
    private Label calibrationLabel;
    private ComboBox<String> dacModeComboBox;
    private Label dacModeLabel;
    private Button generateSignalButton;
    private LineChart<Number, Number> graph;
    private LTR34SettingsModel ltr34SettingsModel;
    private ComboBox<String> signalTypeComboBox;
    private Label signalTypeLabel;
    private Button stopSignalButton;

    LTR34ModuleSettings(LTR34Settings ltr34Settings) {
        this.calibrationComboBox = ltr34Settings.getCalibrationComboBox();
        this.calibrationLabel = ltr34Settings.getCalibrationLabel();
        this.dacModeComboBox = ltr34Settings.getDacModeComboBox();
        this.dacModeLabel = ltr34Settings.getDacModeLabel();
        this.generateSignalButton = ltr34Settings.getGenerateSignalButton();
        this.graph = ltr34Settings.getGraph();
        this.ltr34SettingsModel = ltr34Settings.getLtr34SettingsModel();
        this.signalTypeComboBox = ltr34Settings.getSignalTypeComboBox();
        this.signalTypeLabel = ltr34Settings.getSignalTypeLabel();
        this.stopSignalButton = ltr34Settings.getStopSignalButton();

        addSignalTypes();
        addCalibrations();
        addDACModes();
    }

    private void addSignalTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("Синусоидальный");
        types.add("Прямоугольный");
        types.add("Треугольный");
        types.add("Пила");
        types.add("Отраженная пила");
        types.add("Периодический шум");

        signalTypeComboBox.getItems().addAll(types);
        signalTypeComboBox.getSelectionModel().select(0);
    }

    private void addCalibrations() {
        ObservableList<String> calibrations = FXCollections.observableArrayList();

        calibrations.add("Не используются");
        calibrations.add("Заводские");

        calibrationComboBox.getItems().addAll(calibrations);
        calibrationComboBox.getSelectionModel().select(1);
    }

    private void addDACModes() {
        ObservableList<String> modes = FXCollections.observableArrayList();

        modes.add("Потоковый режим генерации");
        modes.add("Режим автогенерации");

        dacModeComboBox.getItems().addAll(modes);
        dacModeComboBox.getSelectionModel().select(0);
    }

    void setSettings() {
        // TODO: Refactor a hashmap
        int dacMode = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.DAC_MODE.getSettingName());
        int factoryCalibration = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName());
        int signalType = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.SIGNAL_TYPE.getSettingName());
        calibrationComboBox.getSelectionModel().select(factoryCalibration);
        dacModeComboBox.getSelectionModel().select(dacMode);
        signalTypeComboBox.getSelectionModel().select(signalType);
    }

    void saveSettings() {
        int dacMode = dacModeComboBox.getSelectionModel().getSelectedIndex();
        int factoryCalibration = calibrationComboBox.getSelectionModel().getSelectedIndex();
        int signalType = signalTypeComboBox.getSelectionModel().getSelectedIndex();
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.DAC_MODE.getSettingName(), dacMode);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), factoryCalibration);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.SIGNAL_TYPE.getSettingName(), signalType);
    }

    void disableUiElementsState() {
        calibrationComboBox.setDisable(true);
        calibrationLabel.setDisable(true);
        dacModeComboBox.setDisable(true);
        dacModeLabel.setDisable(true);
        signalTypeComboBox.setDisable(true);
        signalTypeLabel.setDisable(true);
        generateSignalButton.setDisable(true);
    }

    void enableUiElements() {
        calibrationComboBox.setDisable(false);
        calibrationLabel.setDisable(false);
        dacModeComboBox.setDisable(false);
        dacModeLabel.setDisable(false);
        graph.setDisable(true);
        generateSignalButton.setDisable(false);
        signalTypeComboBox.setDisable(false);
        signalTypeLabel.setDisable(false);
        stopSignalButton.setDisable(true);
    }
}
