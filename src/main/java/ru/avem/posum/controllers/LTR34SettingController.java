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
    private ComboBox crateSlot;
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

    private List<CheckBox> channelsCheckBoxes = new ArrayList<>();
    private List<TextField> frequencyTextFields = new ArrayList<>();
    private List<TextField> amplitudeTextFields = new ArrayList<>();

    private WindowsManager wm;
    private ControllerManager cm;
    private LTR34 ltr34 = new LTR34();

    private CrateModel crateModel;
    private int selectedCrate;
    private String[] cratesSN;
    private int selectedModule;
    private int selectedSlot;
    private int channels;

    private List<Pair<Integer, Integer>> signalParameters;
    double signal[] = new double[500_000]; // массив данных для генерации сигнала для каждого канала


    @FXML
    private void initialize() {
        fillListOfChannelsCheckBoxes();
        fillListOfChannelsFrequencyTextFields();
        fillListOfChannelsAmplitudeTextFields();

        addListOfCrateSlots(crateSlot);

        addListenerForAllChannels();
        setDigitFilter();
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

    private void setDefaultParameters() {
        crateSlot.getSelectionModel().select(0);
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

    public void handleGenerateSignal() {
        selectedCrate = cm.getSelectedCrate();
        cratesSN = crateModel.getCrates()[0];
        selectedModule = cm.getSelectedModule();
        selectedSlot = crateSlot.getSelectionModel().getSelectedIndex() + 1;

        ltr34.countChannels();
        ltr34.setCrate(cratesSN[selectedCrate]);
        ltr34.setSlot(selectedSlot);
        ltr34.initModule();

        if (ltr34.getStatus().equals("Операция успешно выполнена")) {
            String oldName = (crateModel.getModulesNames(selectedCrate).get(selectedModule));
            crateModel.getModulesNames(selectedCrate).set(selectedModule, oldName + " (" + crateSlot.getValue() + ")");

            calculateSignal();

            ltr34.dataSend(signal);
            ltr34.start();

            disableChannelsUiElements();
            generateSignalButton.setDisable(true);
            stopSignalButton.setDisable(false);
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

    private void disableChannelsUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(true);
            frequencyTextFields.get(i).setDisable(true);
            amplitudeTextFields.get(i).setDisable(true);
            crateSlot.setDisable(true);
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
        generateSignalButton.setDisable(false);
        stopSignalButton.setDisable(true);
    }

    private void enableChannelsUiElements() {
        for (int i = 0; i < channelsCheckBoxes.size(); i++) {
            channelsCheckBoxes.get(i).setDisable(false);
            if (channelsCheckBoxes.get(i).isSelected()) {
                frequencyTextFields.get(i).setDisable(false);
                amplitudeTextFields.get(i).setDisable(false);
            }
        }
        crateSlot.setDisable(false);
    }

    public void handleBackButton() {
        cm.loadItemsForMainTableView();
        cm.loadItemsForModulesTableView();
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }
}
