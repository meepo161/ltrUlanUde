package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
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
    private TextField calibratedDcTextField;
    @FXML
    private Label checkIcon;
    @FXML
    private Slider dcSlider;
    @FXML
    private TextField dcTextField;
    @FXML
    private Slider dSlider;
    @FXML
    private TextField dValueTextField;
    @FXML
    private Slider frequencySlider;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private Slider iSlider;
    @FXML
    private TextField iValueTextField;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private LineChart<Number, Number> processGraph;
    @FXML
    private TableColumn<Events, String> eventTimeColumn;
    @FXML
    private TableColumn<Events, String> eventDescriptionColumn;
    @FXML
    private TableView<PairModel> table;
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
    private TextField pValueTextField;
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

    private ContextMenu contextMenu = new ContextMenu();
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
        initContextMenu();
        listenMouse();

        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);
        statusBarLine.setProcessView(true);
        statusBarLine.setStatus("Программа испытаний загружена", true);

        processModel.chart(processGraph);
        experimentModel.setProcessModel(processModel);

        programController = new ProgramController(amplitudeSlider, amplitudeTextField, calibratedAmplitudeTextField,
                calibratedDcTextField, dcSlider, dcTextField, dSlider, dValueTextField, frequencySlider,
                frequencyTextField, iSlider, iValueTextField, mainPanel, phaseSlider, phaseTextField, pSlider,
                pValueTextField, toolbarSettings, topPanel);
    }

    private void initTableView() {
        processModel.initProcessSampleData(table);
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
        ampResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseAmplitudeProperty());
        ampColumn.setCellValueFactory(cellData -> cellData.getValue().amplitudeProperty());
        ampRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseAmplitudeProperty());
        frequencyResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseFrequencyProperty());
        frequencyColumn.setCellValueFactory(cellData -> cellData.getValue().frequencyProperty());
        frequencyRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseFrequencyProperty());
        phaseResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responsePhaseProperty());
        phaseColumn.setCellValueFactory(cellData -> cellData.getValue().phaseProperty());
        phaseRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponsePhaseProperty());
    }

    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deletePairModel());
        menuItemClear.setOnAction(event -> clearPairModels());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deletePairModel() {
        PairModel selectedPairModel = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedPairModel);
        deleteDescriptions(selectedPairModel);
        statusBarLine.setStatus("Пара успешно удалена", true);
    }

    private void deleteDescriptions(PairModel pairModel) {
        String descriptionOfDacChannel = pairModel.getName().split(" - ")[0];
        String descriptionOfAdcChannel = pairModel.getName().split(" - ")[1];

        for (Pair<CheckBox, CheckBox> descriptions : cm.getRemovedDescriptions()) {
            if (descriptions.getKey().getText().contains(descriptionOfDacChannel) ||
                    descriptions.getValue().getText().contains(descriptionOfAdcChannel)) {
                Platform.runLater(() -> cm.getRemovedDescriptions().remove(descriptions));
            }
        }
    }

    private void clearPairModels() {
       ObservableList<PairModel> pairModels = table.getItems();
       table.getItems().removeAll(pairModels);
       clearDescriptions();
       statusBarLine.setStatus("Все пары успешно удалены", true);
    }

    private void clearDescriptions() {
        ObservableList<Pair<CheckBox, CheckBox>> descriptions = cm.getRemovedDescriptions();
        cm.getRemovedDescriptions().removeAll(descriptions);
    }

    private void listenMouse() {
        table.setRowFactory(tv -> {
            TableRow<PairModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(table, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }

                if (event.getButton() == MouseButton.PRIMARY && (!row.isEmpty())) {
                    PairModel pair = table.getSelectionModel().getSelectedItem();
                    amplitudeTextField.setText(pair.getAmplitude());
                    dcTextField.setText(pair.getDc());
                    frequencyTextField.setText(pair.getFrequency());
                    phaseTextField.setText(pair.getPhase());
                    pValueTextField.setText(pair.getpValue());
                    iValueTextField.setText(pair.getiValue());
                    dValueTextField.setText(pair.getdValue());
                }
            });
            return row;
        });
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

    public void handleSavePair() {
        if (table.getSelectionModel().getSelectedIndex() != -1) {
            PairModel pair = table.getSelectionModel().getSelectedItem();

            pair.setAmplitude(amplitudeTextField.getText());
            pair.setDc(dcTextField.getText());
            pair.setFrequency(frequencyTextField.getText());
            pair.setPhase(phaseTextField.getText());
            pair.setPvalue(pValueTextField.getText());
            pair.setIvalue(iValueTextField.getText());
            pair.setDvalue(dValueTextField.getText());

            statusBarLine.setStatus("Пара успешно сохранена", true);
        } else {
            statusBarLine.setStatus("Не выбрана пара для сохранения", false);
        }
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
