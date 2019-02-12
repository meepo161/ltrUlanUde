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
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR24SettingController implements BaseController {
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
    private CrateModel crateModel;
    private boolean connectionOpen;
    private LTR24 ltr24 = new LTR24();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private List<Button> valueOfChannelsButtons = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<TextField> channelsDescription = new ArrayList<>();
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();

    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsDescriptionTextFields();
        fillListOfChannelsTypesComboBoxes();
        fillListOfMeasuringRangesComboBoxes();
        fillListOfChannelsValuesButtons();

        addListOfChannelsTypes(channelsTypesComboBoxes);
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

    private void addListOfChannelsTypes(List<ComboBox<String>> channelsTypesComboBoxes) {
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
                measuringRangesComboBoxes.get(channel).getSelectionModel().select(0);
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
                addListOfICPMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            } else {
                addListOfDifferentialMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            }
        });
    }

    private void addListOfICPMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("~1 В");
        strings.add("~5 В");

        measuringRange.getItems().setAll(strings);
    }

    private void addListOfDifferentialMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-2 В/+2 В");
        strings.add("-10 В/+10 В");

        measuringRange.getItems().setAll(strings);
    }

    public void loadSettings() {
        findLTR24Module();
        loadChannelsSettings();
    }

    private void findLTR24Module() {
        int slot = cm.getSlot();

        for (Pair<Integer, LTR24> module : crateModel.getLtr24ModulesList()) {
            if (module.getValue().getSlot() == slot) {
                ltr24 = module.getValue();
            }
        }
    }

    private void loadChannelsSettings() {
        boolean[] checkedChannels = ltr24.getCheckedChannels();
        int[] channelsTypes = ltr24.getChannelsTypes();
        int[] measuringRanges = ltr24.getMeasuringRanges();
        String[] descriptions = ltr24.getChannelsDescription();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            channelsTypesComboBoxes.get(i).getSelectionModel().select(channelsTypes[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(measuringRanges[i]);
            channelsDescription.get(i).setText(descriptions[i]);
        }
    }

    public void handleInitialize() {
        saveChannelsSettings();
        initializeModule();
        statusBarLine.setStatus(ltr24.getStatus(), statusBar);

        if (ltr24.getStatus().equals("Операция успешно выполнена")) {
            disableChannelsUiElements();
            enableChannelsButtons();
        }
    }

    private void initializeModule() {
        if (!connectionOpen) {
            ltr24.openConnection();
            connectionOpen = true;
        }

        ltr24.initModule();
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

    private void enableChannelsButtons() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                valueOfChannelsButtons.get(i).setDisable(false);
            }
        }
    }

    public void handleBackButton() {
        findLTR24Module();
        saveChannelsSettings();
        if (connectionOpen) {
            ltr24.closeConnection();
            connectionOpen = false;
        }

        enableChannelsUiElements();
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    private void enableChannelsUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            CheckBox channel = channelsCheckBoxes.get(i);
            channel.setDisable(false);

            if (channel.isSelected()) {
                channelsCheckBoxes.get(i).setDisable(false);
                channelsDescription.get(i).setDisable(false);
                channelsTypesComboBoxes.get(i).setDisable(false);
                measuringRangesComboBoxes.get(i).setDisable(false);
            }
        }

        initializeButton.setDisable(false);
    }

    private void saveChannelsSettings() {
        int selectedCrate = cm.getSelectedCrate();
        String[] cratesSN = crateModel.getCrates()[0];
        int selectedModule = cm.getSlot();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                ltr24.getCheckedChannels()[i] = true; // true - канал выбран
                ltr24.getChannelsDescription()[i] = channelsDescription.get(i).getText();
                ltr24.getChannelsTypes()[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24.setCrate(cratesSN[selectedCrate]);
                ltr24.setSlot(selectedModule);
            } else {
                ltr24.getCheckedChannels()[i] = false; // false - канал не выбран
                ltr24.getChannelsDescription()[i] = "";
                ltr24.getChannelsTypes()[i] = 0;
                ltr24.getMeasuringRanges()[i] = 0;
                ltr24.setCrate(cratesSN[selectedCrate]);
                ltr24.setSlot(selectedModule);
            }
        }
    }

    public void handleValueOfChannelN1() {
        cm.showChannelData(CrateModel.Moudules.LTR24, ltr24.getSlot(), 1);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN2() {
        cm.showChannelData(CrateModel.Moudules.LTR24, ltr24.getSlot(), 2);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN3() {
        cm.showChannelData(CrateModel.Moudules.LTR24, ltr24.getSlot(), 3);
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleValueOfChannelN4() {
        cm.showChannelData(CrateModel.Moudules.LTR24, ltr24.getSlot(), 4);
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
