package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.SignalParametersModel;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
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
    private boolean showStopped;
    private SignalParametersModel signalParametersModel = new SignalParametersModel();

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
        toggle();
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
            ObservableList<ColorPicker> colorPickers = getColorPickers();

            for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                listen(checkBoxes.get(channelIndex), channelIndex);
                listen(colorPickers.get(channelIndex), channelIndex);
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

    private ObservableList<ColorPicker> getColorPickers() {
        ObservableList<ChannelModel> channels = tableView.getItems();
        ObservableList<ColorPicker> colorPickers = FXCollections.observableArrayList();

        if (!channels.isEmpty()) {
            for (ChannelModel channel : channels) {
                if (!colorPickers.contains(channel.getColorPicker())) {
                    colorPickers.add(channel.getColorPicker());
                }
            }
        }

        return colorPickers;
    }

    private void listen(CheckBox checkBox, int channelIndex) {
        checkBox.selectedProperty().addListener(observable -> {
            toggleGraphControls();

            if (checkBox.isSelected()) {
                ObservableList<CheckBox> checkBoxes = getCheckBoxes();
                checkBoxes.remove(checkBox);

                for (CheckBox channel : checkBoxes) {
                    channel.setSelected(false);
                }

                checkBox.setSelected(true);
                setSeriesColor(channelIndex);


                if (!graphController.isShowingThreadStopped()) {
                    graphController.stopShowingThread();
                    graphController.restartShow();
                }

                ObservableList<ChannelModel> channels = tableView.getItems();
                String channelDescription = channels.get(channelIndex).getName();
                int slot = parseSlot(channelDescription);
                int channel = parseChannel(channelDescription, slot);

                graphController.setFields(slot, channel);
                graphController.showGraph();
            } else {
                ObservableList<CheckBox> checkBoxes = getCheckBoxes();
                int nonSelectedCheckBoxesCount = 0;

                for (CheckBox channel : checkBoxes) {
                    if (!channel.isSelected()) {
                        nonSelectedCheckBoxesCount++;
                    }
                }

                if (nonSelectedCheckBoxesCount == checkBoxes.size()) {
                    graphController.setStopped(true);
                }
                graphController.getGraphModel().clear();

            }
        });
    }

    private int parseSlot(String channelDescription) {
        return Integer.parseInt(channelDescription.split("слот ")[1].split("\\)")[0]);
    }

    private int parseChannel(String channelDescription, int slot) {
        List<Modules> modules = processController.getProcessModel().getModules();
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

    private void toggleGraphControls() {
        ObservableList<CheckBox> checkBoxes = getCheckBoxes();
        int disabledCheckBoxesCount = 0;

        for (CheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                disabledCheckBoxesCount++;
            }
        }

        boolean isGraphEnable = disabledCheckBoxesCount == checkBoxes.size();
        graphController.getAutoscaleCheckBox().setDisable(isGraphEnable);
        graphController.getRarefactionCoefficientLabel().setDisable(isGraphEnable);
        graphController.getRarefactionCoefficientComboBox().setDisable(isGraphEnable);
        graphController.getHorizontalScaleLabel().setDisable(isGraphEnable);
        graphController.getHorizontalScaleComboBox().setDisable(isGraphEnable);
        graphController.getVerticalScaleLabel().setDisable(isGraphEnable);
        graphController.getVerticalScaleComboBox().setDisable(isGraphEnable);
    }

    public void showParametersOfSignal() {
        new Thread(() -> {
            signalParametersModel.setTypesOfModules(processController.getProcessModel().getTypesOfModules());

            while (!showStopped && !processController.getProcess().isStopped()) {
                signalParametersModel.setData(processController.getProcess().getData());
                signalParametersModel.setAdcFrequencies(processController.getProcessModel().getModules());
                signalParametersModel.calculateParameters();

                show();
                Utils.sleep(1000);
            }
        }).start();
    }

    private void show() {
        ObservableList<ChannelModel> channels = tableView.getItems();
        ObservableList<Modules> modules = processController.getProcessModel().getModules();

        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            Modules module = modules.get(moduleIndex);
            List<Pair<Integer, String>> checkedChannels = new ArrayList<>();

            if (!module.getModuleType().equals(Crate.LTR34)) {
                checkedChannels = Modules.getChannelsDescriptions(module);
            }

            for (Pair<Integer, String> checkedChannel : checkedChannels) {
                for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
                    if (channels.get(channelIndex).getName().contains(checkedChannel.getValue())) {
                        ChannelModel channel = channels.get(channelIndex);
                        double amplitude = signalParametersModel.getAmplitude(moduleIndex, channelIndex);
                        double neededAmplitude = Double.parseDouble(channel.getAmplitude());
                        double dc = signalParametersModel.getDc(moduleIndex, channelIndex);
                        double neededDc = Double.parseDouble(channel.getDc());
                        double frequency = signalParametersModel.getFrequency(moduleIndex, channelIndex);
                        double neededFrequency = Double.parseDouble(channel.getFrequency());
                        double rms = signalParametersModel.getRms(moduleIndex, channelIndex);
                        double neededRms = Double.parseDouble(channel.getRms());

                        channel.setResponseAmplitude(String.valueOf(Utils.roundValue(amplitude, 1000)));
                        channel.setRelativeResponseAmplitude(String.valueOf(neededAmplitude == 0 ? 0 : amplitude / neededAmplitude * 100.0));
                        channel.setResponseRms(String.valueOf(Utils.roundValue(rms, 1000)));
                        channel.setRelativeResponseRms(String.valueOf(neededRms == 0 ? 0 : rms / neededRms * 100.0));
                        channel.setResponseFrequency(String.valueOf(Utils.roundValue(frequency, 1000)));
                        channel.setRelativeResponseFrequency(String.valueOf(neededFrequency == 0 ? 0 : frequency / neededFrequency * 100.0));

                    }
                }

            }
        }

    }

    private void listen(ColorPicker colorPicker, int channelIndex) {
        colorPicker.valueProperty().addListener(observable -> setSeriesColor(channelIndex));
    }

    private void setSeriesColor(int channelIndex) {
        if (getCheckBoxes().get(channelIndex).isSelected()) {
            ObservableList<ChannelModel> channels = tableView.getItems();
            String selectedColor = channels.get(channelIndex).getResponseColor();
            Node line = graphController.getGraphModel().getGraphSeries().getNode();
            line.setStyle("-fx-stroke: " + selectedColor);
        }
    }

    private void toggle() {
        processController.getStopButton().disableProperty().addListener(observable -> {
            ObservableList<CheckBox> checkBoxes = getCheckBoxes();
            ObservableList<ColorPicker> colorPickers = getColorPickers();
            boolean isProcessStopped = processController.getStopButton().isDisable();

            for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                int finalChannelIndex = channelIndex;

                Platform.runLater(() -> {
                    checkBoxes.get(finalChannelIndex).setDisable(isProcessStopped);
                    Utils.sleep(10); // пауза для переключения состояния
                    colorPickers.get(finalChannelIndex).setDisable(isProcessStopped);
                    Utils.sleep(10); // пауза для переключения состояния
                });
            }

            graphController.getAutoscaleCheckBox().setDisable(isProcessStopped);
            graphController.getRarefactionCoefficientLabel().setDisable(isProcessStopped);
            graphController.getRarefactionCoefficientComboBox().setDisable(isProcessStopped);
            graphController.getVerticalScaleLabel().setDisable(isProcessStopped);
            graphController.getVerticalScaleComboBox().setDisable(isProcessStopped);
            graphController.getHorizontalScaleLabel().setDisable(isProcessStopped);
            graphController.getHorizontalScaleComboBox().setDisable(isProcessStopped);
        });
    }

    public void setDefaultChannelsState() {
        disableChannels();
        resetColors();
    }

    private void disableChannels() {
        ObservableList<CheckBox> checkBoxes = getCheckBoxes();

        for (CheckBox checkBox : checkBoxes) {
            Platform.runLater(() -> checkBox.setSelected(false));
        }
    }

    private void resetColors() {
        ObservableList<ColorPicker> colorPickers = getColorPickers();

        for (ColorPicker colorPicker : colorPickers) {
            Platform.runLater(() -> colorPicker.setValue(Color.DARKRED));
        }
    }
}
