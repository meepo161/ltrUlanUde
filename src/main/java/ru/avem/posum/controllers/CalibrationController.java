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
    private Label loadValueNameLabel;
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
    private TextField loadValueNameTextField;

    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private CalibrationModel calibrationModel;
    private ObservableList<CalibrationModel> calibrationModels = FXCollections.observableArrayList();
    private StatusBarLine statusBarLine = new StatusBarLine();
    private CrateModel.Moudules moduleType;
    private boolean stopped;
    private int channel;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleCalibrationSettings;
    private double loadValue;
    private double channelValue;
    private String valueName;
    private ControllerManager cm;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
        valueNameColumn.setCellValueFactory(new PropertyValueFactory<>("valueName"));
        calibrationTableView.setItems(calibrationModels);
        calibrationGraph.getData().add(graphSeries);
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

    public void handleAddToTable() {
        addCalibrationDataToTable();
        addCalibrationPointToGraph();
        disableValueNameTextField();
    }

    private void addCalibrationDataToTable() {
        loadValue = Double.parseDouble(loadValueTextField.getText());
        channelValue = Double.parseDouble(channelValueTextField.getText());
        valueName = loadValueNameTextField.getText();

        calibrationModel = new CalibrationModel(loadValue, channelValue, valueName);
        calibrationModels.add(calibrationModel);
        toggleUiElementsIfHaveTwoPoints();
    }

    private void toggleUiElementsIfHaveTwoPoints() {
        if (calibrationModels.size() == 2) {
            loadValueLabel.setDisable(true);
            channelValueLabel.setDisable(true);
            loadValueNameLabel.setDisable(true);
            loadValueTextField.setDisable(true);
            channelValueTextField.setDisable(true);
            loadValueNameTextField.setDisable(true);
            addToTableButton.setDisable(true);
            saveButton.setDisable(false);
            saveButton.requestFocus();
        }
    }

    private void addCalibrationPointToGraph() {
        graphSeries.getData().add(new XYChart.Data<>(calibrationModel.getLoadValue(), calibrationModel.getChannelValue()));
    }

    private void disableValueNameTextField() {
        loadValueNameTextField.setDisable(true);
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
        calibrationModels.clear();
        graphSeries.getData().clear();
    }

    public void loadDefaults(CrateModel.Moudules moduleType, int channel) {
        this.moduleType = moduleType;
        this.channel = channel;
        this.stopped = false;

        loadDefaultUiElementsState();

        if (moduleType == CrateModel.Moudules.LTR24) {
            ltr24 = cm.getLTR24Instance();
            moduleCalibrationSettings = ltr24.getCalibrationSettings()[channel];
            loadCalibrationSettings();
        } else {
            ltr212 = cm.getLTR212Instance();
            moduleCalibrationSettings = ltr212.getCalibrationSettings()[channel];
            loadCalibrationSettings();
        }
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
        String[] separatedSettings = moduleCalibrationSettings.split(", ", 6);
        if (separatedSettings[0].equals("setted")) {
            addPoint(Double.parseDouble(separatedSettings[1]), Double.parseDouble(separatedSettings[2]), separatedSettings[5]);
            addPoint(Double.parseDouble(separatedSettings[3]), Double.parseDouble(separatedSettings[4]), separatedSettings[5]);
            loadValueTextField.setText(separatedSettings[3]);
            loadValueNameTextField.setText(separatedSettings[5]);
            toggleUiElementsIfHaveTwoPoints();
            saveButton.setDisable(true);
        }
    }

    private void addPoint(double loadValue, double channelValue, String loadValueName) {
        graphSeries.getData().add(new XYChart.Data<>(loadValue, channelValue));
        calibrationModel = new CalibrationModel(loadValue, channelValue, loadValueName);
        calibrationModels.add(calibrationModel);
    }

    public void handleSaveButton() {
        String firstPoint = "setted, " + calibrationModels.get(0).getLoadValue() + ", " + calibrationModels.get(0).getChannelValue() + ", " ;
        String secondPoint = calibrationModels.get(1).getLoadValue() + ", " + calibrationModels.get(1).getChannelValue() + ", " + calibrationModels.get(1).getValueName();
        StringBuilder calibrationSettings = new StringBuilder();
        calibrationSettings.append(firstPoint).append(secondPoint);

        if (moduleType == CrateModel.Moudules.LTR24) {
            ltr24 = cm.getLTR24Instance();
            ltr24.getCalibrationSettings()[channel] = calibrationSettings.toString();
        } else {
            ltr212 = cm.getLTR212Instance();
            ltr212.getCalibrationSettings()[channel] = calibrationSettings.toString();
        }

        saveButton.setDisable(true);
        statusBarLine.setStatus("Настройки успешно сохранены", statusBar);
    }

    public void showChannelValue() {
        new Thread(() -> {
            while (!stopped) {
                Platform.runLater(() -> {
                    channelValueTextField.setText(String.format("%.6f", cm.getMaxValue()));
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
