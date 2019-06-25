package ru.avem.posum.controllers.settings.LTR27;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LTR27SubmodulesSettings {
    private LTR27Settings ltr27Settings;
    private List<Label> channelOneLabels;
    private List<Label> channelTwoLabels;
    private List<TextField> channelOneTextFields;
    private List<TextField> channelTwoTextFields;
    private List<CheckBox> checkBoxes;
    private int rarefactionCoefficient = 2;
    private final String submoduleIsAbsentee = "Субмодуль отсутствует";

    private int average = 1;
    private int averageCount;
    private double[] bufferedData;

    public LTR27SubmodulesSettings(LTR27Settings ltr27Settings) {
        this.ltr27Settings = ltr27Settings;
    }

    public void initializeView() {
        fillUiElementsLists();
        setFrequencies();
        setRarefactionCoefficients();
        listenCheckBoxes();
        listen(ltr27Settings.getRarefactionComboBox());
        listen(ltr27Settings.getAverageTextField());
    }

    private void fillUiElementsLists() {
        fillListOfCheckBoxes();
        fillListOfChannelOneLabels();
        fillListOfChannelTwoLabels();
        fillListOfChannelOneTextFields();
        fillListOfChannelTwoTextFields();
    }

    private void fillListOfCheckBoxes() {
        checkBoxes = new ArrayList<>();
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
        channelOneLabels = new ArrayList<>();
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
        channelTwoLabels = new ArrayList<>();
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
        channelOneTextFields = new ArrayList<>();
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
        channelTwoTextFields = new ArrayList<>();
        channelTwoTextFields.add(ltr27Settings.getSubModuleOneChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleTwoChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleThreeChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleFourChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleFiveChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleSixChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleSevenChannelTwoTextField());
        channelTwoTextFields.add(ltr27Settings.getSubModuleEightChannelTwoTextField());
    }

    private void listenCheckBoxes() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            CheckBox checkBox = checkBoxes.get(channelIndex);
            int finalChannelIndex = channelIndex;
            checkBox.selectedProperty().addListener(observable -> {
                toggleSubmoduleUiElements(finalChannelIndex, !checkBox.isSelected());
                toggleSettingsUiElements();
            });
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

    private void setRarefactionCoefficients() {
        ObservableList<String> coefficients = FXCollections.observableArrayList();
        for (int i = 1; i < 8; i++) {
            coefficients.add(String.valueOf(i));
        }
        ltr27Settings.getRarefactionComboBox().setItems(coefficients);
        ltr27Settings.getRarefactionComboBox().getSelectionModel().select(1);
    }

    private void listen(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            rarefactionCoefficient = Integer.parseInt(comboBox.getSelectionModel().getSelectedItem());
        });
    }

    private void listen(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digits = newValue.replaceAll("[^\\d]", "");
            textField.setText(digits);

            if (!newValue.matches("^[\\d]+|$")) {
                textField.setText(oldValue);
            }

            if (!textField.getText().isEmpty()) {
                average = Integer.parseInt(textField.getText());
            }
        });
    }

    public void setSubmodulesNames() {
        String[][] descriptions = ltr27Settings.getSubmodulesDescriptions();

        for (int submoduleIndex = 0; submoduleIndex < descriptions.length; submoduleIndex++) {
            String description = String.format("Субмодуль %s (%s)", descriptions[submoduleIndex][0], descriptions[submoduleIndex][1]);
            description = description.contains("EMPTY ()") ? submoduleIsAbsentee : description;
            checkBoxes.get(submoduleIndex).setText(description);
        }
    }

    public void setSubmodulesUnits() {
        String[][] descriptions = ltr27Settings.getSubmodulesDescriptions();

        for (int submoduleIndex = 0; submoduleIndex < checkBoxes.size(); submoduleIndex++) {
            if (!checkBoxes.get(submoduleIndex).getText().equals(submoduleIsAbsentee)) {
                createNewLabel(channelOneLabels.get(submoduleIndex), descriptions[submoduleIndex][2]);
                createNewLabel(channelTwoLabels.get(submoduleIndex), descriptions[submoduleIndex][2]);
            }
        }
    }

    private void createNewLabel(Label label, String unit) {
        String oldText = label.getText();
        if (!label.getText().contains(unit)) {
            label.setText(String.format("%s, %s:", oldText.substring(0, oldText.length() - 1), unit));
        }
    }

    public void toggleCheckBoxesState(boolean isDisable) {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals(submoduleIsAbsentee)) {
                checkBox.setDisable(isDisable);
            }
        }
    }

    public void toggleCheckBoxes(boolean isSelected) {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals(submoduleIsAbsentee)) {
                checkBox.setSelected(isSelected);
            }
        }
    }

    private void toggleSubmoduleUiElements(int channelIndex, boolean isDisable) {
        channelOneLabels.get(channelIndex).setDisable(isDisable);
        channelOneTextFields.get(channelIndex).setDisable(isDisable);
        channelTwoLabels.get(channelIndex).setDisable(isDisable);
        channelTwoTextFields.get(channelIndex).setDisable(isDisable);

        if (isDisable) {
            clearTextFields(channelIndex);
        }
    }

    private void toggleSettingsUiElements() {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                toggle(false);
                return;
            }
        }
        toggle(true);
    }

    private void toggle(boolean isDisable) {
        ltr27Settings.getAverageLabel().setDisable(isDisable);
        ltr27Settings.getAverageTextField().setDisable(isDisable);
        ltr27Settings.getRarefactionLabel().setDisable(isDisable);
        ltr27Settings.getRarefactionComboBox().setDisable(isDisable);
    }

    private void clearTextFields(int channelIndex) {
        channelOneTextFields.get(channelIndex).setText("");
        channelTwoTextFields.get(channelIndex).setText("");
    }

    public void showValues() {
        new Thread(() -> {
            double[] data = ltr27Settings.getData();
            bufferedData = new double[data.length];

            while (!ltr27Settings.isStopped()) {
                int channelIndex = 0;

                for (int submodelIndex = 0; submodelIndex < LTR27.MAX_SUBMODULES; submodelIndex++) {
                    if (!checkBoxes.get(submodelIndex).getText().equals(submoduleIsAbsentee) && checkBoxes.get(submodelIndex).isSelected()) {
                        int finalSubmodelIndex = submodelIndex;
                        int finalChannelIndex = channelIndex;

                        if (average == 1) {
                            Platform.runLater(() -> {
                                String channelOneValue = String.valueOf(Utils.roundValue(data[finalChannelIndex], Utils.getRounder(rarefactionCoefficient)));
                                String channelTwoValue = String.valueOf(Utils.roundValue(data[finalChannelIndex + 1], Utils.getRounder(rarefactionCoefficient)));
                                channelOneTextFields.get(finalSubmodelIndex).setText(channelOneValue);
                                channelTwoTextFields.get(finalSubmodelIndex).setText(channelTwoValue);
                            });
                        } else if (averageCount < average) {
                            bufferedData[finalChannelIndex] += data[finalChannelIndex];
                            bufferedData[finalChannelIndex + 1] += data[finalChannelIndex + 1];
                        } else {
                                double channelOneAverageValue = bufferedData[finalChannelIndex] / (double) average;
                                double channelTwoAverageValue = bufferedData[finalChannelIndex + 1] / (double) average;
                            Platform.runLater(() -> {
                                String channelOneValue = String.valueOf(Utils.roundValue(channelOneAverageValue, Utils.getRounder(rarefactionCoefficient)));
                                String channelTwoValue = String.valueOf(Utils.roundValue(channelTwoAverageValue, Utils.getRounder(rarefactionCoefficient)));
                                channelOneTextFields.get(finalSubmodelIndex).setText(channelOneValue);
                                channelTwoTextFields.get(finalSubmodelIndex).setText(channelTwoValue);
                            });
                        }
                    }
                    channelIndex += 2;
                }

                if (averageCount < average) {
                    averageCount++;
                } else {
                    averageCount = 0;
                    for (int i = 0; i < bufferedData.length; i++) {
                        bufferedData[i] = 0;
                    }
                }
                Utils.sleep(200);
            }
        }).start();
    }

    public void enableAll() {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals(submoduleIsAbsentee)) {
                checkBox.setSelected(true);
            }
        }
    }
}
