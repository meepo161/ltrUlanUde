package ru.avem.posum.controllers.Settings.LTR212;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Settings.LTR212SettingsModel;

import java.util.HashMap;

class LTR212ModuleSettings extends LTR212Settings {
    private CheckBox factoryCalibration;
    private CheckBox firCheckBox;
    private Button firPathButton;
    private TextField firPathTextField;
    private CheckBox iirCheckBox;
    private Button iirPathButton;
    private TextField iirPathTextField;
    private LTR212SettingsModel ltr212SettingsModel;
    private ComboBox<String> adcModes;
    private CheckBox referenceVoltageCheckBox;
    private ComboBox<String> referenceVoltageComboBox;

    LTR212ModuleSettings(LTR212Settings ltr212Settings) {
        this.ltr212SettingsModel = ltr212Settings.getLtr212SettingsModel();
        this.adcModes = ltr212Settings.getModuleModesComboBox();
        this.factoryCalibration = ltr212Settings.getFactoryCalibrationCheckBox();
        this.firCheckBox = ltr212Settings.getFirCheckBox();
        this.firPathButton = ltr212Settings.getFirPathButton();
        this.firPathTextField = ltr212Settings.getFirPathTextField();
        this.iirCheckBox = ltr212Settings.getIirCheckBox();
        this.iirPathButton = ltr212Settings.getIirPathButton();
        this.iirPathTextField = ltr212Settings.getIirPathTextField();
        this.referenceVoltageCheckBox = ltr212Settings.getReferenceVoltageTypeCheckBox();
        this.referenceVoltageComboBox = ltr212Settings.getReferenceVoltageComboBox();
        addModuleModes();
        addReferenceVoltages();
        addListener(firCheckBox, firPathTextField, firPathButton);
        addListener(iirCheckBox, iirPathTextField, iirPathButton);
    }

    private void addModuleModes() {
        ObservableList<String> modes = FXCollections.observableArrayList();

        modes.add("Средней точности");
        modes.add("Высокой точности");

        adcModes.getItems().addAll(modes);
        adcModes.getSelectionModel().select(0);
    }

    private void addReferenceVoltages() {
        ObservableList<String> modes = FXCollections.observableArrayList();

        modes.add("2.5 В");
        modes.add("5.0 В");

        referenceVoltageComboBox.getItems().addAll(modes);
        referenceVoltageComboBox.getSelectionModel().select(1);
    }

    private void addListener(CheckBox filterCheckBox, TextField filterTextField, Button filterButton) {
        filterCheckBox.selectedProperty().addListener(observable -> {
            if (filterCheckBox.isSelected()) {
                filterTextField.setDisable(false);
                filterButton.setDisable(false);
            } else {
                filterTextField.setDisable(true);
                filterButton.setDisable(true);
            }
        });
    }

    void setSettings() {
        int adcMode = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.ADC_MODE.getSettingName());
        int factoryCalibration = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName());
        int fir = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.FIR.getSettingName());
        int iir = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.IIR.getSettingName());
        int referenceVoltage = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.REFERENCE_VOLTAGE.getSettingName());
        int referenceVoltageType = ltr212SettingsModel.getLTR212Instance().getModuleSettings().get(ADC.Settings.REFERENCE_VOLTAGE_TYPE.getSettingName());

        adcModes.getSelectionModel().select(adcMode);
        this.factoryCalibration.setSelected(factoryCalibration == 1);
        firCheckBox.setSelected(fir == 1);
        iirCheckBox.setSelected(iir == 1);
        this.referenceVoltageCheckBox.setSelected(referenceVoltageType == 1);
        this.referenceVoltageComboBox.getSelectionModel().select(referenceVoltage);
    }

    void saveSettings() {
        HashMap<String, Integer> settings = ltr212SettingsModel.getLTR212Instance().getModuleSettings();
        int adcMode = adcModes.getSelectionModel().getSelectedIndex();
        int fir = firCheckBox.isSelected() ? 1 : 0;
        int iir = iirCheckBox.isSelected() ? 1 : 0;
        int factoryCalibration = this.factoryCalibration.isSelected() ? 1 : 0;
        int referenceVoltage = referenceVoltageComboBox.getSelectionModel().getSelectedIndex();
        int referenceVoltageType = referenceVoltageCheckBox.isSelected() ? 1 : 0;

        settings.put(ADC.Settings.ADC_MODE.getSettingName(), adcMode);
        settings.put(ADC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), factoryCalibration);
        settings.put(ADC.Settings.FIR.getSettingName(), fir);
        settings.put(ADC.Settings.IIR.getSettingName(), iir);
        settings.put(ADC.Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), referenceVoltageType);
        settings.put(ADC.Settings.REFERENCE_VOLTAGE.getSettingName(), referenceVoltage);

        ltr212SettingsModel.getLTR212Instance().setFirFilePath(firPathTextField.getText().replace("\\", "/"));
        ltr212SettingsModel.getLTR212Instance().setIirFilePath(iirPathTextField.getText().replace("\\", "/"));
    }

    void toggleUiElementsState(boolean isDisable) {
        adcModes.setDisable(isDisable);
        factoryCalibration.setDisable(isDisable);
        firPathTextField.setDisable(isDisable);
        firPathButton.setDisable(isDisable);
        firCheckBox.setDisable(isDisable);
        iirCheckBox.setDisable(isDisable);
        iirPathTextField.setDisable(isDisable);
        iirPathButton.setDisable(isDisable);
        referenceVoltageCheckBox.setDisable(isDisable);
        referenceVoltageComboBox.setDisable(isDisable);
    }

    void enableUiElements() {
        toggleUiElementsState(false);
        iirPathTextField.setDisable(!iirCheckBox.isSelected());
        iirPathButton.setDisable(!iirCheckBox.isSelected());
        firPathTextField.setDisable(!firCheckBox.isSelected());
        firPathButton.setDisable(!firCheckBox.isSelected());
    }
}
