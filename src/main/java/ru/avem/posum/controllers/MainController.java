package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.TestProgrammRepository;
import ru.avem.posum.db.models.TestProgramm;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.utils.Toast;

import java.util.List;

import static ru.avem.posum.utils.View.showConfirmDialog;

public class MainController implements BaseController {
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
    @FXML
    private TableView<TestProgramm> experimentsTableView;


    private WindowsManager wm;
    private ControllerManager cm;
    private ObservableList<TestProgramm> testProgramms;

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
    }

    public void showTestProgramm() {
        TestProgrammRepository.updateTestProgrammId();
        List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
        testProgramms = FXCollections.observableArrayList(allTestProgramms);
        experimentsTableView.setItems(testProgramms);
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
            clearModulesList();
            List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
            cm.showTestProgramm(allTestProgramms.get(getSelectedItemIndex()));
            showTestProgramm();
            cm.setEditMode(true);
            wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        }
    }

    private int getSelectedItemIndex() {
        return experimentsTableView.getSelectionModel().getSelectedIndex();
    }

    public void handleMenuItemCopy() {
        if (getSelectedItemIndex() != -1) {
            List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
            TestProgramm testProgramm = allTestProgramms.get(getSelectedItemIndex());
            TestProgrammRepository.insertTestProgramm(testProgramm);
            cm.loadItemsForMainTableView();
        }
    }

    public void handleMenuItemDelete() {
        showConfirmDialog("Вы действительно хотите удалить?", ()->{}, ()->{});
        List<TestProgramm> allTestProgramms = TestProgrammRepository.getAllTestProgramms();
        TestProgrammRepository.deleteTestProgramm(allTestProgramms.get(getSelectedItemIndex()));
        cm.loadItemsForMainTableView();
    }

    public void handleMenuItemAboutUs() {
    }

    public void handleOpenExpirement() {
        //проверка выбрана ли позиция в таблице и существует ли она
        if(testProgramms.size() > 0) {
            TestProgramm testProgrammItem = experimentsTableView.getSelectionModel().getSelectedItem();
            if(testProgrammItem == null) {
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
