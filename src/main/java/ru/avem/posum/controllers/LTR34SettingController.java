package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private Button stopSignalButton;
    @FXML
    private StatusBar statusBar;

    private int[] amplitudes;
    private List<TextField> amplitudeTextFields = new ArrayList<>();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private boolean[] checkedChannels;
    private ControllerManager cm;
    private boolean connectionOpen;
    private CrateModel crateModel;
    private int[] frequencies;
    private List<TextField> frequencyTextFields = new ArrayList<>();
    private LTR34 ltr34 = new LTR34();
    private String moduleName;
    private int[] phases;
    private List<TextField> phaseTextFields = new ArrayList<>();
    private double[] signal = new double[31_250]; // массив данных для генерации сигнала для каждого канала
    private int slot;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private boolean stopped;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsAmplitudeTextFields();
        fillListOfChannelsFrequencyTextFields();
        fillListOfChannelsPhases();

        addListenerForAllChannels();
        setDigitFilter();
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

    private void addListenerForAllChannels() {
        for (int channel = 0; channel < channelsCheckBoxes.size(); channel++) {
            toggleChannelsUiElements(channelsCheckBoxes.get(channel), channel);
            addListener(amplitudeTextFields, channel);
            addListener(frequencyTextFields, channel);
            addListener(phaseTextFields, channel);
        }
    }

    private void toggleChannelsUiElements(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                toggleUiElements(channel, false);
            } else {
                toggleUiElements(channel, true);
                setDefaultSettings(channel);
            }
            checkConditionForGenerateButtonTurningOn();
            checkConditionForGenerateButtonTurningOff();
        });
    }

    private void checkConditionForGenerateButtonTurningOn() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected() &
                    !amplitudeTextFields.get(i).getText().isEmpty() &
                    !frequencyTextFields.get(i).getText().isEmpty() &
                    !phaseTextFields.get(i).getText().isEmpty()) {
                generateSignalButton.setDisable(false);
            }
        }
    }

    private void checkConditionForGenerateButtonTurningOff() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected() & (amplitudeTextFields.get(i).getText().isEmpty() ||
                    frequencyTextFields.get(i).getText().isEmpty() ||
                    phaseTextFields.get(i).getText().isEmpty())) {
                generateSignalButton.setDisable(true);
            }
        }

        int disabledChannelsCounter = 0;
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (!channelsCheckBoxes.get(i).isSelected()) {
                disabledChannelsCounter++;
            }
            if (disabledChannelsCounter == channelsCheckBoxes.size()) {
                generateSignalButton.setDisable(true);
            }
        }
    }

    private void addListener(List<TextField> textFields, int channel) {
        textFields.get(channel).textProperty().addListener(observable -> {
            checkConditionForGenerateButtonTurningOn();
            checkConditionForGenerateButtonTurningOff();
        });
    }

    private void toggleUiElements(int channel, boolean isDisable) {
        amplitudeTextFields.get(channel).setDisable(isDisable);
        frequencyTextFields.get(channel).setDisable(isDisable);
        phaseTextFields.get(channel).setDisable(isDisable);
    }

    private void setDefaultSettings(int channel) {
        channelsCheckBoxes.get(channel).setSelected(false);
        amplitudeTextFields.get(channel).setText("");
        frequencyTextFields.get(channel).setText("");
        phaseTextFields.get(channel).setText("");
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
        setField(moduleName);
        parseSlotNumber();
        setTitleLabel(moduleName);
        findLTR34Module();
        loadChannelsSettings();
    }

    private void setField(String moduleName) {
        this.moduleName = moduleName;
    }

    private void parseSlotNumber() {
        slot = Utils.parseSlotNumber(moduleName);
    }

    private void setTitleLabel(String moduleName) {
        sceneTitleLabel.setText("Настройки модуля " + moduleName);
    }

    private void findLTR34Module() {
        HashMap<Integer, Module> modules = cm.getCrateModelInstance().getModulesList();
        ltr34 = (LTR34) modules.get(slot);
    }

    private void loadChannelsSettings() {
        loadSettingsFields();
        setSettings();
    }

    private void loadSettingsFields() {
        checkedChannels = ltr34.getCheckedChannels();
        amplitudes = ltr34.getAmplitudes();
        frequencies = ltr34.getFrequencies();
        phases = ltr34.getPhases();
    }

    private void setSettings() {
        for (int i = 0; i < ltr34.getChannelsCount(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            amplitudeTextFields.get(i).setText(String.valueOf(amplitudes[i]));
            frequencyTextFields.get(i).setText(String.valueOf(frequencies[i]));

            if (channelsCheckBoxes.get(i).isSelected() && phases[i] == 0) {
                phaseTextFields.get(i).setText("0");
            } else if (!channelsCheckBoxes.get(i).isSelected() && phases[i] == 0) {
                phaseTextFields.get(i).setText("");
            } else {
                phaseTextFields.get(i).setText(String.valueOf(phases[i]));
            }
        }
    }

    public void handleGenerateSignal() {
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
        generateSignalButton.setDisable(true);
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            amplitudeTextFields.get(i).setDisable(true);
            frequencyTextFields.get(i).setDisable(true);
            phaseTextFields.get(i).setDisable(true);
        }
    }

    private void saveChannelsSettings() {
        for (int i = 0; i < ltr34.getChannelsCount(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                checkedChannels[i] = true; // true - канал выбран
                amplitudes[i] = parse(amplitudeTextFields.get(i));
                frequencies[i] = parse(frequencyTextFields.get(i));
                phases[i] = parse(phaseTextFields.get(i));
            } else {
                checkedChannels[i] = false; // false - канал не выбран
                amplitudes[i] = 0;
                frequencies[i] = 0;
                phases[i] = 0;
            }
        }
    }

    private int parse(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    private void initializeModule() {
        if (!connectionOpen) {
            ltr34.openConnection();
            connectionOpen = true;
        }

        ltr34.countChannels();
        ltr34.initModule();
    }

    private void checkResult() {
        if (ltr34.getStatus().equals("Операция успешно выполнена")) {
            generate();
        } else {
            Platform.runLater(() -> {
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }
    }

    private void generate() {
        createDataForGenerating();
        prepareGenerating();
        startGenerating();
        showGraph();
    }

    private void createDataForGenerating() {
        List<double[]> channelsData = new ArrayList<>();

        if (ltr34.getCheckedChannelsCounter() <= 4) {
            for (int i = 0; i < 4; i++) {
                channelsData.add(createSin(signal.length / 4, amplitudes[i], frequencies[i], phases[i]));
            }
        } else {
            for (int i = 0; i < 8; i++) {
                channelsData.add(createSin(signal.length / 8, amplitudes[i], frequencies[i], phases[i]));
            }
        }

        signal = mergeArrays(channelsData);
    }

    private double[] createSin(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 1; i < length; i++) {
            data[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / length + channelPhase);
        }

        return data;
    }

    private static double[] mergeArrays(List<double[]> channelsData) {
        int resultArraySize = 0;
        int numOfArrays = 0;
        for (double[] array : channelsData) {
            resultArraySize += array.length;
            numOfArrays++;
        }
        double[] resultArray = new double[resultArraySize];
        int[] countsOfArrays = new int[numOfArrays];

        for (int i = 0; i < resultArraySize; ) {
            double[] currentArray = channelsData.get(i % numOfArrays);
            int countsOfArray = countsOfArrays[i % numOfArrays]++;
            resultArray[i++] = currentArray[countsOfArray];
        }

        return resultArray;
    }

    private void prepareGenerating() {
        ltr34.generate(signal);
        ltr34.start();
        stopped = false;
    }

    private void startGenerating() {
        new Thread(() -> {
            while (!stopped) {
                ltr34.generate(signal);
                Utils.sleep(1000);
            }
        }).start();
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
        for (int channel = 0; channel < ltr34.getChannelsCount(); channel++) {
            if (channelsCheckBoxes.get(channel).isSelected()) {
                XYChart.Series<Number, Number> channelSeries = createSeries(channel);
                addSeries(channel, channelSeries);
            }
        }
    }

    private XYChart.Series<Number, Number> createSeries(int channel) {
        XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();

        graphSeries.setName("Канал " + (channel + 1));
        graph.getData().add(graphSeries);

        return graphSeries;
    }

    private void addSeries(int channel, XYChart.Series<Number, Number> graphSeries) {
        int channels;
        if (ltr34.getCheckedChannelsCounter() <= 4) {
            channels = 4;
        } else {
            channels = 8;
        }

        for (int j = channel; j < signal.length; j += channels * 10) { // коэффициент 10 введен для того, чтобы не отрисовывать все точки
            graphSeries.getData().add(new XYChart.Data<>((double) j / (signal.length - channels * 10 + channel), signal[j]));
        }
    }

    private void indicateResult() {
        Platform.runLater(() -> statusBarLine.setStatus(ltr34.getStatus(), statusBar));
    }

    public void handleStopSignal() {
        stopModule();
        clearGraph();
        enableChannelsUiElements();
    }

    private void stopModule() {
        if (connectionOpen) {
            ltr34.stop();
            stopped = true;
            ltr34.closeConnection();
            connectionOpen = false;
        }
    }

    private void clearGraph() {
        graph.getData().clear();
    }

    private void enableChannelsUiElements() {
        graph.setDisable(true);
        generateSignalButton.setDisable(false);
        stopSignalButton.setDisable(true);
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(false);
            if (channelsCheckBoxes.get(i).isSelected()) {
                amplitudeTextFields.get(i).setDisable(false);
                frequencyTextFields.get(i).setDisable(false);
                phaseTextFields.get(i).setDisable(false);
            }
        }
    }

    public void handleBackButton() {
        new Thread(() -> {
            findLTR34Module();
            saveChannelsSettings();
            prepareSettingsScene();
        }).start();

        changeScene();
    }

    private void prepareSettingsScene() {
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
    }

    private void changeScene() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
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
