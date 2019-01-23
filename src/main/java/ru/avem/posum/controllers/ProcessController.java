package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.models.Events;
import ru.avem.posum.models.EventsModel;
import ru.avem.posum.models.ProcessSample;
import ru.avem.posum.models.ProcessSampleModel;


import java.util.Optional;

public class ProcessController implements BaseController {
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
    private TableColumn<ProcessSample, String> group1Value4SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group1Value5SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2ColorSampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value2SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group2Value3SampleColumn;


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
        group1Value4SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value4Property());
        group1Value5SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value5Property());
        group2ColorSampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2ColorProperty());
        group2Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value1Property());
        group2Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value2Property());
        group2Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value3Property());
        processSampleModel.fitTable(tableSample);
        processSampleModel.SetProcessSampleColumnFunction(group1ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value4SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value5SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value3SampleColumn);

    }

    public void handleInitButton() {
    }

    public void handleRunButton() {
    }

    public void handleSmoothStopButton() {
    }

    public void handleStopButton() {
    }

    public void handleToProgrammButton() {
    }

    public void handleSavePointButton() {
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
