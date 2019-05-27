package ru.avem.posum.controllers.Process;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.GraphModel;
import ru.avem.posum.utils.Utils;

public class TableController {
    private TableView<ChannelModel> tableView;
    private TableColumn<ChannelModel, String> channelsColumn;
    private TableColumn<ChannelModel, HBox> responseColumn;
    private TableColumn<ChannelModel, String> ampResponseColumn;
    private TableColumn<ChannelModel, String> ampColumn;
    private TableColumn<ChannelModel, String> ampRelativeResponseColumn;
    private TableColumn<ChannelModel, String> frequencyResponseColumn;
    private TableColumn<ChannelModel, String> frequencyColumn;
    private TableColumn<ChannelModel, String> frequencyRelativeResponseColumn;
    private TableColumn<ChannelModel, String> rmsResponseColumn;
    private TableColumn<ChannelModel, String> rmsColumn;
    private TableColumn<ChannelModel, String> rmsRelativeResponseColumn;

    private GraphModel graphModel;

    public TableController(TableView<ChannelModel> tableView, TableColumn<ChannelModel, String> channelsColumn,
                           TableColumn<ChannelModel, HBox> responseColumn, TableColumn<ChannelModel, String> ampResponseColumn,
                           TableColumn<ChannelModel, String> ampColumn, TableColumn<ChannelModel, String> ampRelativeResponseColumn,
                           TableColumn<ChannelModel, String> frequencyResponseColumn, TableColumn<ChannelModel, String> frequencyColumn,
                           TableColumn<ChannelModel, String> frequencyRelativeResponseColumn, TableColumn<ChannelModel, String> rmsResponseColumn,
                           TableColumn<ChannelModel, String> rmsColumn, TableColumn<ChannelModel, String> rmsRelativeResponseColumn,
                           GraphModel graphModel) {

        this.tableView = tableView;
        this.channelsColumn = channelsColumn;
        this.responseColumn = responseColumn;
        this.ampResponseColumn = ampResponseColumn;
        this.ampColumn = ampColumn;
        this.ampRelativeResponseColumn = ampRelativeResponseColumn;
        this.frequencyResponseColumn = frequencyResponseColumn;
        this.frequencyColumn = frequencyColumn;
        this.frequencyRelativeResponseColumn = frequencyRelativeResponseColumn;
        this.rmsResponseColumn = rmsResponseColumn;
        this.rmsColumn = rmsColumn;
        this.rmsRelativeResponseColumn = rmsRelativeResponseColumn;
        this.graphModel = graphModel;

        initTableView();
    }

    private void initTableView() {
        tableView.setItems(graphModel.getChannels());

        Utils.makeHeaderWrappable(channelsColumn);

        initResponse(responseColumn);
        init(ampResponseColumn);
        init(ampColumn);
        init(ampRelativeResponseColumn);
        init(rmsResponseColumn);
        init(rmsColumn);
        init(rmsRelativeResponseColumn);
        init(frequencyResponseColumn);
        init(frequencyColumn);
        init(frequencyRelativeResponseColumn);

        channelsColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        responseColumn.setCellValueFactory(cellData -> cellData.getValue().getResponse());
        ampResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseAmplitudeProperty());
        ampColumn.setCellValueFactory(cellData -> cellData.getValue().amplitudeProperty());
        ampRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseAmplitudeProperty());
        rmsResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseRmsProperty());
        rmsColumn.setCellValueFactory(cellData -> cellData.getValue().rmsProperty());
        rmsRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseRmsProperty());
        frequencyResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseFrequencyProperty());
        frequencyColumn.setCellValueFactory(cellData -> cellData.getValue().frequencyProperty());
        frequencyRelativeResponseColumn.setCellValueFactory(cellData -> cellData.getValue().relativeResponseFrequencyProperty());
    }

    public void initResponse(TableColumn<ChannelModel, HBox> columnOfTable) {
        Utils.makeHeaderWrappable(columnOfTable);

        columnOfTable.setCellFactory(column -> new TableCell<ChannelModel, HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
    }

    public void init(TableColumn<ChannelModel, String> columnOfTable) {
        Utils.makeHeaderWrappable(columnOfTable);

        columnOfTable.setCellFactory(column -> new TableCell<ChannelModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { // If the cell is empty
                    setText(null);
                } else {
                    setText(item);
                }
                setGraphic(null);
            }
        });
    }
}
