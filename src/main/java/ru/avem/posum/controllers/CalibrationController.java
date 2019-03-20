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
import ru.avem.posum.models.CalibrationPoint;
import ru.avem.posum.models.Calibration;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

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
    private Label channelValueLabel;
    @FXML
    private ComboBox<String> channelValueMultiplierComboBox;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private TableColumn<CalibrationPoint, Double> loadChannelColumn;
    @FXML
    private ComboBox<String> loadValueMultiplierComboBox;
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
    private CheckBox setChannelValueCheckBox;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label titleLabel;

    private ADC adc;
    private ObservableList<CalibrationPoint> calibrationPoints = FXCollections.observableArrayList();
    private ContextMenu contextMenu = new ContextMenu();
    private int channel;
    private double channelValue;
    private double channelValueCoefficient;
    private ControllerManager cm;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private double loadValue;
    private double loadValueCoefficient;
    private String moduleType;
    private List<XYChart.Data<Double, Double>> rawData;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private int slot;
    private boolean stopped;
    private String valueName;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        initComboBoxes();
        listenSetChannelValueCheckBox();
        initColumns();
        initGraph();
        initTextFields();
        createContextMenu();
        addMouseListener();
    }

    private void initComboBoxes() {
        addCoefficientsList();
        setDefaultCoefficient();
    }

    private void listenSetChannelValueCheckBox() {
        setChannelValueCheckBox.selectedProperty().addListener(observable -> {
            if (setChannelValueCheckBox.isSelected()) {
                stopped = true;
                channelValueTextField.setEditable(true);
                channelValueTextField.setFocusTraversable(true);
                channelValueTextField.setMouseTransparent(false);
                channelValueTextField.setText("");
            } else {
                stopped = false;
                channelValueTextField.setEditable(false);
                channelValueTextField.setFocusTraversable(false);
                channelValueTextField.setMouseTransparent(true);
                showChannelValue();
            }
        });
    }

    private void addCoefficientsList() {
        ObservableList<String> coefficients = FXCollections.observableArrayList();

        coefficients.add("0.00001");
        coefficients.add("0.0001");
        coefficients.add("0.001");
        coefficients.add("0.01");
        coefficients.add("0.1");
        coefficients.add("1");
        coefficients.add("10");
        coefficients.add("100");
        coefficients.add("1000");
        coefficients.add("10000");
        coefficients.add("100000");

        channelValueMultiplierComboBox.setItems(coefficients);
        loadValueMultiplierComboBox.setItems(coefficients);
    }

    private void setDefaultCoefficient() {
        channelValueMultiplierComboBox.getSelectionModel().select(5);
        loadValueMultiplierComboBox.getSelectionModel().select(5);
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
        int selectedPoint = calibrationTableView.getSelectionModel().getSelectedIndex();

        if (selectedPoint != -1) {
            graphSeries.getData().remove(selectedPoint);
            calibrationPoints.remove(selectedPoint);
        }

        checkNumberOfCalibrationPoints();
    }

    private void checkNumberOfCalibrationPoints() {
        if (calibrationPoints.size() == 12) {
            changeState(true);
        } else {
            changeState(false);
        }

        if (calibrationPoints.size() < 2) {
            saveButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
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
            loadValueTextField.setText(newValue.replaceAll("[^-\\d.]", ""));
            if (!newValue.matches("(^[-]?[0-9]{1,5}\\.[0-9]{1,2}|$)|^[-]?[0-9]+\\.|^[-]?([0-9]{1,5})|^-")) {
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
        saveButton.setDisable(false);
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
    }

    private void loadCalibrationSettings() {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);

        for (String settings : calibrationSettings) {
            load(settings);
        }
    }

    private void load(String settings) {
        int channel = Integer.parseInt(settings.substring(9, 10));

        if (this.channel == channel) {
            loadValue = CalibrationPoint.parseLoadValue(settings);
            channelValue = CalibrationPoint.parseChannelValue(settings);
            valueName = CalibrationPoint.parseValueName(settings);

            showCalibration();
            setUiElements();
        }
    }

    private void showCalibration() {
        addCalibrationPointToTable();
        addPointToGraph();
        checkNumberOfCalibrationPoints();
    }

    private void addCalibrationPointToTable() {
        CalibrationPoint point = new CalibrationPoint(channel, loadValue, channelValue, valueName);
        calibrationPoints.add(point);
    }

    private void setUiElements() {
        loadValueTextField.setText(String.valueOf(loadValue));
        loadValueNameTextField.setText(String.valueOf(valueName));
    }

    public void handleAddPoint() {
        getCoefficients();
        parseData();
        showCalibration();
    }

    private void getCoefficients() {
        loadValueCoefficient = Double.parseDouble(loadValueMultiplierComboBox.getSelectionModel().getSelectedItem());
        channelValueCoefficient = Double.parseDouble(channelValueMultiplierComboBox.getSelectionModel().getSelectedItem());
    }


    private void parseData() {
        loadValue = Double.parseDouble(loadValueTextField.getText()) * loadValueCoefficient;
        channelValue = Double.parseDouble(channelValueTextField.getText()) * channelValueCoefficient;
        valueName = loadValueNameTextField.getText();
    }

    private void addPointToGraph() {
        int lastPointIndex = calibrationPoints.size() - 1;
        CalibrationPoint lastPoint = calibrationPoints.get(lastPointIndex);
        graphSeries.getData().add(new XYChart.Data<>(lastPoint.getLoadValue(), lastPoint.getChannelValue()));
    }

    public void handleBackButton() {
        stopped = true;
        clearCalibrationData();
        cm.checkCalibration();
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
        indicateResult();
    }

    private void savePoints() {
        adc.getCalibrationSettings().get(channel).clear();
        adc.getCalibrationSettings().get(channel).addAll(CalibrationPoint.toString(calibrationPoints));

        Calibration calibration = new Calibration();
        calibration.calibrate(adc, channel);
        adc.getCalibrationCoefficients().get(channel).clear();
        adc.getCalibrationCoefficients().get(channel).addAll(calibration.getCalibrationCoefficients());
    }

    private void indicateResult() {
        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }

    public void showChannelValue() {
        new Thread(() -> {
            while (!stopped) {
                Platform.runLater(() -> channelValueTextField.setText(String.format("%.5f", cm.getZeroShift())));
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
