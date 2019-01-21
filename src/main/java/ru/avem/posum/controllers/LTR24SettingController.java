package ru.avem.posum.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.models.HardwareModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR24SettingController implements BaseController {
    @FXML
    private Button backButton;
    @FXML
    private Button graphOfChannelN1;
    @FXML
    private Button graphOfChannelN2;
    @FXML
    private Button graphOfChannelN3;
    @FXML
    private Button graphOfChannelN4;
    @FXML
    private Button graphOfChannelN5;
    @FXML
    private Button graphOfChannelN6;
    @FXML
    private Button graphOfChannelN7;
    @FXML
    private Button graphOfChannelN8;
    @FXML
    private Button calibrateChannelN1;
    @FXML
    private Button calibrateChannelN2;
    @FXML
    private Button calibrateChannelN3;
    @FXML
    private Button calibrateChannelN4;
    @FXML
    private Button calibrateChannelN5;
    @FXML
    private Button calibrateChannelN6;
    @FXML
    private Button calibrateChannelN7;
    @FXML
    private Button calibrateChannelN8;
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
    private TextField currentValueOfChannelN1;
    @FXML
    private TextField currentValueOfChannelN2;
    @FXML
    private TextField currentValueOfChannelN3;
    @FXML
    private TextField currentValueOfChannelN4;
    @FXML
    private TextField currentValueOfChannelN5;
    @FXML
    private TextField currentValueOfChannelN6;
    @FXML
    private TextField currentValueOfChannelN7;
    @FXML
    private TextField currentValueOfChannelN8;

    private List<Button> graphOfChannelsButtons = new ArrayList<>();
    private List<Button> calibrateChannelsButtons = new ArrayList<>();
    private List<TextField> channelsValues = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<ComboBox> channelsTypesComboBoxes = new ArrayList<>();
    private List<ComboBox> measuringRangesComboBoxes = new ArrayList<>();

    private WindowsManager wm;
    private ControllerManager cm;

    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsTypesComboBoxes();
        fillListOfMeasuringRangesComboBoxes();
        fillListOfChannelsValuesTextFields();
        fillListOfChannelsGraphsButtons();
        fillListOfCalibrateChannelsButtons();

        addListOfChannelsTypes(channelsTypesComboBoxes);
        addListenerForAllChannels();

        loadDefaultParameters();
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

    private void fillListOfChannelsValuesTextFields() {
        channelsValues.addAll(Arrays.asList(
                currentValueOfChannelN1,
                currentValueOfChannelN2,
                currentValueOfChannelN3,
                currentValueOfChannelN4,
                currentValueOfChannelN5,
                currentValueOfChannelN6,
                currentValueOfChannelN7,
                currentValueOfChannelN8
        ));
    }

    private void fillListOfChannelsGraphsButtons() {
        graphOfChannelsButtons.addAll(Arrays.asList(
                graphOfChannelN1,
                graphOfChannelN2,
                graphOfChannelN3,
                graphOfChannelN4,
                graphOfChannelN5,
                graphOfChannelN6,
                graphOfChannelN7,
                graphOfChannelN8
        ));
    }

    private void fillListOfCalibrateChannelsButtons() {
        calibrateChannelsButtons.addAll(Arrays.asList(
                calibrateChannelN1,
                calibrateChannelN2,
                calibrateChannelN3,
                calibrateChannelN4,
                calibrateChannelN5,
                calibrateChannelN6,
                calibrateChannelN7,
                calibrateChannelN8
        ));
    }

    private void addListOfChannelsTypes(List<ComboBox> channelsTypesComboBoxes) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("Дифференциальный вход без отсечки постоянной составляющей");
        strings.add("Дифференциальный вход с отсечкой постоянной составляющей");
        strings.add("Режим ICP-вход");

        setComboBox(channelsTypesComboBoxes, strings);
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
                HardwareModel.getInstance().getLtr24ModuleN8().getLtr24().getCheckedChannels()[channel] = 1;
                toggleUiElements(channel, false);
            } else {
                HardwareModel.getInstance().getLtr24ModuleN8().getLtr24().getCheckedChannels()[channel] = 0;
                toggleUiElements(channel, true);
            }
        });
    }

    private void toggleUiElements(int channel, boolean isDisable) {
        channelsTypesComboBoxes.get(channel).setDisable(isDisable);
        measuringRangesComboBoxes.get(channel).setDisable(isDisable);
        channelsValues.get(channel).setDisable(isDisable);
        graphOfChannelsButtons.get(channel).setDisable(isDisable);
        calibrateChannelsButtons.get(channel).setDisable(isDisable);
    }

    private void loadDefaultParameters() {
        int[] channelsTypes = HardwareModel.getInstance().getLtr24ModuleN8().getLtr24().getChannelsTypes();
        int[] measurinRanges = HardwareModel.getInstance().getLtr24ModuleN8().getLtr24().getMeasuringRanges();

        for (int i = 0; i < channelsTypes.length; i++) {
            channelsTypesComboBoxes.get(i).getSelectionModel().select(channelsTypes[i]);
            measuringRangesComboBoxes.get(i).getSelectionModel().select(measurinRanges[i]);
        }
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }




    protected void checkChannelType(List<CheckBox> channels, List<ComboBox> channelsTypes, List<ComboBox> measuringRanges) {
        for (int i = 0; i < channels.size(); i++) {
            toggleICPChannels(channelsTypes.get(i), measuringRanges.get(i));
        }
    }

    private void toggleICPChannels(ComboBox channelType, ComboBox measuringRange) {
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

    private void setComboBox(List<ComboBox> measuringRangeComboBoxes, ObservableList<String> strings) {
        for (ComboBox comboBox : measuringRangeComboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    private void addListOfDifferentialMeasuringRanges(ComboBox measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-2 В/+2 В");
        strings.add("-10 В/+10 В");

        measuringRange.getItems().setAll(strings);
    }

    private void addListOfICPMeasuringRanges(ComboBox measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("~1 В");
        strings.add("~5 В");

        measuringRange.getItems().setAll(strings);
    }
}
