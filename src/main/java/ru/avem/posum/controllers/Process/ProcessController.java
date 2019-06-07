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
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;

public class ProcessController implements BaseController {
    @FXML
    private Button addChannelsButton;
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
    private TableView<Command> commandsTableView;
    @FXML
    private TableColumn<Command, String> commandsTypesColumn;
    @FXML
    private TableColumn<Command, String> commandsDescriptionsColumn;
    @FXML
    private Label checkIcon;
    @FXML
    private CheckBox dcCheckBox;
    @FXML
    private Label dLabel;
    @FXML
    private Label dcLabel;
    @FXML
    private Slider dcSlider;
    @FXML
    private TextField dcTextField;
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
    private TableView<Event> eventsTableView;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private TableColumn<Event, String> eventTimeColumn;
    @FXML
    private TableColumn<Event, String> eventDescriptionColumn;
    @FXML
    private TableView<ChannelModel> table;
    @FXML
    private TableColumn<ChannelModel, String> channelsColumn;
    @FXML
    private TableColumn<ChannelModel, HBox> responseColumn;
    @FXML
    private TableColumn<ChannelModel, String> ampResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> dcResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> frequencyResponseColumn;
    @FXML
    private TableColumn<ChannelModel, String> loadsCounterColumn;
    @FXML
    private TableColumn<ChannelModel, String> rmsResponseColumn;
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

    private CalibrationModel calibrationModel = new CalibrationModel();
    private CommandsController commandsController;
    private ControllerManager cm;
    private EventsController eventsController;
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

        commandsController = new CommandsController(this, commandsTableView);

        eventsController = new EventsController(this, eventsTableView);

        graphController = new GraphController(autoscaleCheckBox, graph, horizontalScaleLabel, horizontalScaleComboBox,
                process, rarefactionCoefficientLabel, rarefactionCoefficientComboBox, verticalScaleLabel, verticalScaleComboBox,
                this);

        regulatorParametersController = new RegulatorParametersController(amplitudeCheckBox, amplitudeVoltLabel, amplitudeTextField,
                calibratedAmplitudeLabel, calibratedAmplitudeTextField, amplitudeSlider, dcCheckBox, dcLabel,
                dcTextField, calibratedDcLabel, calibratedDcTextField, dcSlider, frequencyCheckBox, frequencyLabel,
                frequencyTextField, frequencySlider, pLabel, pSlider, pTextField, iLabel, iSlider, iTextField,
                dLabel, dSlider, dTextField, mainPanel, toolbarSettings, topPanel, table, statusBarLine, saveButton,
                process, this);

        tableController = new TableController(table, channelsColumn, responseColumn, ampResponseColumn,
                dcResponseColumn, frequencyResponseColumn, loadsCounterColumn, rmsResponseColumn, graphController,
                this);

        stopwatchController = new StopwatchController(this, timeLabel, timeTextField);

