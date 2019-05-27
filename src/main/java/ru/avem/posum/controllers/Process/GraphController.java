package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import ru.avem.posum.hardware.Process;
import ru.avem.posum.models.Process.GraphModel;
import ru.avem.posum.utils.Utils;

public class GraphController {
    private CheckBox autoscaleCheckBox;
    private LineChart<Number, Number> graph;
    private GraphModel graphModel = new GraphModel();
    private Label horizontalScaleLabel;
    private ComboBox<String> horizontalScaleComboBox;
    private Process process;
    private Label verticalScaleLabel;
    private ComboBox<String> verticalScaleComboBox;

    public GraphController(CheckBox autoscaleCheckBox, LineChart<Number, Number> graph, Label horizontalScaleLabel,
                           ComboBox<String> horizontalScaleComboBox, Process process, Label verticalScaleLabel,
                           ComboBox<String> verticalScaleComboBox) {

        this.autoscaleCheckBox = autoscaleCheckBox;
        this.graph = graph;
        this.horizontalScaleLabel = horizontalScaleLabel;
        this.horizontalScaleComboBox = horizontalScaleComboBox;
        this.process = process;
        this.verticalScaleLabel = verticalScaleLabel;
        this.verticalScaleComboBox = verticalScaleComboBox;

        graph.getData().add(graphModel.getGraphSeries());
        initScales();
    }

    private void initScales() {
        listen(autoscaleCheckBox);
        addVerticalScales();
        listenVerticalScales();
        addHorizontalScales();
        listenHorizontalScales();
    }

    private void listen(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(observable -> {
            NumberAxis yAxis = (NumberAxis) graph.getYAxis();
            yAxis.setAutoRanging(checkBox.isSelected());
        });
    }

    private void addVerticalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 мВ/дел");
        scales.add("10 мВ/дел");
        scales.add("100 мВ/дел");
        scales.add("1 В/дел");
        scales.add("10 В/дел");
        scales.add("100 В/дел");

        verticalScaleComboBox.setItems(scales);
        verticalScaleComboBox.getSelectionModel().select(3);
    }

    private void listenVerticalScales() {
        verticalScaleComboBox.valueProperty().addListener(observable -> {
            String selectedScale = verticalScaleComboBox.getSelectionModel().getSelectedItem();
            double scaleValue = Integer.parseInt(selectedScale.split(" ")[0]);
            String scaleName = selectedScale.split(" ")[1].split("/")[0];

            if (scaleName.equals("мВ")) {
                scaleValue *= 0.001;
            }

            int divisions = 10; // количество делений
            double lowerBound = -scaleValue * divisions / 2;
            double upperBound = scaleValue * divisions / 2;
            double tickUnit = scaleValue;

            scaleName = "Напряжение, В";
            NumberAxis yAxis = (NumberAxis) graph.getYAxis();
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setLabel(scaleName);
        });
    }

    private void addHorizontalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 мс/дел");
        scales.add("10 мс/дел");
        scales.add("100 мс/дел");
        scales.add("1 с/дел");
        scales.add("10 с/дел");
        scales.add("1 мин/дел");
        scales.add("10 мин/дел");
        scales.add("1 ч/дел");
        scales.add("10 ч/дел");
        scales.add("1 д/дел");
        scales.add("10 д/дел");

        horizontalScaleComboBox.setItems(scales);
        horizontalScaleComboBox.getSelectionModel().select(2);
    }

    private void listenHorizontalScales() {
        horizontalScaleComboBox.valueProperty().addListener(observable -> {
            String selectedScale = horizontalScaleComboBox.getSelectionModel().getSelectedItem();
            double scaleValue = Integer.parseInt(selectedScale.split(" ")[0]);
            String scaleName = selectedScale.split(" ")[1].split("/")[0];

            switch (scaleName) {
                case "мс":
                    scaleName = "Время, секунд";
                    scaleValue *= 0.001;
                    break;
                case "с":
                    scaleName = "Время, секунд";
                    break;
                case "мин":
                    scaleName = "Время, минут";
                    break;
                case "ч":
                    scaleName = "Время, часов";
                    break;
                case "д":
                    scaleName = "Время, дней";
                    break;
            }

            int divisions = 10; // количество делений
            double lowerBound = 0;
            double upperBound = scaleValue * divisions;
            double tickUnit = scaleValue;

            NumberAxis yAxis = (NumberAxis) graph.getXAxis();
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setLabel(scaleName);
        });
    }

    public void showGraph(int slot, int channel) {
        double[] data = process.getData()[slot];
        double[] buffer = new double[data.length];
        System.arraycopy(data, 0, buffer, 0, data.length);

        graphModel.setFields(buffer, slot, channel);
        show(buffer, channel);
    }

    public void show(double[] data, int channel) {
        int channels = 4; // количество каналов АЦП
        int rarefactionCoefficient = 1;

        showLoop: for (int index = channel; index < data.length && !process.isStopped(); index += channels * rarefactionCoefficient) {
            XYChart.Data<Number, Number> point = graphModel.getPoint(index);
            Runnable addPoint = () -> {
                if (!graphModel.getGraphSeries().getData().contains(point)) {
                    graphModel.getGraphSeries().getData().add(point);
                }
            };

            if ((double) point.getXValue() < 1) { // TODO: change this shit
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }

            if (index + (channels * rarefactionCoefficient) >= data.length) {
                double xValue = 1;
                double yValue = (double) graphModel.getPoint(index).getYValue();
                XYChart.Data<Number, Number> lastPoint = new XYChart.Data<>(xValue, yValue);
                Platform.runLater(() -> graphModel.getGraphSeries().getData().add(lastPoint));
                Utils.sleep(1);
            }
//            } else if ((double) point.getXValue() >= graphModel.getGraphSeries().getUpperBound()) {
//                Platform.runLater(addPoint);
//                Utils.sleep(1);
//                break;
//            }
        }
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }
}
