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
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.models.CalibrationModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;


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
    private Label valueNameLabel;
    @FXML
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TableView<CalibrationModel> calibrationTableView;
    @FXML
    private TableColumn<CalibrationModel, Double> loadChannelColumn;
    @FXML
    private TableColumn<CalibrationModel, Double> channelValueColumn;
    @FXML
    private TableColumn<CalibrationModel, Double> valueNameColumn;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField channelValueTextField;
    @FXML
    private TextField valueNameTextField;

    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private CalibrationModel calibrationModel;
    private ObservableList<CalibrationModel> calibrationModels = FXCollections.observableArrayList();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private CrateModel.Moudules moduleType;
    private boolean stopped;
    private int channel;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private ControllerManager cm;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
        valueNameColumn.setCellValueFactory(new PropertyValueFactory<>("valueName"));
        calibrationTableView.setItems(calibrationModels);
        calibrationGraph.getData().add(graphSeries);
        setDigitFilter();
        checkEmptyFields(loadValueTextField);
        checkEmptyFields(valueNameTextField);
    }

    private void setDigitFilter() {
        loadValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadValueTextField.setText(newValue.replaceAll("[^\\d]", ""));
            if (!newValue.matches("^[0-9]+|$")) {
                loadValueTextField.setText(oldValue);
            }
        });
    }

    private void checkEmptyFields(TextField textField) {
        textField.textProperty().addListener((observable) -> {
            if (!loadValueTextField.getText().isEmpty() & !channelValueTextField.getText().isEmpty() & !valueNameTextField.getText().isEmpty()) {
                addToTableButton.setDisable(false);
            } else {
                addToTableButton.setDisable(true);
            }
        });
    }

    public void handleAddToTable() {
        addCalibrationDataToTable();
        graphSeries.getData().add(new XYChart.Data<>(calibrationModel.getLoadValue(), calibrationModel.getChannelValue()));
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

    private void addCalibrationDataToTable() {
        double loadValue = Double.parseDouble(loadValueTextField.getText());
        double channelValue = Double.parseDouble(channelValueTextField.getText());
        String valueName = valueNameTextField.getText();
        calibrationModel = new CalibrationModel(loadValue, channelValue, valueName);

        calibrationModels.add(calibrationModel);
        ToggleUiElements();
    }

    private void ToggleUiElements() {
        if (calibrationModels.size() == 2) {
            loadValueLabel.setDisable(true);
            channelValueLabel.setDisable(true);
            valueNameLabel.setDisable(true);
            loadValueTextField.setDisable(true);
            channelValueTextField.setDisable(true);
            valueNameTextField.setDisable(true);
            addToTableButton.setDisable(true);
            saveButton.setDisable(false);
            saveButton.requestFocus();
        }
    }

    public void handleBackButton() {
        stopped = true;
        clearCalibrationData();
        wm.setScene(WindowsManager.Scenes.SIGNAL_GRAPH_SCENE);
    }

    private void clearCalibrationData() {
        loadValueTextField.setText("");
        channelValueTextField.setText("");
        valueNameTextField.setText("");
        calibrationModels.clear();
        graphSeries.getData().clear();
    }

    public void loadDefaults(CrateModel.Moudules moduleType, int channel) {
        this.moduleType = moduleType;
        this.channel = channel;


        this.stopped = false;
        loadValueLabel.setDisable(false);
        channelValueLabel.setDisable(false);
        valueNameLabel.setDisable(false);
        loadValueTextField.setDisable(false);
        channelValueTextField.setDisable(false);
        valueNameTextField.setDisable(false);
        saveButton.setDisable(true);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    public void handleSaveButton() {
        String firstPoint = calibrationModels.get(0).getLoadValue() + ", " + calibrationModels.get(0).getChannelValue() + ", " ;
        String secondPoint = calibrationModels.get(1).getLoadValue() + ", " + calibrationModels.get(1).getChannelValue() + ", " + calibrationModels.get(1).getValueName();
        StringBuffer calibrationSettings = new StringBuffer();

        calibrationSettings.append(firstPoint).append(secondPoint);
        if (moduleType == CrateModel.Moudules.LTR24) {
            ltr24 = cm.getLTR24Instance();
            ltr24.getCalibrationSettings()[channel] = calibrationSettings.toString();
        } else {
            ltr212 = cm.getLTR212Instance();
        }

        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }
}
