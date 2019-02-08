package ru.avem.posum.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR212SettingController implements BaseController {
    @FXML
    private Button initializeButton;
    @FXML
    private Button valueOfChannelN1;
    @FXML
    private Button valueOfChannelN2;
    @FXML
    private Button valueOfChannelN3;
    @FXML
    private Button valueOfChannelN4;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN1;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN2;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN3;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN4;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TextField descriptionOfChannelN1;
    @FXML
    private TextField descriptionOfChannelN2;
    @FXML
    private TextField descriptionOfChannelN3;
    @FXML
    private TextField descriptionOfChannelN4;

    private WindowsManager wm;
    private ControllerManager cm;
    private List<TextField> channelsDescription = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private List<Button> valueOfChannelsButtons = new ArrayList<>();
    private LTR212 ltr212 = new LTR212();
    private CrateModel crateModel;
    private StatusBarLine statusBarLine = new StatusBarLine();

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
        setDefaultParameters();
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
        strings.add("Сбалансированный мост (200 Ом)");
        strings.add("Сбалансированный мост (350 Ом)");
        strings.add("Сбалансированный мост (внешний резистор)");
        strings.add("Разбалансированный мост (200 Ом)");
        strings.add("Разбалансированный мост (350 Ом)");
        strings.add("Разбалансированный мост (внешний резистор)");

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

    /**
     * В массив checkedChannels сохраняются значения 1 - канал отмечен, 0 - канал не отмечен
     */
    private void toggleChannelsUiElements(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                toggleUiElements(channel, false);
            } else {
                toggleUiElements(channel, true);
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

    /**
     * Для каналов измерения виброускорения выбраны:
     * 0 - Сбалансированный мост (350 Ом)
     * 1 - -10 мВ/+10 мВ
     */
    private void setDefaultParameters() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsTypesComboBoxes.get(i).getSelectionModel().select(1);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(3);
        }
    }

    public void handleInitialize() {
        saveChannelsSettings();
        ltr212.initModule();
        avoidInitializeErrors();

        statusBarLine.setStatus(ltr212.getStatus(), statusBar);

        if (ltr212.getStatus().equals("Операция успешно выполнена")) {
            disableUiElements();
            enableChannelsButtons();
        }
    }

    private void avoidInitializeErrors() {
        if (ltr212.getStatus().equals("Четверть-мостовые резисторы должны быть одинаковы для всех каналов АЦП")) {
            statusBarLine.setStatus(ltr212.getStatus(), statusBar);
            ltr212.closeConnection();
        } else if (ltr212.getStatus().equals("Использование калибровки невозможно для установленных параметров")) {
            while (ltr212.getStatus().equals("Использование калибровки невозможно для установленных параметров")) {
                ltr212.closeConnection();
                ltr212.initModule();
            }
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
        searchLTR212Instance();
        saveChannelsSettings();

        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    private void searchLTR212Instance() {
        int selectedModule = cm.getSelectedModule();

        for (Pair<Integer, LTR212> module : crateModel.getLtr212ModulesList()) {
            if (module.getKey() == selectedModule) {
                ltr212 = module.getValue();
            }
        }
    }

    private void saveChannelsSettings() {
        int selectedCrate = cm.getSelectedCrate();
        String[] cratesSN = crateModel.getCrates()[0];
        int slot = cm.getSlot();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                ltr212.getCheckedChannels()[i] = true; // true - канал выбран
                ltr212.getChannelsDescription()[i] = channelsDescription.get(i).getText();
                ltr212.getChannelsTypes()[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr212.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr212.setCrate(cratesSN[selectedCrate]);
                ltr212.setSlot(slot);
            }
        }
    }

    public void handleValueOfChannelN1() {
        cm.showChannelData(CrateModel.Moudules.LTR212, ltr212.getSlot(), 1);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN2() {
        cm.showChannelData(CrateModel.Moudules.LTR212, ltr212.getSlot(), 2);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN3() {
        cm.showChannelData(CrateModel.Moudules.LTR212, ltr212.getSlot(), 3);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN4() {
        cm.showChannelData(CrateModel.Moudules.LTR212, ltr212.getSlot(), 4);
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