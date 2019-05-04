package ru.avem.posum.controllers.Settings.LTR212;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.avem.posum.models.Settings.LTR212SettingsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LTR212ChannelsSettings extends LTR212Settings {
    private CheckBox applyForAllChannels;
    private List<ComboBox<String>> typesOfChannelComboBoxes = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<TextField> descriptions = new ArrayList<>();
    private Button initializeButton;
    private LTR212SettingsModel ltr212SettingsModel;
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private List<Button> valueOnChannelButtons = new ArrayList<>();

    LTR212ChannelsSettings(LTR212Settings ltr212Settings) {
        this.applyForAllChannels = ltr212Settings.getApplyForAllChannels();
        this.initializeButton = ltr212Settings.getInitializeButton();
        this.ltr212SettingsModel = ltr212Settings.getLtr212SettingsModel();
        fillListOfCheckBoxes(ltr212Settings);
        fillListOfDescriptionsTextFields(ltr212Settings);
        fillListOfChannelsTypesComboBoxes(ltr212Settings);
        fillListOfMeasuringRangesComboBoxes(ltr212Settings);
        fillListOfValueOnChannelButtons(ltr212Settings);
        addTypesOfChannels();
        addMeasuringRanges();
        listenCheckBoxes();
        listenComboBoxes(typesOfChannelComboBoxes);
        listenComboBoxes(measuringRangesComboBoxes);
    }

    private void fillListOfCheckBoxes(LTR212Settings ltr212Settings) {
        checkBoxes.addAll(Arrays.asList(
                ltr212Settings.getCheckChannelN1(),
                ltr212Settings.getCheckChannelN2(),
                ltr212Settings.getCheckChannelN3(),
                ltr212Settings.getCheckChannelN4()
        ));
    }

    private void fillListOfDescriptionsTextFields(LTR212Settings ltr212Settings) {
        descriptions.addAll(Arrays.asList(
                ltr212Settings.getDescriptionOfChannelN1(),
                ltr212Settings.getDescriptionOfChannelN2(),
                ltr212Settings.getDescriptionOfChannelN3(),
                ltr212Settings.getDescriptionOfChannelN4()
        ));
    }

    private void fillListOfChannelsTypesComboBoxes(LTR212Settings ltr212Settings) {
        typesOfChannelComboBoxes.addAll(Arrays.asList(
                ltr212Settings.getTypeOfChannelN1(),
                ltr212Settings.getTypeOfChannelN2(),
                ltr212Settings.getTypeOfChannelN3(),
                ltr212Settings.getTypeOfChannelN4()
        ));
    }

    private void fillListOfMeasuringRangesComboBoxes(LTR212Settings ltr212Settings) {
        measuringRangesComboBoxes.addAll(Arrays.asList(
                ltr212Settings.getMeasuringRangeOfChannelN1(),
                ltr212Settings.getMeasuringRangeOfChannelN2(),
                ltr212Settings.getMeasuringRangeOfChannelN3(),
                ltr212Settings.getMeasuringRangeOfChannelN4()
        ));
    }

    private void fillListOfValueOnChannelButtons(LTR212Settings ltr212Settings) {
        valueOnChannelButtons.addAll(Arrays.asList(
                ltr212Settings.getValueOnChannelN1(),
                ltr212Settings.getValueOnChannelN2(),
                ltr212Settings.getValueOnChannelN3(),
                ltr212Settings.getValueOnChannelN4()
        ));
    }

    private void addTypesOfChannels() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("Полно- или полу-мостовая схема");
        types.add("Четверть-мостовая схема (200 Ом)");
        types.add("Четверть-мостовая схема (350 Ом)");
        types.add("Четверть-мостовая схема (внешний резистор)");
        types.add("Четверть-мостовая схема с нормирующим разбалансом (200 Ом)");
        types.add("Четверть-мостовая схема с нормирующим разбалансом (350 Ом)");
        types.add("Четверть-мостовая схема с нормирующим разбалансом (внешний резистор)");

        setComboBox(typesOfChannelComboBoxes, types);
    }

    private void setComboBox(List<ComboBox<String>> comboBoxes, ObservableList<String> strings) {
        for (ComboBox<String> comboBox : comboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    private void addMeasuringRanges() {
        ObservableList<String> ranges = FXCollections.observableArrayList();

        ranges.add("-10 мВ/+10 мВ");
        ranges.add("-20 мВ/+20 мВ");
        ranges.add("-40 мВ/+40 мВ");
        ranges.add("-80 мВ/+80 мВ");
        ranges.add("0 мВ/+10 мВ");
        ranges.add("0 мВ/+20 мВ");
        ranges.add("0 мВ/+40 мВ");
        ranges.add("0 мВ/+80 мВ");

        setComboBox(measuringRangesComboBoxes, ranges);
    }

    private void listenCheckBoxes() {
        for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.size(); checkBoxIndex++) {
            toggleUiElementsState(checkBoxIndex);
        }
    }

    private void toggleUiElementsState(int checkBoxIndex) {
        checkBoxes.get(checkBoxIndex).selectedProperty().addListener(observable -> {
            selectCheckBoxes(checkBoxes.get(checkBoxIndex).isSelected());
            changeUiElementsState(checkBoxIndex, !checkBoxes.get(checkBoxIndex).isSelected());
            resetChannelSettings(checkBoxIndex);
        });
    }

    private void selectCheckBoxes(boolean isCheckBoxSelected) {
        if (applyForAllChannels.isSelected()) {
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setSelected(isCheckBoxSelected);
            }
        }
    }

    private void changeUiElementsState(int channelIndex, boolean isDisable) {
        typesOfChannelComboBoxes.get(channelIndex).setDisable(isDisable);
        measuringRangesComboBoxes.get(channelIndex).setDisable(isDisable);
        descriptions.get(channelIndex).setDisable(isDisable);

        int disabledCheckBoxesCounter = 0;
        for (CheckBox channelCheckBox : checkBoxes) {
            if (!channelCheckBox.isSelected()) {
                disabledCheckBoxesCounter++;
            }
        }

        initializeButton.setDisable(disabledCheckBoxesCounter == checkBoxes.size());
    }

    private void resetChannelSettings(int channelIndex) {
        if (!checkBoxes.get(channelIndex).isSelected()) {
            descriptions.get(channelIndex).setText("");
            typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(0);
            measuringRangesComboBoxes.get(channelIndex).getSelectionModel().select(3);
        }
    }

    private void listenComboBoxes(List<ComboBox<String>> comboBoxes) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.valueProperty().addListener(observable -> {
                if (applyForAllChannels.isSelected()) {
                    selectItem(comboBoxes, comboBox.getSelectionModel().getSelectedIndex());
                }
            });
        }
    }

    private void selectItem(List<ComboBox<String>> comboBoxes, int itemIndex) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.getSelectionModel().select(itemIndex);
        }
    }

    void setSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            boolean isCheckBoxSelected = ltr212SettingsModel.getCheckedChannels()[channelIndex];
            int typeOfChannel = ltr212SettingsModel.getTypesOfChannels()[channelIndex];
            int measuringRange = ltr212SettingsModel.getMeasuringRanges()[channelIndex];
            String description = ltr212SettingsModel.getDescriptions()[channelIndex].replace(", ", "");

            checkBoxes.get(channelIndex).setSelected(isCheckBoxSelected);
            typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(typeOfChannel);
            measuringRangesComboBoxes.get(channelIndex).getSelectionModel().select(measuringRange);
            descriptions.get(channelIndex).setText(description);
        }
    }

    void saveSettings() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                ltr212SettingsModel.getCheckedChannels()[i] = true; // true - канал выбран
                ltr212SettingsModel.getDescriptions()[i] = descriptions.get(i).getText() + ", ";
                ltr212SettingsModel.getTypesOfChannels()[i] = typesOfChannelComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr212SettingsModel.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
            } else {
                ltr212SettingsModel.getCheckedChannels()[i] = false; // false - канал не выбран
                ltr212SettingsModel.getDescriptions()[i] = ", ";
                ltr212SettingsModel.getTypesOfChannels()[i] = 0;
                ltr212SettingsModel.getMeasuringRanges()[i] = 3;
            }
        }
    }

    void disableUiElements() {
        for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.size(); checkBoxIndex++) {
            checkBoxes.get(checkBoxIndex).setDisable(true);
            descriptions.get(checkBoxIndex).setDisable(true);
            typesOfChannelComboBoxes.get(checkBoxIndex).setDisable(true);
            measuringRangesComboBoxes.get(checkBoxIndex).setDisable(true);
            valueOnChannelButtons.get(checkBoxIndex).setDisable(!checkBoxes.get(checkBoxIndex).isSelected());
        }
        applyForAllChannels.setDisable(true);
        initializeButton.setDisable(true);
    }

    void enableUiElements() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            CheckBox checkBox = checkBoxes.get(channelIndex);
            checkBox.setDisable(false);
            descriptions.get(channelIndex).setDisable(!checkBox.isSelected());
            typesOfChannelComboBoxes.get(channelIndex).setDisable(!checkBox.isSelected());
            measuringRangesComboBoxes.get(channelIndex).setDisable(!checkBox.isSelected());
            valueOnChannelButtons.get(channelIndex).setDisable(true);
        }
        applyForAllChannels.setDisable(false);
        initializeButton.setDisable(false);
    }

    void disableValueOnChannelButtonsState() {
        for (Button valueOnChannelButton : valueOnChannelButtons) {
            valueOnChannelButton.setDisable(true);
        }
    }

    void enableValueOnChannelButtonsState() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            valueOnChannelButtons.get(i).setDisable(!checkBoxes.get(i).isSelected());
        }
    }
}
