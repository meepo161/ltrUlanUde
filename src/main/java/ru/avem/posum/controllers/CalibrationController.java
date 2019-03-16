package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.hardware.ADC;
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
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private TableView<CalibrationPoint> calibrationTableView;
    @FXML
    private TableColumn<CalibrationPoint, Double> channelValueColumn;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private Label channelValueLabel;
    @FXML
    private TableColumn<CalibrationPoint, Double> loadChannelColumn;
    @FXML
    private Label loadValueLabel;
    @FXML
    private Label loadValueNameLabel;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField loadValueNameTextField;
    @FXML
    private TableColumn<CalibrationPoint, Double> valueNameColumn;
    @FXML
    private Button saveButton;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label titleLabel;

    private ADC adc;
    private ObservableList<CalibrationPoint> calibrationPoints = FXCollections.observableArrayList();
    private CalibrationModel calibrationModel;
    private ContextMenu contextMenu = new ContextMenu();
    private int channel;
    private double channelValue;
    private ControllerManager cm;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private double loadValue;
    private String moduleCalibrationSettings;
    private String moduleType;
    private List<XYChart.Data<Double, Double>> rawData;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private int slot;
    private boolean stopped;
    private String valueName;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        initColumns();
        initGraph();
        initTextFields();
        createContextMenu();
        addMouseListener();
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

    private void createContextMenu() {
        MenuItem menuItemCopy = new MenuItem("Копировать");
        MenuItem menuItemDelete = new MenuItem("Удалить");

        menuItemCopy.setOnAction(event -> copyCalibrationPoint());
        menuItemDelete.setOnAction(event -> deleteCalibrationPoint());

        contextMenu.getItems().addAll(menuItemCopy, menuItemDelete);
    }

    private void deleteCalibrationPoint() {
        calibrationPoints.remove(calibrationTableView.getSelectionModel().getSelectedIndex());
        checkNumberOfCalibrationPoints();
        checkSaveButtonState();
    }


    private void checkNumberOfCalibrationPoints() {
        if (calibrationPoints.size() == 7) {
            changeState(true);
        } else {
            changeState(false);
        }
    }

    private void changeState(boolean isDisable) {
        if (calibrationPoints.size() == 0) {
            loadValueNameTextField.setDisable(false);
        } else {
            loadValueNameTextField.setDisable(true);
        }

        loadValueLabel.setDisable(isDisable);
        loadValueTextField.setDisable(isDisable);
        channelValueLabel.setDisable(isDisable);
        channelValueTextField.setDisable(isDisable);
        loadValueNameLabel.setDisable(isDisable);
        addToTableButton.setDisable(isDisable);
    }

    private void copyCalibrationPoint() {
        if (calibrationPoints.size() <= 7) {
            calibrationPoints.add(calibrationPoints.get(calibrationTableView.getSelectionModel().getSelectedIndex()));
            checkNumberOfCalibrationPoints();
            checkSaveButtonState();
        }
    }

    private void addMouseListener() {
        calibrationTableView.setRowFactory(tv -> {
            TableRow<CalibrationPoint> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(calibrationTableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }
            });
            return row;
        });
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
            if (!loadValueTextField.getText().isEmpty() &
                    !channelValueTextField.getText().isEmpty() &
                    !loadValueNameTextField.getText().isEmpty() &
                    calibrationPoints.size() <= 7) {
                addToTableButton.setDisable(false);
            } else {
                addToTableButton.setDisable(true);
            }
        });
    }

    public void loadDefaults(ADC adc, String moduleType, int channel) {
        setFields(adc, moduleType, channel);
        setTitleLabel();
        loadDefaultUiElementsState();
        loadCalibrationSettings();
    }

    private void setFields(ADC adc, String moduleType, int channel) {
        this.adc = adc;
        this.moduleType = moduleType;
        this.slot = adc.getSlot();
        this.channel = channel;
        this.stopped = false;
    }

    private void setTitleLabel() {
        titleLabel.setText("Градуировка " + (channel + 1) + " канала" + " (" + moduleType + " слот " + slot + ")");
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

    public void handleAddPoint() {
        addPointToTable();
        addPointToGraph();
        checkNumberOfCalibrationPoints();
    }

    private void addPointToTable() {
        parseData();
        addCalibrationPointToTable();
        checkSaveButtonState();
    }

    private void parseData() {
        loadValue = Double.parseDouble(loadValueTextField.getText());
        channelValue = (double) Math.round(Double.parseDouble(channelValueTextField.getText()) * 1000) / 1000;
        valueName = loadValueNameTextField.getText();
    }

    private void addCalibrationPointToTable() {
        CalibrationPoint point = new CalibrationPoint(loadValue, channelValue, valueName);
        calibrationPoints.add(point);
    }

    private void checkSaveButtonState() {
        if (calibrationPoints.size() >= 2) {
            saveButton.setDisable(false);
            saveButton.requestFocus();
        } else {
            saveButton.setDisable(true);
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
                Platform.runLater(() -> channelValueTextField.setText(String.format("%.3f", cm.getZeroShift())));
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
