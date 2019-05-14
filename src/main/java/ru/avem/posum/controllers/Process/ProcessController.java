package ru.avem.posum.controllers.Process;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.models.Process.*;
import ru.avem.posum.utils.StatusBarLine;

import java.util.Optional;

public class ProcessController implements BaseController {
    @FXML
    private Label checkIcon;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private LineChart<Number, Number> processGraph;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<ProcessSample> tableSample;
    @FXML
    private TableColumn<ProcessSample, String> mainTextSampleColumn;
    @FXML
    private TableColumn<ProcessSample, Void> group1ColorSampleColumn;
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
    private TableColumn<ProcessSample, Void> group4ColorSampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group4Value1SampleColumn;
    @FXML
    private TableColumn<ProcessSample, String> group4Value2SampleColumn;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TableView<Events> tableEvent;
    @FXML
    private ToolBar toolbarSettings;
    @FXML
    private VBox topPanel;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private EventsModel eventModel = new EventsModel();
    private ExperimentModel experimentModel = new ExperimentModel();
    private ProcessSampleModel processSampleModel = new ProcessSampleModel();
    private ProgramController programController;
    private TestProgram testProgram;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        eventModel.initEventData(tableEvent);
        eventModel.SetEventsTableFunction(tableEvent);
        eventTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eventDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        processSampleModel.initProcessSampleData(tableSample);
        mainTextSampleColumn.setCellValueFactory(cellData -> cellData.getValue().mainTextProperty());
        group1Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value1Property());
        group1Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value2Property());
        group1Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value3Property());
        group2Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value1Property());
        group2Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value2Property());
        group2Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value3Property());
        group3Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value1Property());
        group3Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value2Property());
        group3Value3SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value3Property());
        group4Value1SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group4Value1Property());
        group4Value2SampleColumn.setCellValueFactory(cellData -> cellData.getValue().group4Value2Property());

        processSampleModel.SetProcessSampleColumnColorFunction(group1ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group1Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group2Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value2SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group3Value3SampleColumn);
        processSampleModel.SetProcessSampleColumnColorFunction(group4ColorSampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group4Value1SampleColumn);
        processSampleModel.SetProcessSampleColumnFunction(group4Value2SampleColumn);
        processSampleModel.fitTable();
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);
        statusBarLine.setProcessView(true);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        processSampleModel.chart(processGraph);
        experimentModel.setProcessSampleModel(processSampleModel);

        programController = new ProgramController(mainPanel, processSampleModel, toolbarSettings, topPanel);
    }

    public void handleInitButton() {
        experimentModel.Init();
    }

    public void handleRunButton() {
        experimentModel.Run();
    }

    public void handleSmoothStopButton() {
        experimentModel.SmoothStop();
    }

    public void handleStopButton() {
        experimentModel.Stop();
    }

    public void handleToProgramButton() {
        experimentModel.ChangeParam();
        programController.toggleSettingsPanel();
    }

    public void handleLinkButton() {
        cm.initListViews();
        wm.setScene(WindowsManager.Scenes.LINKING_SCENE);
    }

    public void handleSavePointButton() {
        experimentModel.SavePoint();
    }

    public void handleSaveWaveformButton() {
        experimentModel.SaveWaveform();
    }

    public void handleSaveProtocolButton() {
        experimentModel.SaveProtocol();
    }

    public void handleBackButton() {
        if (experimentModel.getRun()) {
            ButtonType ok = new ButtonType("Да", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ok, cancel);
            alert.setTitle("Подтвердите действие");
            alert.setHeaderText("Вернуться в главное окно?");
            ButtonBar buttonBar = (ButtonBar)alert.getDialogPane().lookup(".button-bar");
            buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 14px;\n" + "-fx-background-radius: 5px;\n" +
                    "\t-fx-border-radius: 5px;"));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == ok) {
                    experimentModel.Terminate();
                    wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
                }
            }
        }
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
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public ExperimentModel getExperimentModel() {
        return experimentModel;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
