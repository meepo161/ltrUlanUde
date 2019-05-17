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
import ru.avem.posum.models.Calibration.CalibrationPoint;
import ru.avem.posum.models.Calibration.CalibrationModel;
import ru.avem.posum.models.Signal.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.List;

public class CalibrationController implements BaseController {
    @FXML
    private Label amplitudeCoefficientLabel;
    @FXML
    private ComboBox<String> dcMultipliersComboBox;
    @FXML
    private Button addToTableButton;
    @FXML
    private Button backButton;
    @FXML
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private TableView<CalibrationPoint> calibrationTableView;
    @FXML
    private TableColumn<CalibrationPoint, String> channelValueColumn;
    @FXML
    private Label checkIcon;
    @FXML
    private Label dcLabel;
    @FXML
    private TextField dcTextField;
    @FXML
    private TableColumn<CalibrationPoint, String> loadChannelColumn;
    @FXML
    private ComboBox<String> loadValueMultiplierComboBox;
    @FXML
    private Label loadValueCoefficientLabel;
    @FXML
    private Label loadValueLabel;
    @FXML
    private Label loadValueNameLabel;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField loadValueNameTextField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button saveButton;
    @FXML
    private CheckBox setChannelValueCheckBox;
    @FXML
    private CheckBox setNulCheckBox;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label titleLabel;
    @FXML
    private Label warningIcon;

