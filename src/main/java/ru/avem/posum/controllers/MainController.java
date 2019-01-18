package ru.avem.posum.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.ProtocolRepository;
import ru.avem.posum.db.models.Experiment;
import ru.avem.posum.db.models.Protocol;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private TableView expirements_TableView;
    @FXML
    private TableColumn<Experiment, Integer> columnProtocolId;
    @FXML
    private TableColumn<Experiment, String> columnExpirementName;
    @FXML
    private TableColumn<Experiment, String> columnProtocolCreatingDate;
    @FXML
    private TableColumn<Experiment, String> columnProtocolChangingDate;
    @FXML
    private TableColumn<Experiment, String> columnExpirementTime;
    @FXML
    private TableColumn<Experiment, String> columnExpirementType;
    @FXML
    private TableColumn<Experiment, String> columnTestingSample;

    private ObservableList<Protocol> protocols;

    private WindowsManager wm;

    @FXML
    private void initialize() {
        initData();

        columnProtocolId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnExpirementName.setCellValueFactory(new PropertyValueFactory<>("experimentName"));
        columnProtocolCreatingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnProtocolChangingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnExpirementTime.setCellValueFactory(new PropertyValueFactory<>("experimentTime"));
        columnExpirementType.setCellValueFactory(new PropertyValueFactory<>("experimentType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));

        expirements_TableView.setItems(protocols);
    }

    private void initData() {
        List<Protocol> allProtocols = ProtocolRepository.getAllProtocols();
        protocols = FXCollections.observableArrayList(allProtocols);
    }

    public void handleMenuItemExit() {
    }

    public void handleMenuItemAdd() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    public void handleMenuItemSettings() {
    }

    public void handleMenuItemDelete() {
    }

    public void handleMenuItemAboutUs() {
    }

    public void handleMenuItemProcess() {
        wm.setScene(WindowsManager.Scenes.PROCESS_SCENE);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
