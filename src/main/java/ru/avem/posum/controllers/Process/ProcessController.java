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
import ru.avem.posum.utils.Utils;

import java.util.Optional;

public class ProcessController implements BaseController {
    @FXML
    private Slider amplitudeSlider;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private TextField calibratedAmplitudeTextField;
    @FXML
    private Label checkIcon;
    @FXML
    private Slider dSlider;
    @FXML
    private TextField dTextField;
    @FXML
    private Slider frequencySlider;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private Slider iSlider;
    @FXML
    private TextField iTextField;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private LineChart<Number, Number> processGraph;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<PairModel> tableSample;
    @FXML
    private TableColumn<PairModel, String> pairColumn;
    @FXML
    private TableColumn<PairModel, Void> responseColumn;
    @FXML
    private TableColumn<PairModel, String> ampResponseColumn;
    @FXML
    private TableColumn<PairModel, String> ampColumn;
    @FXML
    private TableColumn<PairModel, String> ampRelativeResponseColumn;
    @FXML
    private TableColumn<PairModel, String> frequencyResponseColumn;
    @FXML
    private TableColumn<PairModel, String> frequencyColumn;
    @FXML
    private TableColumn<PairModel, String> frequencyRelativeResponseColumn;
    @FXML
    private TableColumn<PairModel, String> phaseResponseColumn;
    @FXML
    private TableColumn<PairModel, String> phaseColumn;
    @FXML
    private TableColumn<PairModel, String> phaseRelativeResponseColumn;
    @FXML
    private Slider phaseSlider;
    @FXML
    private TextField phaseTextField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Slider pSlider;
    @FXML
    private TextField pTextField;
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
    private ProcessModel processModel = new ProcessModel();
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

        initTableView();

        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);
        statusBarLine.setProcessView(true);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        processModel.chart(processGraph);
        experimentModel.setProcessModel(processModel);

        programController = new ProgramController(amplitudeSlider, amplitudeTextField, calibratedAmplitudeTextField,
                dSlider, dTextField, frequencySlider, frequencyTextField, iSlider, iTextField, mainPanel,
                phaseSlider, phaseTextField, pSlider, pTextField, toolbarSettings, topPanel);
    }

    private void initTableView() {
        processModel.initProcessSampleData(tableSample);
        processModel.SetProcessSampleColumnColorFunction(responseColumn);
        Utils.makeHeaderWrappable(pairColumn);
        processModel.init(ampResponseColumn);
        processModel.init(ampColumn);
        processModel.init(ampRelativeResponseColumn);
        processModel.init(frequencyResponseColumn);
        processModel.init(frequencyColumn);
        processModel.init(frequencyRelativeResponseColumn);
        processModel.init(phaseResponseColumn);
        processModel.init(phaseColumn);
        processModel.init(phaseRelativeResponseColumn);

        pairColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ampResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value1Property());
        ampColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value2Property());
        ampRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group1Value3Property());
        phaseResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value1Property());
        phaseColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value2Property());
        phaseRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group2Value3Property());
        frequencyResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value1Property());
        frequencyColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value2Property());
        frequencyRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().group3Value3Property());
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

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
