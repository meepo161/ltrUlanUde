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
import org.apache.poi.hssf.record.RecalcIdRecord;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.ReceivedSignal;
import ru.avem.posum.models.SignalGraphModel;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static ru.avem.posum.utils.Utils.sleep;

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
    private TextField frequencyTextField;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private Label phaseLabel;
    @FXML
    private TextField phaseTextField;
    @FXML
    private Label zeroShiftLabel;
    @FXML
    private TextField zeroShiftTextField;
    @FXML
    private Label titleLabel;

    private int averageCount = 1;
    private ControllerManager cm;
    private int decimalFormatScale = 100;
    private volatile XYChart.Series<Number, Number> graphSeries;
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
    private volatile boolean isDone;
    private SignalGraphModel signalGraphModel = new SignalGraphModel();
    private WindowsManager wm;

    public void initializeView() {
        isStopped(false);
        setTitleLabel();
        listenAverageCheckBox();
        listenAutoScaleCheckBox();
        listenCalibrationCheckBox();
        addDecimalFormats();
        setDefaultDecimalFormat();
        initializeGraph();
        initAverage();
        setSignalParametersLabels();
        startShow();
    }

    private void isStopped(boolean isStopped) {
        cm.setStopped(isStopped);
    }

    private void setTitleLabel() {
        titleLabel.setText("Текущая нагрузка на " + (signalGraphModel.getChannel() + 1) + " канале"
                + " (" + signalGraphModel.getModuleType() + " слот " + signalGraphModel.getSlot() + ")");
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

    private void toggleAutoScale(boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(isAutoRangeEnabled);
    }

    private void setGraphBounds() {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();

        yAxis.setLowerBound(signalGraphModel.getLowerBound());
        yAxis.setUpperBound(signalGraphModel.getUpperBound());
        yAxis.setTickUnit(signalGraphModel.getTickUnit());
    }

    private void listenCalibrationCheckBox() {
        calibrationCheckBox.selectedProperty().addListener(observable -> {
            if (calibrationCheckBox.isSelected()) {
                checkCalibration();
                setValueNameToGraph();
                setGraphBounds();
                clearGraph();
                setSignalParametersLabels();
                setCalibrationExists(true);
            } else {
                setDefaultValueName();
                setSignalParametersLabels();
                setDefaultBounds();
                setCalibrationExists(false);
            }
        });
    }

    public void checkCalibration() {
        signalGraphModel.defineModuleInstance(cm.getCrateModelInstance().getModulesList());
        signalGraphModel.checkCalibration();
        signalGraphModel.parseCalibration();

        if (signalGraphModel.isCalibrationExists()) {
            calibrationCheckBox.setSelected(true);
        }
    }

    private void setValueNameToGraph() {
        Platform.runLater(() -> graph.getYAxis().setLabel(signalGraphModel.getValueName()));
    }

    private void clearGraph() {
        intermediateList = new ArrayList<>();
        graphSeries = new XYChart.Series<>();
        graphSeries.getData().addAll(intermediateList);
        graph.getData().clear();
        graph.getData().add(graphSeries);
    }

    private void setSignalParametersLabels() {
        amplitudeLabel.setText(String.format("Амлитуда, %s:", signalGraphModel.getValueName()));
        frequencyLabel.setText("Частота, Гц:");
        phaseLabel.setText("Фаза, °:");
        zeroShiftLabel.setText(String.format("Статика, %s:", signalGraphModel.getValueName()));
    }

    private void setCalibrationExists(boolean isExists) {
        signalGraphModel.setCalibrationExists(isExists);
    }

    private void setDefaultValueName() {
        signalGraphModel.setDefaultValueName();
        setValueNameToGraph();
    }

    private void setDefaultBounds() {
        signalGraphModel.defineModuleInstance(cm.getCrateModelInstance().getModulesList());
        signalGraphModel.setICPMode(cm.getICPMode());
        signalGraphModel.getDefaultBounds();
        setGraphBounds();
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

    private void setDefaultDecimalFormat() {
        decimalFormatComboBox.getSelectionModel().select(1);
    }

    private void initializeGraph() {
        setDefaultBounds();
        toggleAutoScale(false);
        clearGraph();
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
        new Thread(() -> {
            while (!cm.isClosed()) {
                getData();
                showData();
                pause();
            }
            isDone = true;
        }).start();
    }

    private void getData() {
        if (!signalGraphModel.getAdc().isBusy()) {
            signalGraphModel.getData(averageCount);
            clearSeries();
            fillSeries();
        }
    }

    private void clearSeries() {
        intermediateList.clear();
    }

    private void fillSeries() {
        int scale = 1;
        if (signalGraphModel.getModuleType().equals(CrateModel.LTR24)) {
            scale = 32;
        }

        double[] buffer = signalGraphModel.getBuffer();
        int channels = signalGraphModel.getAdc().getChannelsCount();
        for (int i = signalGraphModel.getChannel(); i < buffer.length; i += channels * scale) {
            addPointToGraph(buffer, i);
        }
    }

    private void addPointToGraph(double[] buffer, int i) {
        if (signalGraphModel.isCalibrationExists()) {
            ReceivedSignal receivedSignal = signalGraphModel.getReceivedSignal();
            double calibratedValue = receivedSignal.applyCalibration(signalGraphModel.getAdc(), buffer[i]);
            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, calibratedValue));
        } else {
            intermediateList.add(new XYChart.Data<>((double) i / buffer.length, buffer[i]));
        }
    }

    private void showData() {
        double amplitude = Utils.roundValue(signalGraphModel.getAmplitude(), decimalFormatScale);
        double frequency = Utils.roundValue(signalGraphModel.getFrequency(), decimalFormatScale);
        double phase = Utils.roundValue(signalGraphModel.getPhase(), decimalFormatScale);
        double zeroShift = Utils.roundValue(signalGraphModel.getZeroShift(), decimalFormatScale);

        isDone = false;
        Platform.runLater(() -> {
            graphSeries.getData().clear();
            graphSeries.getData().addAll(intermediateList);
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, decimalFormatScale));
            frequencyTextField.setText(Utils.convertFromExponentialFormat(frequency, decimalFormatScale));
            phaseTextField.setText(Utils.convertFromExponentialFormat(phase, decimalFormatScale));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, decimalFormatScale));
            isDone = true;
        });
    }

    private void pause() {
        while (!isDone && !cm.isClosed()) {
            sleep(1000);
        }
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(signalGraphModel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        String moduleType = signalGraphModel.getModuleType();
        int slot = signalGraphModel.getSlot();

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
        signalGraphModel.setCalibrationExists(false);
    }

    public int getDecimalFormatScale() {
        return decimalFormatScale;
    }

    public SignalGraphModel getSignalGraphModel() {
        return signalGraphModel;
    }

    public void setDecimalFormatScale() {
        decimalFormatScale = (int) Math.pow(10, decimalFormatComboBox.getSelectionModel().getSelectedIndex() + 1);
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
