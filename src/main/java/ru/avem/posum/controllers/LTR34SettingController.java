package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR34;

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

    private WindowsManager wm;
    private ControllerManager cm;
    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<TextField> amplitudeTextFields = new ArrayList<>();
    private List<TextField> frequencyTextFields = new ArrayList<>();
    private LTR34 ltr34 = new LTR34();
    private CrateModel crateModel;
    private List<Pair<Integer, Integer>> signalParameters;
    private double[] signal = new double[500_000]; // массив данных для генерации сигнала для каждого канала


    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsFrequencyTextFields();
        fillListOfChannelsAmplitudeTextFields();

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

    private void addListenerForAllChannels() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            toggleChannelsUiElements(channelsCheckBoxes.get(i), i);
        }
    }

    /**
     * В массив checkedChannels сохраняются значения true - канал отмечен, false - канал не отмечен
     */
    private void toggleChannelsUiElements(CheckBox checkBox, int channel) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                ltr34.getCheckedChannels()[channel] = true;
                toggleUiElements(channel, false);
            } else {
                ltr34.getCheckedChannels()[channel] = false;
                toggleUiElements(channel, true);
                frequencyTextFields.get(channel).setText("");
                amplitudeTextFields.get(channel).setText("");
            }
            enableGenerateButton();
            disableGenerateButton();
        });

        frequencyTextFields.get(channel).textProperty().addListener(observable -> {
            enableGenerateButton();
            disableGenerateButton();
        });

        amplitudeTextFields.get(channel).textProperty().addListener(observable -> {
            enableGenerateButton();
            disableGenerateButton();
        });
    }

    private void toggleUiElements(int channel, boolean isDisable) {
        frequencyTextFields.get(channel).setDisable(isDisable);
        amplitudeTextFields.get(channel).setDisable(isDisable);
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

    public void handleGenerateSignal() {
        int selectedCrate;
        String[] cratesSN;
        int selectedSlot;

        selectedCrate = cm.getSelectedCrate();
        cratesSN = crateModel.getCrates()[0];
        selectedSlot = cm.getSlot();

        ltr34.countChannels();
        ltr34.setCrate(cratesSN[selectedCrate]);
        ltr34.setSlot(selectedSlot);
        ltr34.initModule();

        if (ltr34.getStatus().equals("Операция успешно выполнена")) {
            calculateSignal();

            ltr34.dataSend(signal);
            ltr34.start();

            drawGraph();

            disableUiElements();
        }

        statusBar.setText(ltr34.getStatus());
    }

    private void calculateSignal() {
        signalParameters = new ArrayList<>();

        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            int frequency = parse(frequencyTextFields.get(i));
            int amplitude = parse(amplitudeTextFields.get(i));
            signalParameters.add(new Pair<>(frequency, amplitude));
        }
        createChannelsData();
    }

    private int parse(TextField textField) {
        if (!textField.getText().isEmpty()) {
            return Integer.parseInt(textField.getText());
        } else {
            return 0;
        }
    }

    private void createChannelsData() {
        List<double[]> channelsData = new ArrayList<>();

        if (ltr34.getChannelsCounter() <= 4) {
            for (int i = 0; i < 4; i++) {
                channelsData.add(createSin(125_000, signalParameters.get(i).getValue(), signalParameters.get(i).getKey()));
            }
        } else {
            for (int i = 0; i < 8; i++) {
                channelsData.add(createSin(62_500, signalParameters.get(i).getValue(), signalParameters.get(i).getKey()));
            }
        }

        signal = mergeArrays(channelsData);
    }

    private double[] createSin(int length, int amplitude, int frequency) {
        double[] data = new double[length];

        for (int i = 0; i < length; i++) {
            data[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / length);
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

                for (int j = i; j < signal.length; j += channels * 100) {
                    graphSeries.getData().add(new XYChart.Data<>((double) j / (signal.length - channels * 100 + i), signal[j]));
                }
            }
        }
    }

    private void disableUiElements() {
        graph.setDisable(false);
        generateSignalButton.setDisable(true);
        stopSignalButton.setDisable(false);
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            frequencyTextFields.get(i).setDisable(true);
            amplitudeTextFields.get(i).setDisable(true);
        }
    }

    public void handleStopSignal() throws InterruptedException {
        ltr34.stop();

        for (int i = 0; i < signal.length; i++) {
            signal[i] = 0;
        }
        ltr34.initModule();
        ltr34.dataSend(signal);
        ltr34.start();
        Thread.sleep(1000);
        ltr34.stop();


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
                frequencyTextFields.get(i).setDisable(false);
                amplitudeTextFields.get(i).setDisable(false);
            }
        }
    }

    public void handleBackButton() {
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
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

    public LTR34 getLtr34() {
        return ltr34;
    }
}
