package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.CalibrationModel;
import ru.avem.posum.models.CalibrationPoint;
import ru.avem.posum.utils.LinearApproximation;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class CalibrationController implements BaseController {
    @FXML
    private Button addToTableButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label loadValueLabel;
    @FXML
    private Label channelValueLabel;
    @FXML
    private Label loadValueNameLabel;
    @FXML
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TableView<CalibrationPoint> calibrationTableView;
    @FXML
    private TableColumn<CalibrationPoint, Double> loadChannelColumn;
    @FXML
    private TableColumn<CalibrationPoint, Double> channelValueColumn;
    @FXML
    private TableColumn<CalibrationPoint, Double> valueNameColumn;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private TextField loadValueNameTextField;

    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private ObservableList<CalibrationPoint> calibrationPoints = FXCollections.observableArrayList();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private CrateModel.Moudules moduleType;
    private boolean stopped;
    private int channel;
    private ADC adc;
    private String moduleCalibrationSettings;
    private List<XYChart.Data<Double, Double>> rawData;
    private CalibrationModel calibrationModel;
    private double loadValue;
    private double channelValue;
    private String valueName;
    private ControllerManager cm;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        initColumns();
        initGraph();
        initTextFields();
    }

    private void initColumns() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
        valueNameColumn.setCellValueFactory(new PropertyValueFactory<>("valueName"));
    }

    private void initGraph() {
        calibrationTableView.setItems(calibrationPoints);
        calibrationGraph.getData().add(graphSeries);
    }

    private void initTextFields() {
        setDigitFilterToLoadValueTextField();
        toggleUiElementsIfEmptyField(loadValueTextField);
        toggleUiElementsIfEmptyField(loadValueNameTextField);
    }

    private void setDigitFilterToLoadValueTextField() {
        loadValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadValueTextField.setText(newValue.replaceAll("[^\\d.]", ""));
            if (!newValue.matches("(^[0-9]{1,5}\\.[0-9]{1,2}|$)|^[0-9]+\\.|([0-9]{1,5})")) {
                loadValueTextField.setText(oldValue);
            }
        });
    }

    private void toggleUiElementsIfEmptyField(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() & !channelValueTextField.getText().isEmpty() & !loadValueNameTextField.getText().isEmpty()) {
                addToTableButton.setDisable(false);
            } else {
                addToTableButton.setDisable(true);
            }
        });
    }

    public void loadDefaults(ADC adc, int channel) {
        setFields(adc, channel);
        loadDefaultUiElementsState();
        loadCalibrationSettings();
    }

    private void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
        this.stopped = false;
    }

    private void loadDefaultUiElementsState() {
        loadValueLabel.setDisable(false);
        channelValueLabel.setDisable(false);
        loadValueNameLabel.setDisable(false);
        loadValueTextField.setDisable(false);
        channelValueTextField.setDisable(false);
        loadValueNameTextField.setDisable(false);
        saveButton.setDisable(true);
    }

    private void loadCalibrationSettings() {

    }

    private void addPoint() {

    }

    public void handleAddPoint() {
        addPointToTable();
        addPointToGraph();
    }

    private void addPointToTable() {
        parseData();
        addCalibrationPointToTable();
        toggleUiElements();
    }

    private void parseData() {
        loadValue = Double.parseDouble(loadValueTextField.getText());
        channelValue = Double.parseDouble(channelValueTextField.getText());
        valueName = loadValueNameTextField.getText();
    }

    private void addCalibrationPointToTable() {
        CalibrationPoint point = new CalibrationPoint(loadValue, channelValue, valueName);
        calibrationPoints.add(point);
    }

    private void toggleUiElements() {
        if (calibrationPoints.size() >= 2) {
            saveButton.setDisable(false);
            saveButton.requestFocus();
        }
    }

    private void addPointToGraph() {
        int lastPointIndex = calibrationPoints.size() - 1;
        CalibrationPoint lastPoint = calibrationPoints.get(lastPointIndex);
        graphSeries.getData().add(new XYChart.Data<>(lastPoint.getLoadValue(), lastPoint.getChannelValue()));
    }

    public void handleBackButton() {
        stopped = true;
        clearCalibrationData();
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    private void clearCalibrationData() {
        loadValueTextField.setText("");
        channelValueTextField.setText("");
        loadValueNameTextField.setText("");
        calibrationPoints.clear();
        graphSeries.getData().clear();
    }

    public void handleSaveButton() {
        savePoints();
        approximatePoints();
        indicateResult();
    }

    private void savePoints() {
        calibrationModel = new CalibrationModel();
        calibrationModel.setChannel(channel);
//        adc.getCalibrationModel().add(calibrationModel);

        for (CalibrationPoint point : calibrationPoints) {
            calibrationModel.getLoadValue().add(point.getLoadValue());
            calibrationModel.getChannelValue().add(point.getChannelValue());
            calibrationModel.getValueName().add(point.getValueName());
        }
    }

    private void approximatePoints() {
        prepareData();
        approximate();
    }

    private void prepareData() {
        rawData = new ArrayList<>();

        for (CalibrationPoint point : calibrationPoints) {
            rawData.add(new XYChart.Data<>(point.getLoadValue(), point.getChannelValue()));
        }
    }

    private void approximate() {
        LinearApproximation approximation = new LinearApproximation(rawData, calibrationModel);
        approximation.createEquationSystem();
        approximation.calculateRoots();
    }

    private void indicateResult() {
        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }

    public void showChannelValue() {
        new Thread(() -> {
            while (!stopped) {
                Platform.runLater(() -> {
                    channelValueTextField.setText(String.valueOf(cm.getMaxValue()));
                });
                Utils.sleep(100);
            }
        }).start();
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
