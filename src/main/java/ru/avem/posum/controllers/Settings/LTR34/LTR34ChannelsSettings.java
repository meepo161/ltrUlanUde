package ru.avem.posum.controllers.Settings.LTR34;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LTR34ChannelsSettings extends LTR34Settings {
    private List<TextField> amplitudesTextFields = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<TextField> dcTextFields = new ArrayList<>();
    private List<TextField> descriptionsTextFields = new ArrayList<>();
    private List<TextField> frequenciesTextFields = new ArrayList<>();
    private Button generateSignalButton;
    private LTR34SettingsModel ltr34SettingsModel;
    private List<TextField> phasesTextFields = new ArrayList<>();

    LTR34ChannelsSettings(LTR34Settings ltr34Settings) {
        this.generateSignalButton = ltr34Settings.getGenerateSignalButton();
        this.ltr34SettingsModel = ltr34Settings.getLtr34SettingsModel();

        fillListOfChannelsCheckBoxes(ltr34Settings);
        fillListOfChannelsAmplitudeTextFields(ltr34Settings);
        fillListOfChannelsDescription(ltr34Settings);
        fillListOfChannelsFrequencyTextFields(ltr34Settings);
        fillListOfChannelsPhases(ltr34Settings);
        fillListOfDcTextFields(ltr34Settings);
        listenCheckBoxes();
        setDigitFilter();
    }

    private void fillListOfChannelsCheckBoxes(LTR34Settings ltr34Settings) {
        checkBoxes.addAll(Arrays.asList(
                ltr34Settings.getCheckChannelN1(),
                ltr34Settings.getCheckChannelN2(),
                ltr34Settings.getCheckChannelN3(),
                ltr34Settings.getCheckChannelN4(),
                ltr34Settings.getCheckChannelN5(),
                ltr34Settings.getCheckChannelN6(),
                ltr34Settings.getCheckChannelN7(),
                ltr34Settings.getCheckChannelN8()
        ));
    }

    private void fillListOfChannelsDescription(LTR34Settings ltr34Settings) {
        descriptionsTextFields.addAll(Arrays.asList(
                ltr34Settings.getDescriptionOfChannelN1(),
                ltr34Settings.getDescriptionOfChannelN2(),
                ltr34Settings.getDescriptionOfChannelN3(),
                ltr34Settings.getDescriptionOfChannelN4(),
                ltr34Settings.getDescriptionOfChannelN5(),
                ltr34Settings.getDescriptionOfChannelN6(),
                ltr34Settings.getDescriptionOfChannelN7(),
                ltr34Settings.getDescriptionOfChannelN8()
        ));
    }

    private void fillListOfChannelsAmplitudeTextFields(LTR34Settings ltr34Settings) {
        amplitudesTextFields.addAll(Arrays.asList(
                ltr34Settings.getAmplitudeOfChannelN1(),
                ltr34Settings.getAmplitudeOfChannelN2(),
                ltr34Settings.getAmplitudeOfChannelN3(),
                ltr34Settings.getAmplitudeOfChannelN4(),
                ltr34Settings.getAmplitudeOfChannelN5(),
                ltr34Settings.getAmplitudeOfChannelN6(),
                ltr34Settings.getAmplitudeOfChannelN7(),
                ltr34Settings.getAmplitudeOfChannelN8()
        ));
    }

    private void fillListOfChannelsFrequencyTextFields(LTR34Settings ltr34Settings) {
        frequenciesTextFields.addAll(Arrays.asList(
                ltr34Settings.getFrequencyOfChannelN1(),
                ltr34Settings.getFrequencyOfChannelN2(),
                ltr34Settings.getFrequencyOfChannelN3(),
                ltr34Settings.getFrequencyOfChannelN4(),
                ltr34Settings.getFrequencyOfChannelN5(),
                ltr34Settings.getFrequencyOfChannelN6(),
                ltr34Settings.getFrequencyOfChannelN7(),
                ltr34Settings.getFrequencyOfChannelN8()
        ));
    }

    private void fillListOfChannelsPhases(LTR34Settings ltr34Settings) {
        phasesTextFields.addAll(Arrays.asList(
                ltr34Settings.getPhaseOfChannelN1(),
                ltr34Settings.getPhaseOfChannelN2(),
                ltr34Settings.getPhaseOfChannelN3(),
                ltr34Settings.getPhaseOfChannelN4(),
                ltr34Settings.getPhaseOfChannelN5(),
                ltr34Settings.getPhaseOfChannelN6(),
                ltr34Settings.getPhaseOfChannelN7(),
                ltr34Settings.getPhaseOfChannelN8()
        ));
    }

    private void fillListOfDcTextFields(LTR34Settings ltr34Settings) {
        dcTextFields.addAll(Arrays.asList(
                ltr34Settings.getDcOfChannelN1(),
                ltr34Settings.getDcOfChannelN2(),
                ltr34Settings.getDcOfChannelN3(),
                ltr34Settings.getDcOfChannelN4(),
                ltr34Settings.getDcOfChannelN5(),
                ltr34Settings.getDcOfChannelN6(),
                ltr34Settings.getDcOfChannelN7(),
                ltr34Settings.getDcOfChannelN8()
        ));
    }

    private void listenCheckBoxes() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            toggleUiElementsState(checkBoxes.get(channelIndex), channelIndex);
            listen(amplitudesTextFields, channelIndex);
            listen(dcTextFields, channelIndex);
            listen(frequenciesTextFields, channelIndex);
            listen(phasesTextFields, channelIndex);
        }
    }

    private void toggleUiElementsState(CheckBox checkBox, int channelNumber) {
        checkBox.selectedProperty().addListener(observable -> {
            if (!checkBox.isSelected()) {
                resetSettings(channelNumber);
            }
            toggleUiElementsState(channelNumber, !checkBox.isSelected());
            checkConditionForTurningOnTheGenerateButton();
            checkConditionForTurningOffTheGenerateButton();
        });
    }

    private void resetSettings(int channelNumber) {
        checkBoxes.get(channelNumber).setSelected(false);
        amplitudesTextFields.get(channelNumber).setText("");
        dcTextFields.get(channelNumber).setText("");
        descriptionsTextFields.get(channelNumber).setText("");
        frequenciesTextFields.get(channelNumber).setText("");
        phasesTextFields.get(channelNumber).setText("");
    }

    private void toggleUiElementsState(int channelNumber, boolean isDisable) {
        amplitudesTextFields.get(channelNumber).setDisable(isDisable);
        dcTextFields.get(channelNumber).setDisable(isDisable);
        descriptionsTextFields.get(channelNumber).setDisable(isDisable);
        frequenciesTextFields.get(channelNumber).setDisable(isDisable);
        phasesTextFields.get(channelNumber).setDisable(isDisable);
    }

    private void listen(List<TextField> textFields, int channelNumber) {
        textFields.get(channelNumber).textProperty().addListener(observable -> {
            checkConditionForTurningOnTheGenerateButton();
            checkConditionForTurningOffTheGenerateButton();
        });
    }

    private void checkConditionForTurningOnTheGenerateButton() {
        int disabledChannelsCounter = 0;

        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected() &
                    !amplitudesTextFields.get(channelIndex).getText().isEmpty() &
                    !dcTextFields.get(channelIndex).getText().isEmpty() &
                    !frequenciesTextFields.get(channelIndex).getText().isEmpty() &
                    !phasesTextFields.get(channelIndex).getText().isEmpty()) {
                generateSignalButton.setDisable(false);
            }

            if (!checkBoxes.get(channelIndex).isSelected()) {
                disabledChannelsCounter++;
            }
        }

        generateSignalButton.setDisable(disabledChannelsCounter == checkBoxes.size());
    }

    private void checkConditionForTurningOffTheGenerateButton() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected() &
                    (amplitudesTextFields.get(channelIndex).getText().isEmpty() ||
                            dcTextFields.get(channelIndex).getText().isEmpty() ||
                            frequenciesTextFields.get(channelIndex).getText().isEmpty() ||
                            phasesTextFields.get(channelIndex).getText().isEmpty())) {
                generateSignalButton.setDisable(true);
            }
        }
    }

    private void setDigitFilter() {
        for (TextField textField : amplitudesTextFields) {
            setAmplitudeFilter(textField);
        }

        for (TextField textField : frequenciesTextFields) {
            setFrequencyFilter(textField);
        }

        for (TextField textField : phasesTextFields) {
            setPhaseFilter(textField);
        }

        for (TextField textField : dcTextFields) {
            setDcFilter(textField);
        }
    }

    /**
     * Ввод только цифр 1-10 в текстовых полях "Амплитуда"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setAmplitudeFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^[1-9](\\.|,)\\d+|^[1-9](\\.|,)|^[1-9]|^10|^10(\\.|,)0|^[0](\\.|,)\\d+|^[0](\\.|,)|^[0]|$")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Ввод только цифр 1-50 в текстовых полях "Частота"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setFrequencyFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("(^[1-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|(50)|$)")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Ввод только цифр 0-360 в текстовых полях "Фаза"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setPhaseFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^(?:360|3[0-5]\\d|[12]\\d{2}|[1-9]\\d?)|0|$")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Ввод дробных цифр -10...10 в текстовых полях "Фаза"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setDcFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[1-9](\\.|,)\\d+|^-?[1-9](\\.|,)|^-?[1-9]|-|10|-10(\\.|,)0|^-?[0](\\.|,)\\d+|^-?[0](\\.|,)|^-?[0]|$")) {
                textField.setText(oldValue);
            }
        });
    }

    void setSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setSelected(ltr34SettingsModel.getCheckedChannels()[channelIndex]);
            amplitudesTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getAmplitudes()[channelIndex]));
            dcTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getDc()[channelIndex]));
            descriptionsTextFields.get(channelIndex).setText(ltr34SettingsModel.getDescriptions()[channelIndex]);
            frequenciesTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getFrequencies()[channelIndex]));

            replaceNul(checkBoxes.get(channelIndex), ltr34SettingsModel.getAmplitudes()[channelIndex], amplitudesTextFields.get(channelIndex));
            replaceNul(checkBoxes.get(channelIndex), ltr34SettingsModel.getPhases()[channelIndex], phasesTextFields.get(channelIndex));
            replaceNul(checkBoxes.get(channelIndex), ltr34SettingsModel.getDc()[channelIndex], dcTextFields.get(channelIndex));
        }
    }

    private void replaceNul(CheckBox channel, double value, TextField textField) {
        String textFieldName = textField.getId();
        int roundedValue = 0;

        if (!textFieldName.contains("dc") || !textFieldName.contains("amplitude")) {
            roundedValue = (int) value;
        }

        if (value != 0) {
            if (textFieldName.contains("amplitude") || textFieldName.contains("dc")) {
                textField.setText(String.valueOf(value));
            } else {
                textField.setText(String.valueOf(roundedValue));
            }
        } else if (channel.isSelected()) {
            textField.setText("0");
        } else {
            textField.setText("");
        }
    }

    void disableUiElementsState() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setDisable(true);
            amplitudesTextFields.get(channelIndex).setDisable(true);
            dcTextFields.get(channelIndex).setDisable(true);
            descriptionsTextFields.get(channelIndex).setDisable(true);
            frequenciesTextFields.get(channelIndex).setDisable(true);
            phasesTextFields.get(channelIndex).setDisable(true);
        }
    }

    void saveSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected()) {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = true; // true - канал выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = parseDouble(amplitudesTextFields.get(channelIndex));
                ltr34SettingsModel.getDc()[channelIndex] = parseDouble(dcTextFields.get(channelIndex));
                ltr34SettingsModel.getDescriptions()[channelIndex] = descriptionsTextFields.get(channelIndex).getText();
                ltr34SettingsModel.getFrequencies()[channelIndex] = parseInteger(frequenciesTextFields.get(channelIndex));
                ltr34SettingsModel.getPhases()[channelIndex] = parseInteger(phasesTextFields.get(channelIndex));
            } else {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = false; // false - канал не выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = 0;
                ltr34SettingsModel.getDc()[channelIndex] = 0;
                ltr34SettingsModel.getDescriptions()[channelIndex] = "";
                ltr34SettingsModel.getFrequencies()[channelIndex] = 0;
                ltr34SettingsModel.getPhases()[channelIndex] = 0;
            }
        }
    }

    private int parseInteger(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    private double parseDouble(TextField textField) {
        if (!textField.getText().isEmpty()) {
            String value = textField.getText().replace(",", ".");
            return Double.parseDouble(value);
        } else {
            return 0;
        }
    }

    void enableUiElements() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setDisable(false);
            amplitudesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            dcTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            descriptionsTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            frequenciesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            phasesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
        }
    }

    List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
}
