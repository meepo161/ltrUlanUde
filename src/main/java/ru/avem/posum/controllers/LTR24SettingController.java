package ru.avem.posum.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR24;

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
    private Button valueOfChannelN5;
    @FXML
    private Button valueOfChannelN6;
    @FXML
    private Button valueOfChannelN7;
    @FXML
    private Button valueOfChannelN8;
    @FXML
    private CheckBox checkChannelN1;
    @FXML
    private CheckBox checkChannelN2;
    @FXML
    private CheckBox checkChannelN3;
    @FXML
    private CheckBox checkChannelN4;
    @FXML
    private CheckBox checkChannelN5;
    @FXML
    private CheckBox checkChannelN6;
    @FXML
    private CheckBox checkChannelN7;
    @FXML
    private CheckBox checkChannelN8;
    @FXML
    private ComboBox<String> crateSlot;
    @FXML
    private ComboBox<String> typeOfChannelN1;
    @FXML
    private ComboBox<String> typeOfChannelN2;
    @FXML
    private ComboBox<String> typeOfChannelN3;
    @FXML
    private ComboBox<String> typeOfChannelN4;
    @FXML
    private ComboBox<String> typeOfChannelN5;
    @FXML
    private ComboBox<String> typeOfChannelN6;
    @FXML
    private ComboBox<String> typeOfChannelN7;
    @FXML
    private ComboBox<String> typeOfChannelN8;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN1;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN2;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN3;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN4;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN5;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN6;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN7;
    @FXML
    private ComboBox<String> measuringRangeOfChannelN8;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label crateSlotLabel;
    @FXML
    private TextField descriptionOfChannelN1;
    @FXML
    private TextField descriptionOfChannelN2;
    @FXML
    private TextField descriptionOfChannelN3;
    @FXML
    private TextField descriptionOfChannelN4;
    @FXML
    private TextField descriptionOfChannelN5;
    @FXML
    private TextField descriptionOfChannelN6;
    @FXML
    private TextField descriptionOfChannelN7;
    @FXML
    private TextField descriptionOfChannelN8;

    private List<TextField> channelsDescription = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<ComboBox<String>> channelsTypesComboBoxes = new ArrayList<>();
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private List<Button> valueOfChannelsButtons = new ArrayList<>();

    private WindowsManager wm;
    private ControllerManager cm;

    private CrateModel crateModel;
    private int selectedCrate;
    private String[] cratesSN;
    private int selectedModule;
    private int selectedSlot;
    private String status;

    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsDescriptionTextFields();
        fillListOfChannelsTypesComboBoxes();
        fillListOfMeasuringRangesComboBoxes();
        fillListOfChannelsValuesButtons();

        addListOfChannelsTypes(channelsTypesComboBoxes);
        addListenerForAllChannels();
        addListOfCrateSlots(crateSlot);
        checkChannelType(channelsTypesComboBoxes, measuringRangesComboBoxes);
        setDefaultParameters();
    }

    private void fillListOfChannelsCheckBoxes() {
        channelsCheckBoxes.addAll(Arrays.asList(
                checkChannelN1,
                checkChannelN2,
                checkChannelN3,
                checkChannelN4,
                checkChannelN5,
                checkChannelN6,
                checkChannelN7,
                checkChannelN8
        ));
    }

    private void fillListOfChannelsDescriptionTextFields() {
        channelsDescription.addAll(Arrays.asList(
                descriptionOfChannelN1,
                descriptionOfChannelN2,
                descriptionOfChannelN3,
                descriptionOfChannelN4,
                descriptionOfChannelN5,
                descriptionOfChannelN6,
                descriptionOfChannelN7,
                descriptionOfChannelN8
        ));
    }

    private void fillListOfChannelsTypesComboBoxes() {
        channelsTypesComboBoxes.addAll(Arrays.asList(
                typeOfChannelN1,
                typeOfChannelN2,
                typeOfChannelN3,
                typeOfChannelN4,
                typeOfChannelN5,
                typeOfChannelN6,
                typeOfChannelN7,
                typeOfChannelN8
        ));
    }

    private void fillListOfMeasuringRangesComboBoxes() {
        measuringRangesComboBoxes.addAll(Arrays.asList(
                measuringRangeOfChannelN1,
                measuringRangeOfChannelN2,
                measuringRangeOfChannelN3,
                measuringRangeOfChannelN4,
                measuringRangeOfChannelN5,
                measuringRangeOfChannelN6,
                measuringRangeOfChannelN7,
                measuringRangeOfChannelN8
        ));
    }

    private void fillListOfChannelsValuesButtons() {
        valueOfChannelsButtons.addAll(Arrays.asList(
                valueOfChannelN1,
                valueOfChannelN2,
                valueOfChannelN3,
                valueOfChannelN4,
                valueOfChannelN5,
                valueOfChannelN6,
                valueOfChannelN7,
                valueOfChannelN8
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
            toggleChannelsUiElements(channelsCheckBoxes.get(i), i);
        }
    }

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
        valueOfChannelsButtons.get(channel).setDisable(isDisable);

        if (!crateSlot.getSelectionModel().isEmpty()) {
            initializeButton.setDisable(isDisable);
        }
    }

    private void addListOfCrateSlots(ComboBox<String> crateSlot) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("Слот 1");
        strings.add("Слот 2");
        strings.add("Слот 3");
        strings.add("Слот 4");
        strings.add("Слот 5");
        strings.add("Слот 6");
        strings.add("Слот 7");
        strings.add("Слот 8");
        strings.add("Слот 9");
        strings.add("Слот 10");
        strings.add("Слот 11");
        strings.add("Слот 12");
        strings.add("Слот 13");
        strings.add("Слот 14");
        strings.add("Слот 15");
        strings.add("Слот 16");

        crateSlot.getItems().setAll(strings);
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

    private void addListOfDifferentialMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-2 В/+2 В");
        strings.add("-10 В/+10 В");

        measuringRange.getItems().setAll(strings);
    }

    private void addListOfICPMeasuringRanges(ComboBox<String> measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("~1 В");
        strings.add("~5 В");

        measuringRange.getItems().setAll(strings);
    }

    /**
     * Для каналов измерения виброускорения выбраны:
     * 0 - Режим ICP-вход
     * 1 - ~5 В
     * <p>
     * Для каналов измерения перемещения выбраны:
     * 0 - Дифференциальный вход без отсечки постоянной составляющей
     * 1 - -10 В/+10 В
     */
    private void setDefaultParameters() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsTypesComboBoxes.get(i).getSelectionModel().select(0);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(1);
            crateSlot.getSelectionModel().select(0);
        }
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

    public void handleValueOfChannel() {
        new Thread(() -> {
            LTR24 ltr24 = crateModel.getLtr24ModulesList().get(crateModel.getLtr24ModulesList().size() - 1);
        }).start();

        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    public void handleInitialize() {
        selectedCrate = cm.getSelectedCrate();
        cratesSN = crateModel.getCrates()[0];
        selectedModule = cm.getSelectedModule();
        selectedSlot = crateSlot.getSelectionModel().getSelectedIndex() + 1;

        LTR24 ltr24 = new LTR24();
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                ltr24.getCheckedChannels()[i] = true; // 1 - канал выбран
                ltr24.getChannelsDescription()[i] = channelsDescription.get(i).getText();
                ltr24.getChannelsTypes()[i] = channelsTypesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24.setCrate(cratesSN[selectedCrate]);
                ltr24.setSlot(selectedSlot);
            }
        }

        ltr24.initModule();
        statusBar.setText(ltr24.getStatus());

        if (ltr24.getStatus().equals("Операция успешно выполнена")) {
            crateModel.getLtr24ModulesList().add(ltr24);
            ltr24.start();
            disableUiElements();

            String oldName = (crateModel.getModulesNames(selectedCrate).get(selectedModule));
            crateModel.getModules()[selectedCrate][selectedModule] = oldName + " (" + crateSlot.getValue() + ")";
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
        crateSlotLabel.setDisable(true);
        crateSlot.setDisable(true);
    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    public void refreshView() {
        if (!crateModel.getLtr24ModulesList().isEmpty()) {
            for (int channel = 0; channel < channelsCheckBoxes.size(); channel++) {
                LTR24 module = crateModel.getLtr24ModulesList().get(0);

                channelsCheckBoxes.get(channel).setSelected(module.getCheckedChannels()[channel]);
                channelsDescription.get(channel).setText(module.getChannelsDescription()[channel]);
                channelsTypesComboBoxes.get(channel).getSelectionModel().select(module.getChannelsTypes()[channel]);
                measuringRangesComboBoxes.get(channel).getSelectionModel().select(module.getMeasuringRanges()[channel]);
                crateSlot.getSelectionModel().select(module.getSlot() - 1);

                if (module.getCheckedChannels()[channel]) {
                    channelsDescription.get(channel).setDisable(false);
                    channelsTypesComboBoxes.get(channel).setDisable(false);
                    measuringRangesComboBoxes.get(channel).setDisable(false);
                    channelsCheckBoxes.get(channel).setDisable(false);
                }
            }
        }
    }
}
