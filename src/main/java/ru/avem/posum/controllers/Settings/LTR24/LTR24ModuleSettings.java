package ru.avem.posum.controllers.Settings.LTR24;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Settings.LTR24SettingsModel;

class LTR24ModuleSettings extends LTR24Settings {
    private ComboBox<String> frequencyComboBox;
    private LTR24SettingsModel ltr24SettingsModel;

    LTR24ModuleSettings(LTR24Settings ltr24Settings) {
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

    void setSettings() {
        int frequency = ltr24SettingsModel.getLTR24Instance().getSettingsOfModule().get(ADC.Settings.FREQUENCY);
        frequencyComboBox.getSelectionModel().select(frequency);
    }

    void saveSettings() {
        int frequency = frequencyComboBox.getSelectionModel().getSelectedIndex();
        ltr24SettingsModel.getLTR24Instance().getSettingsOfModule().put(ADC.Settings.FREQUENCY, frequency);

        int dataLength = (int) ltr24SettingsModel.getLTR24Instance().getFrequency();

        if (dataLength == 0) {
            switch (frequency) {
                case 0:
                    dataLength = 117188;
                    break;
                case 1:
                    dataLength = 78126;
                    break;
                case 2:
                    dataLength = 58594;
                    break;
                case 3:
                    dataLength = 39063;
                    break;
                case 4:
                    dataLength = 29297;
                    break;
                case 5:
                    dataLength = 19532;
                    break;
                case 6:
                    dataLength = 14649;
                    break;
                case 7:
                    dataLength = 9766;
                    break;
                case 8:
                    dataLength = 7325;
                    break;
                case 9:
                    dataLength = 4883;
                    break;
                case 10:
                    dataLength = 3663;
                    break;
                case 11:
                    dataLength = 2442;
                    break;
                case 12:
                    dataLength = 1832;
                    break;
                case 13:
                    dataLength = 1221;
                    break;
                case 14:
                    dataLength = 916;
                    break;
                case 15:
                    dataLength = 611;
                    break;
            }
        }

        ltr24SettingsModel.getLTR24Instance().setData(new double[dataLength * ltr24SettingsModel.getLTR24Instance().getChannelsCount()]);
    }

    void toggleUiElementsState(boolean isDisable) {
        frequencyComboBox.setDisable(isDisable);
    }
}
