package ru.avem.posum.controllers.settings.LTR27;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class LTR27SubmodulesSettings {
    private LTR27Settings ltr27Settings;
    private List<Label> channelOneLabels = new ArrayList<>();
    private List<Label> channelTwoLabels = new ArrayList<>();
    private List<TextField> channelOneTextFields = new ArrayList<>();
    private List<TextField> channelTwoTextFields = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private final String submoduleIsAbsentee = "Субмодуль отсутствует";

    public LTR27SubmodulesSettings(LTR27Settings ltr27Settings) {
        this.ltr27Settings = ltr27Settings;
    }

    public void initializeView() {
        fillUiElementsLists();
        setSubmodulesNames();
        setFrequencies();
    }

    private void fillUiElementsLists() {
        fillListOfCheckBoxes();
        fillListOfChannelOneLabels();
        fillListOfChannelTwoLabels();
        fillListOfChannelOneTextFields();
        fillListOfChannelTwoTextFields();
        listenCheckBoxes();
    }

    private void fillListOfCheckBoxes() {
        checkBoxes.add(ltr27Settings.getSubmoduleOneCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleTwoCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleThreeCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleFourCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleFiveCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleSixCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleSevenCheckBox());
        checkBoxes.add(ltr27Settings.getSubmoduleEightCheckBox());
    }

    private void fillListOfChannelOneLabels() {
        channelOneLabels.add(ltr27Settings.getSubModuleOneChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleTwoChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleThreeChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleFourChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleFiveChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleSixChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleSevenChannelOneLabel());
        channelOneLabels.add(ltr27Settings.getSubModuleEightChannelOneLabel());
    }

    private void fillListOfChannelTwoLabels() {
        channelTwoLabels.add(ltr27Settings.getSubModuleOneChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleTwoChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleThreeChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleFourChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleFiveChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleSixChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleSevenChannelTwoLabel());
        channelTwoLabels.add(ltr27Settings.getSubModuleEightChannelTwoLabel());
    }

    private void fillListOfChannelOneTextFields() {
        channelOneTextFields.add(ltr27Settings.getSubModuleOneChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleTwoChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleThreeChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleFourChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleFiveChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleSixChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleSevenChannelOneTextField());
        channelOneTextFields.add(ltr27Settings.getSubModuleEightChannelOneTextField());
    }

    private void fillListOfChannelTwoTextFields() {
        channelTwoTextFields.add(ltr27Settings.getSubModuleOneChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleTwoChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleThreeChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleFourChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleFiveChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleSixChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleSevenChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleEightChannelTwoTextField());
    }

    private void setSubmodulesNames() {
        String[][] descriptions = ltr27Settings.getSubmodulesDescriptions();

        for (int submoduleIndex = 0; submoduleIndex < descriptions.length; submoduleIndex++) {
            String description = String.format("Субмодуль %s (%s)", descriptions[submoduleIndex][0], descriptions[submoduleIndex][1]);
            description = description.contains("EMPTY ()") ? submoduleIsAbsentee : description;
            checkBoxes.get(submoduleIndex).setText(description);
        }
    }

    private void setFrequencies() {
        ObservableList<String> frequencies = FXCollections.observableArrayList();
        final double MAX_FREQUENCY = 1000;
        for (int i = 0; i < 256; i++) {
            String frequency = String.format("%.2f Гц", MAX_FREQUENCY / (i + 1));
            frequencies.add(frequency);
        }
        ltr27Settings.getFrequencyComboBox().getItems().addAll(frequencies);
        ltr27Settings.getFrequencyComboBox().getSelectionModel().select(0);
    }

    private void listenCheckBoxes() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            CheckBox checkBox = checkBoxes.get(channelIndex);
            int finalChannelIndex = channelIndex;
            checkBox.selectedProperty().addListener(observable -> {
                toggleSubmoduleUiElements(finalChannelIndex, !checkBox.isSelected());
            });
        }
    }

    public void toggleCheckBoxes(boolean isDisable) {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals(submoduleIsAbsentee)) {
                checkBox.setDisable(isDisable);
            }
        }
    }

    private void toggleSubmoduleUiElements(int channelIndex, boolean isDisable) {
        channelOneLabels.get(channelIndex).setDisable(isDisable);
        channelOneTextFields.get(channelIndex).setDisable(isDisable);
        channelTwoLabels.get(channelIndex).setDisable(isDisable);
        channelTwoTextFields.get(channelIndex).setDisable(isDisable);
    }
}
