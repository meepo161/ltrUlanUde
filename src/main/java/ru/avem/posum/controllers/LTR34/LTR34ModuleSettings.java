package ru.avem.posum.controllers.LTR34;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import ru.avem.posum.hardware.DAC;
import ru.avem.posum.models.LTR34SettingsModel;

class LTR34ModuleSettings extends LTR34Settings {
    private ComboBox<String> signalTypeComboBox;
    private ComboBox<String> calibrationComboBox;
    private ComboBox<String> dacModeComboBox;
    private Button generateSignalButton;
    private LineChart<Number, Number> graph;
    private LTR34SettingsModel ltr34SettingsModel;
    private Button stopSignalButton;

    LTR34ModuleSettings(LTR34Settings ltr34Settings) {
        this.signalTypeComboBox = ltr34Settings.getSignalTypeComboBox();
        this.calibrationComboBox = ltr34Settings.getCalibrationComboBox();
        this.dacModeComboBox = ltr34Settings.getDacModeComboBox();
        this.generateSignalButton = ltr34Settings.getGenerateSignalButton();
        this.graph = ltr34Settings.getGraph();
        this.ltr34SettingsModel = ltr34Settings.getLtr34SettingsModel();
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

    void disableUiElementsState() {
        calibrationComboBox.setDisable(true);
        dacModeComboBox.setDisable(true);
        signalTypeComboBox.setDisable(true);
        generateSignalButton.setDisable(true);
    }

    void saveSettings() {
        int dacMode = dacModeComboBox.getSelectionModel().getSelectedIndex();
        int factoryCalibration = calibrationComboBox.getSelectionModel().getSelectedIndex();
        int signalType = signalTypeComboBox.getSelectionModel().getSelectedIndex();
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.DAC_MODE.getSettingName(), dacMode);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), factoryCalibration);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.SIGNAL_TYPE.getSettingName(), signalType);
    }

    void enableUiElements() {
        calibrationComboBox.setDisable(false);
        dacModeComboBox.setDisable(false);
        graph.setDisable(true);
        generateSignalButton.setDisable(false);
        signalTypeComboBox.setDisable(false);
        stopSignalButton.setDisable(true);
    }
}
