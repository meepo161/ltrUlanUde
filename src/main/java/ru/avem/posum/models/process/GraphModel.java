package ru.avem.posum.models.process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.avem.posum.db.ChannelsRepository;
import ru.avem.posum.db.models.Channels;
import ru.avem.posum.db.models.Modules;

public class GraphModel {
    private double[] data; // данные
    private int channel; // номер канала
    private ObservableList<ChannelModel> channels = FXCollections.observableArrayList(); // список добавленных и связанных каналов
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private int rarefactionCoefficient = 10; // коэффициент прореживания
    private int slot; // номер слота

    public void setFields(double[] data, int slot, int channel) {
        this.data = data;
        this.slot = slot;
        this.channel = channel;
    }

    // Возвращает точку графика для заданного канала
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
                channelModel.getChosenParameterIndex(), channelModel.getChosenParameterValue(), channelModel.getResponseColor());

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
