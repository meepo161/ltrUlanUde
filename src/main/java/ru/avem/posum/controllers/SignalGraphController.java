package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.GraphModel;
import ru.avem.posum.models.ReceivedSignal;
import ru.avem.posum.models.SignalModel;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SignalGraphController implements BaseController {
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
    private Label frequencyLabel;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private TextField loadsCounterTextField;
    @FXML
    private Label phaseLabel;
    @FXML
    private TextField rmsTextField;
    @FXML
    private Label zeroShiftLabel;
    @FXML
    private TextField zeroShiftTextField;
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> verticalScalesComboBox;

    private int averageCount = 1;
    private ControllerManager cm;
    private int decimalFormatScale = 100;
    private GraphModel graphModel = new GraphModel();
    private volatile XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private volatile boolean isDone;
    private SignalModel signalModel = new SignalModel();
    private WindowsManager wm;

    public void initializeView() {
        setTitleLabel();
        initializeGraph();
        initializeTextFields();
        initializeCheckBoxes();
        setSignalParametersLabels();
        checkCalibration();
        startShow();
    }

    private void setTitleLabel() {
        titleLabel.setText("Текущая нагрузка на " + (signalModel.getChannel() + 1) + " канале"
                + " (" + signalModel.getModuleType() + " слот " + signalModel.getSlot() + ")");
    }

    private void initializeGraph() {
        addGraphSeries();
        clearSeries();
        initializeGraphScale();
        toggleAutoScale(false);
    }

    private void clearSeries() {
        intermediateList.clear();
        graphSeries.getData().clear();
    }

    private void addGraphSeries() {
        graphSeries.getData().addAll(intermediateList);
        graph.getData().add(graphSeries);
    }

    private void initializeGraphScale() {
        addVerticalScaleValues();
        addHorizontalScaleValues();
        setDefaultScales();
        listenScalesComboBox(verticalScalesComboBox);
        listenScalesComboBox(horizontalScalesComboBox);
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

    private void setDefaultScales() {
        verticalScalesComboBox.getSelectionModel().select(3);
        graphModel.parseScale(verticalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) graph.getYAxis());

        horizontalScalesComboBox.getSelectionModel().select(2);
        graphModel.parseScale(horizontalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateBounds();
        setScale((NumberAxis) graph.getXAxis());
    }

    private void setScale(NumberAxis axis) {
        axis.setLowerBound(graphModel.getLowerBound());
        axis.setTickUnit(graphModel.getTickUnit());
        axis.setUpperBound(graphModel.getUpperBound());
    }

    private void listenScalesComboBox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                graphModel.parseScale(comboBox.getSelectionModel().getSelectedItem());
                graphModel.calculateBounds();

                if (comboBox == verticalScalesComboBox) {
                    setScale((NumberAxis) graph.getYAxis());
                } else {
                    setScale((NumberAxis) graph.getXAxis());
                }
            }
        });
    }

    private void toggleAutoScale(boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(isAutoRangeEnabled);
    }

    private void initializeTextFields() {
        addDecimalFormats();
        setDecimalFormat(1);
    }

    private void addDecimalFormats() {
        if (decimalFormatComboBox.getItems().isEmpty()) {
            ObservableList<String> strings = FXCollections.observableArrayList();
            for (int i = 1; i <= Utils.getDecimalScaleLimit(); i++) {
                strings.add(String.format("%d", i));
            }

            decimalFormatComboBox.getItems().addAll(strings);
        }
    }

    private void setDecimalFormat(int formatIndex) {
        decimalFormatComboBox.getSelectionModel().select(formatIndex);
    }

    private void initializeCheckBoxes() {
        listenAverageCheckBox();
        listenAutoScaleCheckBox();
        listenCalibrationCheckBox();
        initAverage();
    }

    private void listenAverageCheckBox() {
        averageCheckBox.selectedProperty().addListener(observable -> {
            if (averageCheckBox.isSelected()) {
                averageTextField.setDisable(false);
            } else {
                averageTextField.setDisable(true);
                averageTextField.setText("");
                averageCount = 1;
            }
        });
    }

    private void listenAutoScaleCheckBox() {
        autoScaleCheckBox.selectedProperty().addListener(observable -> {
            if (autoScaleCheckBox.isSelected()) {
                toggleAutoScale(true);
            } else {
                toggleAutoScale(false);
                setGraphBounds();
            }
        });
    }

    private void setGraphBounds() {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();

        yAxis.setLowerBound(signalModel.getLowerBound());
        yAxis.setUpperBound(signalModel.getUpperBound());
        yAxis.setTickUnit(signalModel.getTickUnit());
    }

    private void listenCalibrationCheckBox() {
        calibrationCheckBox.selectedProperty().addListener(observable -> {
            if (calibrationCheckBox.isSelected()) {
                checkCalibration();
                setValueNameToGraph();
                setGraphBounds();
                clearSeries();
                addGraphSeries();
                setSignalParametersLabels();
                setCalibrationExists(true);
            } else {
                setDefaultValueName();
                setSignalParametersLabels();
//                setDefaultBounds();
                setCalibrationExists(false);
            }
        });
    }

    public void checkCalibration() {
        signalModel.defineModuleInstance(cm.getCrateModelInstance().getModulesList());
        signalModel.checkCalibration();
        signalModel.parseCalibration();

        if (signalModel.isCalibrationExists()) {
            calibrationCheckBox.setSelected(true);
        }
    }

    private void setValueNameToGraph() {
        Platform.runLater(() -> graph.getYAxis().setLabel(signalModel.getValueName()));
    }

    private void setSignalParametersLabels() {
        amplitudeLabel.setText(String.format("Амлитуда, %s:", signalModel.getValueName()));
        frequencyLabel.setText("Частота, Гц:");
        phaseLabel.setText("Фаза, °:");
        zeroShiftLabel.setText(String.format("Статика, %s:", signalModel.getValueName()));
    }

    private void setCalibrationExists(boolean isExists) {
        signalModel.setCalibrationExists(isExists);
    }

    private void setDefaultValueName() {
        signalModel.setDefaultValueName();
        setValueNameToGraph();
    }

    private void initAverage() {
        setDigitFilter();
        changeAverageUiElementsState();
    }

    private void setDigitFilter() {
        averageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            averageTextField.setText(newValue.replaceAll("[^1-9][\\d]{2,3}", ""));
            if (!newValue.matches("^[1-9]|\\d{2,3}|$")) {
                averageTextField.setText(oldValue);
            }

            if (!averageTextField.getText().isEmpty()) {
                averageCount = Integer.parseInt(averageTextField.getText());
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

    private void startShow() {
        isStopped(false);
        receiveData();
        show();
    }

    private void receiveData() {
        new Thread(() -> {
            while (!cm.isClosed()) {
                getData();
                Utils.sleep(100);
            }
            isDone = true;
        }).start();
    }

    private void getData() {
        if (!signalModel.getAdc().isBusy()) {
            signalModel.getData(averageCount);
        }
    }

    private void show() {
        new Thread(() -> {
            while (!cm.isClosed()) {
                signalModel.processData();
                intermediateList.clear();
                fillSeries();
                showData();
                Utils.sleep(1000);
            }
        }).start();
    }

    private void fillSeries() {
        int scale = 1;
        if (signalModel.getModuleType().equals(CrateModel.LTR24)) {
            scale = 32;
        }

        double[] buffer = signalModel.getBuffer();
        int channels = signalModel.getAdc().getChannelsCount();
        for (int i = signalModel.getChannel(); i < buffer.length; i += channels * scale) {
            addPointToGraph(buffer, i);
        }
    }

    private void addPointToGraph(double[] buffer, int i) {
        if (signalModel.isCalibrationExists()) {
            ReceivedSignal receivedSignal = signalModel.getReceivedSignal();
            double calibratedValue = receivedSignal.applyCalibration(signalModel.getAdc(), buffer[i]);
            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, calibratedValue));
        } else {
            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
        }
    }

    private void showData() {
        double amplitude = Utils.roundValue(signalModel.getAmplitude(), decimalFormatScale);
        double loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), decimalFormatScale);
        double rms = Utils.roundValue(signalModel.getRms(), decimalFormatScale);
        double zeroShift = Utils.roundValue(signalModel.getZeroShift(), decimalFormatScale);

        isDone = false;
        Platform.runLater(() -> {
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, decimalFormatScale));
            loadsCounterTextField.setText(Utils.convertFromExponentialFormat(loadsCounter, decimalFormatScale));
            rmsTextField.setText(Utils.convertFromExponentialFormat(rms, decimalFormatScale));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, decimalFormatScale));
            isDone = true;
        });
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(signalModel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        String moduleType = signalModel.getModuleType();
        int slot = signalModel.getSlot();

        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
        isStopped(true);
        disableAutoRange();
        disableAverage();
        disableCalibration();
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

    public SignalModel getSignalModel() {
        return signalModel;
    }

    public int getDecimalFormatScale() {
        return decimalFormatScale;
    }

    public void setDecimalFormatScale() {
        decimalFormatScale = (int) Math.pow(10, decimalFormatComboBox.getSelectionModel().getSelectedIndex() + 1);
    }

    private void isStopped(boolean isStopped) {
        cm.setStopped(isStopped);
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
