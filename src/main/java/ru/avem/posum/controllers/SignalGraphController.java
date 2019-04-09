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
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.models.GraphModel;
import ru.avem.posum.models.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

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
    private Label rmsLabel;
    @FXML
    private TextField rmsTextField;
    @FXML
    private StatusBar statusBar;
    @FXML
    private StatusBarLine statusBarLine = new StatusBarLine();
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> verticalScalesComboBox;
    @FXML
    private Label zeroShiftLabel;
    @FXML
    private TextField zeroShiftTextField;

    private ControllerManager cm;
    private GraphModel graphModel = new GraphModel();
    private volatile XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private SignalModel signalModel = new SignalModel();
    private WindowsManager wm;

    public void initializeView() {
        setTitleLabel();
        initializeGraph();
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
        graph.getData().removeAll();
        graph.getData().add(graphSeries);
        clearSeries();
        initializeGraphScale();
        toggleAutoScale(false);
    }

    private void clearSeries() {
        Platform.runLater(() -> graphSeries.getData().clear());
        Utils.sleep(50);
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
        graphModel.parseGraphScale(verticalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateGraphBounds();
        setScale((NumberAxis) graph.getYAxis());

        horizontalScalesComboBox.getSelectionModel().select(2);
        graphModel.parseGraphScale(horizontalScalesComboBox.getSelectionModel().getSelectedItem());
        graphModel.calculateGraphBounds();
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
                graphModel.parseGraphScale(comboBox.getSelectionModel().getSelectedItem());
                graphModel.calculateGraphBounds();

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
        Utils.sleep(100);
    }

    private void initializeTextFields() {
        addDecimalFormats();
        decimalFormatComboBox.getSelectionModel().select(1);
        setSignalParametersLabels();
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
            calibrationCheckBox.setSelected(true);
            verticalScalesComboBox.setDisable(true);
            setValueNameToGraph();
            setGraphBounds();
            setSignalParametersLabels();
            clearSeries();
            showGraph();
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
                Utils.sleep(100);
            }
        }).start();
    }

    private void showData() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.calculateData();
                showCalculatedValues();
                Utils.sleep(1000);
            }
        }).start();

        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.defineDataRarefactionCoefficient();
                signalModel.fillBuffer();
                clearSeries();
                showGraph();
                Utils.sleep(100);
            }
        }).start();
    }

    private void showCalculatedValues() {
        double amplitude = Utils.roundValue(signalModel.getAmplitude(), getDecimalFormatScale());
        double frequency = Utils.roundValue(signalModel.getFrequency(), getDecimalFormatScale());
        double loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), getDecimalFormatScale());
        double rms = Utils.roundValue(signalModel.getRms(), getDecimalFormatScale());
        double zeroShift = Utils.roundValue(signalModel.getZeroShift(), getDecimalFormatScale());

        Platform.runLater(() -> {
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, getDecimalFormatScale()));
            frequencyTextField.setText(Utils.convertFromExponentialFormat(frequency, getDecimalFormatScale()));
            loadsCounterTextField.setText(Utils.convertFromExponentialFormat(loadsCounter, getDecimalFormatScale()));
            rmsTextField.setText(Utils.convertFromExponentialFormat(rms, getDecimalFormatScale()));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, getDecimalFormatScale()));
        });
    }

    private void showGraph() {
        String selectedGraphScale = horizontalScalesComboBox.getSelectionModel().getSelectedItem();
        graphModel.parseGraphScale(selectedGraphScale);
        graphModel.calculateGraphBounds();
        int index;
        int channel = signalModel.getChannel();
        int channels = signalModel.getAdc().getChannelsCount();
        double[] data = signalModel.getBuffer();
        int scale = signalModel.getDataRarefactionCoefficient();

        for (index = channel; index < data.length && !cm.isStopped(); index += channels * scale) {
            XYChart.Data point = signalModel.getPoint(index);
            Runnable addPoint = () -> {
                if (!graphSeries.getData().contains(point))
                    graphSeries.getData().add(point);
            };

            if ((double) point.getXValue() < graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }

            if ((index == data.length - channels * scale) || index == data.length - 1) {
                XYChart.Data lastPoint = new XYChart.Data(1.01, data[data.length - channels * scale]);
                Platform.runLater(() -> graphSeries.getData().add(lastPoint));
                Utils.sleep(400);
            } else if ((double) point.getXValue() >= graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(400);
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
        cm.setStopped(true);
        disableAutoRange();
        disableAverage();
        disableCalibration();
        String moduleType = signalModel.getModuleType();
        int slot = signalModel.getSlot();
        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
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
