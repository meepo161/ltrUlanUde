package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.DAC;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.models.LTR34SettingsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.*;

public class LTR34SettingController implements BaseController {
    @FXML
    private TextField amplitudeOfChannelN1;
    @FXML
    private TextField amplitudeOfChannelN2;
    @FXML
    private TextField amplitudeOfChannelN3;
    @FXML
    private TextField amplitudeOfChannelN4;
    @FXML
    private TextField amplitudeOfChannelN5;
    @FXML
    private TextField amplitudeOfChannelN6;
    @FXML
    private TextField amplitudeOfChannelN7;
    @FXML
    private TextField amplitudeOfChannelN8;
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
    private ComboBox<String> calibrationComboBox;
    @FXML
    private ComboBox<String> dacModeComboBox;
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
    @FXML
    private TextField frequencyOfChannelN1;
    @FXML
    private TextField frequencyOfChannelN2;
    @FXML
    private TextField frequencyOfChannelN3;
    @FXML
    private TextField frequencyOfChannelN4;
    @FXML
    private TextField frequencyOfChannelN5;
    @FXML
    private TextField frequencyOfChannelN6;
    @FXML
    private TextField frequencyOfChannelN7;
    @FXML
    private TextField frequencyOfChannelN8;
    @FXML
    private Button generateSignalButton;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TextField phaseOfChannelN1;
    @FXML
    private TextField phaseOfChannelN2;
    @FXML
    private TextField phaseOfChannelN3;
    @FXML
    private TextField phaseOfChannelN4;
    @FXML
    private TextField phaseOfChannelN5;
    @FXML
    private TextField phaseOfChannelN6;
    @FXML
    private TextField phaseOfChannelN7;
    @FXML
    private TextField phaseOfChannelN8;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private ComboBox<String> signalTypeComboBox;
    @FXML
    private Button stopSignalButton;
    @FXML
    private StatusBar statusBar;

