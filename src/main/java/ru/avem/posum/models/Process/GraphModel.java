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
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {
    private double[] data;
    private int channel;
    private ObservableList<ChannelModel> channels = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private int rarefactionCoefficient;
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

    public String setStyleByCode(int code) {
        switch (code) {
            case 0:
                return "-fx-background-color: black; -fx-text-background-color: grey; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 1:
                return "-fx-background-color: black; -fx-text-background-color: white; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 2:
                return "-fx-background-color: black; -fx-text-background-color: yellow; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 3:
                return "-fx-background-color: black; -fx-text-background-color: #99d777; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 4:
                return "-fx-background-color: black; -fx-text-background-color: green; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 5:
                return "-fx-background-color: black; -fx-text-background-color: red; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 6:
                return "-fx-background-color: black; -fx-text-background-color: blue; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 7:
                return "-fx-background-color: black; -fx-text-background-color: yellow; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            case 8:
                return "-fx-background-color: black; -fx-text-background-color: green; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            case 9:
                return "-fx-background-color: black; -fx-text-background-color: red; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            default:
                return null;
        }
    }

    public void clear() {
        graphSeries.getData().clear();
    }

    public ObservableList<ChannelModel> getChannels() {
        return channels;
    }

    public XYChart.Series<Number, Number> getGraphSeries() {
        return graphSeries;
    }

    public void setRarefactionCoefficient(int rarefactionCoefficient) {
        this.rarefactionCoefficient = rarefactionCoefficient;
    }
}
