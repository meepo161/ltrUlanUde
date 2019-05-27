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
    private int currentIndex;
    private List<XYChart.Series<Number, Number>> graphSeries = new ArrayList<>();
    private Boolean[] graphSeriesEnabled = new Boolean[24];
    private LineChart<Number, Number> graph;
    private ObservableList<ChannelModel> channels = FXCollections.observableArrayList();
    private TableView<ChannelModel> table;

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

    public ObservableList<ChannelModel> getChannels() {
        return channels;
    }

    public void setXAxis(double xLength) {
        if (graph != null) {
            NumberAxis pNumAxis = (NumberAxis) graph.getXAxis();
            pNumAxis.setAutoRanging(false);
            pNumAxis.setUpperBound(xLength);
            pNumAxis.setTickUnit(xLength / 10);
        }
    }


    public void fillSeries(double[] data, XYChart.Series series) {
        List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            intermediateList.add(new XYChart.Data<>((double) i / data.length, data[i]));
        }

        Platform.runLater(() -> {
            series.getData().clear();
            series.getData().addAll(intermediateList);
        });
    }

    public void chart(LineChart<Number, Number> lineChart) {
        graph = lineChart;
        graph.setLegendVisible(false);
        graph.setAnimated(false);
    }

    // метод для получения номера линии с графика, и инициализация ее.
    private int chartsAdd() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("channel " + currentIndex);

        graphSeries.add(series);
        graphSeriesEnabled[currentIndex] = true;
        graph.getData().add(graphSeries.get(currentIndex));
        currentIndex++;

        return currentIndex - 1;
    }

    private void toggleSeries(int seriesIndex, Boolean status) {
        if (!status) {
            graphSeries.get(seriesIndex).getData().clear();
            graphSeriesEnabled[seriesIndex] = false;
        } else {
            graphSeriesEnabled[seriesIndex] = true;
        }
    }

    private void setSeriesColor(int seriesIndex, String color) {
        if (seriesIndex >= 0) {
            graphSeries.get(seriesIndex).getNode().setStyle("-fx-stroke: rgb(" + color + ");");
        }
    }

    public void clearSeries(int seriesIndex) {
        if (graphSeriesEnabled[seriesIndex]) {
            graphSeries.get(seriesIndex).getData().clear();
        }
    }

    public void setSeriesData(int seriesIndex, double[] array) {
        if (graphSeriesEnabled[seriesIndex]) {
            fillSeries(array, graphSeries.get(seriesIndex));
        }
    }

    public void addSeriesData(int seriesIndex, double value, double time) {
        if (graphSeriesEnabled[seriesIndex]) {
            if (graphSeries.get(seriesIndex).getData() != null) {
                Platform.runLater(() -> graphSeries.get(seriesIndex).getData().add(new XYChart.Data<>(time, value)));
            } else {
                List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
                intermediateList.add(new XYChart.Data<>(time, value));
                Platform.runLater(() -> {
                    graphSeries.get(seriesIndex).getData().clear();
                    graphSeries.get(seriesIndex).getData().addAll(intermediateList);
                });
            }
        }
    }

    public void chartAdd() {
        double[] arr1 = new double[255];

        for (int j = 0; j < currentIndex; j++) {
            for (int i = 0; i < 255; i++) {
                arr1[i] = Math.sin((i + j) * 0.1);
            }
            setSeriesData(j, arr1);
        }
    }

    public void clearLineProcessSample(int lineId) {
        channels.clear();
    }

    public void resetData() {
        channels.clear();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setLineToProcessSample(String mainText) {
        channels.add(new ChannelModel(mainText));
    }
}
