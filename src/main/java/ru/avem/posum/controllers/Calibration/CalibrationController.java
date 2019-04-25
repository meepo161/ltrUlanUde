package ru.avem.posum.controllers.Calibration;

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
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Calibration.CalibrationPointModel;
import ru.avem.posum.models.Calibration.CalibrationModel;
import ru.avem.posum.models.Signal.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.List;

public class CalibrationController implements BaseController {
    @FXML
    private Button addToTableButton;
    @FXML
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private TableView<CalibrationPointModel> calibrationTableView;
    @FXML
    private TableColumn<CalibrationPointModel, String> channelValueColumn;
    @FXML
    private Label channelValueLabel;
    @FXML
    private ComboBox<String> channelValueMultiplierComboBox;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private TableColumn<CalibrationPointModel, String> loadChannelColumn;
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
    private SignalModel signalModel;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private WindowsManager wm;
    private boolean stopped;

    @FXML
    private void initialize() {
        initTextFields();
        initComboBoxes();
        initTableView();
        initGraph();
        initContextMenu();
        listenSetChannelValueCheckBox();
        listenMouse();
    }

    private void initTextFields() {
        toggleUiElementsIfEmptyField(loadValueTextField);
        toggleUiElementsIfEmptyField(loadValueNameTextField);
        setDigitFilterToTextField(channelValueTextField);
        setDigitFilterToTextField(loadValueTextField);
    }

    private void toggleUiElementsIfEmptyField(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() &
                    !channelValueTextField.getText().isEmpty() &
                    !loadValueNameTextField.getText().isEmpty() &
                    calibrationModel.getCalibrationPointModels().size() <= 20) {
                addToTableButton.setDisable(false);
            } else {
                addToTableButton.setDisable(true);
            }
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

    private void initComboBoxes() {
        addMultipliersList();
    }

    private void addMultipliersList() {
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
        channelValueMultiplierComboBox.getSelectionModel().select(5);
        loadValueMultiplierComboBox.getSelectionModel().select(5);
    }

