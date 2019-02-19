package ru.avem.posum.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.models.CalibrationModel;


public class CalibrationController implements BaseController {
    @FXML
    private LineChart<Number, Number> calibrationGraph;
    @FXML
    private TableView<CalibrationModel> calibrationTableView;
    @FXML
    private TableColumn<CalibrationModel, Double> loadChannelColumn;
    @FXML
    private TableColumn<CalibrationModel, Double> channelValueColumn;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField channelValueTextField;

    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private CalibrationModel calibrationModel;
    private ObservableList<CalibrationModel> calibrationModels = FXCollections.observableArrayList();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        loadChannelColumn.setCellValueFactory(new PropertyValueFactory<>("loadValue"));
        channelValueColumn.setCellValueFactory(new PropertyValueFactory<>("channelValue"));
        calibrationTableView.setItems(calibrationModels);
    }

    public void handleAddToTable() {
        addCalibraionDataToTable();
        addPointToGraph();
    }

    private void addCalibraionDataToTable() {
        double loadValue = Double.parseDouble(loadValueTextField.getText());
        double channelValue = Double.parseDouble(channelValueTextField.getText());
        calibrationModel = new CalibrationModel(loadValue, channelValue);

        calibrationModels.add(calibrationModel);
    }

    private void addPointToGraph() {
        graphSeries.getData().add(new XYChart.Data<>(calibrationModel.getLoadValue(), calibrationModel.getChannelValue()));
        calibrationGraph.getData().add(graphSeries);
    }

    public void handleBackButton() {

    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {

    }
}
