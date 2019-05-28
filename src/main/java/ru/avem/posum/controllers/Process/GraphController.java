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
    private ProcessController processController;
    private Label rarefactionCoefficientLabel;
    private ComboBox<String> rarefactionCoefficientComboBox;
    private boolean stopped = true;
    private Thread showingThread;
    private Label verticalScaleLabel;
    private ComboBox<String> verticalScaleComboBox;

    private int channel;
    private int slot;

    public GraphController(CheckBox autoscaleCheckBox, LineChart<Number, Number> graph, Label horizontalScaleLabel,
                           ComboBox<String> horizontalScaleComboBox, Process process, Label rarefactionCoefficientLabel,
                           ComboBox<String> rarefactionCoefficientComboBox, Label verticalScaleLabel,
                           ComboBox<String> verticalScaleComboBox, ProcessController processController) {

        this.autoscaleCheckBox = autoscaleCheckBox;
        this.graph = graph;
        this.horizontalScaleLabel = horizontalScaleLabel;
        this.horizontalScaleComboBox = horizontalScaleComboBox;
        this.process = process;
        this.rarefactionCoefficientLabel = rarefactionCoefficientLabel;
        this.rarefactionCoefficientComboBox = rarefactionCoefficientComboBox;
        this.verticalScaleLabel = verticalScaleLabel;
        this.verticalScaleComboBox = verticalScaleComboBox;
        this.processController = processController;

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
            processController.getStatusBarLine().clearStatusBar();
            processController.getStatusBarLine().toggleProgressIndicator(false);
            processController.getStatusBarLine().setStatusOfProgress("Обработка графика");

            new Thread(() -> {
                if (rarefactionCoefficientComboBox.getSelectionModel().getSelectedIndex() != 0) {
                    String digits = selection.split(" ")[1].split(" ")[0];
                    int coefficient = Integer.parseInt(digits);
                    graphModel.setRarefactionCoefficient(coefficient);
                } else {
                    graphModel.setRarefactionCoefficient(1);
                }

                restartShow();
                Utils.sleep(400); // ожидание начала отрисовки графика
                processController.getStatusBarLine().toggleProgressIndicator(true);
                processController.getStatusBarLine().clearStatusBar();
            }).start();
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

    public void setFields(int slot, int channel) {
        this.slot = slot - 1;
        this.channel = channel - 1;
    }

    public void showGraph() {
        stopped = false;
        System.out.println("Thread started");

        showingThread = new Thread(() -> {
            while (!process.isStopped()) {
                double[] data = process.getData(slot);
                graphModel.setFields(data, slot, channel);
                graphModel.getGraphSeries().getData().clear();

                show(data, channel);
                if (stopped) {
                    break;
                }
                Utils.sleep(1000);
            }
        });

        showingThread.start();
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
            } else if ((double) point.getXValue() >= getUpperBoundOfHorizontalAxis()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                break;
            }
        }
    }

    private double getUpperBoundOfHorizontalAxis() {
        String selection = horizontalScaleComboBox.getSelectionModel().getSelectedItem();
        String valueName = selection.split(" ")[1].split("/дел")[0];
        String digits = selection.split(" ")[0];
        double uppedBound = Double.parseDouble(digits);

        switch (valueName) {
            case "мс":
                uppedBound *= 0.001 * 10;
                break;
            case "с":
                uppedBound *= 10;
                break;
            case "мин":
                uppedBound *= 60;
                break;
            case "ч":
                uppedBound *= 3600;
                break;
            case "д":
                uppedBound *= 86_400;
                break;
        }

        return uppedBound;
    }

    public void restartShow() {
        stopped = true;
        showingThread.interrupt();
        Utils.sleep(100);
        graphModel.getGraphSeries().getData().clear();
        stopped = false;
        showGraph();
    }

    public LineChart<Number, Number> getGraph() {
        return graph;
    }

    public CheckBox getAutoscaleCheckBox() {
        return autoscaleCheckBox;
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public Label getHorizontalScaleLabel() {
        return horizontalScaleLabel;
    }

    public ComboBox<String> getHorizontalScaleComboBox() {
        return horizontalScaleComboBox;
    }

    public Label getRarefactionCoefficientLabel() {
        return rarefactionCoefficientLabel;
    }

    public ComboBox<String> getRarefactionCoefficientComboBox() {
        return rarefactionCoefficientComboBox;
    }

    public boolean isShowingThreadStopped() {
        return showingThread == null;
    }

    public void stopShowingThread() {
        stopped = true;
        Utils.sleep(100);
        showingThread.interrupt();
    }

    public Label getVerticalScaleLabel() {
        return verticalScaleLabel;
    }

    public ComboBox<String> getVerticalScaleComboBox() {
        return verticalScaleComboBox;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
