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
import ru.avem.posum.db.LTR212ModuleRepository;
import ru.avem.posum.db.LTR24TablesRepository;
import ru.avem.posum.db.LTR34ModuleRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR212Module;
import ru.avem.posum.db.models.LTR24Table;
import ru.avem.posum.db.models.LTR34Module;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Toast;

import java.util.List;

public class MainController implements BaseController {
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
    private ObservableList<TestProgram> testPrograms;
    private StatusBarLine statusBarLine = new StatusBarLine();

    @FXML
    private void initialize() {
        showTestProgram();

        makeColumnTitleWrapper(columnTestProgramName);
        makeColumnTitleWrapper(columnTestProgramCreatingDate);
        makeColumnTitleWrapper(columnTestProgramChangingDate);
        makeColumnTitleWrapper(columnTestProgramTime);
        makeColumnTitleWrapper(columnTestProgramType);
        makeColumnTitleWrapper(columnTestingSample);

        columnTableViewIndex.setCellValueFactory(new PropertyValueFactory<>("testProgramId"));
        columnTestProgramName.setCellValueFactory(new PropertyValueFactory<>("testProgramName"));
        columnTestProgramCreatingDate.setCellValueFactory(new PropertyValueFactory<>("testProgramDate"));
        columnTestProgramChangingDate.setCellValueFactory(new PropertyValueFactory<>("testProgramDate"));
        columnTestProgramTime.setCellValueFactory(new PropertyValueFactory<>("testProgramTime"));
        columnTestProgramType.setCellValueFactory(new PropertyValueFactory<>("testProgramType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));

        addDoubleClickListener();
    }

    public void showTestProgram() {
        TestProgramRepository.updateTestProgramId();
        List<TestProgram> allTestPrograms = TestProgramRepository.getAllTestPrograms();
        testPrograms = FXCollections.observableArrayList(allTestPrograms);
        testProgramTableView.setItems(testPrograms);
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

    public void handleMenuItemExit() {
        Platform.exit();
    }

    public void handleMenuItemAdd() {
        clearModulesList();
        cm.loadDefaultSettings();
        cm.setEditMode(false);
        cm.hideRequiredFieldsSymbols();
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    private void clearModulesList() {
        CrateModel crateModel = cm.getCrateModelInstance();
        crateModel.getLtr24ModulesList().clear();
        crateModel.getLtr34ModulesList().clear();
        crateModel.getLtr212ModulesList().clear();
    }

    public void handleMenuItemEdit() {
        if (getSelectedItemIndex() != -1) {
            new Thread(() -> {
                clearModulesList();
                List<TestProgram> allTestPrograms = TestProgramRepository.getAllTestPrograms();
                cm.showTestProgram(allTestPrograms.get(getSelectedItemIndex()));
                showTestProgram();
                cm.setEditMode(true);
            }).start();
            wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        }
    }

    private int getSelectedItemIndex() {
        return testProgramTableView.getSelectionModel().getSelectedIndex();
    }

    public void handleMenuItemCopy() {
        if (getSelectedItemIndex() != -1) {
            toggleProgressIndicatorState(false);
            new Thread(() -> {
                List<TestProgram> allTestPrograms = TestProgramRepository.getAllTestPrograms();
                TestProgram testProgram = allTestPrograms.get(getSelectedItemIndex());
                long oldTestProgrammId = testProgram.getId();
                TestProgramRepository.insertTestProgram(testProgram);

                allTestPrograms = TestProgramRepository.getAllTestPrograms();
                testProgram = allTestPrograms.get(allTestPrograms.size() - 1);
                long newTestProgrammId = testProgram.getId();

                for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
                    if (oldTestProgrammId == ltr24Table.getTestProgramId()) {
                        ltr24Table.setTestProgramId(newTestProgrammId);
                        LTR24TablesRepository.insertLTR24Table(ltr24Table);
                    }
                }

                for (LTR212Module ltr212Module : LTR212ModuleRepository.getAllLTR212Modules()) {
                    if (oldTestProgrammId == ltr212Module.getTestProgrammId()) {
                        ltr212Module.setTestProgrammId(newTestProgrammId);
                        LTR212ModuleRepository.insertLTR212Module(ltr212Module);
                    }
                }

                for (LTR34Module ltr34Module : LTR34ModuleRepository.getAllLTR34Modules()) {
                    if (oldTestProgrammId == ltr34Module.getTestProgrammId()) {
                        ltr34Module.setTestProgrammId(newTestProgrammId);
                        LTR34ModuleRepository.insertLTR34Module(ltr34Module);
                    }
                }

                cm.loadItemsForMainTableView();
                Platform.runLater(() -> {
                    toggleProgressIndicatorState(true);
                    statusBarLine.setStatus("Программа испытаний скопирована", statusBar);
                });
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

    public void handleMenuItemDelete() {
        toggleProgressIndicatorState(false);
        new Thread(() -> {
            List<TestProgram> allTestPrograms = TestProgramRepository.getAllTestPrograms();
            TestProgram testProgram = allTestPrograms.get(getSelectedItemIndex());
            long testProgrammId = testProgram.getId();
            TestProgramRepository.deleteTestProgram(testProgram);

            for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
                if (testProgrammId == ltr24Table.getTestProgramId()) {
                    LTR24TablesRepository.deleteLTR24Module(ltr24Table);
                }
            }

            for (LTR212Module ltr212Module : LTR212ModuleRepository.getAllLTR212Modules()) {
                if (testProgrammId == ltr212Module.getTestProgrammId()) {
                    LTR212ModuleRepository.deleteLTR212Module(ltr212Module);
                }
            }

            for (LTR34Module ltr34Module : LTR34ModuleRepository.getAllLTR34Modules()) {
                if (testProgrammId == ltr34Module.getTestProgrammId()) {
                    LTR34ModuleRepository.deleteLTR34Module(ltr34Module);
                }
            }

            cm.loadItemsForMainTableView();

            Platform.runLater(() -> {
                toggleProgressIndicatorState(true);
                statusBarLine.setStatus("Программа испытаний удалена", statusBar);
            });
        }).start();
    }

    public void handleMenuItemAboutUs() {
    }

    public void handleOpenExpirement() {
        //проверка выбрана ли позиция в таблице и существует ли она
        if (testPrograms.size() > 0) {
            TestProgram testProgramItem = testProgramTableView.getSelectionModel().getSelectedItem();
            if (testProgramItem == null) {
                Toast.makeText("Испытание не выбрано").show(Toast.ToastType.WARNING);
                return;
            }
            // передать данные в класс проведения эксперемента
            cm.getExperimentModel().SetTestId(testProgramItem.getId());
            // вызвать окно проведения эксперемента
            wm.setScene(WindowsManager.Scenes.PROCESS_SCENE);
        } else {
            Toast.makeText("Отсутствуют настроенные испытания").show(Toast.ToastType.WARNING);
            return;
        }
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
