package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Process;
import ru.avem.posum.models.Process.*;
import ru.avem.posum.utils.StatusBarLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessController implements BaseController {
    @FXML
    private Button addCommandButton;
    @FXML
    private Button addEventButton;
    @FXML
    private CheckBox amplitudeCheckBox;
    @FXML
    private Slider amplitudeSlider;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private Label amplitudeVoltLabel;
    @FXML
    private CheckBox autoscaleCheckBox;
    @FXML
    private Button backButton;
    @FXML
    private Label calibratedAmplitudeLabel;
    @FXML
    private TextField calibratedAmplitudeTextField;
    @FXML
    private TextField calibratedDcTextField;
    @FXML
    private Label calibratedDcLabel;
    @FXML
    private TextField calibratedRmsTextField;
    @FXML
    private Label calibratedRmsLabel;
    @FXML
    private TableView commandsTableView;
    @FXML
    private Label checkIcon;
    @FXML
    private CheckBox dcCheckBox;
    @FXML
    private Label dLabel;
    @FXML
    private Slider dcSlider;
    @FXML
    private TextField dcTextField;
    @FXML
    private Label dcLabel;
    @FXML
    private Slider dSlider;
    @FXML
    private TextField dTextField;
    @FXML
    private CheckBox frequencyCheckBox;
    @FXML
    private Label frequencyLabel;
    @FXML
    private Slider frequencySlider;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private Label horizontalScaleLabel;
    @FXML
    private ComboBox<String> horizontalScaleComboBox;
    @FXML
    private Label iLabel;
    @FXML
    private Button initializeButton;
    @FXML
    private Slider iSlider;
    @FXML
    private TextField iTextField;
    @FXML
    private TableView<Events> journalTableView;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<ChannelModel> table;
    @FXML
    private TableColumn<ChannelModel, String> channelsColumn;
    @FXML
    private TableColumn<ChannelModel, HBox> responseColumn;
    @FXML
    private TableColumn<ChannelModel, String> ampResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> ampColumn;
    @FXML
    private TableColumn<ChannelModel, String> ampRelativeResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> frequencyResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> frequencyColumn;
    @FXML
    private TableColumn<ChannelModel, String> frequencyRelativeResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> rmsResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> rmsColumn;
    @FXML
    private TableColumn<ChannelModel, String> rmsRelativeResponseColumn;
    @FXML
    private CheckBox rmsCheckBox;
    @FXML
    private Slider rmsSlider;
    @FXML
    private TextField rmsTextField;
    @FXML
    private Label rmsLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label pLabel;
    @FXML
    private Slider pSlider;
    @FXML
    private TextField pTextField;
    @FXML
    private Label rarefactionCoefficientLabel;
    @FXML
    private ComboBox<String> rarefactionCoefficientComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button saveJournalButton;
    @FXML
    private Button savePointButton;
    @FXML
    private Button saveProtocolButton;
    @FXML
    private Button saveWaveformButton;
    @FXML
    private Button smoothStopButton;
    @FXML
    private Button startButton;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Button stopButton;
    @FXML
    private Label timeLabel;
    @FXML
    private TextField timeTextField;
    @FXML
    private ToolBar toolbarSettings;
    @FXML
    private VBox topPanel;
    @FXML
    private Button toProgramButton;
    @FXML
    private Label verticalScaleLabel;
    @FXML
    private ComboBox<String> verticalScaleComboBox;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private EventsController eventsController = new EventsController();
    private GraphController graphController;
    private boolean initialized;
    private LinkingController linkingController;
    private Process process = new Process();
    private ProcessModel processModel = new ProcessModel();
    private RegulatorParametersController regulatorParametersController;
    private StatusBarLine statusBarLine;
    private TableController tableController;
    private StopwatchController stopwatchController;
    private TestProgram testProgram;
    private List<Node> uiElements = new ArrayList<>();
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, true, progressIndicator, statusBar, warningIcon);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        graphController = new GraphController(autoscaleCheckBox, graph, horizontalScaleLabel, horizontalScaleComboBox,
                process, rarefactionCoefficientLabel, rarefactionCoefficientComboBox, verticalScaleLabel, verticalScaleComboBox,
                this);

        regulatorParametersController = new RegulatorParametersController(amplitudeCheckBox, amplitudeVoltLabel, amplitudeTextField,
                calibratedAmplitudeLabel, calibratedAmplitudeTextField, amplitudeSlider, dcCheckBox, dcLabel,
                dcTextField, calibratedDcLabel, calibratedDcTextField, dcSlider, rmsCheckBox, rmsLabel, rmsTextField,
                calibratedRmsLabel, calibratedRmsTextField, rmsSlider, frequencyCheckBox, frequencyLabel,
                frequencyTextField, frequencySlider, pLabel, pSlider, pTextField, iLabel, iSlider, iTextField,
                dLabel, dSlider, dTextField, mainPanel, toolbarSettings, topPanel, table, statusBarLine, saveButton,
                process);

        tableController = new TableController(table, channelsColumn, responseColumn, ampResponseColumn,
                ampColumn, ampRelativeResponseColumn, frequencyResponseColumn, frequencyColumn, frequencyRelativeResponseColumn,
                rmsResponseColumn, rmsColumn, rmsRelativeResponseColumn, graphController, this);

        stopwatchController = new StopwatchController(this, timeLabel, timeTextField);

        initEventsTableView();
        listenTableViews();
        fillListOfUiElements();
    }

    private void initEventsTableView() {
        journalTableView.setItems(eventsController.getEventModel().getEvents());
        eventsController.setEventsColors(journalTableView);
        eventTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eventDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
    }

    private void listenTableViews() {
        table.getItems().addListener((ListChangeListener<ChannelModel>) observable -> {
            initializeButton.setDisable(table.getItems().isEmpty());
        });

        journalTableView.getItems().addListener((ListChangeListener<Events>) observable -> {
            initializeButton.setDisable(journalTableView.getItems().isEmpty());
        });
    }

    private void fillListOfUiElements() {
        uiElements.add(toProgramButton);
        uiElements.add(initializeButton);
        uiElements.add(startButton);
        uiElements.add(smoothStopButton);
        uiElements.add(stopButton);
        uiElements.add(timeLabel);
        uiElements.add(timeTextField);
        uiElements.add(savePointButton);
        uiElements.add(saveWaveformButton);
        uiElements.add(saveProtocolButton);
        uiElements.add(backButton);
        uiElements.add(table);
        uiElements.add(graph);
        uiElements.add(commandsTableView);
        uiElements.add(addCommandButton);
        uiElements.add(journalTableView);
        uiElements.add(addEventButton);
        uiElements.add(saveJournalButton);
        uiElements.add(autoscaleCheckBox);
        uiElements.add(rarefactionCoefficientLabel);
        uiElements.add(rarefactionCoefficientComboBox);
        uiElements.add(verticalScaleLabel);
        uiElements.add(verticalScaleComboBox);
        uiElements.add(horizontalScaleLabel);
        uiElements.add(horizontalScaleComboBox);
    }

    public void handleInitialize() {
        if (!processModel.getModules().isEmpty()) {
            doInitialization();
        } else {
            statusBarLine.setStatus("Отсутствуют каналы для инициализации", false);
        }
    }

    private void doInitialization() {
        if (!initialized) {
            statusBarLine.toggleProgressIndicator(false);
            statusBarLine.setStatusOfProgress("Инициализация модулей");
            eventsController.getEventModel().addEvent("Инициализация модулей", EventsTypes.LOG);

            toggleUiElements(true);
            regulatorParametersController.clear();

            new Thread(() -> {
                processModel.parseSettings();
                setSettings();

                process.connect();

                if (process.isConnected()) {
                    process.initialize();
                    checkInitialization();
                } else {
                    showErrors();
                    statusBarLine.toggleProgressIndicator(true);
                    statusBarLine.clearStatusBar();
                    statusBarLine.setStatus("Ошибка открытия соединений с модулями", false);

                    toggleInitializationUiElements();
                }
            }).start();
        } else {
            statusBarLine.toggleProgressIndicator(false);
            statusBarLine.setStatusOfProgress("Отмена инициализации модулей");
            eventsController.getEventModel().addEvent("Отмена инициализации модулей", EventsTypes.LOG);

            toggleUiElements(true);
            regulatorParametersController.clear();

            new Thread(() -> {
                process.disconnect();
                statusBarLine.toggleProgressIndicator(true);
                statusBarLine.clearStatusBar();

                if (!process.isConnected()) {
                    statusBarLine.setStatus("Отмена инициализации модулей успешно выполнена", true);
                    eventsController.getEventModel().addEvent("Отмена инициализации модулей успешно выполнена", EventsTypes.OK);
                    initialized = false;
                } else {
                    statusBarLine.setStatus("Произошла ошибка при отмене инициализации модулей", false);
                    eventsController.getEventModel().addEvent("Произошла ошибка при отмене инициализации модулей", EventsTypes.WARNING);
                }

                toggleInitializationUiElements();
            }).start();
        }
    }

    private void toggleInitializationUiElements() {
        if (!initialized) {
            toProgramButton.setDisable(false);
            initializeButton.setDisable(false);
            backButton.setDisable(false);
            table.setDisable(false);
            graph.setDisable(false);
            commandsTableView.setDisable(false);
            addCommandButton.setDisable(false);
            journalTableView.setDisable(false);
            addEventButton.setDisable(false);
            saveJournalButton.setDisable(journalTableView.getItems().isEmpty());
            Platform.runLater(() -> initializeButton.setText("Инициализация"));
        } else {
            startButton.setDisable(false);
            backButton.setDisable(false);
            table.setDisable(false);
            graph.setDisable(false);
            commandsTableView.setDisable(false);
            addCommandButton.setDisable(false);
            journalTableView.setDisable(false);
            addEventButton.setDisable(false);
            saveJournalButton.setDisable(journalTableView.getItems().isEmpty());
            Platform.runLater(() -> initializeButton.setText("Отменить"));
        }
    }

    private void toggleUiElements(boolean isDisable) {
        for (Node element : uiElements) {
            element.setDisable(isDisable);
        }
    }

    private void setSettings() {
        process.setModulesTypes(processModel.getModulesTypes());
        process.setSlots(processModel.getSlots());
        process.setCrateSerialNumber(processModel.getCrateSerialNumber());
        process.setTypesOfChannels(processModel.getTypesOfChannels());
        process.setMeasuringRanges(processModel.getMeasuringRanges());
        process.setSettingsOfModules(processModel.getSettingsOfModules());
        process.setFirPaths(processModel.getFirPath());
        process.setIirPaths(processModel.getIirPath());
        process.setChannelsCounts(processModel.getChannelsCounts());
    }

    private void checkInitialization() {
        statusBarLine.toggleProgressIndicator(true);

        if (process.isInitialized()) {
            process.setStopped(false);
            statusBarLine.setStatus("Операция успешно выполнена", true);
            eventsController.getEventModel().addEvent("Успешная инициализация модулей", EventsTypes.OK);
            initialized = true;
            Platform.runLater(() -> startButton.requestFocus());
        } else {
            statusBarLine.setStatus("Ошибка инициализации модулей", false);
            showErrors();
        }

        toggleInitializationUiElements();
    }

    private void showErrors() {
        List<Pair<String, String>> statuses = process.getBadStatus();

        for (Pair<String, String> status : statuses) {
            String error = String.format("%s. %s.", status.getKey(), status.getValue());
            eventsController.getEventModel().addEvent(error, EventsTypes.ERROR);
        }
    }

    public void handleStart() {
        statusBarLine.setStatusOfProgress("Запуск программы испытаний");
        eventsController.getEventModel().addEvent("Запуск программы испытаний", EventsTypes.LOG);

        toggleUiElements(true);

        new Thread(() -> {
            process.run();
            checkRunning();
        }).start();
    }

    private void checkRunning() {
        statusBarLine.clearStatusBar();
        statusBarLine.toggleProgressIndicator(true);

        if (process.isRan()) {
            statusBarLine.setStatus("Операция успешно выполнена", true);
            eventsController.getEventModel().addEvent("Успешный запуск модулей", EventsTypes.OK);

            initializeButton.setDisable(true);
            smoothStopButton.setDisable(false);
            stopButton.setDisable(false);
            timeLabel.setDisable(false);
            timeTextField.setDisable(false);
            savePointButton.setDisable(false);
            saveWaveformButton.setDisable(false);
            saveProtocolButton.setDisable(false);
            backButton.setDisable(false);
            table.setDisable(false);
            graph.setDisable(false);
            commandsTableView.setDisable(false);
            journalTableView.setDisable(false);
            saveJournalButton.setDisable(journalTableView.getItems().isEmpty());
            addEventButton.setDisable(false);
            Platform.runLater(() -> stopButton.requestFocus());

            process.setStopped(false);
            process.initData(processModel.getModules());
            tableController.showParametersOfSignal();
            stopwatchController.startStopwatch();
            tableController.getRegulatorController().setModules(processModel.getModules());
            tableController.getRegulatorController().setTypesOfModules(processModel.getTypesOfModules());

            while (!process.isStopped()) {
                double[] regulatorSignal = tableController.getRegulatorController().getSignalForDac();
                int dacIndex = tableController.getRegulatorController().getDacIndex();
                process.getData()[dacIndex] = regulatorSignal;
                process.perform();
            }
        } else {
            statusBarLine.setStatus("Ошибка запуска программы испытаний", false);
            showErrors();
            process.finish();
            toggleInitializationUiElements();
        }
    }

    public void handleSmoothStopButton() {

    }

    public void handleStop() {
        statusBarLine.setStatusOfProgress("Завершение программы испытаний");
        eventsController.getEventModel().addEvent("Завершение программы испытаний", EventsTypes.LOG);

        graphController.getGraphModel().clear();
        toggleUiElements(true);

        new Thread(() -> {
            process.setStopped(true);
            process.finish();
            stopwatchController.pauseStopwatch();
            checkFinish();
        }).start();
    }

    private void checkFinish() {
        statusBarLine.toggleProgressIndicator(true);

        if (process.isFinished()) {
            statusBarLine.setStatus("Программа испытаний успешно завершена", true);
            eventsController.getEventModel().addEvent("Успешное завершение программы испытаний", EventsTypes.OK);
        } else {
            statusBarLine.setStatus("Ошибка завершения программы испытаний", false);
            showErrors();
        }

        tableController.setDefaultChannelsState();
        graphController.setDefaultGraphControlsState();

        initialized = false;
        toggleInitializationUiElements();
        saveProtocolButton.setDisable(false);
        Platform.runLater(() -> saveProtocolButton.requestFocus());
    }

    public void handleToProgramButton() {
        regulatorParametersController.toggleSettingsPanel();
    }

    public void handleLinkButton() {
        processModel.initListViews();
        wm.setScene(WindowsManager.Scenes.LINKING_SCENE);
    }

    public void handleSavePointButton() {

    }

    public void handleSaveWaveformButton() {

    }

    public void handleSaveProtocolButton() {

    }

    public void handleBack() {
        ButtonType ok = new ButtonType("Да", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = processModel.createExitAlert(ok, cancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ok) {
            if (!process.isStopped()) {
                handleStop();
            }

            regulatorParametersController.clear();
            stopwatchController.stopStopwatch();
            wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
        }
    }

    public void handleAddEvent() {
        eventsController.showDialogOfEventAdding();
    }

    public void handleSaveJournal() {
    }

    public void handleSaveRegulatorParameters() {
        if (table.getSelectionModel().getSelectedIndex() != -1) {
            ChannelModel selectedChannel = table.getSelectionModel().getSelectedItem();
            regulatorParametersController.save(selectedChannel);
            statusBarLine.setStatus("Настройки успешно сохранены", true);
        } else {
            statusBarLine.setStatus("Не выбран канал для сохранения", false);
        }
    }

    public void handlePlugButton() {
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public Process getProcess() {
        return process;
    }

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public StatusBarLine getStatusBarLine() {
        return statusBarLine;
    }

    public Button getStopButton() {
        return stopButton;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
        linkingController = cm.getLinkingController();
        linkingController.setProcessController(this);
        processModel.setLm(linkingController);
        regulatorParametersController.setLm(linkingController);
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
        loadTestProgram();
    }

    private void loadTestProgram() {
        Platform.runLater(() -> table.getItems().clear());
        processModel.clear();
        commandsTableView.getItems().clear();
        journalTableView.getItems().clear();
        statusBarLine.toggleProgressIndicator(true);
        statusBarLine.clearStatusBar();
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
