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
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR212SettingController implements BaseController {
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
    private boolean connectionOpen;
    private CrateModel crateModel;
    private ControllerManager cm;
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

        addListOfChannelsTypes(channelsTypesComboBoxes);
        addListenerForAllChannels();
        addListOfMeasuringRanges(measuringRangesComboBoxes);
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

    private void addListOfChannelsTypes(List<ComboBox<String>> channelsTypesComboBoxes) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("Полно- или полу- мостовая схема");
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

    private void addListenerForAllChannels() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            toggleChannelsUiElements(channelsCheckBoxes.get(i), i);
        }
    }

    private void toggleChannelsUiElements(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                toggleUiElements(channel, false);
            } else {
                toggleUiElements(channel, true);
                channelsDescription.get(channel).setText("");
                channelsTypesComboBoxes.get(channel).getSelectionModel().select(0);
                measuringRangesComboBoxes.get(channel).getSelectionModel().select(0);
                valueOfChannelsButtons.get(channel).setDisable(true);
            }
        });
    }

    private void toggleUiElements(int channel, boolean isDisable) {
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

    private void addListOfMeasuringRanges(List<ComboBox<String>> measuringRangesComboBoxes) {
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

    public void loadSettings(String moduleName) {
        writeField(moduleName);
        parseSlotNumber();
        setTitleLabel();
        findLTR212Module();
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

    private void findLTR212Module() {
        for (Pair<Integer, Module> module : crateModel.getModulesList()) {
            if (module.getKey() == slot) {
                ltr212 = (LTR212) module.getValue();
            }
        }
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
        for (int i = 0; i < ltr212.getChannelsCount(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            channelsTypesComboBoxes.get(i).getSelectionModel().select(channelsTypes[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(measuringRanges[i]);
            channelsDescription.get(i).setText(channelsDescriptions[i]);
        }
    }

    public void handleInitialize() {
        toggleProgressIndicatorState(false);
        disableUiElements();

        new Thread(() -> {
            saveChannelsSettings();
            initializeModule();

            Platform.runLater(() -> {
                statusBarLine.setStatus(ltr212.getStatus(), statusBar);
            });

            if (ltr212.getStatus().equals("Операция успешно выполнена")) {
                Platform.runLater(() -> {
                    toggleProgressIndicatorState(true);
                    enableChannelsButtons();
                });
            } else {
                Platform.runLater(() -> {
                    toggleProgressIndicatorState(true);
                    enableChannelsUiElements();
                });
            }
        }).start();
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private void initializeModule() {

        if (!connectionOpen) {
            ltr212.openConnection();
            connectionOpen = true;
        }

        ltr212.initModule();

        String error = ltr212.getStatus();
        while (error.equals("Использование калибровки невозможно для установленных параметров") || error.equals("Канал связи с ltrd не был создан или закрыт")) {
            ltr212.closeConnection();
            ltr212.openConnection();
            ltr212.initModule();
            error = ltr212.getStatus();
            System.out.println(error);
        }
    }

    private void disableUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            channelsDescription.get(i).setDisable(true);
            channelsTypesComboBoxes.get(i).setDisable(true);
            measuringRangesComboBoxes.get(i).setDisable(true);
        }

        initializeButton.setDisable(true);
    }

    private void enableChannelsButtons() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                valueOfChannelsButtons.get(i).setDisable(false);
            }
        }
    }

    public void handleBackButton() {
        new Thread(() -> {
            findLTR212Module();
            saveChannelsSettings();
            if (connectionOpen) {
                ltr212.closeConnection();
                connectionOpen = false;
            }

            enableChannelsUiElements();
            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void saveChannelsSettings() {
        int selectedCrate = cm.getSelectedCrate();
        String[] cratesSN = crateModel.getCrates()[0];
//        int slot = cm.getSlot();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                ltr212.getCheckedChannels()[i] = true; // true - канал выбран
//                ltr212.getChannelsDescription()[i] = channelsDescription.get(i).getText();
//                ltr212.getChannelsTypes()[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
//                ltr212.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr212.setCrate(cratesSN[selectedCrate]);
//                ltr212.setSlot(slot);
            } else {
                ltr212.getCheckedChannels()[i] = false; // false - канал не выбран
//                ltr212.getChannelsDescription()[i] = "";
//                ltr212.getChannelsTypes()[i] = 0;
//                ltr212.getMeasuringRanges()[i] = 0;
                ltr212.setCrate(cratesSN[selectedCrate]);
//                ltr212.setSlot(slot);
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

    public void handleValueOfChannelN1() {
        cm.showChannelData(ltr212, ltr212.getSlot(), 0);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN2() {
        cm.showChannelData(ltr212, ltr212.getSlot(), 1);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN3() {
        cm.showChannelData(ltr212, ltr212.getSlot(), 2);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN4() {
        cm.showChannelData(ltr212, ltr212.getSlot(), 3);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
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