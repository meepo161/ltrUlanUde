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
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.*;

public class LTR212SettingController implements BaseController {
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
    private ComboBox<String> moduleModesComboBox;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<String> referenceVoltageComboBox;
    @FXML
    private CheckBox referenceVoltageTypeCheckBox;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private CheckBox useCalibrationCheckBox;
    @FXML
    private CheckBox useFabricCalibrationCheckBox;
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
    private String[] channelsDescriptions;
    private int[] channelsTypes;
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private boolean[] checkedChannels;
    private ControllerManager cm;
    private CrateModel crateModel;
    private int disabledChannels;
    private boolean isConnectionOpen;
    private LTR212 ltr212 = new LTR212();
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private int[] measuringRanges;
    private String moduleName;
    private int slot;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private List<Button> valueOfChannelsButtons = new ArrayList<>();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsDescriptionTextFields();
        fillListOfChannelsTypesComboBoxes();
        fillListOfMeasuringRangesComboBoxes();
        fillListOfChannelsValuesButtons();

        addChannelsTypes(channelsTypesComboBoxes);
        addMeasuringRanges(measuringRangesComboBoxes);
        addModuleModes(moduleModesComboBox);
        addReferenceVoltageModes(referenceVoltageComboBox);
        addListenerForCheckBoxes(channelsCheckBoxes);
        addListenerForComboBoxes(channelsTypesComboBoxes);
        addListenerForComboBoxes(measuringRangesComboBoxes);
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
        valueOfChannelsButtons.addAll(Arrays.asList(
                valueOfChannelN1,
                valueOfChannelN2,
                valueOfChannelN3,
                valueOfChannelN4
        ));
    }

    private void addChannelsTypes(List<ComboBox<String>> channelsTypesComboBoxes) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("Полно- или полу-мостовая схема");
        strings.add("Четверть-мостовая схема (200 Ом)");
        strings.add("Четверть-мостовая схема (350 Ом)");
        strings.add("Четверть-мостовая схема (внешний резистор)");
        strings.add("Четверть-мостовая схема с нормирующим разбалансом (200 Ом)");
        strings.add("Четверть-мостовая схема с нормирующим разбалансом (350 Ом)");
        strings.add("Четверть-мостовая схема с нормирующим разбалансом (внешний резистор)");

        setComboBox(channelsTypesComboBoxes, strings);
    }

    private void setComboBox(List<ComboBox<String>> comboBoxes, ObservableList<String> strings) {
        for (ComboBox<String> comboBox : comboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    private void addMeasuringRanges(List<ComboBox<String>> measuringRangesComboBoxes) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-10 мВ/+10 мВ");
        strings.add("-20 мВ/+20 мВ");
        strings.add("-40 мВ/+40 мВ");
        strings.add("-80 мВ/+80 мВ");
        strings.add("0 мВ/+10 мВ");
        strings.add("0 мВ/+20 мВ");
        strings.add("0 мВ/+40 мВ");
        strings.add("0 мВ/+80 мВ");

        setComboBox(measuringRangesComboBoxes, strings);
    }

    private void addModuleModes(ComboBox<String> moduleModesComboBox) {
        ObservableList<String> modes = FXCollections.observableArrayList();
        modes.add("Средней точности");
        modes.add("Высокой точности");

        moduleModesComboBox.getItems().addAll(modes);
    }

    private void addReferenceVoltageModes(ComboBox<String> referenceVoltageComboBox) {
        ObservableList<String> modes = FXCollections.observableArrayList();
        modes.add("2.5 В");
        modes.add("5.0 В");

        referenceVoltageComboBox.getItems().addAll(modes);
    }

    private void addListenerForCheckBoxes(List<CheckBox> checkBoxes) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            changeChannelsUiElementsState(checkBoxes.get(i), i);
        }
    }

    private void changeChannelsUiElementsState(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                applyForAllChannels(true);
                changeChannelUiElementsState(channel, false);
            } else {
                applyForAllChannels(false);
                changeChannelUiElementsState(channel, true);
                setDefaultChannelsSettings(channel);
            }
        });
    }

    private void applyForAllChannels(boolean isChannelSelected) {
        if (applyForAllCheckBox.isSelected()) {
            selectSetting(isChannelSelected);
        }
    }

    private void selectSetting(boolean isChannelSelected) {
        for (CheckBox checkBox : channelsCheckBoxes) {
            checkBox.setSelected(isChannelSelected);
        }
    }

    private void changeChannelUiElementsState(int channel, boolean isDisable) {
        channelsTypesComboBoxes.get(channel).setDisable(isDisable);
        measuringRangesComboBoxes.get(channel).setDisable(isDisable);
        channelsDescription.get(channel).setDisable(isDisable);
        changeInitializeButtonState();
    }

    private void changeInitializeButtonState() {
        countDisabledChannels();
        checkCounterOfDisabledChannels();
    }

    private void countDisabledChannels() {
        for (CheckBox channelCheckBox : channelsCheckBoxes) {
            if (channelCheckBox.isSelected()) {
                initializeButton.setDisable(false);
            } else {
                disabledChannels++;
            }
        }
    }

    private void checkCounterOfDisabledChannels() {
        if (disabledChannels == ltr212.getChannelsCount()) { // общее количество каналов
            initializeButton.setDisable(true);
        }
    }

    private void setDefaultChannelsSettings(int channel) {
        channelsDescription.get(channel).setText("");
        channelsTypesComboBoxes.get(channel).getSelectionModel().select(0);
        measuringRangesComboBoxes.get(channel).getSelectionModel().select(3);
    }

    private void addListenerForComboBoxes(List<ComboBox<String>> comboBoxes) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.valueProperty().addListener(observable -> {
                int setting = comboBox.getSelectionModel().getSelectedIndex();
                applyForAllChannels(comboBoxes, setting);
            });
        }
    }

    private void applyForAllChannels(List<ComboBox<String>> comboBoxes, int setting) {
        if (applyForAllCheckBox.isSelected()) {
            selectComboBoxes(comboBoxes, setting);
        }
    }

    private void selectComboBoxes(List<ComboBox<String>> comboBoxes, int setting) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.getSelectionModel().select(setting);
        }
    }

    public void loadSettings(String moduleName) {
        setFields(moduleName);
        parseSlotNumber();
        setTitleLabel();
        findLTR212Module();
        loadModuleSettings();
    }

    private void setFields(String moduleName) {
        this.moduleName = moduleName;
    }

    private void parseSlotNumber() {
        slot = Utils.parseSlotNumber(moduleName);
    }

    private void setTitleLabel() {
        sceneTitleLabel.setText("Настройки модуля " + moduleName);
    }

    private void findLTR212Module() {
        HashMap<Integer, Module> modules = cm.getCrateModelInstance().getModulesList();
        ltr212 = (LTR212) modules.get(slot);
    }

    private void loadModuleSettings() {
        loadSettingsFields();
        setSettings();
    }

    private void loadSettingsFields() {
        checkedChannels = ltr212.getCheckedChannels();
        channelsTypes = ltr212.getChannelsTypes();
        measuringRanges = ltr212.getMeasuringRanges();
        channelsDescriptions = ltr212.getChannelsDescription();
    }

    private void setSettings() {
        int moduleMode = ltr212.getModuleSettings().get(ADC.Settings.ADC_MODE.getSettingName());
        int referenceVoltage = ltr212.getModuleSettings().get(ADC.Settings.REFERENCE_VOLTAGE.getSettingName());
        int useCalibration = ltr212.getModuleSettings().get(ADC.Settings.CALIBRATION_COEFFICIENTS.getSettingName());
        int useFabricCalibration = ltr212.getModuleSettings().get(ADC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName());
        int referenceVoltageType = ltr212.getModuleSettings().get(ADC.Settings.REFERENCE_VOLTAGE_TYPE.getSettingName());

        for (int i = 0; i < ltr212.getChannelsCount(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            channelsTypesComboBoxes.get(i).getSelectionModel().select(channelsTypes[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(measuringRanges[i]);
            channelsDescription.get(i).setText(channelsDescriptions[i].replace(", ", ""));
            moduleModesComboBox.getSelectionModel().select(moduleMode);
            referenceVoltageComboBox.getSelectionModel().select(referenceVoltage);
            useCalibrationCheckBox.setSelected(useCalibration == 1);
            useFabricCalibrationCheckBox.setSelected(useFabricCalibration == 1);
            referenceVoltageTypeCheckBox.setSelected(referenceVoltageType == 1);
        }
    }

    public void handleInitialize() {
        changeUiElementsState();

        new Thread(() -> {
            saveChannelsSettings();
            saveModuleSettings();
            initializeModule();
            checkResult();
            indicateResult();
        }).start();
    }

    private void changeUiElementsState() {
        toggleProgressIndicatorState(false);
        disableChannelsUiElements();
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private void disableChannelsUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            channelsDescription.get(i).setDisable(true);
            channelsTypesComboBoxes.get(i).setDisable(true);
            measuringRangesComboBoxes.get(i).setDisable(true);
        }

        applyForAllCheckBox.setDisable(true);
        moduleModesComboBox.setDisable(true);
        referenceVoltageComboBox.setDisable(true);
        useCalibrationCheckBox.setDisable(true);
        useFabricCalibrationCheckBox.setDisable(true);
        referenceVoltageTypeCheckBox.setDisable(true);
        initializeButton.setDisable(true);
    }

    private void saveChannelsSettings() {
        for (int i = 0; i < ltr212.getChannelsCount(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                checkedChannels[i] = true; // true - канал выбран
                channelsDescriptions[i] = channelsDescription.get(i).getText() + ", ";
                channelsTypes[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                measuringRanges[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
            } else {
                checkedChannels[i] = false; // false - канал не выбран
                channelsDescriptions[i] = ", ";
                channelsTypes[i] = 0;
                measuringRanges[i] = 3;
            }
        }
    }

    private void saveModuleSettings() {
        HashMap<String, Integer> settings = ltr212.getModuleSettings();
        int selectedADCMode = moduleModesComboBox.getSelectionModel().getSelectedIndex();
        int selectedReferenceVoltage = referenceVoltageComboBox.getSelectionModel().getSelectedIndex();
        int useCalibration = useCalibrationCheckBox.isSelected() ? 1 : 0;
        int useFabricCalibration = useFabricCalibrationCheckBox.isSelected() ? 1 : 0;
        int referenceVoltageType = referenceVoltageTypeCheckBox.isSelected() ? 1 : 0;

        settings.put(ADC.Settings.ADC_MODE.getSettingName(), selectedADCMode);
        settings.put(ADC.Settings.REFERENCE_VOLTAGE.getSettingName(), selectedReferenceVoltage);
        settings.put(ADC.Settings.CALIBRATION_COEFFICIENTS.getSettingName(), useCalibration);
        settings.put(ADC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), useFabricCalibration);
        settings.put(ADC.Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), referenceVoltageType);
    }

    private void initializeModule() {
        if (!isConnectionOpen) {
            ltr212.openConnection();
        }

        ltr212.initModule();
    }

    private void checkResult() {
        if (ltr212.getStatus().equals("Операция успешно выполнена")) {
            Platform.runLater(() -> {
                isConnectionOpen = true;
                toggleProgressIndicatorState(true);
                enableChannelValueButtons();
            });
        } else {
            Platform.runLater(() -> {
                isConnectionOpen = false;
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }
    }

    private void enableChannelValueButtons() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                valueOfChannelsButtons.get(i).setDisable(false);
            }
        }
    }

    private void enableChannelsUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            CheckBox channel = channelsCheckBoxes.get(i);
            channel.setDisable(false);
            valueOfChannelsButtons.get(i).setDisable(true);

            if (channel.isSelected()) {
                channelsCheckBoxes.get(i).setDisable(false);
                channelsDescription.get(i).setDisable(false);
                channelsTypesComboBoxes.get(i).setDisable(false);
                measuringRangesComboBoxes.get(i).setDisable(false);
            }
        }

        applyForAllCheckBox.setDisable(false);
        moduleModesComboBox.setDisable(false);
        referenceVoltageComboBox.setDisable(false);
        useCalibrationCheckBox.setDisable(false);
        useFabricCalibrationCheckBox.setDisable(false);
        referenceVoltageTypeCheckBox.setDisable(false);
        initializeButton.setDisable(false);
    }

    private void indicateResult() {
        Platform.runLater(() -> statusBarLine.setStatus(ltr212.getStatus(), statusBar));
    }

    public void handleBackButton() {
        new Thread(() -> {
            findLTR212Module();
            saveChannelsSettings();
            saveModuleSettings();
            closeConnection();
            enableChannelsUiElements();
            prepareSettingScene();
        }).start();

        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void closeConnection() {
        if (isConnectionOpen) {
            ltr212.closeConnection();
            isConnectionOpen = false;
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
        cm.giveChannelInfo(channel, CrateModel.LTR212, ltr212.getSlot());
        cm.initializeSignalGraphView();
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
        crateModel = cm.getCrateModelInstance();
    }
}