    private List<TextField> amplitudeTextFields = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private ControllerManager cm;
    private List<TextField> descriptionsTextFields = new ArrayList<>();
    private List<TextField> frequencyTextFields = new ArrayList<>();
    private LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
    private List<TextField> phaseTextFields = new ArrayList<>();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        fillListsOfUiElements();
        initComboBoxes();
        listenCheckBoxes();
        setDigitFilter();
    }


    private void fillListsOfUiElements() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsAmplitudeTextFields();
        fillListOfChannelsDescription();
        fillListOfChannelsFrequencyTextFields();
        fillListOfChannelsPhases();
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

    private void fillListOfChannelsDescription() {
        descriptionsTextFields.addAll(Arrays.asList(
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

    private void fillListOfChannelsAmplitudeTextFields() {
        amplitudeTextFields.addAll(Arrays.asList(
                amplitudeOfChannelN1,
                amplitudeOfChannelN2,
                amplitudeOfChannelN3,
                amplitudeOfChannelN4,
                amplitudeOfChannelN5,
                amplitudeOfChannelN6,
                amplitudeOfChannelN7,
                amplitudeOfChannelN8
        ));
    }

    private void fillListOfChannelsFrequencyTextFields() {
        frequencyTextFields.addAll(Arrays.asList(
                frequencyOfChannelN1,
                frequencyOfChannelN2,
                frequencyOfChannelN3,
                frequencyOfChannelN4,
                frequencyOfChannelN5,
                frequencyOfChannelN6,
                frequencyOfChannelN7,
                frequencyOfChannelN8
        ));
    }

    private void fillListOfChannelsPhases() {
        phaseTextFields.addAll(Arrays.asList(
                phaseOfChannelN1,
                phaseOfChannelN2,
                phaseOfChannelN3,
                phaseOfChannelN4,
                phaseOfChannelN5,
                phaseOfChannelN6,
                phaseOfChannelN7,
                phaseOfChannelN8
        ));
    }

    private void initComboBoxes() {
        addSignalTypes();
        addCalibrations();
        addDACModes();
    }

    private void addSignalTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("Синусоидальный");
        types.add("Прямоугольный");

        signalTypeComboBox.getItems().addAll(types);
        signalTypeComboBox.getSelectionModel().select(0);
    }

    private void addCalibrations() {
        ObservableList<String> calibrations = FXCollections.observableArrayList();

        calibrations.add("Не используются");
        calibrations.add("Заводские");

        calibrationComboBox.getItems().addAll(calibrations);
        calibrationComboBox.getSelectionModel().select(1);
    }


    private void addDACModes() {
        ObservableList<String> modes = FXCollections.observableArrayList();

        modes.add("Потоковый режим генерации");
        modes.add("Режим автогенерации");

        dacModeComboBox.getItems().addAll(modes);
        dacModeComboBox.getSelectionModel().select(0);
    }

    private void listenCheckBoxes() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            toggleChannelsUiElements(channelsCheckBoxes.get(channelIndex), channelIndex);
            listen(amplitudeTextFields, channelIndex);
            listen(frequencyTextFields, channelIndex);
            listen(phaseTextFields, channelIndex);
        }
    }

    private void toggleChannelsUiElements(CheckBox checkBox, int channelNumber) {
        checkBox.selectedProperty().addListener(observable -> {
            if (!checkBox.isSelected()) {
                resetSettings(channelNumber);
            }
            toggleUiElements(channelNumber, !checkBox.isSelected());
            checkConditionForTurningOnTheGenerateButton();
            checkConditionForTurningOffTheGenerateButton();
        });
    }

    private void toggleUiElements(int channelNumber, boolean isDisable) {
        amplitudeTextFields.get(channelNumber).setDisable(isDisable);
        descriptionsTextFields.get(channelNumber).setDisable(isDisable);
        frequencyTextFields.get(channelNumber).setDisable(isDisable);
        phaseTextFields.get(channelNumber).setDisable(isDisable);
    }

    private void checkConditionForTurningOnTheGenerateButton() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            if (channelsCheckBoxes.get(channelIndex).isSelected() &
                    !amplitudeTextFields.get(channelIndex).getText().isEmpty() &
                    !frequencyTextFields.get(channelIndex).getText().isEmpty() &
                    !phaseTextFields.get(channelIndex).getText().isEmpty()) {
                generateSignalButton.setDisable(false);
            }
        }
    }

    private void checkConditionForTurningOffTheGenerateButton() {
        int disabledChannelsCounter = 0;
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            if (channelsCheckBoxes.get(channelIndex).isSelected() & (amplitudeTextFields.get(channelIndex).getText().isEmpty() ||
                    frequencyTextFields.get(channelIndex).getText().isEmpty() ||
                    phaseTextFields.get(channelIndex).getText().isEmpty())) {
                generateSignalButton.setDisable(true);
            }

            if (!channelsCheckBoxes.get(channelIndex).isSelected()) {
                disabledChannelsCounter++;
            }
        }
        generateSignalButton.setDisable(disabledChannelsCounter == channelsCheckBoxes.size());
    }

    private void listen(List<TextField> textFields, int channelNumber) {
        textFields.get(channelNumber).textProperty().addListener(observable -> {
            checkConditionForTurningOnTheGenerateButton();
            checkConditionForTurningOffTheGenerateButton();
        });
    }

    private void resetSettings(int channelNumber) {
        channelsCheckBoxes.get(channelNumber).setSelected(false);
        amplitudeTextFields.get(channelNumber).setText("");
        descriptionsTextFields.get(channelNumber).setText("");
        frequencyTextFields.get(channelNumber).setText("");
        phaseTextFields.get(channelNumber).setText("");
    }

    private void setDigitFilter() {
        for (TextField textField : amplitudeTextFields) {
            setAmplitudeFilter(textField);
        }

        for (TextField textField : frequencyTextFields) {
            setFrequencyFilter(textField);
        }

        for (TextField textField : phaseTextFields) {
            setPhaseFilter(textField);
        }
    }

    /**
     * Ввод только цифр 1-10 в текстовых полях "Амплитуда"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setAmplitudeFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^[1-9]|(10)|$")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Ввод только цифр 1-50 в текстовых полях "Частота"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setFrequencyFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("(^[1-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|(50)|$)")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Ввод только цифр 0-360 в текстовых полях "Фаза"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setPhaseFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^(?:360|3[0-5]\\d|[12]\\d{2}|[1-9]\\d?)|0|$")) {
                textField.setText(oldValue);
            }
        });
    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr34SettingsModel.setModuleName(moduleName);
        ltr34SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        setLTR34Instance();
        setSettings();
    }

    private void setLTR34Instance() {
        HashMap<Integer, Module> modulesInstances = cm.getCrateModelInstance().getModulesList();
        ltr34SettingsModel.setLTR34Instance((LTR34) modulesInstances.get(ltr34SettingsModel.getSlot()));
    }

    private void setSettings() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            channelsCheckBoxes.get(channelIndex).setSelected(ltr34SettingsModel.getCheckedChannels()[channelIndex]);
            amplitudeTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getAmplitudes()[channelIndex]));
            descriptionsTextFields.get(channelIndex).setText(ltr34SettingsModel.getDescriptions()[channelIndex]);
            frequencyTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getFrequencies()[channelIndex]));

            if (ltr34SettingsModel.getPhases()[channelIndex] != 0) {
                phaseTextFields.get(channelIndex).setText(String.valueOf(ltr34SettingsModel.getPhases()[channelIndex]));
            } else if (channelsCheckBoxes.get(channelIndex).isSelected()) {
                phaseTextFields.get(channelIndex).setText("0");
            } else {
                phaseTextFields.get(channelIndex).setText("");
            }

            int dacMode = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.DAC_MODE.getSettingName());
            int factoryCalibration = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName());
            int signalType = ltr34SettingsModel.getLTR34Instance().getModuleSettings().get(DAC.Settings.SIGNAL_TYPE.getSettingName());
            calibrationComboBox.getSelectionModel().select(factoryCalibration);
            dacModeComboBox.getSelectionModel().select(dacMode);
            signalTypeComboBox.getSelectionModel().select(signalType);
        }
    }

    @FXML
    public void handleGenerateSignal() {
        changeUiElementsState();

        new Thread(() -> {
            saveChannelsSettings();
            saveModuleSettings();
            ltr34SettingsModel.initModule();
            checkModuleStatus();
            Platform.runLater(() -> statusBarLine.setStatus(ltr34SettingsModel.getLTR34Instance().getStatus(), statusBar));
        }).start();
    }

    private void changeUiElementsState() {
        toggleProgressIndicatorState(false);
        disableChannelsUiElements();
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void disableChannelsUiElements() {
        generateSignalButton.setDisable(true);
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            channelsCheckBoxes.get(channelIndex).setDisable(true);
            amplitudeTextFields.get(channelIndex).setDisable(true);
            descriptionsTextFields.get(channelIndex).setDisable(true);
            frequencyTextFields.get(channelIndex).setDisable(true);
            phaseTextFields.get(channelIndex).setDisable(true);
        }
    }

    private void saveChannelsSettings() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            if (channelsCheckBoxes.get(channelIndex).isSelected()) {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = true; // true - канал выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = parse(amplitudeTextFields.get(channelIndex));
                ltr34SettingsModel.getDescriptions()[channelIndex] = descriptionsTextFields.get(channelIndex).getText();
                ltr34SettingsModel.getFrequencies()[channelIndex] = parse(frequencyTextFields.get(channelIndex));
                ltr34SettingsModel.getPhases()[channelIndex] = parse(phaseTextFields.get(channelIndex));
            } else {
                ltr34SettingsModel.getCheckedChannels()[channelIndex] = false; // false - канал не выбран
                ltr34SettingsModel.getAmplitudes()[channelIndex] = 0;
                ltr34SettingsModel.getDescriptions()[channelIndex] = "";
                ltr34SettingsModel.getFrequencies()[channelIndex] = 0;
                ltr34SettingsModel.getPhases()[channelIndex] = 0;
            }
        }
    }

    private void saveModuleSettings() {
        int dacMode = dacModeComboBox.getSelectionModel().getSelectedIndex();
        int factoryCalibration = calibrationComboBox.getSelectionModel().getSelectedIndex();
        int signalType = signalTypeComboBox.getSelectionModel().getSelectedIndex();
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.DAC_MODE.getSettingName(), dacMode);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), factoryCalibration);
        ltr34SettingsModel.getLTR34Instance().getModuleSettings().put(DAC.Settings.SIGNAL_TYPE.getSettingName(), signalType);
    }

    private int parse(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    private void checkModuleStatus() {
        if (ltr34SettingsModel.getLTR34Instance().getStatus().equals("Операция успешно выполнена")) {
            ltr34SettingsModel.calculateSignal(signalTypeComboBox.getSelectionModel().getSelectedIndex());
            ltr34SettingsModel.generate(dacModeComboBox.getSelectionModel().getSelectedIndex() == 1);
            showGraph();
        } else {
            Platform.runLater(() -> {
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }
    }


    private void showGraph() {
        Platform.runLater(() -> {
            toggleUiElements();
            drawGraph();
        });
    }

    private void toggleUiElements() {
        toggleProgressIndicatorState(true);
        graph.setDisable(false);
        stopSignalButton.setDisable(false);
        stopSignalButton.requestFocus();
    }

    private void drawGraph() {
        for (int channelIndex = 0; channelIndex < ltr34SettingsModel.getLTR34Instance().getChannelsCount(); channelIndex++) {
            if (channelsCheckBoxes.get(channelIndex).isSelected()) {
                graph.getData().add(ltr34SettingsModel.createSeries(channelIndex));
            }
        }
    }

    @FXML
    public void handleStopSignal() {
        ltr34SettingsModel.stopModule();
        graph.getData().clear();
        enableChannelsUiElements();
    }

    private void enableChannelsUiElements() {
        for (int channelIndex = 0; channelIndex < channelsCheckBoxes.size(); channelIndex++) {
            channelsCheckBoxes.get(channelIndex).setDisable(false);
            amplitudeTextFields.get(channelIndex).setDisable(!channelsCheckBoxes.get(channelIndex).isSelected());
            descriptionsTextFields.get(channelIndex).setDisable(!channelsCheckBoxes.get(channelIndex).isSelected());
            frequencyTextFields.get(channelIndex).setDisable(!channelsCheckBoxes.get(channelIndex).isSelected());
            phaseTextFields.get(channelIndex).setDisable(!channelsCheckBoxes.get(channelIndex).isSelected());
        }

        graph.setDisable(true);
        generateSignalButton.setDisable(false);
        stopSignalButton.setDisable(true);
    }

    @FXML
    public void handleBackButton() {
        new Thread(() -> {
            setLTR34Instance();
            saveChannelsSettings();
            saveModuleSettings();
            prepareSettingsScene();
        }).start();

        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void prepareSettingsScene() {
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
