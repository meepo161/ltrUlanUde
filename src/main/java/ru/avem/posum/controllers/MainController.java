package ru.avem.posum.controllers;

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
import ru.avem.posum.db.models.Protocol;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private TableView<Protocol> experimentsTableView;
    @FXML
    private TableColumn<Protocol, Integer> columnProtocolId;
    @FXML
    private TableColumn<Protocol, String> columnExperimentName;
    @FXML
    private TableColumn<Protocol, String> columnProtocolCreatingDate;
    @FXML
    private TableColumn<Protocol, String> columnProtocolChangingDate;
    @FXML
    private TableColumn<Protocol, String> columnExperimentTime;
    @FXML
    private TableColumn<Protocol, String> columnExperimentType;
    @FXML
    private TableColumn<Protocol, String> columnTestingSample;

    private ObservableList<Protocol> protocols;
    private WindowsManager wm;
    private ControllerManager cm;

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

    private int getSelectedItemIndex() {
         return experimentsTableView.getSelectionModel().getSelectedIndex();
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

    public void showPotocols() {
        ProtocolRepository.updateProtocolIndex();
        List<Protocol> allProtocols = ProtocolRepository.getAllProtocols();
        protocols = FXCollections.observableArrayList(allProtocols);
        experimentsTableView.setItems(protocols);
    }

    public void handleMenuItemExit() {
    }

    public void handleMenuItemAdd() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    public void handleMenuItemSettings() {
    }

    public void handleMenuItemDelete() {
        List<Protocol> allProtocols = ProtocolRepository.getAllProtocols();
        ProtocolRepository.deleteProtocol(allProtocols.get(getSelectedItemIndex()));
        cm.loadItemsForMainTableView();
    }

    public void handleMenuItemAboutUs() {
    }

    public void handleOpenExpirement() {
        wm.setScene(WindowsManager.Scenes.PROCESS_SCENE);
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
