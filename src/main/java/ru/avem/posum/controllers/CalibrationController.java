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
import ru.avem.posum.models.CalibrationModel;
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
    private TableColumn<CalibrationPoint, String> channelValueColumn;
    @FXML
    private Label channelValueLabel;
    @FXML
    private ComboBox<String> channelValueMultiplierComboBox;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private TableColumn<CalibrationPoint, String> loadChannelColumn;
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
    private int decimalFormatScale;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private double loadValue;
    private double loadValueCoefficient;
    private String moduleType;
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
        setDigitFilterToTextField(channelValueTextField);
        setDigitFilterToTextField(loadValueTextField);
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
    }

    private void initGraph() {
        calibrationTableView.setItems(calibrationPoints);
        calibrationGraph.getData().add(graphSeries);
    }

    private void initTextFields() {
        toggleUiElementsIfEmptyField(loadValueTextField);
        toggleUiElementsIfEmptyField(loadValueNameTextField);
    }

    private void createContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteCalibrationPoint());
        menuItemClear.setOnAction(event -> clearCalibrationPoints());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void checkNumberOfCalibrationPoints() {
        int MAX_CALIBRATION_POINTS = 20;
        if (calibrationPoints.size() == MAX_CALIBRATION_POINTS) {
            changeState(true);
        } else {
            changeState(false);
        }

        int MIN_CALIBRATION_POINTS = 2;
        if (calibrationPoints.size() < MIN_CALIBRATION_POINTS) {
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

    private void deleteCalibrationPoint() {
        int selectedPointIndex = calibrationTableView.getSelectionModel().getSelectedIndex();
        graphSeries.getData().remove(selectedPointIndex);
        calibrationPoints.remove(selectedPointIndex);
        checkNumberOfCalibrationPoints();
    }

    private void clearCalibrationPoints() {
        graphSeries.getData().clear();
        calibrationPoints.clear();
        checkNumberOfCalibrationPoints();
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

    private void setDigitFilterToTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void toggleUiElementsIfEmptyField(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() &
                    !channelValueTextField.getText().isEmpty() &
                    !loadValueNameTextField.getText().isEmpty() &
                    calibrationPoints.size() <= 20) {
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

        decimalFormatScale = cm.getDecimalFormatScale();
        setUiElements();
    }

    private void load(String settings) {
        int channel = Integer.parseInt(settings.substring(9, 10));

        if (this.channel == channel) {
            loadValue = CalibrationPoint.parseLoadValue(settings);
            channelValue = CalibrationPoint.parseChannelValue(settings);
            valueName = CalibrationPoint.parseValueName(settings);

            showCalibration();
        }
    }

    private void setUiElements() {
        loadValueTextField.setText(String.valueOf(Utils.roundValue(loadValue, decimalFormatScale)));
        loadValueNameTextField.setText(valueName);
    }

    private void showCalibration() {
        addCalibrationPointToTable();
        addPointToGraph();
        checkNumberOfCalibrationPoints();
    }

    private void addCalibrationPointToTable() {
        CalibrationPoint point = new CalibrationPoint(channel, loadValue, channelValue, valueName);
        setColumnTitle(loadChannelColumn, valueName);
        calibrationPoints.add(point);
    }

    private void addPointToGraph() {
        int lastPointIndex = calibrationPoints.size() - 1;
        CalibrationPoint lastPoint = calibrationPoints.get(lastPointIndex);
        double xValue = Double.parseDouble(lastPoint.getLoadValue());
        double yValue = Double.parseDouble(lastPoint.getChannelValue());

        try {
            graphSeries.getData().add(new XYChart.Data<>(xValue, yValue));
        } catch (NumberFormatException e) {
            System.out.println("Point added");
        }
    }

    private void setColumnTitle(TableColumn<CalibrationPoint, String> column, String valueName) {
        column.textProperty().set("Величина нагрузки, " + valueName);
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
        decimalFormatScale = cm.getDecimalFormatScale();

        if (setChannelValueCheckBox.isSelected()) {
            channelValue = parseFrom(channelValueTextField, channelValueCoefficient);
        } else {
            channelValue = Utils.roundValue(cm.getZeroShift(), decimalFormatScale) * channelValueCoefficient;
        }

        loadValue = parseFrom(loadValueTextField, loadValueCoefficient);
        valueName = loadValueNameTextField.getText();
    }

    private double parseFrom(TextField textField, double multiplierCoefficient) {
        if (!textField.getText().equals("-")) {
            String digits = textField.getText().replaceAll(",", ".");
            double value = Utils.roundValue(Double.valueOf(digits), decimalFormatScale);

            return  value * multiplierCoefficient;
        } else {
            return 0;
        }
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
        setChannelValueCheckBox.setSelected(false);
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

        CalibrationModel calibrationModel = new CalibrationModel();
        calibrationModel.calibrate(adc, channel);
        adc.getCalibrationCoefficients().get(channel).clear();
        adc.getCalibrationCoefficients().get(channel).addAll(calibrationModel.getCalibrationCoefficients());
    }

    private void indicateResult() {
        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }

    public void showChannelValue() {
        decimalFormatScale = cm.getDecimalFormatScale();

        new Thread(() -> {
            setValueName();
            while (!stopped) {
                double value = Utils.roundValue(cm.getZeroShift(), decimalFormatScale);
                Platform.runLater(() -> channelValueTextField.setText(String.valueOf(value)));
                Utils.sleep(100);
            }
        }).start();
    }

    private void setValueName() {
        Platform.runLater(() -> channelValueLabel.setText(String.format("Значение, %s:", cm.getValueName())));
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
