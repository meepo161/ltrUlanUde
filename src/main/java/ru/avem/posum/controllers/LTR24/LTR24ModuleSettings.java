package ru.avem.posum.controllers.LTR24;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.LTR24SettingsModel;

public class LTR24ModuleSettings extends LTR24Settings {
    private ComboBox<String> frequencyComboBox;
    private LTR24SettingsModel ltr24SettingsModel;

    public LTR24ModuleSettings(LTR24Settings ltr24Settings) {
        this.frequencyComboBox = ltr24Settings.getFrequencyComboBox();
        this.ltr24SettingsModel = ltr24Settings.getLtr24SettingsModel();
        addFrequencies();
    }

    private void addFrequencies() {
        ObservableList<String> frequencies = FXCollections.observableArrayList();

        frequencies.add("117.19 кГц");
        frequencies.add("78.13 кГц");
        frequencies.add("58.59 кГц");
        frequencies.add("39.06 кГц");
        frequencies.add("29.30 кГц");
        frequencies.add("19.53 кГц");
        frequencies.add("14.64 кГц");
        frequencies.add("9.77 кГц");
        frequencies.add("7.32 кГц");
        frequencies.add("4.88 кГц");
        frequencies.add("3.66 кГц");
        frequencies.add("2.44 кГц");
        frequencies.add("1.83 кГц");
        frequencies.add("1.22 кГц");
        frequencies.add("915.53 Гц");
        frequencies.add("610.35 Гц");

        frequencyComboBox.getItems().addAll(frequencies);
        frequencyComboBox.getSelectionModel().select(7);
    }

    public void setSettings() {
        int frequency = ltr24SettingsModel.getLTR24Instance().getModuleSettings().get(ADC.Settings.FREQUENCY.getSettingName());
        frequencyComboBox.getSelectionModel().select(frequency);
    }

    public void saveSettings() {
        int frequency = frequencyComboBox.getSelectionModel().getSelectedIndex();
        ltr24SettingsModel.getLTR24Instance().getModuleSettings().put(ADC.Settings.FREQUENCY.getSettingName(), frequency);
    }

    public void toggleUiElementsState(boolean isDisable) {
        frequencyComboBox.setDisable(isDisable);
    }
}
