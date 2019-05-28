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
    private Label rarefactionCoefficientLabel;
    private ComboBox<String> rarefactionCoefficientComboBox;
    private boolean stopped = true;
    private Label verticalScaleLabel;
    private ComboBox<String> verticalScaleComboBox;

    public GraphController(CheckBox autoscaleCheckBox, LineChart<Number, Number> graph, Label horizontalScaleLabel,
                           ComboBox<String> horizontalScaleComboBox, Process process, Label rarefactionCoefficientLabel,
                           ComboBox<String> rarefactionCoefficientComboBox, Label verticalScaleLabel,
                           ComboBox<String> verticalScaleComboBox) {

        this.autoscaleCheckBox = autoscaleCheckBox;
        this.graph = graph;
        this.horizontalScaleLabel = horizontalScaleLabel;
        this.horizontalScaleComboBox = horizontalScaleComboBox;
        this.process = process;
        this.rarefactionCoefficientLabel = rarefactionCoefficientLabel;
        this.rarefactionCoefficientComboBox = rarefactionCoefficientComboBox;
        this.verticalScaleLabel = verticalScaleLabel;
        this.verticalScaleComboBox = verticalScaleComboBox;

        graph.getData().add(graphModel.getGraphSeries());
        initScales();
    }

    private void initScales() {
        listen(autoscaleCheckBox);
        addRarefactionCoefficients();
        listenRarefactionCoefficients();
        addVerticalScales();
        listenVerticalScales();
        addHorizontalScales();
        listenHorizontalScales();
    }

    private void listen(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(observable -> {
            NumberAxis yAxis = (NumberAxis) graph.getYAxis();
            yAxis.setAutoRanging(checkBox.isSelected());
            horizontalScaleLabel.setDisable(checkBox.isSelected());
            horizontalScaleComboBox.setDisable(checkBox.isSelected());
            verticalScaleLabel.setDisable(checkBox.isSelected());
            verticalScaleComboBox.setDisable(checkBox.isSelected());

            if (!checkBox.isSelected()) {
                setHorizontalGraphBounds();
                setVerticalGraphBounds();
            }

            restartShow();
        });
    }

    private void setHorizontalGraphBounds() {
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
    }

    private void setVerticalGraphBounds() {
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
    }

    private void addRarefactionCoefficients() {
        ObservableList<String> coefficients = FXCollections.observableArrayList();

        coefficients.add("Все");
        coefficients.add("В 2 меньше");
        coefficients.add("В 5 меньше");
        coefficients.add("В 10 меньше");
        coefficients.add("В 15 меньше");
        coefficients.add("В 20 меньше");
        coefficients.add("В 25 меньше");

        rarefactionCoefficientComboBox.setItems(coefficients);
        rarefactionCoefficientComboBox.getSelectionModel().select(3);
    }

    private void listenRarefactionCoefficients() {
        rarefactionCoefficientComboBox.valueProperty().addListener(observable -> {
            String selection = rarefactionCoefficientComboBox.getSelectionModel().getSelectedItem();
            if (rarefactionCoefficientComboBox.getSelectionModel().getSelectedIndex() != 0) {
                String digits = selection.split(" ")[1].split(" ")[0];
                int coefficient = Integer.parseInt(digits);
                graphModel.setRarefactionCoefficient(coefficient);
            } else {
                graphModel.setRarefactionCoefficient(1);
            }

            restartShow();
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
            setVerticalGraphBounds();
            restartShow();
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
            setHorizontalGraphBounds();
            restartShow();
        });
    }

    public void showGraph(int slot, int channel) {
        double[] data = process.getData(slot - 1);
        graphModel.setFields(data, slot, channel - 1);


        new Thread(() -> {
            stopped = false;
            while (!process.isStopped()) {
//                System.out.printf("Data[0]: %f\n", data[0]);
                show(data, channel - 1);
                Utils.sleep(1000);
            }
        }).start();
    }

    public void show(double[] data, int channel) {

        int channels = 4; // количество каналов АЦП
        int rarefactionCoefficient = graphModel.getRarefactionCoefficient();
        double upperBoundOfHorizontalAxis = getUpperBoundOfHorizontalAxis();

        for (int index = channel; index < data.length && !process.isStopped(); index += channels * rarefactionCoefficient) {
            if (stopped) {
                break;
            }


            XYChart.Data<Number, Number> point = graphModel.getPoint(index);
            Runnable addPoint = () -> {
                if (!graphModel.getGraphSeries().getData().contains(point)) {
                    graphModel.getGraphSeries().getData().add(point);
                }
            };

            if ((double) point.getXValue() < upperBoundOfHorizontalAxis) {
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

    private double getUpperBoundOfHorizontalAxis() {
        String selection = horizontalScaleComboBox.getSelectionModel().getSelectedItem();
        String digits = selection.split(" ")[0];
        return Double.parseDouble(digits);
    }

    public void restartShow() {
        stopped = true;
        Utils.sleep(100);
        stopped = false;
    }

    public LineChart<Number, Number> getGraph() {
        return graph;
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
