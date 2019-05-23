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

public class ProcessModel {
    private int currentIndex;
    private List<XYChart.Series<Number, Number>> graphSeries = new ArrayList<>();
    private Boolean[] graphSeriesEnabled = new Boolean[24];
    private LineChart<Number, Number> graph;
    private ObservableList<ChannelModel> processData = FXCollections.observableArrayList();
    private TableView<ChannelModel> table;

    public void loadData(long idTest) {
        int index = processData.size() + 1;
        processData.add(new ChannelModel("АЦП" + index + " ЦАП" + index));
    }

    public void testData() {
        for (int i = 0; i < processData.size(); i++) {
            processData.get(i).setAmplitudeStatus(i % 7);
            processData.get(i).setFrequencyStatus((i + 1) % 7);
            processData.get(i).setRmsStatus(7 + (i) % 3);
            processData.get(i).setResponseAmplitude(i + " %");
            processData.get(i).setResponseFrequency((i + 1) + " В");
            processData.get(i).setResponseRms((i + 2) + "234 %\n" + (i + 3) + "123 В");
        }
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

    public void init(TableColumn<ChannelModel, String> columnOfTable) {
        Utils.makeHeaderWrappable(columnOfTable);

        columnOfTable.setCellFactory(column -> new TableCell<ChannelModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { //If the cell is empty
                    setText(null);
                } else {
                    setText(item);
                }
                setGraphic(null);
            }
        });
    }

    public void SetProcessSampleColumnColorFunction(TableColumn<ChannelModel, Void> columnProcessSample) {
        Utils.makeHeaderWrappable(columnProcessSample);
        Callback<TableColumn<ChannelModel, Void>, TableCell<ChannelModel, Void>> cellFactory = new Callback<TableColumn<ChannelModel, Void>, TableCell<ChannelModel, Void>>() {

            @Override
            public TableCell<ChannelModel, Void> call(final TableColumn<ChannelModel, Void> param) {
                final TableCell<ChannelModel, Void> cell = new TableCell<ChannelModel, Void>() {
                    private final CheckBox chBox = new CheckBox("");

                    {
                        chBox.setMaxHeight(20);
                        chBox.setOnAction((ActionEvent event) -> {
                            String groupNum = this.getTableColumn().getId().substring(5, 6);
                            ChannelModel row = getTableView().getItems().get(getIndex());
                            switch (Integer.parseInt(groupNum)) {
                                case 1:
                                    row.setAmplitudeEnable(chBox.isSelected());
                                    if (row.getAmplitudeGraphNum() < 0) {
                                        row.setAmplitudeGraphNum(chartsAdd());
                                        setSeriesColor(row.getAmplitudeGraphNum(), row.getAmplitudeColor());
                                    }
                                    toggleSeries(row.getAmplitudeGraphNum(), chBox.isSelected());
                                    break;
                                case 2:
                                    row.setFrequencyEnable(chBox.isSelected());
                                    if (row.getFrequencyGraphNum() < 0) {
                                        row.setFrequencyGraphNum(chartsAdd());
                                        setSeriesColor(row.getFrequencyGraphNum(), row.getFrequencyColor());
                                    }
                                    toggleSeries(row.getFrequencyGraphNum(), chBox.isSelected());
                                    break;
                                case 3:
                                    row.setRmsEnable(chBox.isSelected());
                                    if (row.getRmsGraphNum() < 0) {
                                        row.setRmsGraphNum(chartsAdd());
                                        setSeriesColor(row.getRmsGraphNum(), row.getRmsColor());
                                    }
                                    toggleSeries(row.getRmsGraphNum(), chBox.isSelected());
                                    break;
                            }
                        });
                    }

                    private final ColorPicker colorPicker = new ColorPicker();

                    {
                        colorPicker.setMaxHeight(20);
                        colorPicker.setStyle("-fx-color-label-visible: false;");
                        colorPicker.setOnAction((ActionEvent event) -> {
                            String groupNum = this.getTableColumn().getId().substring(5, 6);
                            String colorValue = String.format("%d, %d, %d",
                                    (int) (colorPicker.getValue().getRed() * 255),
                                    (int) (colorPicker.getValue().getGreen() * 255),
                                    (int) (colorPicker.getValue().getBlue() * 255));


                            ChannelModel row = getTableView().getItems().get(getIndex());
                            System.out.println("ColorPicker: " + row + " groupNum:" + groupNum + " isColor:" + colorValue);
                            switch (Integer.parseInt(groupNum)) {
                                case 1:
                                    row.setAmplitudeColor(colorValue);
                                    setSeriesColor(row.getAmplitudeGraphNum(), row.getAmplitudeColor());
                                    break;
                                case 2:
                                    row.setFrequencyColor(colorValue);
                                    setSeriesColor(row.getFrequencyGraphNum(), row.getFrequencyColor());
                                    break;
                                case 3:
                                    row.setRmsColor(colorValue);
                                    setSeriesColor(row.getRmsGraphNum(), row.getRmsColor());
                                    break;
                            }
                        });
                    }

                    private final HBox hBox = new HBox();

                    {
                        hBox.setMaxHeight(22);
                        hBox.getChildren().add(chBox);
                        hBox.getChildren().add(colorPicker);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            }
        };

        columnProcessSample.setCellFactory(cellFactory);
    }

    public ObservableList<ChannelModel> getProcessData() {
        return processData;
    }

    public void initProcessSampleData(TableView<ChannelModel> newTableProcessSample) {
        table = newTableProcessSample;
        table.setItems(this.getProcessData());
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
        processData.clear();
    }

    public void resetData() {
        processData.clear();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setLineToProcessSample(String mainText) {
        processData.add(new ChannelModel(mainText));
    }
}
