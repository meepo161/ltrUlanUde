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
import ru.avem.posum.models.GraphModel;
import ru.avem.posum.models.SignalModel;
import ru.avem.posum.utils.Utils;

import java.sql.SQLOutput;
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
    private Label loadsCounterLabel;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private TextField loadsCounterTextField;
    @FXML
    private Label rmsLabel;
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
        clearGraph();
        clearSeries();
        initializeGraphScale();
        showGraph();
        toggleAutoScale(false);
    }

    private void clearGraph() {
        Platform.runLater(() -> {
            graph.getData().clear();
            graph.getData().add(graphSeries);
        });
        Utils.sleep(50);
    }

    private void clearSeries() {
        Platform.runLater(() -> {
            signalModel.getIntermediateList().clear();
            graphSeries.getData().clear();
        });
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
                showGraph();
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
        loadsCounterLabel.setText("Нагружений:");
        rmsLabel.setText("RMS, В:");
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
        cm.setStopped(false);
        receiveData();
        show();
    }

    private void receiveData() {
        new Thread(() -> {
            while (!cm.isClosed() || !cm.isStopped()) {
                signalModel.getData(averageCount);
                Utils.sleep(100);
            }
        }).start();
    }

    private void show() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.processData();
                signalModel.fillSeries();
                showData();
            }
        }).start();
    }

    private void showData() {
        showGraph();
        showValues();
    }

    private void showGraph() {
        List<XYChart.Data<Number, Number>> intermediateList = signalModel.getIntermediateList();
        String selectedScale = horizontalScalesComboBox.getSelectionModel().getSelectedItem();
        graphModel.parseScale(selectedScale);
        graphModel.calculateBounds();

        Platform.runLater(() -> graphSeries.getData().clear());
        Utils.sleep(50);

        int scale;
        if (signalModel.getFrequency() < 10) {
            scale = 10;
        } else if (signalModel.getFrequency() < 50) {
            scale = 2;
        } else {
            scale = 1;
        }

        int index;
        for (index = 0; index < intermediateList.size() && !cm.isStopped(); index += scale) {
            XYChart.Data point = intermediateList.get(index);
            Runnable addPoint = () -> graphSeries.getData().add(point);

            if ((double) point.getXValue() < graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                index += scale;
            }

            if (index == intermediateList.size() - scale) {
                XYChart.Data lastPoint = new XYChart.Data(graphModel.getUpperBound(), intermediateList.get(0).getYValue());
                Platform.runLater(() -> graphSeries.getData().add(lastPoint));
                Utils.sleep(100);
            } else if ((double) point.getXValue() >= graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(100);
                break;
            }
        }
    }

    private void showValues() {
        double amplitude = Utils.roundValue(signalModel.getAmplitude(), decimalFormatScale);
        double loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), decimalFormatScale);
        double rms = Utils.roundValue(signalModel.getRms(), decimalFormatScale);
        double zeroShift = Utils.roundValue(signalModel.getZeroShift(), decimalFormatScale);

        Platform.runLater(() -> {
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, decimalFormatScale));
            loadsCounterTextField.setText(Utils.convertFromExponentialFormat(loadsCounter, decimalFormatScale));
            rmsTextField.setText(Utils.convertFromExponentialFormat(rms, decimalFormatScale));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, decimalFormatScale));
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
        cm.setStopped(true);
        String moduleType = signalModel.getModuleType();
        int slot = signalModel.getSlot();
        wm.setModuleScene(moduleType, slot - 1);
        cm.loadItemsForModulesTableView();
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

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
