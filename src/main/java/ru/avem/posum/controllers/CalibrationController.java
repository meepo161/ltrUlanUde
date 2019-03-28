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
import ru.avem.posum.models.SignalGraphModel;
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

    private CalibrationModel calibrationModel = new CalibrationModel();
    private ContextMenu contextMenu = new ContextMenu();
    private ControllerManager cm;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private SignalGraphModel signalGraphModel;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private boolean stopped;
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

    private void setDigitFilterToTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
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
        calibrationTableView.setItems(calibrationModel.getCalibrationPoints());
        calibrationGraph.getData().add(graphSeries);
    }

    private void initTextFields() {
        toggleUiElementsIfEmptyField(loadValueTextField);
        toggleUiElementsIfEmptyField(loadValueNameTextField);
    }

    private void toggleUiElementsIfEmptyField(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() &
                    !channelValueTextField.getText().isEmpty() &
                    !loadValueNameTextField.getText().isEmpty() &
                    calibrationModel.getCalibrationPoints().size() <= 20) {
                addToTableButton.setDisable(false);
            } else {
                addToTableButton.setDisable(true);
            }
        });
    }

    private void createContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteCalibrationPoint());
        menuItemClear.setOnAction(event -> clearCalibrationPoints());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
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

    private void deleteCalibrationPoint() {
        int selectedPointIndex = calibrationTableView.getSelectionModel().getSelectedIndex();
        graphSeries.getData().remove(selectedPointIndex);
        calibrationModel.getCalibrationPoints().remove(selectedPointIndex);
        checkNumberOfCalibrationPoints();
    }

    private void checkNumberOfCalibrationPoints() {
        ObservableList<CalibrationPoint> calibrationPoints = calibrationModel.getCalibrationPoints();
        int MAX_CALIBRATION_POINTS = 20;
        int MIN_CALIBRATION_POINTS = 2;

        if (calibrationPoints.size() == MAX_CALIBRATION_POINTS) {
            changeState(true);
        } else {
            changeState(false);
        }

        if (calibrationPoints.size() < MIN_CALIBRATION_POINTS) {
            saveButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
        }
    }

    private void changeState(boolean isDisable) {
        if (calibrationModel.getCalibrationPoints().size() == 0) {
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

    private void clearCalibrationPoints() {
        graphSeries.getData().clear();
        calibrationModel.getCalibrationPoints().clear();
        checkNumberOfCalibrationPoints();
    }

    public void loadDefaults(SignalGraphModel signalGraphModel) {
        this.stopped = false;
        this.signalGraphModel = signalGraphModel;
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());
        setTitleLabel();
        loadDefaultUiElementsState();
        loadCalibrationSettings();
        setLoadValueTextFields();
        saveButton.setDisable(false);
    }

    private void setTitleLabel() {
        titleLabel.setText("Градуировка " + (signalGraphModel.getChannel() + 1) + " канала" + " ("
                + signalGraphModel.getModuleType() + " слот " + signalGraphModel.getSlot() + ")");
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
        ADC adc = signalGraphModel.getAdc();
        int channel = signalGraphModel.getChannel();
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);

        for (String calibration : calibrationSettings) {
            int channelFromCalibration = Integer.parseInt(calibration.substring(9, 10));
            if (channel == channelFromCalibration) {
                calibrationModel.parseCalibration(calibration);
                showCalibration();
            }
        }
    }

    private void showCalibration() {
        addCalibrationPointToTable();
        addPointToGraph();
        checkNumberOfCalibrationPoints();
    }

    private void addCalibrationPointToTable() {
        int channel = signalGraphModel.getChannel();

        CalibrationPoint point = new CalibrationPoint(channel, calibrationModel);
        calibrationModel.getCalibrationPoints().add(point);
        setColumnTitle(loadChannelColumn, calibrationModel.getValueName());
    }

    private void setColumnTitle(TableColumn<CalibrationPoint, String> column, String valueName) {
        column.textProperty().set("Величина нагрузки, " + valueName);
    }

    private void addPointToGraph() {
        int lastPointIndex = calibrationModel.getCalibrationPoints().size() - 1;
        CalibrationPoint lastPoint = calibrationModel.getCalibrationPoints().get(lastPointIndex);
        double xValue = Double.parseDouble(lastPoint.getLoadValue());
        double yValue = Double.parseDouble(lastPoint.getChannelValue());

        graphSeries.getData().add(new XYChart.Data<>(xValue, yValue));
    }

    private void setLoadValueTextFields() {
        loadValueTextField.setText(calibrationModel.getFormattedLoadValue());
        loadValueNameTextField.setText(calibrationModel.getValueName());
    }

    public void handleAddPoint() {
        parseCoefficients();
        parseData();
        showCalibration();
    }

    private void parseCoefficients() {
        double loadCoefficient = Double.parseDouble(loadValueMultiplierComboBox.getSelectionModel().getSelectedItem());
        double channelCoefficient = Double.parseDouble(channelValueMultiplierComboBox.getSelectionModel().getSelectedItem());

        calibrationModel.setLoadValueCoefficient(loadCoefficient);
        calibrationModel.setChannelValueCoefficient(channelCoefficient);
    }

    private void parseData() {
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());
        double channelCoefficient = calibrationModel.getChannelValueCoefficient();
        double loadCoefficient = calibrationModel.getLoadValueCoefficient();
        int decimalFormatScale = calibrationModel.getDecimalFormatScale();

        if (setChannelValueCheckBox.isSelected()) {
            calibrationModel.setChannelValue(parseFrom(channelValueTextField, channelCoefficient));
        } else {
            calibrationModel.setChannelValue(Utils.roundValue(cm.getZeroShift(), decimalFormatScale) * channelCoefficient);
        }

        calibrationModel.setLoadValue(parseFrom(loadValueTextField, loadCoefficient));
        calibrationModel.setValueName(loadValueNameTextField.getText());
    }

    private double parseFrom(TextField textField, double multiplierCoefficient) {
        if (!textField.getText().equals("-")) {
            String digits = textField.getText().replaceAll(",", ".");
            double value = Utils.roundValue(Double.valueOf(digits), calibrationModel.getDecimalFormatScale());

            return value * multiplierCoefficient;
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
        calibrationModel.getCalibrationPoints().clear();
        graphSeries.getData().clear();
    }

    public void handleSaveButton() {
        savePoints();
        indicateResult();
    }

    private void savePoints() {
        ADC adc = signalGraphModel.getAdc();
        int channel = signalGraphModel.getChannel();

        adc.getCalibrationSettings().get(channel).clear();
        adc.getCalibrationSettings().get(channel).addAll(CalibrationPoint.toString(calibrationModel.getCalibrationPoints()));

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
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());

        new Thread(() -> {
            setValueName();
            while (!stopped) {
                double value = Utils.roundValue(cm.getZeroShift(), calibrationModel.getDecimalFormatScale());
                String formattedValue = Utils.convertFromExponentialFormat(value, calibrationModel.getDecimalFormatScale());
                Platform.runLater(() -> channelValueTextField.setText(formattedValue));
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
