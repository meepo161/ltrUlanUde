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
    private RegulatorController regulatorController = new RegulatorController();
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
                Pair<Integer, Integer> channel = parseChannel(channelDescription);

                graphController.setFields(channel.getKey(), channel.getValue());
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
        if (!channelDescription.contains("=>")) {
            return Integer.parseInt(channelDescription.split("слот ")[1].split("\\)")[0]);
        } else {
            String adcChannelDescription = channelDescription.split("=> ")[1];
            return Integer.parseInt(adcChannelDescription.split("слот ")[1].split("\\)")[0]);
        }
    }

    private Pair<Integer, Integer> parseChannel(String channelDescription) {
        List<Modules> modules = processController.getProcessModel().getModules();
        int channel = 0;
        int slot = parseSlot(channelDescription);
        channelDescription = channelDescription.contains("=>") ? channelDescription.split("=> ")[1] : channelDescription;

        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            if (!modules.get(moduleIndex).getModuleType().equals(Crate.LTR34)) {
                if (modules.get(moduleIndex).getSlot() == slot) {
                    List<Pair<Integer, String>> descriptions = Modules.getChannelsDescriptions(modules.get(moduleIndex));

                    for (Pair<Integer, String> description : descriptions) {
                        if (description.getValue().equals(channelDescription)) {
                            return new Pair<>(moduleIndex, description.getKey() - 1);
                        }
                    }
                }

            }
        }

        return new Pair<>(0, 0);
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
            regulatorController.initRegulator(getDacChannels());

            while (!showStopped && !processController.getProcess().isStopped()) {
                signalParametersModel.setData(processController.getProcess().getData());
                signalParametersModel.setAdcFrequencies(processController.getProcessModel().getModules());
                signalParametersModel.calculateParameters();

                regulatorController.setResponse();

                show();
                Utils.sleep(1000);
            }
        }).start();
    }

    private List<ChannelModel> getDacChannels() {
        ObservableList<ChannelModel> channels = tableView.getItems();
        List<ChannelModel> dacModules = new ArrayList<>();

        for (ChannelModel channel : channels) {
            if (channel.getName().contains(Crate.LTR24)) {
                dacModules.add(channel);
            }
        }

        return dacModules;
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
                for (ChannelModel channelModel : channels) {
                    if (channelModel.getName().contains(checkedChannel.getValue())) {
                        int channelIndex = checkedChannel.getKey() - 1;
                        double amplitude = signalParametersModel.getAmplitude(moduleIndex, channelIndex);
                        double neededAmplitude = Double.parseDouble(channelModel.getAmplitude());
                        double dc = signalParametersModel.getDc(moduleIndex, channelIndex);
                        double neededDc = Double.parseDouble(channelModel.getDc());
                        double frequency = signalParametersModel.getFrequency(moduleIndex, channelIndex);
                        double neededFrequency = Double.parseDouble(channelModel.getFrequency());
                        double rms = signalParametersModel.getRms(moduleIndex, channelIndex);
                        double neededRms = Double.parseDouble(channelModel.getRms());

                        channelModel.setResponseAmplitude(String.valueOf(Utils.roundValue(amplitude, 1000)));
                        channelModel.setRelativeResponseAmplitude(String.valueOf(neededAmplitude == 0 ? 0 : Utils.roundValue(amplitude / neededAmplitude * 100.0, 1000)));
                        channelModel.setResponseRms(String.valueOf(Utils.roundValue(rms, 1000)));
                        channelModel.setRelativeResponseRms(String.valueOf(neededRms == 0 ? 0 : Utils.roundValue(rms / neededRms * 100.0, 1000)));
                        channelModel.setResponseFrequency(String.valueOf(Utils.roundValue(frequency, 1000)));
                        channelModel.setRelativeResponseFrequency(String.valueOf(neededFrequency == 0 ? 0 : Utils.roundValue(frequency / neededFrequency * 100.0, 1000)));
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


            Platform.runLater(() -> {
                for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                    checkBoxes.get(channelIndex).setDisable(isProcessStopped);
                    colorPickers.get(channelIndex).setDisable(isProcessStopped);
                }
            });


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

    public RegulatorController getRegulatorController() {
        return regulatorController;
    }
}