    private void initTableView() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
    }

    private void initGraph() {
        calibrationTableView.setItems(calibrationModel.getCalibrationPointModels());
        calibrationGraph.getData().add(graphSeries);
    }

    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteCalibrationPoint());
        menuItemClear.setOnAction(event -> clearCalibrationPoints());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deleteCalibrationPoint() {
        int selectedPointIndex = calibrationTableView.getSelectionModel().getSelectedIndex();
        graphSeries.getData().remove(selectedPointIndex);
        calibrationModel.getCalibrationPointModels().remove(selectedPointIndex);
        checkNumberOfCalibrationPoints();
    }

    private void checkNumberOfCalibrationPoints() {
        ObservableList<CalibrationPointModel> calibrationPointModels = calibrationModel.getCalibrationPointModels();
        int MAX_CALIBRATION_POINTS = 20;
        int MIN_CALIBRATION_POINTS = 2;

        changeUiElementsState(calibrationPointModels.size() == MAX_CALIBRATION_POINTS);
        saveButton.setDisable(calibrationPointModels.size() < MIN_CALIBRATION_POINTS);
    }

    private void changeUiElementsState(boolean isDisable) {
        loadValueNameTextField.setDisable(calibrationModel.getCalibrationPointModels().size() != 0);
        loadValueLabel.setDisable(isDisable);
        loadValueTextField.setDisable(isDisable);
        channelValueLabel.setDisable(isDisable);
        channelValueTextField.setDisable(isDisable);
        loadValueNameLabel.setDisable(isDisable);
        addToTableButton.setDisable(isDisable);
    }

    private void clearCalibrationPoints() {
        graphSeries.getData().clear();
        calibrationModel.getCalibrationPointModels().clear();
        checkNumberOfCalibrationPoints();
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

    public void showChannelValue() {
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());
        Platform.runLater(() -> channelValueLabel.setText(String.format("Значение, %s:", cm.getValueName())));

        new Thread(() -> {
            while (!stopped) {
                double value = Utils.roundValue(cm.getZeroShift(), calibrationModel.getDecimalFormatScale());
                String formattedValue = Utils.convertFromExponentialFormat(value, calibrationModel.getDecimalFormatScale());
                Platform.runLater(() -> channelValueTextField.setText(formattedValue));
                Utils.sleep(100);
            }
        }).start();
    }

    private void listenMouse() {
        calibrationTableView.setRowFactory(tv -> {
            TableRow<CalibrationPointModel> row = new TableRow<>();
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

    public void loadDefaultCalibrationSettings(SignalModel signalModel) {
        this.stopped = false;
        this.signalModel = signalModel;
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());
        titleLabel.setText(String.format("Градуировка %d канала (%s слот %d)", signalModel.getChannel(),
                signalModel.getModuleType(), signalModel.getSlot()));
        loadDefaultUiElementsState();
        loadCalibrationSettings();
        setLoadValueTextFields();
    }

    private void loadDefaultUiElementsState() {
        loadValueLabel.setDisable(false);
        channelValueLabel.setDisable(false);
        loadValueNameLabel.setDisable(false);
        loadValueTextField.setDisable(false);
        channelValueTextField.setDisable(false);
        loadValueNameTextField.setDisable(false);
        saveButton.setDisable(false);
    }

    private void loadCalibrationSettings() {
        int channel = signalModel.getChannel();
        List<String> calibrations = signalModel.getAdc().getCalibrationSettings().get(channel);

        for (String calibration : calibrations) {
            int channelFromCalibration = Integer.parseInt(calibration.substring(9, 10));
            if (channel == channelFromCalibration) {
                calibrationModel.parse(calibration);
                showCalibration();
            }
        }
    }

    private void showCalibration() {
        addCalibrationPointToTableView();
        addPointToGraph();
        checkNumberOfCalibrationPoints();
    }

    private void addCalibrationPointToTableView() {
        int channel = signalModel.getChannel();
        CalibrationPointModel point = new CalibrationPointModel(channel, calibrationModel);
        calibrationModel.getCalibrationPointModels().add(point);
        loadChannelColumn.textProperty().set(String.format("Величина нагрузки, %s", calibrationModel.getValueName()));
    }

    private void addPointToGraph() {
        int lastPointIndex = calibrationModel.getCalibrationPointModels().size() - 1;
        CalibrationPointModel lastPoint = calibrationModel.getCalibrationPointModels().get(lastPointIndex);
        double xValue = Double.parseDouble(lastPoint.getLoadValue());
        double yValue = Double.parseDouble(lastPoint.getChannelValue());
        graphSeries.getData().add(new XYChart.Data<>(xValue, yValue));
    }

    private void setLoadValueTextFields() {
        loadValueTextField.setText(calibrationModel.getFormattedLoadValue());
        loadValueNameTextField.setText(calibrationModel.getValueName());
    }

    @FXML
    public void handleAddPoint() {
        parseData();
        showCalibration();
    }

    private void parseData() {
        double loadValueMultiplierCoefficient = Double.parseDouble(loadValueMultiplierComboBox.getSelectionModel().getSelectedItem());
        double channelValueMultiplierCoefficient = Double.parseDouble(channelValueMultiplierComboBox.getSelectionModel().getSelectedItem());
        int decimalFormatScale = cm.getDecimalFormatScale();
        double channelValue = setChannelValueCheckBox.isSelected() ? parse(channelValueTextField, channelValueMultiplierCoefficient) :
                Utils.roundValue(cm.getZeroShift(), decimalFormatScale) * channelValueMultiplierCoefficient;
        calibrationModel.setDecimalFormatScale(decimalFormatScale);
        calibrationModel.setChannelValue(channelValue);
        calibrationModel.setLoadValue(parse(loadValueTextField, loadValueMultiplierCoefficient));
        calibrationModel.setValueName(loadValueNameTextField.getText());
    }

    private double parse(TextField textField, double multiplierCoefficient) {
        if (!textField.getText().equals("-")) {
            String digits = textField.getText().replaceAll(",", ".");
            double value = Utils.roundValue(Double.valueOf(digits), calibrationModel.getDecimalFormatScale());
            return value * multiplierCoefficient;
        } else {
            return 0;
        }
    }

    @FXML
    public void handleSaveButton() {
        savePoints();
        indicateResult();
    }

    private void savePoints() {
        ADC adc = signalModel.getAdc();
        int channel = signalModel.getChannel();

        adc.getCalibrationSettings().get(channel).clear();
        adc.getCalibrationSettings().get(channel).addAll(CalibrationPointModel.toString(calibrationModel.getCalibrationPointModels()));

        CalibrationModel calibrationModel = new CalibrationModel();
        calibrationModel.calibrate(adc, channel);
        adc.getCalibrationCoefficients().get(channel).clear();
        adc.getCalibrationCoefficients().get(channel).addAll(calibrationModel.getCalibrationCoefficients());
    }

    private void indicateResult() {
        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }

    @FXML
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
        calibrationModel.getCalibrationPointModels().clear();
        graphSeries.getData().clear();
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
