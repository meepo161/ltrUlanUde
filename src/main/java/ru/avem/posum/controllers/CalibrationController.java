package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class CalibrationController {
    @FXML
    private LineChart calibrationGraph;
    @FXML
    private TableView calibrationTableView;
    @FXML
    private TableColumn loadChannelColumn;
    @FXML
    private TableColumn channelValueColumn;
    @FXML
    private TextField loadValueTextField;
    @FXML
    private TextField channelValueTextField;

    public void handleAddToTable() {
        double loadValue = Double.parseDouble(loadValueTextField.getText());
        double channelValue = Double.parseDouble(channelValueTextField.getText());

//        loadChannelColumn.setCellFactory(new PropertyValueFactory<>(loadValue));
    }

    public void handleBackButton() {

    }
}