        initCommandsTableView();
        initEventsTableView();
        listenTableViews();
        fillListOfUiElements();
    }

    private void initCommandsTableView() {
        commandsTableView.setItems(commandsController.getCommands());
        commandsTypesColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        commandsDescriptionsColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
    }

    private void initEventsTableView() {
        eventsTableView.setItems(eventsController.getEventsModel().getEvents());
        eventTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eventDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        eventsController.listen(eventsTableView);
    }

    private void listenTableViews() {
        table.getItems().addListener((ListChangeListener<ChannelModel>) observable -> {
            initializeButton.setDisable(table.getItems().isEmpty());
        });

        eventsTableView.getItems().addListener((ListChangeListener<Event>) observable -> {
            initializeButton.setDisable(eventsTableView.getItems().isEmpty());
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
        uiElements.add(eventsTableView);
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
            eventsController.getEventsModel().addEvent(testProgram.getId(),"Инициализация модулей", EventsTypes.LOG);

            toggleUiElements(true);
            regulatorParametersController.hideToolBar();

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
            eventsController.getEventsModel().addEvent(testProgram.getId(), "Отмена инициализации модулей", EventsTypes.LOG);

            toggleUiElements(true);
            regulatorParametersController.hideToolBar();

            new Thread(() -> {
                process.disconnect();
                statusBarLine.toggleProgressIndicator(true);
                statusBarLine.clearStatusBar();

                if (!process.isConnected()) {
                    statusBarLine.setStatus("Отмена инициализации модулей успешно выполнена", true);
                    eventsController.getEventsModel().addEvent(testProgram.getId(), "Отмена инициализации модулей успешно выполнена", EventsTypes.OK);
                    initialized = false;
                } else {
                    statusBarLine.setStatus("Произошла ошибка при отмене инициализации модулей", false);
                    eventsController.getEventsModel().addEvent(testProgram.getId(), "Произошла ошибка при отмене инициализации модулей", EventsTypes.WARNING);
                }

                toggleInitializationUiElements();
            }).start();
        }
    }

    private void toggleInitializationUiElements() {
        if (!initialized) {
            Platform.runLater(() -> {
                toProgramButton.setDisable(false);
                initializeButton.setDisable(false);
                backButton.setDisable(false);
                table.setDisable(false);
                graph.setDisable(false);
                commandsTableView.setDisable(false);
                addCommandButton.setDisable(false);
                eventsTableView.setDisable(false);
                addEventButton.setDisable(false);
                saveJournalButton.setDisable(eventsTableView.getItems().isEmpty());
                initializeButton.setText("Инициализация");
            });
        } else {
            Platform.runLater(() -> {
                startButton.setDisable(false);
                backButton.setDisable(false);
                table.setDisable(false);
                graph.setDisable(false);
                commandsTableView.setDisable(false);
                addCommandButton.setDisable(false);
                eventsTableView.setDisable(false);
                addEventButton.setDisable(false);
                saveJournalButton.setDisable(eventsTableView.getItems().isEmpty());
                initializeButton.setText("Отменить");
            });
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
            eventsController.getEventsModel().addEvent(testProgram.getId(), "Успешная инициализация модулей", EventsTypes.OK);
            initialized = true;
        } else {
            statusBarLine.setStatus("Ошибка инициализации модулей", false);
            showErrors();
        }

        toggleInitializationUiElements();
        Platform.runLater(() -> startButton.requestFocus());
    }

    private void showErrors() {
        List<Pair<String, String>> statuses = process.getBadStatus();

        for (Pair<String, String> status : statuses) {
            String error = String.format("%s. %s.", status.getKey(), status.getValue());
            eventsController.getEventsModel().addEvent(testProgram.getId(), error, EventsTypes.ERROR);
        }
    }

    public void handleStart() {
        statusBarLine.setStatusOfProgress("Запуск программы испытаний");
        eventsController.getEventsModel().addEvent(testProgram.getId(), "Запуск программы испытаний", EventsTypes.LOG);

        toggleUiElements(true);

        Thread processThread = new Thread(() -> {
            process.run();
            checkRunning();
        });

        processThread.setPriority(Thread.MAX_PRIORITY);
        processThread.start();
    }

    private void checkRunning() {
        statusBarLine.clearStatusBar();
        statusBarLine.toggleProgressIndicator(true);

        if (process.isRan()) {
            statusBarLine.setStatus("Операция успешно выполнена", true);
            eventsController.getEventsModel().addEvent(testProgram.getId(), "Успешный запуск модулей", EventsTypes.OK);

            Platform.runLater(() -> {
                tableController.toggleResponseUiElements(false);
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
                eventsTableView.setDisable(false);
                saveJournalButton.setDisable(eventsTableView.getItems().isEmpty());
                addEventButton.setDisable(false);
                stopButton.requestFocus();
            });

            process.setStopped(false);
            process.initData(processModel.getModules());
            tableController.showParametersOfSignal();
            stopwatchController.startStopwatch();
            tableController.getRegulatorController().setFirstStart(true);
            tableController.getRegulatorController().setModules(processModel.getModules());
            tableController.getRegulatorController().setTypesOfModules(processModel.getTypesOfModules());
            tableController.initRegulator();
            calibrationModel.loadCalibrations(table.getItems(), processModel.getModules());
            commandsController.executeCommands();

            int dacIndex = tableController.getRegulatorController().getDacIndex();
            while (!process.isStopped()) {
                if (dacIndex != -1) {
                    process.getData()[dacIndex] = tableController.getRegulatorController().getSignalForDac();
                }

                process.perform();
                calibrationModel.calibrate(process.getData());
            }
        } else {
            statusBarLine.setStatus("Ошибка запуска программы испытаний", false);
            showErrors();
            process.finish();
            toggleInitializationUiElements();
        }
    }

    public void handleSmoothStopButton() {
        statusBarLine.setStatusOfProgress("Плавная остановка запущена");
        eventsController.getEventsModel().addEvent(testProgram.getId(), "Запущена плавная остановка", EventsTypes.LOG);
        tableController.getRegulatorController().doSmoothStop();

        new Thread(() -> {

            while (!tableController.getRegulatorController().isStopped()) {
                if (tableController.getRegulatorController().isStopped()) {
                    handleStop();
                    statusBarLine.setStatus("Плавная остановка успешно выполнена", true);
                    eventsController.getEventsModel().addEvent(testProgram.getId(), "Плавная остановка успешно выполнена", EventsTypes.OK);
                }
            }
        }).start();
    }

    public void handlePause() {
        statusBarLine.setStatusOfProgress("Программа испытаний поставлена на паузу");
        eventsController.getEventsModel().addEvent(testProgram.getId(), "Пауза программы испытаний", EventsTypes.LOG);

        new Thread(() -> {
            stopwatchController.pauseStopwatch();
            process.setPaused(true);

            while (process.isStopped() && process.isPaused()) {
                Utils.sleep(100);
            }

            process.setPaused(false);
        }).start();
    }

    public void handleStop() {
        statusBarLine.setStatusOfProgress("Завершение программы испытаний");
        eventsController.getEventsModel().addEvent(testProgram.getId(), "Завершение программы испытаний", EventsTypes.LOG);

        graphController.getGraphModel().clear();
        tableController.toggleResponseUiElements(true);
        toggleUiElements(true);

        new Thread(() -> {
            process.setStopped(true);
            Utils.sleep(1000);
            process.finish();
            tableController.getRegulatorController().setFirstStart(false);
            stopwatchController.pauseStopwatch();
            checkFinish();
        }).start();
    }

    private void checkFinish() {
        statusBarLine.toggleProgressIndicator(true);

        if (process.isFinished()) {
            statusBarLine.setStatus("Программа испытаний успешно завершена", true);
            eventsController.getEventsModel().addEvent(testProgram.getId(), "Успешное завершение программы испытаний", EventsTypes.OK);
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
        regulatorParametersController.showRegulationPanel();
        Platform.runLater(() -> addChannelsButton.requestFocus());
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
            regulatorParametersController.hideToolBar();
            stopwatchController.stopStopwatch();
            wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
        }
    }

    public void handleAddCommand() {
        commandsController.showDialogOfCommandAdding();
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

    public CalibrationModel getCalibrationModel() {
        return calibrationModel;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public LinkingController getLinkingController() {
        return linkingController;
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

    public TableController getTableController() {
        return tableController;
    }

    public long getTestProgramId() { return testProgram.getId(); }

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
        commandsController.loadCommands(testProgram.getId());
        eventsTableView.getItems().clear();
        eventsController.loadEvents(testProgram.getId());
        statusBarLine.toggleProgressIndicator(true);
        statusBarLine.clearStatusBar();
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
