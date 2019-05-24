package ru.avem.posum.controllers.Process;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    private LineChart<Number, Number> processGraph;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<ChannelModel> table;
    @FXML
    private TableColumn<ChannelModel, String> pairColumn;
    @FXML
    private TableColumn<ChannelModel, Void> responseColumn;
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
    private Button timeButton;
    @FXML
    private TextField timeTextField;
    @FXML
    private ToolBar toolbarSettings;
    @FXML
    private VBox topPanel;
    @FXML
    private Button toProgramButton;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private EventsModel eventModel = new EventsModel();
    private ExperimentModel experimentModel = new ExperimentModel();
    private Process process = new Process();
    private ProcessModel processModel = new ProcessModel();
    private ProgramController programController;
    private List<Node> uiElements = new ArrayList<>();
    private StatusBarLine statusBarLine;
    private TestProgram testProgram;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        eventModel.initEventData(journalTableView);
        eventModel.SetEventsTableFunction(journalTableView);
        eventTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        eventDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        initTableView();

        statusBarLine = new StatusBarLine(checkIcon, true, progressIndicator, statusBar, warningIcon);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        processModel.chart(processGraph);
        experimentModel.setProcessModel(processModel);

        programController = new ProgramController(amplitudeCheckBox, amplitudeVoltLabel, amplitudeTextField,
                calibratedAmplitudeLabel, calibratedAmplitudeTextField, amplitudeSlider, dcCheckBox, dcLabel,
                dcTextField, calibratedDcLabel, calibratedDcTextField, dcSlider, rmsCheckBox, rmsLabel, rmsTextField,
                calibratedRmsLabel, calibratedRmsTextField, rmsSlider, frequencyCheckBox, frequencyLabel,
                frequencyTextField, frequencySlider, pLabel, pSlider, pTextField, iLabel, iSlider, iTextField,
                dLabel, dSlider, dTextField, mainPanel, toolbarSettings, topPanel, table, statusBarLine, saveButton);

        fillListOfUiElements();
    }

    private void fillListOfUiElements() {
        uiElements.add(toProgramButton);
        uiElements.add(initializeButton);
        uiElements.add(startButton);
        uiElements.add(smoothStopButton);
        uiElements.add(stopButton);
        uiElements.add(timeLabel);
        uiElements.add(timeButton);
        uiElements.add(timeTextField);
        uiElements.add(savePointButton);
        uiElements.add(saveWaveformButton);
        uiElements.add(saveProtocolButton);
        uiElements.add(backButton);
        uiElements.add(table);
        uiElements.add(processGraph);
        uiElements.add(commandsTableView);
        uiElements.add(addCommandButton);
        uiElements.add(journalTableView);
        uiElements.add(addEventButton);
        uiElements.add(saveJournalButton);
    }

    private void initTableView() {
        processModel.initProcessSampleData(table);
        processModel.SetProcessSampleColumnColorFunction(responseColumn);
        Utils.makeHeaderWrappable(pairColumn);
        processModel.init(ampResponseColumn);
        processModel.init(ampColumn);
        processModel.init(ampRelativeResponseColumn);
        processModel.init(rmsResponseColumn);
        processModel.init(rmsColumn);
        processModel.init(rmsRelativeResponseColumn);
        processModel.init(frequencyResponseColumn);
        processModel.init(frequencyColumn);
        processModel.init(frequencyRelativeResponseColumn);

        pairColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ampResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseAmplitudeProperty());
        ampColumn.setCellValueFactory(cellData -> cellData.getValue().amplitudeProperty());
        ampRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseAmplitudeProperty());
        rmsResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseRmsProperty());
        rmsColumn.setCellValueFactory(cellData -> cellData.getValue().rmsProperty());
        rmsRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseRmsProperty());
        frequencyResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseFrequencyProperty());
        frequencyColumn.setCellValueFactory(cellData -> cellData.getValue().frequencyProperty());
        frequencyRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseFrequencyProperty());
    }

    public void handleInitialize() {
        List<Modules> linkedModules = cm.getLinkedModules();
        List<Modules> chosenModules = cm.getChosenModules();

        for (Modules module : linkedModules) {
            if (!chosenModules.contains(module)) {
                chosenModules.add(module);
            }
        }

        if (!chosenModules.isEmpty()) {
            statusBarLine.toggleProgressIndicator(false);
            statusBarLine.setStatusOfProgress("Инициализация модулей");

            toggleUiElements(true);
            programController.clear();

            new Thread(() -> {
                parseSettings(chosenModules);
                process.connect();

                if (process.isConnected()) {
                    process.initialize();
                    checkInitialization();
                } else {
                    Pair<String, String> badStatus = process.getBadStatus();
                    statusBarLine.toggleProgressIndicator(true);
                    statusBarLine.clearStatusBar();
                    statusBarLine.setStatus(String.format("%s. %s", badStatus.getKey(), badStatus.getValue()), false);
                }

                toggleUiElements(false);
            }).start();
        } else {
            statusBarLine.setStatus("Отсутствуют каналы для инициализации", false);
        }
//        experimentModel.Init();
    }

    private void toggleUiElements(boolean isDisable) {
        for (Node element : uiElements) {
            element.setDisable(isDisable);
        }
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

        if (process.isInitialized()) {
            statusBarLine.setStatus("Операция успешно выполнена", true);
        } else {
            Pair<String, String> badStatus = process.getBadStatus();
            statusBarLine.setStatus(String.format("%s. %s", badStatus.getKey(), badStatus.getValue()), false);
        }
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
            ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
            buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 14px;\n" + "-fx-background-radius: 5px;\n" +
                    "\t-fx-border-radius: 5px;"));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == ok) {
                    experimentModel.Terminate();
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
        result.ifPresent(eventText -> eventModel.setEvent(eventText));
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
