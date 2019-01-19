package ru.avem.posum.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.ProtocolRepository;
import ru.avem.posum.db.models.Experiment;
import ru.avem.posum.db.models.Protocol;

import java.util.List;

public class MainController implements BaseController {
    @FXML
    private Button openExpirementButton;
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
    private boolean isFirstStart = true;

    @FXML
    private void initialize() {
        initData();

        makeColumnTitleWrapper(columnExpirementName);
        makeColumnTitleWrapper(columnProtocolCreatingDate);
        makeColumnTitleWrapper(columnProtocolChangingDate);
        makeColumnTitleWrapper(columnExpirementTime);
        makeColumnTitleWrapper(columnExpirementType);
        makeColumnTitleWrapper(columnTestingSample);

        columnProtocolId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnExpirementName.setCellValueFactory(new PropertyValueFactory<>("experimentName"));
        columnProtocolCreatingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnProtocolChangingDate.setCellValueFactory(new PropertyValueFactory<>("experimentDate"));
        columnExpirementTime.setCellValueFactory(new PropertyValueFactory<>("experimentTime"));
        columnExpirementType.setCellValueFactory(new PropertyValueFactory<>("experimentType"));
        columnTestingSample.setCellValueFactory(new PropertyValueFactory<>("sampleName"));

        expirements_TableView.setItems(protocols);

        if (isFirstStart) {
            repeatFocus(openExpirementButton);
        }
    }

    private void printSelectedItemIndex() {
        System.out.println(expirements_TableView.getSelectionModel().getSelectedIndex());
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

    public void handleOpenExpirement() {
        printSelectedItemIndex();
        wm.setScene(WindowsManager.Scenes.PROCESS_SCENE);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    private void repeatFocus(Button button) {
        Platform.runLater(() -> {
            if (!button.isFocused()) {
                button.requestFocus();
                repeatFocus(button);
            }
        });
    }
}
