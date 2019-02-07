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
import ru.avem.posum.db.ProtocolRepository;
import ru.avem.posum.db.models.TestProgramm;
import ru.avem.posum.utils.Toast;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private TableColumn<TestProgramm, Integer> columnProtocolId;
    @FXML
    private TableColumn<TestProgramm, String> columnExperimentName;
    @FXML
    private TableColumn<TestProgramm, String> columnProtocolCreatingDate;
    @FXML
    private TableColumn<TestProgramm, String> columnProtocolChangingDate;
    @FXML
    private TableColumn<TestProgramm, String> columnExperimentTime;
    @FXML
    private TableColumn<TestProgramm, String> columnExperimentType;
    @FXML
    private TableColumn<TestProgramm, String> columnTestingSample;
    @FXML
    private TableView<TestProgramm> experimentsTableView;

    private WindowsManager wm;
    private ControllerManager cm;
    private ObservableList<TestProgramm> testProgramms;

    @FXML
    private void initialize() {
        showPotocols();

        makeColumnTitleWrapper(columnExperimentName);
        makeColumnTitleWrapper(columnProtocolCreatingDate);
        makeColumnTitleWrapper(columnProtocolChangingDate);
        makeColumnTitleWrapper(columnExperimentTime);
        makeColumnTitleWrapper(columnExperimentType);
        makeColumnTitleWrapper(columnTestingSample);

        columnProtocolId.setCellValueFactory(new PropertyValueFactory<>("index"));
        columnExperimentName.setCellValueFactory(new PropertyValueFactory<>("experimentName"));
        columnProtocolCreatingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnProtocolChangingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnExperimentTime.setCellValueFactory(new PropertyValueFactory<>("experimentTime"));
        columnExperimentType.setCellValueFactory(new PropertyValueFactory<>("experimentType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));
    }

    public void showPotocols() {
        ProtocolRepository.updateProtocolIndex();
        List<TestProgramm> allTestProgramms = ProtocolRepository.getAllProtocols();
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
        cm.loadDefaultSettings();
        cm.setEditMode(false);
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    public void handleMenuItemSetup() {
        if (getSelectedItemIndex() != -1) {
            List<TestProgramm> allTestProgramms = ProtocolRepository.getAllProtocols();
            cm.setupProtocol(allTestProgramms.get(getSelectedItemIndex()));
            cm.loadItemsForMainTableView();
            cm.setEditMode(true);
            wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
        }
    }

    private int getSelectedItemIndex() {
        return experimentsTableView.getSelectionModel().getSelectedIndex();
    }

    public void handleMenuItemCopy() {
        if (getSelectedItemIndex() != -1) {
            List<TestProgramm> allTestProgramms = ProtocolRepository.getAllProtocols();
            TestProgramm testProgramm = allTestProgramms.get(getSelectedItemIndex());
            ProtocolRepository.insertProtocol(testProgramm);
            cm.loadItemsForMainTableView();
        }
    }

    public void handleMenuItemDelete() {
        List<TestProgramm> allTestProgramms = ProtocolRepository.getAllProtocols();
        ProtocolRepository.deleteProtocol(allTestProgramms.get(getSelectedItemIndex()));
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
