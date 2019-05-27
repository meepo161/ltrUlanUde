package ru.avem.posum.controllers.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import ru.avem.posum.models.Process.ChannelModel;

public class GraphController {
    private CheckBox autoscaleCheckBox;
    private ChannelModel channelModel;
    private LineChart<Number, Number> graph;
    private Label horizontalScaleLabel;
    private ComboBox<String> horizontalScaleComboBox;
    private Label verticalScaleLabel;
    private ComboBox<String> verticalScaleComboBox;

    public GraphController(CheckBox autoscaleCheckBox, LineChart<Number, Number> graph,
                           Label horizontalScaleLabel, ComboBox<String> horizontalScaleComboBox,
                           Label verticalScaleLabel, ComboBox<String> verticalScaleComboBox) {

        this.autoscaleCheckBox = autoscaleCheckBox;
        this.graph = graph;
        this.horizontalScaleLabel = horizontalScaleLabel;
        this.horizontalScaleComboBox = horizontalScaleComboBox;
        this.verticalScaleLabel = verticalScaleLabel;
        this.verticalScaleComboBox = verticalScaleComboBox;

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
}
