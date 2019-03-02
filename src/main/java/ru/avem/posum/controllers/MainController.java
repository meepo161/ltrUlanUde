package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.LTR212TablesRepository;
import ru.avem.posum.db.LTR24TablesRepository;
import ru.avem.posum.db.LTR34TablesRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR212Table;
import ru.avem.posum.db.models.LTR24Table;
import ru.avem.posum.db.models.LTR34Table;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.utils.StatusBarLine;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private Button openExperimentButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TableView<TestProgram> testProgramTableView;
    @FXML
    private TableColumn<TestProgram, Integer> columnTableViewIndex;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramName;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramCreatingDate;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramChangingDate;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramTime;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramType;
    @FXML
    private TableColumn<TestProgram, String> columnTestingSample;

    private WindowsManager wm;
    private ControllerManager cm;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private ObservableList<TestProgram> testPrograms;
    private List<TestProgram> allTestPrograms;
    private TestProgram testProgram;
    private long testProgramId;
    private int selectedIndex;
    private boolean isTestProgramSelected;

    @FXML
    private void initialize() {
        initTableView();
        addDoubleClickListener();
    }

    private void initTableView() {
        formatColumnsTitles();
        setColumns();
        getTestPrograms();
        showTestPrograms();
    }

    private void formatColumnsTitles() {
        makeColumnTitleWrapper(columnTestProgramName);
        makeColumnTitleWrapper(columnTestProgramCreatingDate);
        makeColumnTitleWrapper(columnTestProgramChangingDate);
        makeColumnTitleWrapper(columnTestProgramTime);
        makeColumnTitleWrapper(columnTestProgramType);
        makeColumnTitleWrapper(columnTestingSample);
    }

    private void makeColumnTitleWrapper(TableColumn col) {
        Label label = new Label(col.getText());
        label.setStyle("-fx-padding: 8px;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        StackPane stack = new StackPane();
        stack.getChildren().add(label);
        stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
        label.prefWidthProperty().bind(stack.prefWidthProperty());
        col.setGraphic(stack);
    }

    private void setColumns() {
        columnTableViewIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
        columnTestProgramName.setCellValueFactory(new PropertyValueFactory<>("testProgramName"));
        columnTestProgramCreatingDate.setCellValueFactory(new PropertyValueFactory<>("testProgramDate"));
        columnTestProgramChangingDate.setCellValueFactory(new PropertyValueFactory<>("testProgramDate"));
        columnTestProgramTime.setCellValueFactory(new PropertyValueFactory<>("testProgramTime"));
        columnTestProgramType.setCellValueFactory(new PropertyValueFactory<>("testProgramType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));
    }

    public void getTestPrograms() {
        TestProgramRepository.updateTestProgramIndexes();
        allTestPrograms = TestProgramRepository.getAllTestPrograms();
    }

    public void showTestPrograms() {
        testPrograms = FXCollections.observableArrayList(allTestPrograms);
        Platform.runLater(() -> {
            testProgramTableView.setItems(testPrograms);
        });
    }

    private void addDoubleClickListener() {
        testProgramTableView.setRowFactory(tv -> {
            TableRow<TestProgram> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleMenuItemEdit();
                }
            });
            return row;
        });
    }

    public void handleMenuItemAdd() {
        initModulesList();
        prepareSettingsScene();
        showSettingsScene();
    }

    private void initModulesList() {
        CrateModel crateModel = cm.getCrateModelInstance();
        crateModel.getLtr24ModulesList().clear();
        crateModel.getLtr34ModulesList().clear();
        crateModel.getLtr212ModulesList().clear();
    }

    private void prepareSettingsScene() {
        cm.loadDefaultSettings();
        cm.setEditMode(false);
        cm.hideRequiredFieldsSymbols();
    }

    private void showSettingsScene() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    public void handleMenuItemEdit() {
        getTestPrograms();
        checkSelection();

        if (isTestProgramSelected) {
            new Thread(() -> {
                initModulesList();
                prepareEditSettingsScene();
            }).start();
            showSettingsScene();
        }
    }

    private void prepareEditSettingsScene() {
        cm.showTestProgram(allTestPrograms.get(getSelectedItemIndex()));
        cm.toggleSettingsSceneButtons(false);
        cm.setEditMode(true);
    }

    private int getSelectedItemIndex() {
        return testProgramTableView.getSelectionModel().getSelectedIndex();
    }

    public void handleMenuItemCopy() {
        getTestPrograms();
        checkSelection();

        if (isTestProgramSelected) {
            toggleProgressIndicatorState(false);
            new Thread(() -> {
                copyTestProgram();
                copyTestProgramSettings();
                reloadTestProgramsList();
                showNotification("Программа испытаний скопирована");
            }).start();
        }
    }

    private void toggleProgressIndicatorState(boolean hide) {
        if (hide) {
            progressIndicator.setStyle("-fx-opacity: 0;");
        } else {
            progressIndicator.setStyle("-fx-opacity: 1.0;");
        }
    }

    private void getTestProgram(int itemIndex) {
        testProgram = allTestPrograms.get(itemIndex);
    }

    private void copyTestProgram() {
        TestProgramRepository.insertTestProgram(testProgram);
    }

    private void copyTestProgramSettings() {
        getTestPrograms();
        getTestProgram(allTestPrograms.size() - 1);
        long oldTestProgramId = testProgram.getId();
        long newTestProgramId = testProgram.getId();

        copyLTR24Tables(oldTestProgramId, newTestProgramId);
        copyLTR34Tables(oldTestProgramId, newTestProgramId);
        copyLTR212Tables(oldTestProgramId, newTestProgramId);
    }

    private void copyLTR24Tables(long oldTestProgramId, long newTestProgramId) {
        for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
            if (oldTestProgramId == ltr24Table.getTestProgramId()) {
                ltr24Table.setTestProgramId(newTestProgramId);
                LTR24TablesRepository.insertLTR24Table(ltr24Table);
            }
        }
    }

    private void copyLTR34Tables(long oldTestProgramId, long newTestProgramId) {
        for (LTR34Table ltr34Table : LTR34TablesRepository.getAllLTR34Tables()) {
            if (oldTestProgramId == ltr34Table.getTestProgramId()) {
                ltr34Table.setTestProgramId(newTestProgramId);
                LTR34TablesRepository.insertLTR34Module(ltr34Table);
            }
        }
    }

    private void copyLTR212Tables(long oldTestProgramId, long newTestProgramId) {
        for (LTR212Table ltr212Table : LTR212TablesRepository.getAllLTR212Tables()) {
            if (oldTestProgramId == ltr212Table.getTestProgramId()) {
                ltr212Table.setTestProgramId(newTestProgramId);
                LTR212TablesRepository.insertLTR212Table(ltr212Table);
            }
        }
    }

    private void reloadTestProgramsList() {
        cm.loadItemsForMainTableView();
    }

    private void showNotification(String text) {
        Platform.runLater(() -> {
            toggleProgressIndicatorState(true);
            statusBarLine.setStatus(text, statusBar);
        });
    }

    public void handleMenuItemDelete() {
        getTestPrograms();
        checkSelection();

        if (isTestProgramSelected) {
            toggleProgressIndicatorState(false);
            new Thread(() -> {
                delete();
                reloadTestProgramsList();
                showNotification("Программа испытаний удалена");
            }).start();
        }
    }

    private void delete() {
        deleteTestProgram();
        deleteLTR24Tables();
        deleteLTR212Tables();
        deleteLTR34Tables();
    }

    private void deleteTestProgram() {
        getTestProgram(getSelectedItemIndex());
        testProgramId = testProgram.getId();
        TestProgramRepository.deleteTestProgram(testProgram);
    }

    private void deleteLTR34Tables() {
        for (LTR34Table ltr34Table : LTR34TablesRepository.getAllLTR34Tables()) {
            if (testProgramId == ltr34Table.getTestProgramId()) {
                LTR34TablesRepository.deleteLTR34Table(ltr34Table);
            }
        }
    }

    private void deleteLTR212Tables() {
        for (LTR212Table ltr212Table : LTR212TablesRepository.getAllLTR212Tables()) {
            if (testProgramId == ltr212Table.getTestProgramId()) {
                LTR212TablesRepository.deleteLTR212Table(ltr212Table);
            }
        }
    }

    private void deleteLTR24Tables() {
        for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
            if (testProgramId == ltr24Table.getTestProgramId()) {
                LTR24TablesRepository.deleteLTR24Table(ltr24Table);
            }
        }
    }

    public void handleMenuItemAboutUs() {
        System.out.println("Hello, world!");
    }

    public void handleOpenExperiment() {
        getTestPrograms();
        checkSelection();

        if (isTestProgramSelected) {
            getTestProgram(selectedIndex);
            setExperimentId();
            showExperimentScene();
        }
    }

    private void checkSelection() {
        selectedIndex = getSelectedItemIndex();

        if (testPrograms.isEmpty()) {
            showNotification("Ошибка: отсутсвуют программы испытаний");
            isTestProgramSelected = false;
        } else if (selectedIndex == -1) {
            showNotification("Ошибка: программа испытаний не выбрана");
            isTestProgramSelected = false;
        } else {
            isTestProgramSelected = true;
        }
    }

    private void setExperimentId() {
        cm.getExperimentModel().SetTestId(testProgram.getId());
    }

    private void showExperimentScene() {
        wm.setScene(WindowsManager.Scenes.EXPERIMENT_SCENE);
    }

    public void handleMenuItemExit() {
        Platform.exit();
    }

    public Button getOpenExperimentButton() {
        return openExperimentButton;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
