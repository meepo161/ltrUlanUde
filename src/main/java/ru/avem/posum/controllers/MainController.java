package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.CalibrationsRepository;
import ru.avem.posum.db.ModulesRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.Calibration;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.utils.StatusBarLine;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private TableColumn<TestProgram, Integer> columnTableViewIndex;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramName;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramChangingDate;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramCreatingDate;
    @FXML
    private TableColumn<TestProgram, String> columnTestingSample;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramTime;
    @FXML
    private TableColumn<TestProgram, String> columnTestProgramType;
    @FXML
    private Button openExperimentButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private StatusBar statusBar;
    @FXML
    private TableView<TestProgram> testProgramTableView;

    private List<TestProgram> allTestPrograms;
    private ControllerManager cm;
    private ContextMenu contextMenu = new ContextMenu();
    private boolean isTestProgramSelected;
    private long newTestProgramId;
    private long oldTestProgramId;
    private int selectedIndex;
    private StatusBarLine statusBarLine = new StatusBarLine();
    private TestProgram testProgram;
    private long testProgramId;
    private ObservableList<TestProgram> testPrograms;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        initTableView();
        addMouseListener();
    }

    private void initTableView() {
        formatColumnsTitles();
        setColumns();
        getTestPrograms();
        showTestPrograms();
        createContextMenu();
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
        columnTestProgramCreatingDate.setCellValueFactory(new PropertyValueFactory<>("created"));
        columnTestProgramChangingDate.setCellValueFactory(new PropertyValueFactory<>("changed"));
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
            testProgramTableView.getSelectionModel().select(selectedIndex - 1);
        });
    }

    private void createContextMenu() {
        MenuItem menuItemEdit = new MenuItem("Настроить");
        MenuItem menuItemCopy = new MenuItem("Копировать");
        MenuItem menuItemDelete = new MenuItem("Удалить");

        menuItemEdit.setOnAction(event -> handleMenuItemEdit());
        menuItemCopy.setOnAction(event -> handleMenuItemCopy());
        menuItemDelete.setOnAction(event -> handleMenuItemDelete());

        contextMenu.getItems().addAll(menuItemEdit, menuItemCopy, menuItemDelete);
    }

    private void addMouseListener() {
        testProgramTableView.setRowFactory(tv -> {
            TableRow<TestProgram> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleMenuItemEdit();
                }

                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(testProgramTableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
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
        Crate crate = cm.getCrateModelInstance();
        crate.getModulesList().clear();
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

    private void prepareEditSettingsScene() {
        cm.setEditMode(true);
        cm.showTestProgram(allTestPrograms.get(getSelectedItemIndex() - 1));
    }

    private int getSelectedItemIndex() {
        selectedIndex = testProgramTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            selectedIndex = testProgramTableView.getItems().get(selectedIndex).getIndex();
        }
        return selectedIndex;
    }

    public void handleMenuItemCopy() {
        getTestPrograms();
        checkSelection();

        if (isTestProgramSelected) {
            toggleProgressIndicatorState(false);
            new Thread(() -> {
                copyTestProgram();
                copyModulesSettings();
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

    private TestProgram getTestProgram(int itemIndex) {
        return testProgram = allTestPrograms.get(itemIndex - 1);
    }

    private void copyTestProgram() {
        getTestProgram(selectedIndex);
        oldTestProgramId = testProgram.getId();
        TestProgramRepository.insertTestProgram(testProgram);
        newTestProgramId = testProgram.getId();
    }

    private void copyModulesSettings() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (oldTestProgramId == module.getTestProgramId()) {
                long oldModuleId = module.getId();
                module.setTestProgramId(newTestProgramId);
                ModulesRepository.insertModule(module);
                long newModuleId = module.getId();
                copyCalibrations(oldModuleId, newModuleId);
            }
        }
    }

    private void copyCalibrations(long oldModuleId, long newModuleId) {
        for (Calibration calibration : CalibrationsRepository.getAllCalibrations()) {
            if (calibration.getModuleId() == oldModuleId & calibration.getTestProgramId() == oldTestProgramId) {
                calibration.setTestProgramId(newTestProgramId);
                calibration.setModuleId(newModuleId);
                CalibrationsRepository.insertCalibration(calibration);
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
        deleteModules();
    }

    private void deleteTestProgram() {
        getTestProgram(getSelectedItemIndex());
        testProgramId = testProgram.getId();
        TestProgramRepository.deleteTestProgram(testProgram);
    }

    private void deleteModules() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (testProgramId == module.getTestProgramId()) {
                deleteCalibrations(module.getId());
                ModulesRepository.deleteModule(module);
            }
        }
    }

    private void deleteCalibrations(long moduleId) {
        for (Calibration calibration : CalibrationsRepository.getAllCalibrations()) {
            if (calibration.getModuleId() == moduleId & calibration.getTestProgramId() == testProgramId) {
                CalibrationsRepository.deleteCalibration(calibration);
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
