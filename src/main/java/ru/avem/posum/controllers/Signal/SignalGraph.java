package ru.avem.posum.controllers.Signal;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.Signal.SignalGraphModel;
import ru.avem.posum.models.Signal.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class SignalGraph implements BaseController {
    @FXML
    private Label amplitudeLabel;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private CheckBox autoScaleCheckBox;
    @FXML
    private CheckBox averageCheckBox;
    @FXML
    private TextField averageTextField;
    @FXML
    private CheckBox calibrationCheckBox;
    @FXML
    private ComboBox<String> decimalFormatComboBox;
    @FXML
    private ComboBox<String> frequencyCalculationComboBox;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private Label loadsCounterLabel;
    @FXML
    private TextField loadsCounterTextField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox<String> rarefactionCoefficientComboBox;
    @FXML
    private Label rmsLabel;
    @FXML
    private TextField rmsTextField;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> verticalScalesComboBox;
    @FXML
    private Label zeroShiftLabel;
    @FXML
    private TextField zeroShiftTextField;

    private ControllerManager cm;
    private SignalGraphModel signalGraphModel = new SignalGraphModel();
    private volatile XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private SignalModel signalModel = new SignalModel();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private WindowsManager wm;
    private double amplitude;
    private double frequency;
    private double loadsCounter;
    private double rms;
    private double zeroShift;

    public void initializeView() {
        setTitleLabel();
        initializeGraph();
        initializeComboBoxes();
        initializeTextFields();
        initializeCheckBoxes();
        checkCalibration();
        startShow();
    }

    private void setTitleLabel() {
        titleLabel.setText("Текущая нагрузка на " + (signalModel.getChannel() + 1) + " канале"
                + " (" + signalModel.getModuleType() + " слот " + signalModel.getSlot() + ")");
    }

    private void initializeGraph() {
        graph.getData().removeAll(graph.getData());
        graph.getData().add(graphSeries);
        clearSeries();
        toggleAutoScale(false);
    }

    private void clearSeries() {
        Platform.runLater(() -> graphSeries.getData().clear());
        Utils.sleep(50);
    }

    private void toggleAutoScale(boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(isAutoRangeEnabled);
        Utils.sleep(100);
    }

    private void initializeComboBoxes() {
        addVerticalScaleValues();
        addHorizontalScaleValues();
        addRarefactionCoefficients();
        setDefaultScales();
        listenScalesComboBox(verticalScalesComboBox);
        listenScalesComboBox(horizontalScalesComboBox);
        listenRarefactionComboBox();
        addFrequencyCalculations();
        listenFrequencyCalculationComboBox();
        addDecimalFormats();
    }

    private void addVerticalScaleValues() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();
        scaleValues.add("1 мВ/дел");
        scaleValues.add("10 мВ/дел");
        scaleValues.add("100 мВ/дел");
        scaleValues.add("1 В/дел");
        scaleValues.add("10 В/дел");
        scaleValues.add("100 В/дел");
        verticalScalesComboBox.setItems(scaleValues);
    }

    private void addHorizontalScaleValues() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();
        scaleValues.add("1 мс/дел");
        scaleValues.add("10 мс/дел");
        scaleValues.add("100 мс/дел");
        horizontalScalesComboBox.setItems(scaleValues);
    }

    private void addRarefactionCoefficients() {
        ObservableList<String> scaleValues = FXCollections.observableArrayList();
        scaleValues.add("Все");
        scaleValues.add("В 2 меньше");
        scaleValues.add("В 5 меньше");
        scaleValues.add("В 10 меньше");
        scaleValues.add("В 15 меньше");
        scaleValues.add("В 20 меньше");
        scaleValues.add("В 25 меньше");
        rarefactionCoefficientComboBox.setItems(scaleValues);
        rarefactionCoefficientComboBox.getSelectionModel().select(3);
    }

    private void setDefaultScales() {
        verticalScalesComboBox.getSelectionModel().select(3);
        signalGraphModel.parseGraphScale(verticalScalesComboBox.getSelectionModel().getSelectedItem());
        signalGraphModel.calculateGraphBounds();
        setScale((NumberAxis) graph.getYAxis());

        horizontalScalesComboBox.getSelectionModel().select(0);
        signalGraphModel.parseGraphScale(horizontalScalesComboBox.getSelectionModel().getSelectedItem());
        signalGraphModel.calculateGraphBounds();
        setScale((NumberAxis) graph.getXAxis());
    }

    private void setScale(NumberAxis axis) {
        axis.setLowerBound(signalGraphModel.getLowerBound());
        axis.setTickUnit(signalGraphModel.getTickUnit());
        axis.setUpperBound(signalGraphModel.getUpperBound());
    }

    private void listenScalesComboBox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                signalGraphModel.parseGraphScale(comboBox.getSelectionModel().getSelectedItem());
                signalGraphModel.calculateGraphBounds();

                if (comboBox == verticalScalesComboBox) {
                    setScale((NumberAxis) graph.getYAxis());
                } else {
                    setScale((NumberAxis) graph.getXAxis());
                }
            }
        });
    }

    private void listenRarefactionComboBox() {
        rarefactionCoefficientComboBox.valueProperty().addListener(observable -> {
            switch (rarefactionCoefficientComboBox.getSelectionModel().getSelectedIndex()) {
                case 0:
                    signalModel.setRarefactionCoefficient(1);
                    break;
                case 1:
                    signalModel.setRarefactionCoefficient(2);
                    break;
                case 2:
                    signalModel.setRarefactionCoefficient(5);
                    break;
                case 3:
                    signalModel.setRarefactionCoefficient(10);
                    break;
                case 4:
                    signalModel.setRarefactionCoefficient(15);
                    break;
                case 5:
                    signalModel.setRarefactionCoefficient(20);
                    break;
                case 6:
                    signalModel.setRarefactionCoefficient(25);
                    break;
            }

            int selectedScale = horizontalScalesComboBox.getSelectionModel().getSelectedIndex();
            int lastScale = horizontalScalesComboBox.getItems().size() - 1;
            if (selectedScale == lastScale) {
                horizontalScalesComboBox.getSelectionModel().select(lastScale - 1);
                Utils.sleep(1000);
                horizontalScalesComboBox.getSelectionModel().select(lastScale);
            } else {
                horizontalScalesComboBox.getSelectionModel().select(lastScale);
                Utils.sleep(1000);
                horizontalScalesComboBox.getSelectionModel().select(selectedScale);
            }
        });
    }

    private void addFrequencyCalculations() {
        frequencyCalculationComboBox.getItems().clear();
        ObservableList<String> chooses = FXCollections.observableArrayList();
        chooses.add("Точный расчет");
        chooses.add("Оценочный расчет");
        frequencyCalculationComboBox.getItems().addAll(chooses);
        frequencyCalculationComboBox.getSelectionModel().select(0);
    }

    private void listenFrequencyCalculationComboBox() {
        frequencyCalculationComboBox.valueProperty().addListener(observable -> {
            switch (frequencyCalculationComboBox.getSelectionModel().getSelectedIndex()) {
                case 0:
                    signalModel.setAccurateFrequencyCalculation(true);
                    break;
                case 1:
                    signalModel.setAccurateFrequencyCalculation(false);
                    break;
                default:
                    signalModel.setAccurateFrequencyCalculation(true);
            }
        });
    }

    private void addDecimalFormats() {
        if (decimalFormatComboBox.getItems().isEmpty()) {
            ObservableList<String> strings = FXCollections.observableArrayList();
            for (int i = 1; i <= Utils.getDecimalScaleLimit(); i++) {
                strings.add(String.format("%d", i));
            }
            decimalFormatComboBox.getItems().addAll(strings);
            decimalFormatComboBox.getSelectionModel().select(1);
        }
    }

    private void initializeTextFields() {
        resetCounters();
        setSignalParametersLabels();
    }

    private void resetCounters() {
        signalModel.setAmplitude(0);
        signalModel.setFrequency(0);
        signalModel.setLoadsCounter(0);
        signalModel.setRMS(0);
        signalModel.setZeroShift(0);
    }

    private void setSignalParametersLabels() {
        amplitudeLabel.setText(String.format("Амлитуда, %s:", signalModel.getValueName()));
        loadsCounterLabel.setText("Нагружений:");
        rmsLabel.setText(String.format("RMS, %s:", signalModel.getValueName()));
        zeroShiftLabel.setText(String.format("Статика, %s:", signalModel.getValueName()));
    }

    private void initializeCheckBoxes() {
        listenAverageCheckBox();
        initAverage();
        listenAutoScaleCheckBox();
        listenCalibrationCheckBox();
    }

    private void listenAverageCheckBox() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
                averageTextField.setText("");
                signalModel.setAverageCount(1);
            }
        });
    }

    private void initAverage() {
        setDigitFilterToAverageTextField();
        changeAverageUiElementsState();
    }

    private void setDigitFilterToAverageTextField() {
        averageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            averageTextField.setText(newValue.replaceAll("[^1-9][\\d]{2,3}", ""));
            if (!newValue.matches("^[1-9]|\\d{2,3}|$")) {
                averageTextField.setText(oldValue);
            }

            if (!averageTextField.getText().isEmpty()) {
                signalModel.setAverageCount(Integer.parseInt(averageTextField.getText()));
            }
        });
    }

    private void changeAverageUiElementsState() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
            }
        });
    }

    private void listenAutoScaleCheckBox() {
        autoScaleCheckBox.selectedProperty().addListener(observable -> {
            if (autoScaleCheckBox.isSelected()) {
                toggleAutoScale(true);
            } else {
                toggleAutoScale(false);
                resetGraphBounds();
            }
        });
    }

    private void setGraphBounds() {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setLowerBound(signalModel.getLowerBound());
        yAxis.setUpperBound(signalModel.getUpperBound());
        yAxis.setTickUnit(signalModel.getTickUnit());
    }

    private void resetGraphBounds() {
        if (signalModel.isCalibrationExists()) {
            setCalibratedGraphBounds();
        } else {
            setNonCalibratedGraphBounds();
        }
    }

    private void setCalibratedGraphBounds() {
        setGraphBounds();
        clearSeries();
    }

    private void setNonCalibratedGraphBounds() {
        int selectedRange = verticalScalesComboBox.getSelectionModel().getSelectedIndex();
        if (selectedRange != 0) {
            verticalScalesComboBox.getSelectionModel().select(0);
        } else {
            verticalScalesComboBox.getSelectionModel().select(1);
        }
        verticalScalesComboBox.getSelectionModel().select(selectedRange);
    }

    private void listenCalibrationCheckBox() {
        calibrationCheckBox.selectedProperty().addListener(observable -> {
            if (calibrationCheckBox.isSelected()) {
                checkCalibration();
            } else {
                signalModel.setCalibrationExists(false);
                signalModel.setDefaultValueName();
                verticalScalesComboBox.setDisable(false);
                setValueNameToGraph();
                setSignalParametersLabels();
                resetGraphBounds();
            }
        });
    }

    public void checkCalibration() {
        signalModel.defineModuleInstance(cm.getCrateModelInstance().getModulesList());
        signalModel.checkCalibration();

        if (signalModel.isCalibrationExists()) {
            clearSeries();
            calibrationCheckBox.setSelected(true);
            verticalScalesComboBox.setDisable(true);
            setValueNameToGraph();
            setGraphBounds();
            setSignalParametersLabels();
        } else {
            statusBarLine.setStatus("Градуировочные коэффициенты отсутсвуют", statusBar);
        }
    }

    private void setValueNameToGraph() {
        Platform.runLater(() -> graph.getYAxis().setLabel(signalModel.getValueName()));
    }

    private void startShow() {
        cm.setStopped(false);
        receiveData();
        showData();
    }

    private void receiveData() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.getData();
            }
        }).start();

        new Thread(() -> {
            clearSeries();
            signalModel.setAccurateFrequencyCalculation(false);
            Utils.sleep(2500);
            Platform.runLater(() -> {
                horizontalScalesComboBox.getSelectionModel().select(2);
                signalModel.setAccurateFrequencyCalculation(true);
            });
        }).start();
    }

    private void showData() {
        checkConnection();
        printData();
        printGraph();
    }

    private void checkConnection() {
        if (signalModel.isConnectionLost()) {
            signalModel.setFrequency(0);
            statusBarLine.setStatus(signalModel.getAdc().getStatus(), statusBar);
        }
    }

    private void printData() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.calculateData();
                showCalculatedValues();
                Utils.sleep(1000);
            }
        }).start();
    }

    private void showCalculatedValues() {
        setParametersFields();
        showParameters();
    }

    private void setParametersFields() {
        int decimalFormatScale = getDecimalFormatScale();
        if (signalModel.isCalibrationExists()) {
            amplitude = Utils.roundValue(signalModel.getCalibratedAmplitude(), decimalFormatScale);
            frequency = Utils.roundValue(signalModel.getFrequency(), decimalFormatScale);
            loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), decimalFormatScale);
            rms = Utils.roundValue(signalModel.getCalibratedRms(), decimalFormatScale);
            zeroShift = Utils.roundValue(signalModel.getCalibratedZeroShift(), decimalFormatScale);
        } else {
            amplitude = Utils.roundValue(signalModel.getAmplitude(), decimalFormatScale);
            frequency = Utils.roundValue(signalModel.getFrequency(), decimalFormatScale);
            loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), decimalFormatScale);
            rms = Utils.roundValue(signalModel.getRms(), decimalFormatScale);
            zeroShift = Utils.roundValue(signalModel.getZeroShift(), decimalFormatScale);
        }
    }

    private void showParameters() {
        Platform.runLater(() -> {
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, getDecimalFormatScale()));
            frequencyTextField.setText(Utils.convertFromExponentialFormat(frequency, getDecimalFormatScale()));
            loadsCounterTextField.setText(Utils.convertFromExponentialFormat(loadsCounter, getDecimalFormatScale()));
            rmsTextField.setText(Utils.convertFromExponentialFormat(rms, getDecimalFormatScale()));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, getDecimalFormatScale()));
        });
    }

    private void printGraph() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.fillBuffer();
                clearSeries();
                showGraph();
                Utils.sleep(1000);
            }
        }).start();
    }

    private void showGraph() {
        String selectedGraphScale = horizontalScalesComboBox.getSelectionModel().getSelectedItem();
        signalGraphModel.parseGraphScale(selectedGraphScale);
        signalGraphModel.calculateGraphBounds();
        int index;
        int channel = signalModel.getChannel();
        int channels = signalModel.getAdc().getChannelsCount();
        double[] data = signalModel.getBuffer();
        int scale = signalModel.getRarefactionCoefficient();

        for (index = channel; index < data.length && !cm.isStopped(); index += channels * scale) {
            XYChart.Data point = signalModel.getPoint(index);
            Runnable addPoint = () -> {
                if (!graphSeries.getData().contains(point))
                    graphSeries.getData().add(point);
            };

            if ((double) point.getXValue() < signalGraphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }

            if (index + (channels * scale) >= data.length) {
                XYChart.Data lastPoint = new XYChart.Data(1, data[index]);
                Platform.runLater(() -> graphSeries.getData().add(lastPoint));
                Utils.sleep(1);
            } else if ((double) point.getXValue() >= signalGraphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                break;
            }
        }
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(signalModel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        stopReceivingOfData();
        resetShowingSettings();
        changeScene();
    }

    private void stopReceivingOfData() {
        toggleProgressIndicatorState(false);
        cm.setStopped(true);
        Utils.sleep(1000);
        signalModel.getAdc().stop();
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void resetShowingSettings() {
        disableAutoRange();
        disableAverage();
        disableCalibration();
        signalModel.setLoadsCounter(0);
    }

    private void disableAutoRange() {
        toggleAutoScale(false);
        autoScaleCheckBox.setSelected(false);
    }

    private void disableAverage() {
        averageTextField.setText("");
        averageCheckBox.setSelected(false);
    }

    private void disableCalibration() {
        calibrationCheckBox.setSelected(false);
        signalModel.setCalibrationExists(false);
    }

    private void changeScene() {
        String moduleType = signalModel.getModuleType();
        int slot = signalModel.getSlot();
        toggleProgressIndicatorState(true);
        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
    }

    public int getDecimalFormatScale() {
        return (int) Math.pow(10, decimalFormatComboBox.getSelectionModel().getSelectedIndex() + 1);
    }

    public SignalModel getSignalModel() {
        return signalModel;
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
