package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR34SettingController implements BaseController {
    @FXML
    private Button generateSignalButton;
    @FXML
    private Button stopSignalButton;
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
    private LineChart<Number, Number> graph;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private StatusBar statusBar;
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

    private boolean stopped;
    private WindowsManager wm;
    private ControllerManager cm;
    private CrateModel crateModel;
    private boolean connectionOpen;
    private LTR34 ltr34 = new LTR34();
    private boolean[] checkedChannels;
    private int[][] channelsParameters;
    private double[] signal = new double[31_250]; // массив данных для генерации сигнала для каждого канала
    private StatusBarLine statusBarLine = new StatusBarLine();
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<TextField> amplitudeTextFields = new ArrayList<>();
    private List<TextField> frequencyTextFields = new ArrayList<>();
    private List<TextField> phasesTextFields = new ArrayList<>();

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
        phasesTextFields.addAll(Arrays.asList(
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
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            toggleChannelsUiElements(channelsCheckBoxes.get(i), i);
        }
    }

    /**
     * В массив checkedChannels сохраняются значения true - канал отмечен, false - канал не отмечен
     */
    private void toggleChannelsUiElements(CheckBox checkBox, int channel) {
        checkedChannels = ltr34.getCheckedChannels();

        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                checkedChannels[channel] = true;
                toggleUiElements(channel, false);
            } else {
                checkedChannels[channel] = false;
                toggleUiElements(channel, true);
                channelsCheckBoxes.get(channel).setSelected(false);
                amplitudeTextFields.get(channel).setText("");
                frequencyTextFields.get(channel).setText("");
                phasesTextFields.get(channel).setText("");
            }
            enableGenerateButton();
            disableGenerateButton();
        });

        amplitudeTextFields.get(channel).textProperty().addListener(observable -> {
            enableGenerateButton();
            disableGenerateButton();
        });

        frequencyTextFields.get(channel).textProperty().addListener(observable -> {
            enableGenerateButton();
            disableGenerateButton();
        });

    }

    private void toggleUiElements(int channel, boolean isDisable) {
        amplitudeTextFields.get(channel).setDisable(isDisable);
        frequencyTextFields.get(channel).setDisable(isDisable);
        phasesTextFields.get(channel).setDisable(isDisable);
    }


    private void enableGenerateButton() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected() &
                    !frequencyTextFields.get(i).getText().isEmpty() &
                    !amplitudeTextFields.get(i).getText().isEmpty()) {
                generateSignalButton.setDisable(false);
            }
        }
    }

    private void disableGenerateButton() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected() &
                    (frequencyTextFields.get(i).getText().isEmpty() ||
                            amplitudeTextFields.get(i).getText().isEmpty())) {
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

    private void setDigitFilter() {
        for (TextField textField : amplitudeTextFields) {
            setAmplitudeFilter(textField);
        }

        for (TextField textField : frequencyTextFields) {
            setFrequencyFilter(textField);
        }

        for (TextField textField : phasesTextFields) {
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
     * Ввод только цифр 1-360 в текстовых полях "Частота"
     *
     * @param textField текстовое поле к которому нужно применить фильтр
     */
    private void setPhaseFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^(?:360|3[0-5]\\d|[12]\\d{2}|[1-9]\\d?)|$")) {
                textField.setText(oldValue);
            }
        });
    }

    public void handleGenerateSignal() {
        toggleProgressIndicatorState(false);
        disableUiElements();

        new Thread(() -> {
            parseChannelsSettings();

            if (!connectionOpen) {
                ltr34.openConnection();
                connectionOpen = true;
            }
            ltr34.countChannels();
            ltr34.initModule();
            generate();

            Platform.runLater(() -> {
                statusBarLine.setStatus(ltr34.getStatus(), statusBar);
            });
        }).start();
    }

    private void generate() {
        if (ltr34.getStatus().equals("Операция успешно выполнена")) {
            createChannelsData();
            ltr34.dataSend(signal);
            ltr34.start();

            new Thread(() -> {
                stopped = false;
                while (!stopped) {
                    ltr34.dataSend(signal);
                    try {
                        Thread.sleep(1000); // пауза подобрана, чтобы ЦАП работал непрерывно
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Sent!");
                }
            }).start();

            Platform.runLater(() -> {
                toggleProgressIndicatorState(true);
                graph.setDisable(false);
                stopSignalButton.setDisable(false);
                stopSignalButton.requestFocus();
                drawGraph();
            });
        } else {
            Platform.runLater(() -> {
                toggleProgressIndicatorState(true);
                enableChannelsUiElements();
            });
        }
    }

    private void parseChannelsSettings() {
        int selectedCrate = cm.getSelectedCrate();
        String[] cratesSN = crateModel.getCrates()[0];
        int selectedSlot = cm.getSlot();
        channelsParameters = ltr34.getChannelsParameters();
        checkedChannels = ltr34.getCheckedChannels();

        ltr34.countChannels();
        ltr34.setCrate(cratesSN[selectedCrate]);
        ltr34.setSlot(selectedSlot);

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                checkedChannels[i] = true; // true - канал выбран
                int frequency = parse(frequencyTextFields.get(i));
                int amplitude = parse(amplitudeTextFields.get(i));
                int phase = parse(phasesTextFields.get(i));
                channelsParameters[0][i] = amplitude;
                channelsParameters[1][i] = frequency;
                channelsParameters[2][i] = phase;
            } else {
                checkedChannels[i] = false; // false - канал не выбран
                channelsParameters[0][i] = 0; // амплитуда
                channelsParameters[1][i] = 0; // частота
                channelsParameters[2][i] = 0; // фаза
            }
        }
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private int parse(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    private void createChannelsData() {
        channelsParameters = ltr34.getChannelsParameters();
        List<double[]> channelsData = new ArrayList<>();

        if (ltr34.getChannelsCounter() <= 4) {
            for (int i = 0; i < 4; i++) {
                channelsData.add(createSin(signal.length / 4, channelsParameters[0][i], channelsParameters[1][i], channelsParameters[2][i]));
            }
        } else {
            for (int i = 0; i < 8; i++) {
                channelsData.add(createSin(signal.length / 8, channelsParameters[0][i], channelsParameters[1][i], channelsParameters[2][i]));
            }
        }

        signal = mergeArrays(channelsData);
    }

    private double[] createSin(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = phase / 57.2958; // перевод градусов в радианы

        for (int i = 0; i < length; i++) {
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

    private void drawGraph() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            if (channelsCheckBoxes.get(i).isSelected()) {
                XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();

                graphSeries.setName("Канал " + (i + 1));
                graph.getData().add(graphSeries);

                int channels;
                if (ltr34.getChannelsCounter() <= 4) {
                    channels = 4;
                } else {
                    channels = 8;
                }

                for (int j = i; j < signal.length; j += channels * 100) { // коэффициент 100 введен для того, чтобы не отрисовывать все 500_000 точек
                    graphSeries.getData().add(new XYChart.Data<>((double) j / (signal.length - channels * 100 + i), signal[j]));
                }
            }
        }
    }

    private void disableUiElements() {
        generateSignalButton.setDisable(true);
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            amplitudeTextFields.get(i).setDisable(true);
            frequencyTextFields.get(i).setDisable(true);
            phasesTextFields.get(i).setDisable(true);
        }
    }

    public void handleStopSignal() {
        ltr34.stop();
        stopped = true;
        ltr34.closeConnection();
        connectionOpen = false;

        enableChannelsUiElements();
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
                phasesTextFields.get(i).setDisable(false);
            }
        }
    }

    public void handleBackButton() {
        new Thread(() -> {
            findLTR34Module();
            parseChannelsSettings();

            if (connectionOpen) {
                ltr34.closeConnection();
                connectionOpen = false;
            }

            clearView();

            cm.loadItemsForMainTableView();
            cm.loadItemsForModulesTableView();
        }).start();

        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void clearView() {
        graph.getData().clear();
        graph.setDisable(true);

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(false);
            if (channelsCheckBoxes.get(i).isSelected()) {
                frequencyTextFields.get(i).setDisable(false);
                amplitudeTextFields.get(i).setDisable(false);
            }
        }
    }

    public void loadSettings() {
        findLTR34Module();
        loadChannelsSettings();
    }

    private void findLTR34Module() {
        int slot = cm.getSlot();

        for (Pair<Integer, LTR34> module : crateModel.getLtr34ModulesList()) {
            if (module.getValue().getSlot() == slot) {
                ltr34 = module.getValue();
            }
        }
    }

    private void loadChannelsSettings() {
        checkedChannels = ltr34.getCheckedChannels();
        channelsParameters = ltr34.getChannelsParameters();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setSelected(checkedChannels[i]);
            amplitudeTextFields.get(i).setText(String.valueOf(channelsParameters[0][i]));
            frequencyTextFields.get(i).setText(String.valueOf(channelsParameters[1][i]));
            phasesTextFields.get(i).setText(String.valueOf(channelsParameters[2][i]));
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
}
