package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.models.Events;
import ru.avem.posum.models.EventsModel;
import ru.avem.posum.models.ProcessSample;
import ru.avem.posum.models.ProcessSampleModel;


import java.util.Optional;

public class ProcessController implements BaseController {
    @FXML
    private StatusBar processStatusBar;
    @FXML
    private LineChart<Number, Number> processLineChart;
    @FXML
    private TableView<Events> tableEvent;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<ProcessSample> tableSample;
    @FXML
    private TableColumn<ProcessSample, String> mainTextSampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group1ColorSampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group1Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group1Value2SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group1Value3SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value2SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value3SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group3Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group3Value2SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group3Value3SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group4ColorSampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group4Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group4Value2SampleColumn;

    @FXML
    private ToolBar toolbarSettings;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private VBox topPanel;

    private EventsModel eventModel = new EventsModel();
    private ProcessSampleModel processSampleModel = new ProcessSampleModel();

    private WindowsManager wm;

    @FXML
    private void initialize() {
        eventModel.initEventData(tableEvent);
        eventModel.SetEventsTableFunction(tableEvent);
        eventTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eventDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        processSampleModel.initProcessSampleData(tableSample);
        processSampleModel.SetProcessSampleTableFunction(tableSample);
        mainTextSampleColumn.setCellValueFactory(cellData -> cellData.getValue().mainTextProperty());
        group1ColorSampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1ColorProperty());
        group1Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value1Property());
        group1Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value2Property());
        group1Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value3Property());
//        group2ColorSampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2ColorProperty());
        group2Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value1Property());
        group2Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value2Property());
        group2Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value3Property());
        group3Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value1Property());
        group3Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value2Property());
        group3Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value3Property());
        group4ColorSampleColumn.setCellValueFactory(cellData -> cellData.getValue().group4ColorProperty());
        group4Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group4Value1Property());
        group4Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group4Value2Property());

        processSampleModel.SetProcessSampleColumnFunction(group1ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group4ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group4Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group4Value2SampleColumn);

        showSettingsPanel(true);
        processStatusBar.setText("Инициализация произведена!");

        processSampleModel.chart(processLineChart);
    }

    public void showSettingsPanel(boolean hide) {
        double needHeight = mainPanel.getMaxHeight();
        toolbarSettings.setVisible(!hide);
        if(!hide) {
            needHeight = needHeight + toolbarSettings.getPrefHeight();
        }
        topPanel.setPrefHeight(needHeight);
        topPanel.maxHeight(needHeight);
        topPanel.minHeight(needHeight);
        processSampleModel.fitTable(tableSample);
    }

    public void handleInitButton() {
        showSettingsPanel(true);
    }

    public void handleRunButton() {
        processSampleModel.loadData(0);
        processSampleModel.fitTable(tableSample);
    }

    public void handleSmoothStopButton() {
        processSampleModel.testData();
    }

    public void handleStopButton() {
        processSampleModel.resetData();
    }

    public void handleToProgrammButton() {
        showSettingsPanel(false);
    }

    public void handleSavePointButton() {
        processSampleModel.chartAdd();
    }

    public void handleSaveWaveformButton() {
    }

    public void handleSaveProtocolButton() {
    }

    public void handleBackButton() {
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void handleAddEventButton() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Добавление события");
        dialog.setHeaderText("Введите событие:");
        dialog.setContentText("Текст:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(eventText -> eventModel.setEvent(eventText));
    }

    public void handleExpandEventTableButton() {
    }

    public void handlePidButton() {
    }

    public void handlePlugButton() {
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {

    }
}
