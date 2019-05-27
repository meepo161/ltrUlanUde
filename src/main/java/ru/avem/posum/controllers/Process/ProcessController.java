package ru.avem.posum.controllers.Process;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.hardware.Process;
import ru.avem.posum.models.Process.*;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

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
    private Process process = new Process();
    private ProgramController programController;
    private List<Node> uiElements = new ArrayList<>();
    private StatusBarLine statusBarLine;
    private TableController tableController;
    private TestProgram testProgram;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, true, progressIndicator, statusBar, warningIcon);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        programController = new ProgramController(amplitudeCheckBox, amplitudeVoltLabel, amplitudeTextField,
                calibratedAmplitudeLabel, calibratedAmplitudeTextField, amplitudeSlider, dcCheckBox, dcLabel,
                dcTextField, calibratedDcLabel, calibratedDcTextField, dcSlider, rmsCheckBox, rmsLabel, rmsTextField,
                calibratedRmsLabel, calibratedRmsTextField, rmsSlider, frequencyCheckBox, frequencyLabel,
                frequencyTextField, frequencySlider, pLabel, pSlider, pTextField, iLabel, iSlider, iTextField,
                dLabel, dSlider, dTextField, mainPanel, toolbarSettings, topPanel, table, statusBarLine, saveButton);

        graphController = new GraphController(autoscaleCheckBox, graph, horizontalScaleLabel, horizontalScaleComboBox,
                process, verticalScaleLabel, verticalScaleComboBox);

        tableController = new TableController(table, channelsColumn, responseColumn, ampResponseColumn,
                ampColumn, ampRelativeResponseColumn, frequencyResponseColumn, frequencyColumn, frequencyRelativeResponseColumn,
                rmsResponseColumn, rmsColumn, rmsRelativeResponseColumn, graphController, this);

        initEventsTableView();
        listenTableViews();
        fillListOfUiElements();
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

    public void handleInitialize() {
        if (!getModules().isEmpty()) {
            statusBarLine.toggleProgressIndicator(false);
            statusBarLine.setStatusOfProgress("Инициализация модулей");
            eventsController.getEventModel().addEvent("Инициализация модулей", EventsTypes.LOG);

            toggleUiElements(true);
            programController.clear();

            new Thread(() -> {
                parseSettings(getModules());
                process.connect();

                if (process.isConnected()) {
                    process.initialize();
                    checkInitialization();
                } else {
                    showErrors();
                    statusBarLine.toggleProgressIndicator(true);
                    statusBarLine.clearStatusBar();
                    statusBarLine.setStatus("Ошибка открытия соединений с модулями", false);

                    toggleUiElements(false);
                    toggleInitializationUiElements(true);
                }
            }).start();
        } else {
            statusBarLine.setStatus("Отсутствуют каналы для инициализации", false);
        }
//        experimentModel.Init();
    }

    public ObservableList<Modules> getModules() {
        List<Modules> linkedModules = cm.getLinkedModules();
        ObservableList<Modules> chosenModules = cm.getChosenModules();

        for (Modules module : linkedModules) {
            if (!chosenModules.contains(module)) {
                chosenModules.add(module);
            }
        }

        return chosenModules;
    }

    private void toggleUiElements(boolean isDisable) {
        for (Node element : uiElements) {
            element.setDisable(isDisable);
        }
    }

    private void toggleInitializationUiElements(boolean isDisable) {
        startButton.setDisable(isDisable);
        smoothStopButton.setDisable(isDisable);
        stopButton.setDisable(isDisable);
        savePointButton.setDisable(isDisable);
        saveWaveformButton.setDisable(isDisable);
        savePointButton.setDisable(isDisable);
        saveProtocolButton.setDisable(isDisable);
    }

    private void parseSettings(List<Modules> modules) {
        List<String> modulesTypes = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();
        String crateSerialNumber = "";
        List<int[]> typesOfChannels = new ArrayList<>();
        List<int[]> measuringRanges = new ArrayList<>();
        List<int[]> settingsOfModules = new ArrayList<>();
        List<String> firPath = new ArrayList<>();
        List<String> iirPath = new ArrayList<>();
        List<Integer> channelsCounts = new ArrayList<>();

        for (Modules module : modules) {
            modulesTypes.add(module.getModuleType());
            slots.add(module.getSlot());
            channelsCounts.add(module.getChannelsCount());

            if (!module.getModuleType().equals(Crate.LTR34)) {
                typesOfChannels.add(Modules.getTypesOfChannels(module));
                measuringRanges.add(Modules.getMeasuringRanges(module));
                settingsOfModules.add(Modules.getSettingsOfModule(module));
                firPath.add(module.getFirPath());
                iirPath.add(module.getIirPath());
            } else {
                typesOfChannels.add(new int[8]);
                measuringRanges.add(new int[8]);
                settingsOfModules.add(new int[8]);
                firPath.add("");
                iirPath.add("");
            }
        }

        List<TestProgram> testPrograms = TestProgramRepository.getAllTestPrograms();

        for (TestProgram testProgram : testPrograms) {
            if (modules.get(0).getTestProgramId() == testProgram.getId()) {
                crateSerialNumber = testProgram.getCrateSerialNumber();
                break;
            }
        }

        process.setModulesTypes(modulesTypes);
        process.setSlots(slots);
        process.setCrateSerialNumber(crateSerialNumber);
        process.setTypesOfChannels(typesOfChannels);
        process.setMeasuringRanges(measuringRanges);
        process.setSettingsOfModules(settingsOfModules);
        process.setFirPaths(firPath);
        process.setIirPaths(iirPath);
        process.setChannelsCounts(channelsCounts);
    }

    private void checkInitialization() {
        statusBarLine.toggleProgressIndicator(true);
        statusBarLine.clearStatusBar();

        toggleUiElements(false);

        if (process.isInitialized()) {
            statusBarLine.setStatus("Операция успешно выполнена", true);
            eventsController.getEventModel().addEvent("Успешная инициализация модулей", EventsTypes.OK);

            toProgramButton.setDisable(true);
            initializeButton.setDisable(true);
            smoothStopButton.setDisable(true);
            stopButton.setDisable(true);
            savePointButton.setDisable(true);
            saveWaveformButton.setDisable(true);
            saveProtocolButton.setDisable(true);
        } else {
            statusBarLine.setStatus("Ошибка инициализации модулей", false);
            showErrors();

            toggleInitializationUiElements(true);
        }
    }

    private void showErrors() {
        List<Pair<String, String>> statuses = process.getBadStatus();

        for (Pair<String, String> status : statuses) {
            String error = String.format("%s. %s.", status.getKey(), status.getValue());
            eventsController.getEventModel().addEvent(error, EventsTypes.ERROR);
        }
    }

    public void handleStart() {
        statusBarLine.clearStatusBar();
        statusBarLine.toggleProgressIndicator(false);
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

        toggleUiElements(false);

        if (process.isRan()) {
            statusBarLine.setStatus("Операция успешно выполнена", true);
            eventsController.getEventModel().addEvent("Успешный запуск модулей", EventsTypes.OK);

            toProgramButton.setDisable(true);
            initializeButton.setDisable(true);
            addCommandButton.setDisable(true);
            startButton.setDisable(true);

            process.setStopped(false);
            while (!process.isStopped()) {
                process.initData(getModules());
                process.perform();
            }
        } else {
            statusBarLine.setStatus("Ошибка запуска программы испытаний", false);
            showErrors();

            process.finish();
            toggleInitializationUiElements(true);
        }
    }

    public void handleSmoothStopButton() {

    }

    public void handleStop() {
        statusBarLine.clearStatusBar();
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Завершение программы испытаний");
        eventsController.getEventModel().addEvent("Завершение программы испытаний", EventsTypes.LOG);

        toggleUiElements(true);

        new Thread(() -> {
            process.setStopped(true);
            process.finish();
            checkFinish();
        }).start();
    }

    private void checkFinish() {
        statusBarLine.clearStatusBar();
        statusBarLine.toggleProgressIndicator(true);

        toggleUiElements(false);

        if (process.isFinished()) {
            statusBarLine.setStatus("Программа испытаний успешно завершена", true);
            eventsController.getEventModel().addEvent("Успешное завершение программы испытаний", EventsTypes.OK);

        } else {
            statusBarLine.setStatus("Ошибка завершения программы испытаний", false);
            showErrors();
        }

        startButton.setDisable(true);
        smoothStopButton.setDisable(true);
        stopButton.setDisable(true);
    }

    public void handleToProgramButton() {
        programController.toggleSettingsPanel();
    }

    public void handleLinkButton() {
        cm.initListViews();
        wm.setScene(WindowsManager.Scenes.LINKING_SCENE);
    }

    public void handleSavePointButton() {

    }

    public void handleSaveWaveformButton() {

    }

    public void handleSaveProtocolButton() {

    }

    public void handleBackButton() {
        if (true) { // TODO: change this shit
            ButtonType ok = new ButtonType("Да", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ok, cancel);
            alert.setTitle("Подтвердите действие");
            alert.setHeaderText("Вернуться в главное окно?");
            ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
            buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 14px;\n" + "-fx-background-radius: 5px;\n" +
                    "\t-fx-border-radius: 5px;"));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == ok) {
                    programController.clear();
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
        result.ifPresent(eventText -> eventsController.getEventModel().setEvent(eventText));
    }

    public void handleExpandEventTableButton() {
    }

    public void handleSave() {
        if (table.getSelectionModel().getSelectedIndex() != -1) {
            ChannelModel selectedChannel = table.getSelectionModel().getSelectedItem();

            selectedChannel.setAmplitude(amplitudeTextField.getText());
            selectedChannel.setDc(dcTextField.getText());
            selectedChannel.setRms(rmsTextField.getText());
            selectedChannel.setFrequency(frequencyTextField.getText());
            selectedChannel.setPvalue(pTextField.getText());
            selectedChannel.setIvalue(iTextField.getText());
            selectedChannel.setDvalue(dTextField.getText());
            selectedChannel.setChosenParameterIndex(String.valueOf(programController.getChosenParameterIndex()));

            statusBarLine.setStatus("Настройки успешно сохранены", true);
        } else {
            statusBarLine.setStatus("Не выбран канал для сохранения", false);
        }
    }

    public void handlePlugButton() {
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
        programController.setCm(cm);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public ControllerManager getCm() {
        return cm;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public TableController getTableController() {
        return tableController;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
