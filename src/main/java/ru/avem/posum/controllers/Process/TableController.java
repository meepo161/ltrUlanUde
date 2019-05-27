package ru.avem.posum.controllers.Process;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.utils.Utils;

import java.util.List;

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

    private GraphController graphController;
    private ProcessController processController;

    public TableController(TableView<ChannelModel> tableView, TableColumn<ChannelModel, String> channelsColumn,
                           TableColumn<ChannelModel, HBox> responseColumn, TableColumn<ChannelModel, String> ampResponseColumn,
                           TableColumn<ChannelModel, String> ampColumn, TableColumn<ChannelModel, String> ampRelativeResponseColumn,
                           TableColumn<ChannelModel, String> frequencyResponseColumn, TableColumn<ChannelModel, String> frequencyColumn,
                           TableColumn<ChannelModel, String> frequencyRelativeResponseColumn, TableColumn<ChannelModel, String> rmsResponseColumn,
                           TableColumn<ChannelModel, String> rmsColumn, TableColumn<ChannelModel, String> rmsRelativeResponseColumn,
                           GraphController graphController, ProcessController processController) {

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
        this.graphController = graphController;
        this.processController = processController;

        initTableView();
    }

    private void initTableView() {
        tableView.setItems(graphController.getGraphModel().getChannels());

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

        listen(tableView);
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

    private void listen(TableView<ChannelModel> tableView) {
        tableView.getItems().addListener((ListChangeListener<ChannelModel>) observable -> {
            ObservableList<CheckBox> checkBoxes = getCheckBoxes();

            for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                listen(checkBoxes.get(channelIndex), channelIndex);
            }
        });
    }

    private ObservableList<CheckBox> getCheckBoxes() {
        ObservableList<ChannelModel> channels = tableView.getItems();
        ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

        if (!channels.isEmpty()) {
            for (ChannelModel channel : channels) {
                if (!checkBoxes.contains(channel.getResponseCheckBox())) {
                    checkBoxes.add(channel.getResponseCheckBox());
                }
            }
        }

        return checkBoxes;
    }

    private void listen(CheckBox checkBox, int channelIndex) {
        checkBox.selectedProperty().addListener(observable -> {
            ObservableList<CheckBox> checkBoxes = getCheckBoxes();
            checkBoxes.remove(checkBox);

            for (CheckBox channel : checkBoxes) {
                channel.setSelected(false);
                graphController.getGraphModel().clear();
            }

            if (checkBox.isSelected()) {
                ObservableList<ChannelModel> channels = tableView.getItems();
                String channelDescription = channels.get(channelIndex).getName();
                int slot = parseSlot(channelDescription);
                int channel = parseChannel(channelDescription, slot);

                graphController.showGraph(slot, channel);
            }
        });
    }

    private int parseSlot(String channelDescription) {
        return Integer.parseInt(channelDescription.split("слот ")[1].split("\\)")[0]);
    }

    private int parseChannel(String channelDescription, int slot) {
        List<Modules> modules = processController.getModules();
        int channel = 0;

        for (Modules module : modules) {
            if (module.getSlot() == slot) {
                List<Pair<Integer, String>> descriptions = Modules.getChannelsDescriptions(module);

                for (Pair<Integer, String> description : descriptions) {
                    if (description.getValue().equals(channelDescription)) {
                        return description.getKey();
                    }
                }
            }
        }

        return channel;
    }
}
