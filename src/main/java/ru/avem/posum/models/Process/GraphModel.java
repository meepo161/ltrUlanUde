package ru.avem.posum.models.Process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ru.avem.posum.db.ChannelsRepository;
import ru.avem.posum.db.models.Channels;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.models.Settings.SettingsModel;
import ru.avem.posum.models.Signal.SignalParametersModel;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphModel {
    private double[] data;
    private int channel;
    private ObservableList<ChannelModel> channels = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private int rarefactionCoefficient = 10;
    private int slot;

    public void setFields(double[] data, int slot, int channel) {
        this.data = data;
        this.slot = slot;
        this.channel = channel;
    }

    public XYChart.Data<Number, Number> getPoint(int index) {
        int channels = 4; // количество каналов АЦП
        double xValue = (double) (index - channels) / data.length;
        double yValue = data[index];

        return new XYChart.Data<>(xValue, yValue);
    }

    private void createADCIntances(ObservableList<Modules> modules) {
        for (Modules module : modules) {
            createADC(module);
        }
    }

    private void createADC(Modules module) {
        String moduleType = module.getModuleType();

    }

    public void clear() {
        Platform.runLater(() -> graphSeries.getData().clear());
    }

    public void add(long testProgramId, ChannelModel channelModel) {
        Channels dbChannel = new Channels(testProgramId, channelModel.getName(),
                channelModel.getPCoefficient(), channelModel.getICoefficient(), channelModel.getDCoefficient(),
                channelModel.getChosenParameterIndex(), channelModel.getResponseColor());

        ChannelsRepository.insertChannel(dbChannel);

        channelModel.setId(dbChannel.getId());
        channels.add(channelModel);
    }

    public ObservableList<ChannelModel> getChannels() {
        return channels;
    }

    public XYChart.Series<Number, Number> getGraphSeries() {
        return graphSeries;
    }

    public int getRarefactionCoefficient() {
        return rarefactionCoefficient;
    }

    public void setRarefactionCoefficient(int rarefactionCoefficient) {
        this.rarefactionCoefficient = rarefactionCoefficient;
    }
}
