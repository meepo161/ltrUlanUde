package ru.avem.posum.controllers.LTR34;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LTR34ChannelsSettings extends LTR34Settings {
    private List<TextField> amplitudesTextFields = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<TextField> descriptionsTextFields = new ArrayList<>();
    private List<TextField> frequenciesTextFields = new ArrayList<>();
    private List<TextField> phasesTextFields = new ArrayList<>();

    LTR34ChannelsSettings() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsAmplitudeTextFields();
        fillListOfChannelsDescription();
        fillListOfChannelsFrequencyTextFields();
        fillListOfChannelsPhases();
        listenCheckBoxes();
        setDigitFilter();
    }

    private void fillListOfChannelsCheckBoxes() {
        checkBoxes.addAll(Arrays.asList(
                checkChannelN1,
                checkChannelN2,
                checkChannelN3,
                checkChannelN4,
                checkChannelN5,
                checkChannelN6,
                checkChannelN7,
                checkChannelN8
        ));
    }

    private void fillListOfChannelsDescription() {
        descriptionsTextFields.addAll(Arrays.asList(
                descriptionOfChannelN1,
                descriptionOfChannelN2,
                descriptionOfChannelN3,
                descriptionOfChannelN4,
                descriptionOfChannelN5,
                descriptionOfChannelN6,
                descriptionOfChannelN7,
                descriptionOfChannelN8
        ));
    }

    private void fillListOfChannelsAmplitudeTextFields() {
        amplitudesTextFields.addAll(Arrays.asList(
                amplitudeOfChannelN1,
                amplitudeOfChannelN2,
                amplitudeOfChannelN3,
                amplitudeOfChannelN4,
                amplitudeOfChannelN5,
                amplitudeOfChannelN6,
                amplitudeOfChannelN7,
                amplitudeOfChannelN8
        ));
    }

    private void fillListOfChannelsFrequencyTextFields() {
        frequenciesTextFields.addAll(Arrays.asList(
                frequencyOfChannelN1,
                frequencyOfChannelN2,
                frequencyOfChannelN3,
                frequencyOfChannelN4,
                frequencyOfChannelN5,
                frequencyOfChannelN6,
                frequencyOfChannelN7,
                frequencyOfChannelN8
        ));
    }

    private void fillListOfChannelsPhases() {
        phasesTextFields.addAll(Arrays.asList(
                phaseOfChannelN1,
                phaseOfChannelN2,
                phaseOfChannelN3,
                phaseOfChannelN4,
                phaseOfChannelN5,
                phaseOfChannelN6,
                phaseOfChannelN7,
                phaseOfChannelN8
        ));
    }


    private void listenCheckBoxes() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            toggleUiElementsState(checkBoxes.get(channelIndex), channelIndex);
            listen(amplitudesTextFields, channelIndex);
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
        descriptionsTextFields.get(channelNumber).setText("");
        frequenciesTextFields.get(channelNumber).setText("");
        phasesTextFields.get(channelNumber).setText("");
    }

    private void toggleUiElementsState(int channelNumber, boolean isDisable) {
        amplitudesTextFields.get(channelNumber).setDisable(isDisable);
        descriptionsTextFields.get(channelNumber).setDisable(isDisable);
        frequenciesTextFields.get(channelNumber).setDisable(isDisable);
        phasesTextFields.get(channelNumber).setDisable(isDisable);
    }

    private void checkConditionForTurningOnTheGenerateButton() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected() &
                    !amplitudesTextFields.get(channelIndex).getText().isEmpty() &
                    !frequenciesTextFields.get(channelIndex).getText().isEmpty() &
                    !phasesTextFields.get(channelIndex).getText().isEmpty()) {
                generateSignalButton.setDisable(false);
            }
        }
    }

    private void checkConditionForTurningOffTheGenerateButton() {
        int disabledChannelsCounter = 0;
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected() &
                    (amplitudesTextFields.get(channelIndex).getText().isEmpty() ||
                    frequenciesTextFields.get(channelIndex).getText().isEmpty() ||
                    phasesTextFields.get(channelIndex).getText().isEmpty())) {
                generateSignalButton.setDisable(true);
            }

            if (!checkBoxes.get(channelIndex).isSelected()) {
                disabledChannelsCounter++;
            }
        }
        generateSignalButton.setDisable(disabledChannelsCounter == checkBoxes.size());
    }

    private void listen(List<TextField> textFields, int channelNumber) {
        textFields.get(channelNumber).textProperty().addListener(observable -> {
            checkConditionForTurningOnTheGenerateButton();
            checkConditionForTurningOffTheGenerateButton();
        });
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
    }

    /**
     * Ввод только цифр 1-10 в текстовых полях "Амплитуда"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setAmplitudeFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^[1-9]|(10)|$")) {
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

    void setSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setSelected(ltr34SettingsModel.getCheckedChannels()[channelIndex]);
            amplitudesTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getAmplitudes()[channelIndex]));
            descriptionsTextFields.get(channelIndex).setText(ltr34SettingsModel.getDescriptions()[channelIndex]);
            frequenciesTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getFrequencies()[channelIndex]));

            if (ltr34SettingsModel.getPhases()[channelIndex] != 0) {
                phasesTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getPhases()[channelIndex]));
            } else if (checkBoxes.get(channelIndex).isSelected()) {
                phasesTextFields.get(channelIndex).setText("0");
            } else {
                phasesTextFields.get(channelIndex).setText("");
            }
        }
    }


    void disableUiElementsState() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setDisable(true);
            amplitudesTextFields.get(channelIndex).setDisable(true);
            descriptionsTextFields.get(channelIndex).setDisable(true);
            frequenciesTextFields.get(channelIndex).setDisable(true);
            phasesTextFields.get(channelIndex).setDisable(true);
        }
    }

    void saveSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            if (checkBoxes.get(channelIndex).isSelected()) {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = true; // true - канал выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = parse(amplitudesTextFields.get(channelIndex));
                ltr34SettingsModel.getDescriptions()[channelIndex] = descriptionsTextFields.get(channelIndex).getText();
                ltr34SettingsModel.getFrequencies()[channelIndex] = parse(frequenciesTextFields.get(channelIndex));
                ltr34SettingsModel.getPhases()[channelIndex] = parse(phasesTextFields.get(channelIndex));
            } else {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = false; // false - канал не выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = 0;
                ltr34SettingsModel.getDescriptions()[channelIndex] = "";
                ltr34SettingsModel.getFrequencies()[channelIndex] = 0;
                ltr34SettingsModel.getPhases()[channelIndex] = 0;
            }
        }
    }

    private int parse(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    void enableUiElements() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            checkBoxes.get(channelIndex).setDisable(false);
            amplitudesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            descriptionsTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            frequenciesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
            phasesTextFields.get(channelIndex).setDisable(!checkBoxes.get(channelIndex).isSelected());
        }
    }

    List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
}
