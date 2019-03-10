package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR24SettingController implements BaseController {
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
    private String[] channelsDescriptions;
    private List<TextField> channelsDescription = new ArrayList<>();
    private int[] channelsTypes;
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private boolean[] checkedChannels;
    private boolean isConnectionOpen;
    private ControllerManager cm;
    private CrateModel crateModel;
    private LTR24 ltr24;
    private int[] measuringRanges;
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
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
        addListenerForAllChannels();
        checkChannelType(channelsTypesComboBoxes, measuringRangesComboBoxes);
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
        strings.add("Дифференциальный вход без отсечки постоянной составляющей");
        strings.add("Дифференциальный вход с отсечкой постоянной составляющей");
        strings.add("Режим ICP-вход");

        setComboBox(channelsTypesComboBoxes, strings);
    }

    private void setComboBox(List<ComboBox<String>> measuringRangeComboBoxes, ObservableList<String> strings) {
        for (ComboBox<String> comboBox : measuringRangeComboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    private void addListenerForAllChannels() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            disableChannelsUiElements(channelsCheckBoxes.get(i), i);
        }
    }

    private void disableChannelsUiElements(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                disableChannelsUiElements(channel, false);
            } else {
                disableChannelsUiElements(channel, true);
                channelsDescription.get(channel).setText("");
                channelsTypesComboBoxes.get(channel).getSelectionModel().select(0);
                measuringRangesComboBoxes.get(channel).getSelectionModel().select(1);
            }
        });
    }

    private void disableChannelsUiElements(int channel, boolean isDisable) {
        channelsTypesComboBoxes.get(channel).setDisable(isDisable);
        measuringRangesComboBoxes.get(channel).setDisable(isDisable);
        channelsDescription.get(channel).setDisable(isDisable);
        toggleInitializeButton();
    }

    private void toggleInitializeButton() {
        int disabledChannels = 0;
        for (CheckBox checkBox : channelsCheckBoxes) {
            if (checkBox.isSelected()) {
                initializeButton.setDisable(false);
            } else {
                disabledChannels++;
            }
        }

        if (disabledChannels == 4) { // 4 - общее количество каналов
            initializeButton.setDisable(true);
        }
    }

    private void checkChannelType(List<ComboBox<String>> channelsTypesComboBoxes, List<ComboBox<String>> measuringRangesComboBoxes) {
        for (int i = 0; i < channelsTypesComboBoxes.size(); i++) {
            toggleICPChannels(channelsTypesComboBoxes.get(i), measuringRangesComboBoxes.get(i));
        }
    }

    private void toggleICPChannels(ComboBox<String> channelType, ComboBox<String> measuringRange) {
        channelType.valueProperty().addListener(observable -> {
            if (channelType.getSelectionModel().isSelected(2)) {
                addICPMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            } else {
                addDifferentialMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            }
        });
    }

    private void addICPMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("~1 В");
        strings.add("~5 В");

        measuringRange.getItems().setAll(strings);
    }

    private void addDifferentialMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-2 В/+2 В");
        strings.add("-10 В/+10 В");

        measuringRange.getItems().setAll(strings);
    }

    public void loadSettings(String moduleName) {
        writeField(moduleName);
        parseSlotNumber();
        setTitleLabel();
        findLTR24Module();
        loadModuleSettings();
    }

    private void writeField(String moduleName) {
        this.moduleName = moduleName;
    }

    private void parseSlotNumber() {
        slot = Utils.parseSlotNumber(moduleName);
    }

    private void setTitleLabel() {
        sceneTitleLabel.setText("Настройки модуля " + moduleName);
    }

    private void findLTR24Module() {
        for (Pair<Integer, Module> module : crateModel.getModulesList()) {
            if (module.getKey() == slot) {
                ltr24 = (LTR24) module.getValue();
            }
        }
    }

    private void loadModuleSettings() {
        loadSettingsFields();
        setSettings();
    }

    private void loadSettingsFields() {
        checkedChannels = ltr24.getCheckedChannels();
        channelsTypes = ltr24.getChannelsTypes();
        measuringRanges = ltr24.getMeasuringRanges();
        channelsDescriptions = ltr24.getChannelsDescription();
    }

    private void setSettings() {
        for (int i = 0; i < ltr24.getChannelsCount(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            channelsTypesComboBoxes.get(i).getSelectionModel().select(channelsTypes[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(measuringRanges[i]);
            channelsDescription.get(i).setText(channelsDescriptions[i].replace(", ", ""));
        }
    }

    public void handleInitialize() {
        changeUiElementsState();

        new Thread(() -> {
            saveChannelsSettings();
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

        initializeButton.setDisable(true);
    }

    private void saveChannelsSettings() {
        for (int i = 0; i < ltr24.getChannelsCount(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                checkedChannels[i] = true; // true - канал выбран
                channelsDescriptions[i] = channelsDescription.get(i).getText() + ", ";
                channelsTypes[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                measuringRanges[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
            } else {
                checkedChannels[i] = false; // false - канал не выбран
                channelsDescriptions[i] = ", ";
                channelsTypes[i] = 0;
                measuringRanges[i] = 0;
            }
        }
    }

    private void initializeModule() {
        if (!isConnectionOpen) {
            ltr24.openConnection();
        }

        ltr24.initModule();
    }

    private void checkResult() {
        if (ltr24.getStatus().equals("Операция успешно выполнена")) {
            Platform.runLater(() -> {
                isConnectionOpen = true;
                toggleProgressIndicatorState(true);
                enableChannelsButtons();
            });
        } else {
            Platform.runLater(() -> {
                isConnectionOpen = false;
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }
    }

    private void enableChannelsButtons() {
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

        initializeButton.setDisable(false);
    }

    private void indicateResult() {
        Platform.runLater(() -> statusBarLine.setStatus(ltr24.getStatus(), statusBar));
    }


    public void handleBackButton() {
        new Thread(() -> {
            findLTR24Module();
            saveChannelsSettings();
            closeConnection();
            enableChannelsUiElements();
            prepareSettingScene();
        }).start();

        changeScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void closeConnection() {
        if (isConnectionOpen) {
            ltr24.closeConnection();
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
        cm.showChannelData(ltr24, ltr24.getSlot(), channel);
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
