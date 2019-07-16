package ru.avem.posum.controllers.settings.LTR27;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import ru.avem.posum.controllers.calibration.LTR27CalibrationManager;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.utils.NewUtils;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LTR27SubmodulesSettings {
    private LTR27SettingsController ltr27SettingsController;
    private List<Button> calibrationButtons;
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
    private LTR27CalibrationManager lcm;

    public LTR27SubmodulesSettings(LTR27SettingsController ltr27SettingsController) {
        this.ltr27SettingsController = ltr27SettingsController;
    }

    public void initializeView() {
        fillUiElementsLists();
        setFrequencies();
        setRarefactionCoefficients();
        listenCheckBoxes();
        listen(ltr27SettingsController.getRarefactionComboBox());
        listen(ltr27SettingsController.getAverageTextField());
    }

    private void fillUiElementsLists() {
        fillListOfCheckBoxes();
        fillListOfChannelOneLabels();
        fillListOfChannelTwoLabels();
        fillListOfChannelOneTextFields();
        fillListOfChannelTwoTextFields();
        fillListOfCalibrationButtons();
    }

    private void fillListOfCheckBoxes() {
        checkBoxes = new ArrayList<>();
        checkBoxes.add(ltr27SettingsController.getSubmoduleOneCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleTwoCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleThreeCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleFourCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleFiveCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleSixCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleSevenCheckBox());
        checkBoxes.add(ltr27SettingsController.getSubmoduleEightCheckBox());
    }

    private void fillListOfChannelOneLabels() {
        channelOneLabels = new ArrayList<>();
        channelOneLabels.add(ltr27SettingsController.getSubModuleOneChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleTwoChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleThreeChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleFourChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleFiveChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleSixChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleSevenChannelOneLabel());
        channelOneLabels.add(ltr27SettingsController.getSubModuleEightChannelOneLabel());
    }

    private void fillListOfChannelTwoLabels() {
        channelTwoLabels = new ArrayList<>();
        channelTwoLabels.add(ltr27SettingsController.getSubModuleOneChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleTwoChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleThreeChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleFourChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleFiveChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleSixChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleSevenChannelTwoLabel());
        channelTwoLabels.add(ltr27SettingsController.getSubModuleEightChannelTwoLabel());
    }

    private void fillListOfChannelOneTextFields() {
        channelOneTextFields = new ArrayList<>();
        channelOneTextFields.add(ltr27SettingsController.getSubModuleOneChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleTwoChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleThreeChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleFourChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleFiveChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleSixChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleSevenChannelOneTextField());
        channelOneTextFields.add(ltr27SettingsController.getSubModuleEightChannelOneTextField());
    }

    private void fillListOfChannelTwoTextFields() {
        channelTwoTextFields = new ArrayList<>();
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleOneChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleTwoChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleThreeChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleFourChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleFiveChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleSixChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleSevenChannelTwoTextField());
        channelTwoTextFields.add(ltr27SettingsController.getSubModuleEightChannelTwoTextField());
    }

    private void fillListOfCalibrationButtons() {
        calibrationButtons = new ArrayList<>();
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleOneButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleTwoButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleThreeButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleFourButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleFiveButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleSixButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleSevenButton());
        calibrationButtons.add(ltr27SettingsController.getCalibrateSubmoduleEightButton());
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

    private void toggleSubmoduleUiElements(int channelIndex, boolean isDisable) {
        channelOneLabels.get(channelIndex).setDisable(isDisable);
        channelOneTextFields.get(channelIndex).setDisable(isDisable);
        channelTwoLabels.get(channelIndex).setDisable(isDisable);
        channelTwoTextFields.get(channelIndex).setDisable(isDisable);
        calibrationButtons.get(channelIndex).setDisable(isDisable);

        if (isDisable) {
            clearTextFields(channelIndex);
        }
    }

    private void clearTextFields(int channelIndex) {
        channelOneTextFields.get(channelIndex).setText("");
        channelTwoTextFields.get(channelIndex).setText("");
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
        ltr27SettingsController.getAverageLabel().setDisable(isDisable);
        ltr27SettingsController.getAverageTextField().setDisable(isDisable);
        ltr27SettingsController.getRarefactionLabel().setDisable(isDisable);
        ltr27SettingsController.getRarefactionComboBox().setDisable(isDisable);
        ltr27SettingsController.getCalibrationCheckBox().setDisable(isDisable);
    }

    private void setFrequencies() {
        ObservableList<String> frequencies = FXCollections.observableArrayList();
        final double MAX_FREQUENCY = 1000;
        for (int i = 0; i < 256; i++) {
            String frequency = String.format("%.2f Гц", MAX_FREQUENCY / (i + 1));
            frequencies.add(frequency);
        }
        ltr27SettingsController.getFrequencyComboBox().getItems().addAll(frequencies);
        ltr27SettingsController.getFrequencyComboBox().getSelectionModel().select(0);
    }

    private void setRarefactionCoefficients() {
        ObservableList<String> coefficients = FXCollections.observableArrayList();
        for (int i = 1; i < 8; i++) {
            coefficients.add(String.valueOf(i));
        }
        ltr27SettingsController.getRarefactionComboBox().setItems(coefficients);
        ltr27SettingsController.getRarefactionComboBox().getSelectionModel().select(1);
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
        String[][] descriptions = ltr27SettingsController.getSubmodulesDescriptions();

        for (int submoduleIndex = 0; submoduleIndex < descriptions.length; submoduleIndex++) {
            String description = String.format("Субмодуль %s (%s)", descriptions[submoduleIndex][0], descriptions[submoduleIndex][1]);
            description = description.contains("EMPTY ()") ? submoduleIsAbsentee : description;
            checkBoxes.get(submoduleIndex).setText(description);
        }
    }

    public void setSubmodulesUnits() {
        String[][] descriptions = ltr27SettingsController.getSubmodulesDescriptions();

        for (int channelIndex = 0; channelIndex < LTR27.MAX_SUBMODULES * 2; channelIndex++) {
            int submoduleIndex = channelIndex / 2;
            if (!checkBoxes.get(submoduleIndex).getText().equals(submoduleIsAbsentee)) {
                createNewLabel(channelOneLabels.get(submoduleIndex), 1, descriptions[submoduleIndex][2]);
                createNewLabel(channelTwoLabels.get(submoduleIndex), 2, descriptions[submoduleIndex][2]);
            }
        }
    }

    public void setCalibratedUnits() {
        List<String> calibratedUnits = lcm.getCalibratedUnits();
        for (int channelIndex = 0; channelIndex < LTR27.MAX_SUBMODULES * 2; channelIndex++) {
            String calibratedUnit = calibratedUnits.get(channelIndex);

            if (!calibratedUnit.isEmpty()) {
                if (channelIndex % 2 == 0) {
                    createNewLabel(channelOneLabels.get(channelIndex / 2), 1, calibratedUnit);
                } else {
                    createNewLabel(channelTwoLabels.get(channelIndex / 2), 2, calibratedUnit);
                }
            }
        }
    }

    private void createNewLabel(Label label, int channelNumber, String unit) {
        if (!label.getText().contains(unit)) {
            Platform.runLater(() -> label.setText(String.format("Канал %d, %s:", channelNumber, unit)));
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

    public void showValues() {
        new Thread(() -> {
            while (!ltr27SettingsController.isStopped()) {
                double[] data = ltr27SettingsController.getData();
                bufferedData = new double[LTR27.MAX_FREQUENCY * LTR27.MAX_SUBMODULES];

                int channelIndex = 0;

                for (int submoduleIndex = 0; submoduleIndex < LTR27.MAX_SUBMODULES; submoduleIndex++) {
                    if (!checkBoxes.get(submoduleIndex).getText().equals(submoduleIsAbsentee) && checkBoxes.get(submoduleIndex).isSelected()) {
                        boolean isCalibrate = ltr27SettingsController.getCalibrationCheckBox().isSelected();
                        if (average == 1) {
                            double valueOfChannelOne = lcm.calibrate(isCalibrate, data[channelIndex], submoduleIndex, channelIndex);
                            double valueOfChannelTwo = lcm.calibrate(isCalibrate, data[channelIndex + 1] / (double) average, submoduleIndex, channelIndex + 1);
                            setValues(valueOfChannelOne, valueOfChannelTwo, submoduleIndex);
                        } else if (averageCount < average) {
                            bufferedData[channelIndex] += data[channelIndex];
                            bufferedData[channelIndex + 1] += data[channelIndex + 1];
                        } else {
                            double valueOfChannelOne = lcm.calibrate(isCalibrate, bufferedData[channelIndex] / (double) average, submoduleIndex, channelIndex);
                            double valueOfChannelTwo = lcm.calibrate(isCalibrate, bufferedData[channelIndex + 1] / (double) average, submoduleIndex, channelIndex + 1);
                            setValues(valueOfChannelOne, valueOfChannelTwo, submoduleIndex);
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

    private void setValues(double valueOfChannelOne, double valueOfChannelTwo, int submoduleIndex) {
        Platform.runLater(() -> {
            String channelOneValue = Utils.convertFromExponentialFormat(valueOfChannelOne, Utils.getRounder(rarefactionCoefficient));
            String channelTwoValue = Utils.convertFromExponentialFormat(valueOfChannelTwo, Utils.getRounder(rarefactionCoefficient));
            channelOneTextFields.get(submoduleIndex).setText(channelOneValue);
            channelTwoTextFields.get(submoduleIndex).setText(channelTwoValue);
        });
    }

    public void enableAll() {
        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.getText().equals(submoduleIsAbsentee)) {
                checkBox.setSelected(true);
            }
        }
    }

    public String[] getSubmodulesNames() {
        String[][] descriptions = ltr27SettingsController.getSubmodulesDescriptions();
        String[] names = new String[descriptions.length];

        for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
            names[nameIndex] = descriptions[nameIndex][0];
        }

        return names;
    }

    public boolean[] getCheckedSubmodules() {
        boolean[] checkedSubmodules = new boolean[LTR27.MAX_SUBMODULES];
        for (int submoduleIndex = 0; submoduleIndex < LTR27.MAX_SUBMODULES; submoduleIndex++) {
            checkedSubmodules[submoduleIndex] = checkBoxes.get(submoduleIndex).isSelected();
        }
        return checkedSubmodules;
    }

    public void setCheckedSubmodules(boolean[] checkedSubmodules) {
        for (int submoduleIndex = 0; submoduleIndex < checkedSubmodules.length; submoduleIndex++) {
            checkBoxes.get(submoduleIndex).setSelected(checkedSubmodules[submoduleIndex]);
        }
    }

    public void disableSubmodulesUiElements() {
        for (int submoduleIndex = 0; submoduleIndex < LTR27.MAX_SUBMODULES; submoduleIndex++) {
            toggleSubmoduleUiElements(submoduleIndex, true);
        }
    }

    public void setLTR27CalibrationManager(LTR27CalibrationManager lcm) {
        this.lcm = lcm;
    }
}
