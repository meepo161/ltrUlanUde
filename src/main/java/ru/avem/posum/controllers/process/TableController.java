package ru.avem.posum.controllers.process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.IndexedColors;
import ru.avem.posum.db.ChannelsRepository;
import ru.avem.posum.db.models.Channels;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.process.ChannelModel;
import ru.avem.posum.models.process.SignalParametersModel;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TableController {
    private TableView<ChannelModel> table;
    private TableColumn<ChannelModel, String> channelsColumn;
    private TableColumn<ChannelModel, HBox> responseColumn;
    private TableColumn<ChannelModel, String> ampResponseColumn;
    private TableColumn<ChannelModel, String> dcResponseColumn;
    private TableColumn<ChannelModel, String> frequencyResponseColumn;
    private TableColumn<ChannelModel, String> loadsCounterColumn;
    private TableColumn<ChannelModel, String> rmsResponseColumn;

    private int disableCount;
    private GraphController graphController;
    private Button initializeButton;
    private ProcessController processController;
    private Thread showingThread;
    private RegulatorController regulatorController;
    private SignalParametersModel signalParametersModel = new SignalParametersModel();

    public TableController(TableView<ChannelModel> table, TableColumn<ChannelModel, String> channelsColumn,
                           TableColumn<ChannelModel, HBox> responseColumn, TableColumn<ChannelModel, String> ampResponseColumn,
                           TableColumn<ChannelModel, String> dcResponseColumn, TableColumn<ChannelModel, String> frequencyResponseColumn,
                           TableColumn<ChannelModel, String> loadsCounterColumn, TableColumn<ChannelModel, String> rmsResponseColumn,
                           GraphController graphController, ProcessController processController, Button initializeButton) {

        this.table = table;
        this.channelsColumn = channelsColumn;
        this.responseColumn = responseColumn;
        this.ampResponseColumn = ampResponseColumn;
        this.dcResponseColumn = dcResponseColumn;
        this.frequencyResponseColumn = frequencyResponseColumn;
        this.loadsCounterColumn = loadsCounterColumn;
        this.rmsResponseColumn = rmsResponseColumn;
        this.graphController = graphController;
        this.processController = processController;
        this.regulatorController = new RegulatorController(processController);
        this.initializeButton = initializeButton;

        initTableView();
        listenStopButton();
    }

    public void init() {
        table.getItems().addListener((ListChangeListener<ChannelModel>) observable -> {
            initializeButton.setDisable(table.getItems().isEmpty());
        });

        loadChannels();
    }

    private void initTableView() {
        table.setItems(graphController.getGraphModel().getChannels());

        Utils.makeHeaderWrappable(channelsColumn);

        initResponse(responseColumn);
        init(ampResponseColumn);
        init(dcResponseColumn);
        init(loadsCounterColumn);
        init(rmsResponseColumn);
        init(frequencyResponseColumn);

        channelsColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        responseColumn.setCellValueFactory(cellData -> cellData.getValue().getResponse());
        ampResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseAmplitudeProperty());
        dcResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseDcProperty());
        frequencyResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseFrequencyProperty());
        loadsCounterColumn.setCellValueFactory(cellData -> cellData.getValue().responseLoadsCounterProperty());
        rmsResponseColumn.setCellValueFactory(cellData -> cellData.getValue().responseRmsProperty());

        listen(table);
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
        ObservableList<ChannelModel> channels = table.getItems();
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
        ObservableList<ChannelModel> channels = table.getItems();
        ObservableList<ColorPicker> colorPickers = FXCollections.observableArrayList();

        if (!channels.isEmpty()) {
            for (ChannelModel channel : channels) {
                colorPickers.add(channel.getColorPicker());
            }
        }

        return colorPickers;
    }

    private void listen(CheckBox checkBox, int channelIndex) {
        checkBox.selectedProperty().addListener(observable -> {
            toggleGraphControls();

            if (checkBox.isSelected()) {
                graphController.restartShow();
                toggleCheckBoxes(checkBox);
                setSeriesColor(channelIndex);
                showSignal(channelIndex);
            } else {
                checkSelection();
            }
        });
    }

    private void toggleCheckBoxes(CheckBox checkBox) {
        ObservableList<CheckBox> checkBoxes = getCheckBoxes();
        checkBoxes.remove(checkBox);

        for (CheckBox channel : checkBoxes) {
            channel.setSelected(false);
        }

        checkBox.setSelected(true);
    }

    private void showSignal(int channelIndex) {
        if (showingThread == null) {
            showingThread = new Thread(() -> {
                if (!graphController.isShowingThreadStopped()) {
                    graphController.stopShowingThread();
                }

                parseData(channelIndex);

                graphController.showGraph();
                new Thread(() -> graphController.restartShow()).start();
            });

            showingThread.start();
        } else {
            if (disableCount == 0) {
                new Thread(() -> {
                    parseData(channelIndex);
                    graphController.restartShow();
                    showingThread.interrupt();
                }).start();
                disableCount++;
            }
        }
    }

    private void parseData(int channelIndex) {
        ObservableList<ChannelModel> channels = table.getItems();
        String channelDescription = channels.get(channelIndex).getName();
        Pair<Integer, Integer> channel = parseSlotAndChannel(channelDescription);

        Utils.sleep(100); // пауза для ожидания ненулевого сигнала
        graphController.setFields(channel.getKey(), channel.getValue());
    }

    public Pair<Integer, Integer> parseSlotAndChannel(String channelDescription) {
        List<Modules> modules = processController.getProcessModel().getModules();
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

    private int parseSlot(String channelDescription) {
        if (!channelDescription.contains("=>")) {
            return Integer.parseInt(channelDescription.split("слот ")[1].split("\\)")[0]);
        } else {
            String adcChannelDescription = channelDescription.split("=> ")[1];
            return Integer.parseInt(adcChannelDescription.split("слот ")[1].split("\\)")[0]);
        }
    }

    private void checkSelection() {
        ObservableList<CheckBox> checkBoxes = getCheckBoxes();
        int nonSelectedCheckBoxesCount = 0;

        for (CheckBox channel : checkBoxes) {
            if (!channel.isSelected()) {
                nonSelectedCheckBoxesCount++;
            }
        }

        if (nonSelectedCheckBoxesCount == checkBoxes.size()) {
            graphController.setStopped(true);
            graphController.getGraphModel().clear();
        } else if (nonSelectedCheckBoxesCount == checkBoxes.size() - 1) {
            disableCount = 0;
        }
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
            while (!processController.getProcess().isStopped()) {
                signalParametersModel.setData(processController.getCalibrationModel().getCalibratedData());
                signalParametersModel.setAdcFrequencies(processController.getProcessModel().getModules());
                signalParametersModel.calculateParameters();

                regulatorController.setResponse();

                show();
                Utils.sleep(1000);
            }
        }).start();
    }

    public void initRegulator() {
        regulatorController.initRegulator(getDacChannels());
    }

    private List<ChannelModel> getDacChannels() {
        ObservableList<ChannelModel> channels = table.getItems();
        List<ChannelModel> dacChannels = new ArrayList<>();

        for (ChannelModel channel : channels) {
            if (channel.getName().contains(Crate.LTR24)) {
                dacChannels.add(channel);
            }
        }

        return dacChannels;
    }

    private void show() {
        ObservableList<ChannelModel> channels = table.getItems();
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
                        double loadsCounter = signalParametersModel.getLoadsCounter(moduleIndex, channelIndex);
                        double frequency = signalParametersModel.getFrequency(moduleIndex, channelIndex);
                        double neededFrequency = Double.parseDouble(channelModel.getFrequency());
                        double rms = signalParametersModel.getRms(moduleIndex, channelIndex);
                        double neededRms = Double.parseDouble(channelModel.getRms());

                        channelModel.setResponseAmplitude(String.valueOf(Utils.roundValue(amplitude, 1000)));
                        channelModel.setRelativeResponseAmplitude(String.valueOf(neededAmplitude == 0 ? 0 : Utils.roundValue(amplitude / neededAmplitude * 100.0, 1000)));
                        channelModel.setResponseDc(String.valueOf(Utils.roundValue(dc, 1000)));
                        channelModel.setRelativeResponseDc(String.valueOf(neededDc == 0 ? 0 : Utils.roundValue(dc / neededDc * 100.0, 1000)));
                        channelModel.setResponseLoadsCounter(String.valueOf((int) loadsCounter));
                        channelModel.setResponseFrequency(String.valueOf(Utils.roundValue(frequency, 1000)));
                        channelModel.setRelativeResponseFrequency(String.valueOf(neededFrequency == 0 ? 0 : Utils.roundValue(frequency / neededFrequency * 100.0, 1000)));
                        channelModel.setResponseRms(String.valueOf(Utils.roundValue(rms, 1000)));
                        channelModel.setRelativeResponseRms(String.valueOf(neededRms == 0 ? 0 : Utils.roundValue(rms / neededRms * 100.0, 1000)));
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
            ObservableList<ChannelModel> channels = table.getItems();
            String selectedColor = channels.get(channelIndex).getResponseColor();
            Node line = graphController.getGraphModel().getGraphSeries().getNode();
            line.setStyle("-fx-stroke: " + selectedColor);
        }
    }

    private void listenStopButton() {
        processController.getStopButton().disableProperty().addListener(observable -> {
            ObservableList<CheckBox> checkBoxes = getCheckBoxes();
            boolean isProcessStopped = processController.getStopButton().isDisable();

            Platform.runLater(() -> {
                for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                    graphController.getAutoscaleCheckBox().setDisable(isProcessStopped);
                    graphController.getRarefactionCoefficientLabel().setDisable(isProcessStopped);
                    graphController.getRarefactionCoefficientComboBox().setDisable(isProcessStopped);
                    graphController.getVerticalScaleLabel().setDisable(isProcessStopped);
                    graphController.getVerticalScaleComboBox().setDisable(isProcessStopped);
                    graphController.getHorizontalScaleLabel().setDisable(isProcessStopped);
                    graphController.getHorizontalScaleComboBox().setDisable(isProcessStopped);
                }
            });
        });
    }

    public void toggleResponseUiElements(boolean isDisable) {
        ObservableList<ChannelModel> channels = table.getItems();

        for (ChannelModel channel : channels) {
            channel.getResponseCheckBox().setDisable(isDisable);
        }
    }

    public void disableChannels() {
        ObservableList<CheckBox> checkBoxes = getCheckBoxes();

        for (CheckBox checkBox : checkBoxes) {
            Platform.runLater(() -> checkBox.setSelected(false));
        }
    }

    public void saveChannels() {
        ObservableList<ChannelModel> channels = table.getItems();
        List<Channels> dbChannels = ChannelsRepository.getAllChannels();

        for (ChannelModel channelModel : channels) {
            for (Channels dbChannel : dbChannels) {
                if (channelModel.getId() == dbChannel.getId()) {
                    dbChannel.setName(channelModel.getName());
                    dbChannel.setChosenParameterIndex(channelModel.getChosenParameterIndex());
                    dbChannel.setChosenParameterValue(channelModel.getChosenParameterValue());
                    dbChannel.setPCoefficient(channelModel.getPCoefficient());
                    dbChannel.setICoefficient(channelModel.getICoefficient());
                    dbChannel.setDCoefficient(channelModel.getDCoefficient());
                    dbChannel.setResponseColor(channelModel.getResponseColor());

                    ChannelsRepository.updateChannel(dbChannel);
                    break;
                }
            }
        }
    }

    public void loadChannels() {
        List<Channels> channels = ChannelsRepository.getAllChannels();

        for (Channels channel : channels) {
            if (channel.getTestProgramId() == processController.getTestProgramId()) {
                ChannelModel channelModel = new ChannelModel(channel.getId(), channel.getName(),
                        channel.getPCoefficient(), channel.getICoefficient(), channel.getDCoefficient(),
                        channel.getChosenParameterIndex(), channel.getChosenParameterValue(), channel.getResponseColor());

                table.getItems().add(channelModel);
                processController.getLinkingController().add(channelModel);
                Platform.runLater(() -> {
                    processController.getRegulatorParametersController().removeColumns();
                    processController.getRegulatorParametersController().addColumns();
                });
            }
        }

        processController.getLinkingController().initModulesList();

    }

    public void delete(ChannelModel channelModel) {
        List<Channels> channels = ChannelsRepository.getAllChannels();

        for (Channels channel : channels) {
            if (channelModel.getId() == channel.getId()) {
                ChannelsRepository.deleteChannel(channel);
                break;
            }
        }
    }

    public void delete(List<ChannelModel> channels) {
        List<Channels> dbChannels = ChannelsRepository.getAllChannels();

        for (ChannelModel channelModel : channels) {
            for (Channels dbChannel : dbChannels) {
                if (channelModel.getId() == dbChannel.getId()) {
                    ChannelsRepository.deleteChannel(dbChannel);
                    break;
                }
            }
        }
    }

    public void clearChannels() {
        ObservableList<ChannelModel> channelModels = table.getItems();
        for (ChannelModel channel : channelModels) {
            channel.clearResponse();
        }
    }

    public ObservableList<ChannelModel> getChannels() {
        return table.getItems();
    }

    public List<List<String>> getChannelsData() {
        ObservableList<ChannelModel> channelModels = table.getItems();

        List<String> names = new ArrayList<>();
        List<String> amplitudes = new ArrayList<>();
        List<String> dc = new ArrayList<>();
        List<String> frequency = new ArrayList<>();
        List<String> rms = new ArrayList<>();
        List<String> loadsCounters = new ArrayList<>();
        List<String> neededAmlitudes = new ArrayList<>();
        List<String> neededDc = new ArrayList<>();
        List<String> neededFrequency = new ArrayList<>();
        List<String> relativeResponseAmplitudes = new ArrayList<>();
        List<String> relativeResponseDc = new ArrayList<>();
        List<String> relativeResponseFrequency = new ArrayList<>();

        for (ChannelModel channelModel : channelModels) {
            names.add(channelModel.getName());
            amplitudes.add(channelModel.getResponseAmplitude());
            dc.add(channelModel.getResponseDc());
            frequency.add(channelModel.getResponseFrequency());
            rms.add(channelModel.getRms());
            loadsCounters.add(channelModel.getLoadsCounter());
            neededAmlitudes.add(channelModel.getChosenParameterIndex().equals("0") ? channelModel.getChosenParameterValue() : "0.0");
            neededDc.add(channelModel.getChosenParameterIndex().equals("1") ? channelModel.getChosenParameterValue() : "0.0");
            neededFrequency.add(channelModel.getChosenParameterIndex().equals("2") ? channelModel.getChosenParameterValue() : "0.0");
            relativeResponseAmplitudes.add(channelModel.getRelativeResponseAmplitude());
            relativeResponseDc.add(channelModel.getRelativeResponseDc());
            relativeResponseFrequency.add(channelModel.getRelativeResponseFrequency());
        }

        List<List<String>> output = new ArrayList<>();
        output.add(names);
        output.add(amplitudes);
        output.add(dc);
        output.add(frequency);
        output.add(rms);
        output.add(loadsCounters);
        for (ChannelModel channelModel : channelModels) {
            switch (Integer.parseInt(channelModel.getChosenParameterIndex())) {
                case 0:
                    if (!output.contains(neededAmlitudes)) {
                        output.add(neededAmlitudes);
                        output.add(relativeResponseAmplitudes);
                    }
                    break;
                case 1:
                    if (!output.contains(neededDc)) {
                        output.add(neededDc);
                        output.add(relativeResponseDc);
                    }
                    break;
                case 2:
                    if (!output.contains(neededFrequency)) {
                        output.add(neededFrequency);
                        output.add(relativeResponseFrequency);
                    }
                    break;
            }
        }

        return output;
    }

    public List<Short> getColorsForProtocol() {
        ObservableList<ChannelModel> channelModels = table.getItems();
        List<Short> colors = new ArrayList<>();
        for (ChannelModel channelModel : channelModels) {
            colors.add(IndexedColors.WHITE.index);
        }
        return colors;
    }

    public RegulatorController getRegulatorController() {
        return regulatorController;
    }
}
