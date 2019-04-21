package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.models.LTR24SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LTR24SettingController implements BaseController {
    @FXML
    private CheckBox applyForAllCheckBox;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
    @FXML
    private TextField descriptionOfChannelN1;
    @FXML
    private TextField descriptionOfChannelN2;
    @FXML
    private TextField descriptionOfChannelN3;
    @FXML
    private TextField descriptionOfChannelN4;
    @FXML
    private ComboBox<String> discretizationFrequencyComboBox;
    @FXML
    private Button initializeButton;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN1;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN2;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN3;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN4;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private Button valueOfChannelN1;
    @FXML
    private Button valueOfChannelN2;
    @FXML
    private Button valueOfChannelN3;
    @FXML
    private Button valueOfChannelN4;

    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<TextField> channelsDescription = new ArrayList<>();
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private ControllerManager cm;
    private LTR24SettingsModel ltr24SettingsModel = new LTR24SettingsModel();
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private List<Button> valueOfChannelButtons = new ArrayList<>();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        fillListsOfUiElements();
        initComboBoxes();
        listenCheckBoxes(channelsCheckBoxes);
        listenComboBoxes(channelsTypesComboBoxes);
        listenComboBoxes(measuringRangesComboBoxes);
        listenICPItemSelection();
    }

    private void fillListsOfUiElements() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsDescriptionTextFields();
        fillListOfChannelsTypesComboBoxes();
        fillListOfMeasuringRangesComboBoxes();
        fillListOfChannelsValuesButtons();
    }

    private void fillListOfChannelsCheckBoxes() {
        channelsCheckBoxes.addAll(Arrays.asList(
                checkChannelN1,
                checkChannelN2,
                checkChannelN3,
                checkChannelN4
        ));
    }

    private void fillListOfChannelsDescriptionTextFields() {
        channelsDescription.addAll(Arrays.asList(
                descriptionOfChannelN1,
                descriptionOfChannelN2,
                descriptionOfChannelN3,
                descriptionOfChannelN4
        ));
    }

    private void fillListOfChannelsTypesComboBoxes() {
        channelsTypesComboBoxes.addAll(Arrays.asList(
                typeOfChannelN1,
                typeOfChannelN2,
                typeOfChannelN3,
                typeOfChannelN4
        ));
    }

    private void fillListOfMeasuringRangesComboBoxes() {
        measuringRangesComboBoxes.addAll(Arrays.asList(
                measuringRangeOfChannelN1,
                measuringRangeOfChannelN2,
                measuringRangeOfChannelN3,
                measuringRangeOfChannelN4
        ));
    }

    private void fillListOfChannelsValuesButtons() {
        valueOfChannelButtons.addAll(Arrays.asList(
                valueOfChannelN1,
                valueOfChannelN2,
                valueOfChannelN3,
                valueOfChannelN4
        ));
    }

    private void initComboBoxes() {
        addChannelsTypes(channelsTypesComboBoxes);
        addFrequencies(discretizationFrequencyComboBox);
    }

    private void addChannelsTypes(List<ComboBox<String>> channelsTypesComboBoxes) {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("Дифференциальный вход без отсечки постоянной составляющей");
        types.add("Дифференциальный вход с отсечкой постоянной составляющей");
        types.add("Режим ICP-вход");

        setComboBoxes(channelsTypesComboBoxes, types);
    }

    private void setComboBoxes(List<ComboBox<String>> measuringRangeComboBoxes, ObservableList<String> strings) {
        for (ComboBox<String> comboBox : measuringRangeComboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    private void addFrequencies(ComboBox<String> comboBox) {
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

        comboBox.getItems().addAll(frequencies);
    }

    @FXML
    private void listenCheckBoxes(List<CheckBox> checkBoxes) {
        for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.size(); checkBoxIndex++) {
            toggleChannelsUiElementsState(checkBoxes.get(checkBoxIndex), checkBoxIndex);
        }
    }

    private void toggleChannelsUiElementsState(CheckBox checkBox, int checkBoxIndex) {
        checkBox.selectedProperty().addListener(observable -> {
            selectCheckBoxes(checkBox.isSelected());
            changeChannelUiElementsState(checkBoxIndex, !checkBox.isSelected());
            if (!checkBox.isSelected()) {
                resetChannelSettings(checkBoxIndex);
            }
        });
    }

    private void selectCheckBoxes(boolean isChannelSelected) {
        if (applyForAllCheckBox.isSelected()) {
            for (CheckBox checkBox : channelsCheckBoxes) {
                checkBox.setSelected(isChannelSelected);
            }
        }
    }

    private void changeChannelUiElementsState(int channelIndex, boolean isDisable) {
        channelsTypesComboBoxes.get(channelIndex).setDisable(isDisable);
        measuringRangesComboBoxes.get(channelIndex).setDisable(isDisable);
        channelsDescription.get(channelIndex).setDisable(isDisable);

        for (CheckBox channelCheckBox : channelsCheckBoxes) {
            if (!channelCheckBox.isSelected()) {
                ltr24SettingsModel.setDisabledChannels(ltr24SettingsModel.getDisabledChannels() + 1);
            }
        }

        initializeButton.setDisable(ltr24SettingsModel.getDisabledChannels() == ltr24SettingsModel.getLTR24Instance().getChannelsCount());
    }

    private void resetChannelSettings(int channel) {
        channelsDescription.get(channel).setText("");
        channelsTypesComboBoxes.get(channel).getSelectionModel().select(0);
        measuringRangesComboBoxes.get(channel).getSelectionModel().select(1);
    }

    @FXML
    private void listenComboBoxes(List<ComboBox<String>> comboBoxes) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.valueProperty().addListener(observable -> {
                if (applyForAllCheckBox.isSelected()) {
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

    @FXML
    private void listenICPItemSelection() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            toggleMeasuringRanges(channelsTypesComboBoxes.get(channelIndex), measuringRangesComboBoxes.get(channelIndex));
        }
    }

    private void toggleMeasuringRanges(ComboBox<String> channelTypes, ComboBox<String> channelMeasuringRanges) {
        channelTypes.valueProperty().addListener(observable -> {
            ObservableList<String> ranges = FXCollections.observableArrayList();

            if (channelTypes.getSelectionModel().isSelected(2)) { // выбран ICP режим
                ranges.add("~1 В");
                ranges.add("~5 В");
            } else { // выбран дифференциальный режим
                ranges.add("-2 В/+2 В");
                ranges.add("-10 В/+10 В");
            }

            channelMeasuringRanges.getItems().setAll(ranges);
            channelMeasuringRanges.getSelectionModel().select(1);
        });
    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr24SettingsModel.setModuleName(moduleName);
        ltr24SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        setLTR24Instance();
        setSettings();
    }

    private void setLTR24Instance() {
        HashMap<Integer, Module> modulesInstances = cm.getCrateModelInstance().getModulesList();
        ltr24SettingsModel.setLTR24Instance((LTR24) modulesInstances.get(ltr24SettingsModel.getSlot()));
    }

    private void setSettings() {
        for (int i = 0; i < ltr24SettingsModel.getLTR24Instance().getChannelsCount(); i++) {
            channelsCheckBoxes.get(i).setSelected(ltr24SettingsModel.getCheckedChannels()[i]);
            channelsTypesComboBoxes.get(i).getSelectionModel().select(ltr24SettingsModel.getChannelsTypes()[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(ltr24SettingsModel.getMeasuringRanges()[i]);
            channelsDescription.get(i).setText(ltr24SettingsModel.getChannelsDescriptions()[i].replace(", ", ""));
        }
        int frequency = ltr24SettingsModel.getLTR24Instance().getModuleSettings().get(ADC.Settings.FREQUENCY.getSettingName());
        discretizationFrequencyComboBox.getSelectionModel().select(frequency);
    }

    @FXML
    public void handleInitialize() {
        changeUiElementsState();

        new Thread(() -> {
            saveChannelsSettings();
            saveModuleSettings();
            ltr24SettingsModel.initModule();
            checkModuleStatus();
        }).start();
    }

    private void changeUiElementsState() {
        toggleProgressIndicatorState(false);
        toggleChannelsUiElementsState(true);
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void toggleChannelsUiElementsState(boolean isDisabled) {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(isDisabled);
            channelsDescription.get(i).setDisable(isDisabled);
            channelsTypesComboBoxes.get(i).setDisable(isDisabled);
            measuringRangesComboBoxes.get(i).setDisable(isDisabled);
        }

        discretizationFrequencyComboBox.setDisable(isDisabled);
        applyForAllCheckBox.setDisable(isDisabled);
        initializeButton.setDisable(isDisabled);
    }

    private void saveChannelsSettings() {
        for (int i = 0; i < ltr24SettingsModel.getLTR24Instance().getChannelsCount(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                ltr24SettingsModel.getCheckedChannels()[i] = true; // true - канал выбран
                ltr24SettingsModel.getChannelsDescriptions()[i] = channelsDescription.get(i).getText() + ", ";
                ltr24SettingsModel.getChannelsTypes()[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24SettingsModel.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
            } else {
                ltr24SettingsModel.getCheckedChannels()[i] = false; // false - канал не выбран
                ltr24SettingsModel.getChannelsDescriptions()[i] = ", ";
                ltr24SettingsModel.getChannelsTypes()[i] = 0;
                ltr24SettingsModel.getMeasuringRanges()[i] = 1;
            }
        }
    }

    private void saveModuleSettings() {
        int frequency = discretizationFrequencyComboBox.getSelectionModel().getSelectedIndex();
        ltr24SettingsModel.getLTR24Instance().getModuleSettings().put(ADC.Settings.FREQUENCY.getSettingName(), frequency);
    }

    private void checkModuleStatus() {
        if (ltr24SettingsModel.getLTR24Instance().getStatus().equals("Операция успешно выполнена")) {
            Platform.runLater(() -> {
                ltr24SettingsModel.setConnectionOpen(true);
                toggleProgressIndicatorState(true);
                enableChannelValueButtons();
            });
        } else {
            Platform.runLater(() -> {
                ltr24SettingsModel.setConnectionOpen(false);
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }

        Platform.runLater(() -> statusBarLine.setStatus(ltr24SettingsModel.getLTR24Instance().getStatus(), statusBar));
    }

    private void enableChannelValueButtons() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            valueOfChannelButtons.get(channelIndex).setDisable(!channelsCheckBoxes.get(channelIndex).isSelected());
        }
    }

    private void enableChannelsUiElements() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            CheckBox channel = channelsCheckBoxes.get(channelIndex);
            channel.setDisable(false);
            valueOfChannelButtons.get(channelIndex).setDisable(true);
            channelsCheckBoxes.get(channelIndex).setDisable(!channel.isSelected());
            channelsDescription.get(channelIndex).setDisable(!channel.isSelected());
            channelsTypesComboBoxes.get(channelIndex).setDisable(!channel.isSelected());
            measuringRangesComboBoxes.get(channelIndex).setDisable(!channel.isSelected());
        }

        discretizationFrequencyComboBox.setDisable(false);
        applyForAllCheckBox.setDisable(false);
        initializeButton.setDisable(false);
    }

    @FXML
    public void handleBackButton() {
        new Thread(() -> {
            setLTR24Instance();
            saveChannelsSettings();
            saveModuleSettings();
            closeConnection();
            enableChannelsUiElements();
            prepareSettingScene();
        }).start();

        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void closeConnection() {
        if (ltr24SettingsModel.isConnectionOpen()) {
            ltr24SettingsModel.getLTR24Instance().closeConnection();
            ltr24SettingsModel.setConnectionOpen(false);
        }
    }

    private void prepareSettingScene() {
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    private void changeScene(WindowsManager.Scenes settingsScene) {
        wm.setScene(settingsScene);
    }

    public void handleValueOfChannelN1() {
        showChannelValue(0);
    }

    private void showChannelValue(int channel) {
        ltr24SettingsModel.getLTR24Instance().defineFrequency();
        ltr24SettingsModel.getLTR24Instance().start(ltr24SettingsModel.getSlot());
        cm.giveChannelInfo(channel, CrateModel.LTR24, ltr24SettingsModel.getLTR24Instance().getSlot());
        cm.initializeSignalGraphView();
        cm.checkCalibration();
        changeScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN2() {
        showChannelValue(1);
    }

    public void handleValueOfChannelN3() {
        showChannelValue(2);
    }

    public void handleValueOfChannelN4() {
        showChannelValue(3);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
