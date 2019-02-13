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
import ru.avem.posum.db.LTR24ModuleRepository;
import ru.avem.posum.db.LTR34ModuleRepository;
import ru.avem.posum.db.TestProgrammRepository;
import ru.avem.posum.db.models.LTR212Module;
import ru.avem.posum.db.models.LTR24Module;
import ru.avem.posum.db.models.LTR34Module;
import ru.avem.posum.db.models.TestProgramm;
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
    private TableView<TestProgramm> testProgrammTableView;
    @FXML
    private TableColumn<TestProgramm, Integer> columnTableViewIndex;
    @FXML
    private TableColumn<TestProgramm, String> columnTestProgrammName;
    @FXML
    private TableColumn<TestProgramm, String> columnTestProgrammCreatingDate;
    @FXML
    private TableColumn<TestProgramm, String> columnTestProgrammChangingDate;
    @FXML
    private TableColumn<TestProgramm, String> columnTestProgrammTime;
    @FXML
    private TableColumn<TestProgramm, String> columnTestProgrammType;
    @FXML
    private TableColumn<TestProgramm, String> columnTestingSample;

    private WindowsManager wm;
    private ControllerManager cm;
    private ObservableList<TestProgramm> testProgramms;
    private StatusBarLine statusBarLine = new StatusBarLine();

    @FXML
    private void initialize() {
        showTestProgramm();

        makeColumnTitleWrapper(columnTestProgrammName);
        makeColumnTitleWrapper(columnTestProgrammCreatingDate);
        makeColumnTitleWrapper(columnTestProgrammChangingDate);
        makeColumnTitleWrapper(columnTestProgrammTime);
        makeColumnTitleWrapper(columnTestProgrammType);
        makeColumnTitleWrapper(columnTestingSample);

        columnTableViewIndex.setCellValueFactory(new PropertyValueFactory<>("testProgrammId"));
        columnTestProgrammName.setCellValueFactory(new PropertyValueFactory<>("testProgrammName"));
        columnTestProgrammCreatingDate.setCellValueFactory(new PropertyValueFactory<>("testProgrammDate"));
        columnTestProgrammChangingDate.setCellValueFactory(new PropertyValueFactory<>("testProgrammDate"));
        columnTestProgrammTime.setCellValueFactory(new PropertyValueFactory<>("testProgrammTime"));
        columnTestProgrammType.setCellValueFactory(new PropertyValueFactory<>("testProgrammType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));

        addDoubleClickListener();
    }

    public void showTestProgramm() {
        TestProgrammRepository.updateTestProgrammId();
        List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
        testProgramms = FXCollections.observableArrayList(allTestProgramms);
        testProgrammTableView.setItems(testProgramms);
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
        testProgrammTableView.setRowFactory(tv -> {
            TableRow<TestProgramm> row = new TableRow<>();
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
                List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
                cm.showTestProgramm(allTestProgramms.get(getSelectedItemIndex()));
                showTestProgramm();
                cm.setEditMode(true);
            }).start();
            wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        }
    }

    private int getSelectedItemIndex() {
        return testProgrammTableView.getSelectionModel().getSelectedIndex();
    }

    public void handleMenuItemCopy() {
        if (getSelectedItemIndex() != -1) {
            toggleProgressIndicatorState(false);
            new Thread(() -> {
                List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
                TestProgramm testProgramm = allTestProgramms.get(getSelectedItemIndex());
                long oldTestProgrammId = testProgramm.getId();
                TestProgrammRepository.insertTestProgramm(testProgramm);

                allTestProgramms = TestProgrammRepository.getAllTestProgramms();
                testProgramm = allTestProgramms.get(allTestProgramms.size() - 1);
                long newTestProgrammId = testProgramm.getId();

                for (LTR24Module ltr24Module : LTR24ModuleRepository.getAllLTR24Modules()) {
                    if (oldTestProgrammId == ltr24Module.getTestProgrammId()) {
                        ltr24Module.setTestProgrammId(newTestProgrammId);
                        LTR24ModuleRepository.insertLTR24Module(ltr24Module);
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
            List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
            TestProgramm testProgramm = allTestProgramms.get(getSelectedItemIndex());
            long testProgrammId = testProgramm.getId();
            TestProgrammRepository.deleteTestProgramm(testProgramm);

            for (LTR24Module ltr24Module : LTR24ModuleRepository.getAllLTR24Modules()) {
                if (testProgrammId == ltr24Module.getTestProgrammId()) {
                    LTR24ModuleRepository.deleteLTR24Module(ltr24Module);
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
        if (testProgramms.size() > 0) {
            TestProgramm testProgrammItem = testProgrammTableView.getSelectionModel().getSelectedItem();
            if (testProgrammItem == null) {
                Toast.makeText("Испытание не выбрано").show(Toast.ToastType.WARNING);
                return;
            }
            // передать данные в класс проведения эксперемента
            cm.getExperimentModel().SetTestId(testProgrammItem.getId());
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