    private CalibrationModel calibrationModel = new CalibrationModel();
    private ContextMenu contextMenu = new ContextMenu();
    private ControllerManager cm;
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private SignalModel signalModel;
    private StatusBarLine statusBarLine;
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
        listenSettingNul();
        listenMouse();
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator,
                statusBar, warningIcon);
    }

    private void initTextFields() {
        toggleUiElementsIfEmptyField(loadValueTextField);
        toggleUiElementsIfEmptyField(loadValueNameTextField);
        setDigitFilterToTextField(dcTextField);
        setDigitFilterToTextField(loadValueTextField);
    }

    private void toggleUiElementsIfEmptyField(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() &
                    !dcTextField.getText().isEmpty() &
                    !loadValueNameTextField.getText().isEmpty() &
                    calibrationModel.getCalibrationPoints().size() <= 20) {
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

        dcMultipliersComboBox.setItems(coefficients);
        dcMultipliersComboBox.getSelectionModel().select(5);

        loadValueMultiplierComboBox.setItems(coefficients);
        loadValueMultiplierComboBox.getSelectionModel().select(5);
    }

    private void initTableView() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
    }

    private void initGraph() {
        calibrationTableView.setItems(calibrationModel.getCalibrationPoints());
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
        calibrationModel.getCalibrationPoints().remove(selectedPointIndex);
        checkNumberOfCalibrationPoints();
    }

    private void checkNumberOfCalibrationPoints() {
        ObservableList<CalibrationPoint> calibrationPoints = calibrationModel.getCalibrationPoints();
        int MAX_CALIBRATION_POINTS = 20;
        int MIN_CALIBRATION_POINTS = 2;

        changeUiElementsState(calibrationPoints.size() == MAX_CALIBRATION_POINTS);
        saveButton.setDisable(checkSettingOfNul() && calibrationPoints.size() < MIN_CALIBRATION_POINTS);
    }

    private void changeUiElementsState(boolean isDisable) {
        double loadValue = Double.parseDouble(loadValueTextField.getText());
        boolean isSetNul = (loadValue == 0);

        loadValueNameLabel.setDisable(isSetNul);
        loadValueNameTextField.setDisable(isSetNul);
        loadValueLabel.setDisable(isSetNul);
        loadValueTextField.setDisable(isSetNul);
        dcLabel.setDisable(isDisable);
        dcTextField.setDisable(isDisable);
        addToTableButton.setDisable(isDisable);
    }

    private boolean checkSettingOfNul() {
        ObservableList<CalibrationPoint> calibrationPoints = calibrationModel.getCalibrationPoints();

        for (CalibrationPoint calibrationPoint : calibrationPoints) {
            if (Double.parseDouble(calibrationPoint.getLoadValue()) == 0 && calibrationModel.getLoadValue() == 0) {
                return false;
            }
        }

        return true;
    }

    private void clearCalibrationPoints() {
        graphSeries.getData().clear();
        calibrationModel.getCalibrationPoints().clear();
        checkNumberOfCalibrationPoints();
    }

    private void listenSetChannelValueCheckBox() {
        setChannelValueCheckBox.selectedProperty().addListener(observable -> {
            if (setChannelValueCheckBox.isSelected()) {
                stopped = true;
                dcTextField.setEditable(true);
                dcTextField.setFocusTraversable(true);
                dcTextField.setMouseTransparent(false);
                dcTextField.setText("");
            } else {
                stopped = false;
                dcTextField.setEditable(false);
                dcTextField.setFocusTraversable(false);
                dcTextField.setMouseTransparent(true);
                showChannelValue();
            }
        });
    }

    public void showChannelValue() {
        calibrationModel.setDecimalFormatScale(cm.getDecimalFormatScale());
        Platform.runLater(() -> dcLabel.setText(String.format("Значение, %s:", cm.getValueName())));

        new Thread(() -> {
            while (!stopped) {
                double value = Utils.roundValue(cm.getDc(), calibrationModel.getDecimalFormatScale());
                String formattedValue = Utils.convertFromExponentialFormat(value, calibrationModel.getDecimalFormatScale());
                Platform.runLater(() -> dcTextField.setText(formattedValue));
                Utils.sleep(100);
            }
        }).start();
    }

    private void listenSettingNul() {
        setNulCheckBox.selectedProperty().addListener(observable -> {
            if (setNulCheckBox.isSelected()) {
                loadValueTextField.setText("0.0");
                loadValueNameTextField.setText("Ноль");
            } else {
                loadValueTextField.setText("");
                loadValueNameTextField.setText("");
            }

            loadValueLabel.setDisable(setNulCheckBox.isSelected());
            loadValueTextField.setDisable(setNulCheckBox.isSelected());
            loadValueNameLabel.setDisable(setNulCheckBox.isSelected());
            loadValueNameTextField.setDisable(setNulCheckBox.isSelected());
            loadValueMultiplierComboBox.setDisable(setNulCheckBox.isSelected());
            loadValueMultiplierComboBox.setDisable(setNulCheckBox.isSelected());
        });
    }

    private void listenMouse() {
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
        dcLabel.setDisable(false);
        loadValueNameLabel.setDisable(false);
        loadValueTextField.setDisable(false);
        dcTextField.setDisable(false);
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
        boolean isSettingOfNul = calibrationModel.getLoadValue() == 0;

        if (isSettingOfNul & !checkSettingOfNul()) {
            statusBarLine.setStatus("Ноль уже градуирован", false);
        } else if (checkExistingPoints()){
            addCalibrationPointToTableView();
            addPointToGraph();
            checkNumberOfCalibrationPoints();
        }
    }

    private boolean checkExistingPoints() {
        ObservableList<CalibrationPoint> calibrationPoints = calibrationModel.getCalibrationPoints();

        for (CalibrationPoint calibrationPoint : calibrationPoints) {
            double channelValue = Double.parseDouble(calibrationPoint.getChannelValue());
            double loadValue = Double.parseDouble(calibrationPoint.getLoadValue());

            if (channelValue == calibrationModel.getChannelValue()) {
                statusBarLine.setStatus("Данное значение уже градуировано", false);
                return false;
            }

            if (loadValue == calibrationModel.getLoadValue()) {
                statusBarLine.setStatus("Данная нагрузка уже градуирована", false);
                return false;
            }
        }

        return true;
    }

    private void addCalibrationPointToTableView() {
        int channel = signalModel.getChannel();
        CalibrationPoint point = new CalibrationPoint(channel, calibrationModel);
        calibrationModel.add(point);

        String valueName = calibrationModel.getValueName();
        if (valueName.isEmpty()) {
            for (CalibrationPoint calibrationPoint : calibrationModel.getCalibrationPoints()) {
                if (!calibrationPoint.getValueName().isEmpty()) {
                    valueName = calibrationPoint.getValueName();
                }
            }
        }

        String loadChannelHeader = valueName.isEmpty() ? "Величина нагрузки" : "Величина нагрузки, " + valueName;
        loadChannelColumn.textProperty().set(loadChannelHeader);
    }

    private void addPointToGraph() {
        for (CalibrationPoint calibrationPoint : calibrationModel.getCalibrationPoints()) {
            double xValue = Double.parseDouble(calibrationPoint.getLoadValue());
            double yValue = Double.parseDouble(calibrationPoint.getChannelValue());
            XYChart.Data<Number, Number> graphPoint = new XYChart.Data<>(xValue, yValue);

            if (!graphSeries.getData().contains(graphPoint)) {
                graphSeries.getData().add(graphPoint);
            }
        }
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
        double channelValueMultiplierCoefficient = Double.parseDouble(dcMultipliersComboBox.getSelectionModel().getSelectedItem());
        int decimalFormatScale = cm.getDecimalFormatScale();
        double channelValue = setChannelValueCheckBox.isSelected() ? parse(dcTextField, channelValueMultiplierCoefficient) :
                Utils.roundValue(cm.getDc(), decimalFormatScale) * channelValueMultiplierCoefficient;
        double loadValue = setNulCheckBox.isSelected() ? 0 : parse(loadValueTextField, loadValueMultiplierCoefficient);
        String valueName = loadValueNameTextField.getText();

        calibrationModel.setDecimalFormatScale(decimalFormatScale);
        calibrationModel.setChannelValue(channelValue);
        calibrationModel.setLoadValue(loadValue);
        calibrationModel.setValueName(valueName);
    }

    private double parse(TextField textField, double multiplierCoefficient) {
        if (!textField.getText().equals("-") && !textField.getText().isEmpty()) {
            String digits = textField.getText().replaceAll(",", ".");
            double value = Utils.roundValue(Double.valueOf(digits), calibrationModel.getDecimalFormatScale());
            return value * multiplierCoefficient;
        } else {
            return 0;
        }
    }

    @FXML
    public void handleSaveButton() {
        saveCalibrationPoints();
        indicateResult();
    }

    private void saveCalibrationPoints() {
        ADC adc = signalModel.getAdc();
        int channel = signalModel.getChannel();

        adc.getCalibrationSettings().get(channel).clear();
        adc.getCalibrationSettings().get(channel).addAll(CalibrationPoint.toString(calibrationModel.getCalibrationPoints()));

        CalibrationModel calibrationModel = new CalibrationModel();
        calibrationModel.calibrate(adc, channel);
        adc.getCalibrationCoefficients().get(channel).clear();
        adc.getCalibrationCoefficients().get(channel).addAll(calibrationModel.getCalibrationCoefficients());
    }

    private void indicateResult() {
        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", true);
    }

    @FXML
    public void handleBackButton() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Подготовка данных для отображения");
        backButton.setDisable(true);
        saveButton.setDisable(true);

        new Thread(() -> {
            stopped = true;
            cm.checkCalibration();
            statusBarLine.clearStatusBar();
            statusBarLine.toggleProgressIndicator(true);
            backButton.setDisable(false);
            saveButton.setDisable(false);
            clearCalibrationData();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE));
        }).start();
    }

    private void clearCalibrationData() {
        loadValueTextField.setText("");
        dcTextField.setText("");
        loadValueNameTextField.setText("");
        Platform.runLater(() -> {
            dcMultipliersComboBox.getSelectionModel().select(5);
            loadValueMultiplierComboBox.getSelectionModel().select(5);
        });
        setChannelValueCheckBox.setSelected(false);
        setNulCheckBox.setSelected(false);
        calibrationModel.getCalibrationPoints().clear();
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
