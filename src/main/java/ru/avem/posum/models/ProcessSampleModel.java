package ru.avem.posum.models;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class ProcessSampleModel {

    private ObservableList<ProcessSample> processSampleData = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> graphSeries_Channel_1 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_2 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_3 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_4 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_5 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_6 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_7 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_8 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_9 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_10 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_11 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_12 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_13 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_14 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_15 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_16 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_17 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_18 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_19 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_20 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_21 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_22 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_23 = new XYChart.Series<>();
    private XYChart.Series<Number, Number> graphSeries_Channel_24 = new XYChart.Series<>();

    private long testId = 0;

    public ProcessSampleModel() {
        this.testId = 0;
        loadData(this.testId);
    }

    public void resetData() {
        processSampleData.clear();
    }

    public void loadData(long idTest) {
        processSampleData.add(new ProcessSample("ADC* DAC*"));
    }

    public void testData() {
        for(int i = 0; i < processSampleData.size(); i++) {
            processSampleData.get(i).setGroup1Status(i%7);
            processSampleData.get(i).setGroup2Status((i+1)%7);
            processSampleData.get(i).setGroup3Status(7+(i)%3);
            processSampleData.get(i).setGroup4Status((i+3)%7);
            processSampleData.get(i).setGroup1Value1(String.valueOf(i)+" %");
            processSampleData.get(i).setGroup2Value1(String.valueOf(i+1)+" В");
            processSampleData.get(i).setGroup3Value1(String.valueOf(i+2)+"234 %\n"+String.valueOf(i+3)+"123 В");
        }
    }

    public void makeHeaderWrappable(TableColumn col) {
        Label label = new Label(col.getText());
        label.setStyle("-fx-padding: 8px;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        StackPane stack = new StackPane();
        stack.getChildren().add(label);
        stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
        label.prefWidthProperty().bind(stack.prefWidthProperty());
        col.setGraphic(stack);
    }

    public String setStyleByCode(int code) {
        switch (code) {
            case 0: return "-fx-background-color: black; -fx-text-background-color: grey; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 1: return "-fx-background-color: black; -fx-text-background-color: white; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 2: return "-fx-background-color: black; -fx-text-background-color: yellow; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 3: return "-fx-background-color: black; -fx-text-background-color: #99d777; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 4: return "-fx-background-color: black; -fx-text-background-color: green; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 5: return "-fx-background-color: black; -fx-text-background-color: red; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 6: return "-fx-background-color: black; -fx-text-background-color: blue; -fx-alignment: CENTER-RIGHT; -fx-border-width: 0.0em;";
            case 7: return "-fx-background-color: black; -fx-text-background-color: yellow; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            case 8: return "-fx-background-color: black; -fx-text-background-color: green; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            case 9: return "-fx-background-color: black; -fx-text-background-color: red; -fx-alignment: CENTER-RIGHT; -fx-font-size: 8px; -fx-border-width: 0.0em; ";
            default: return null;
        }
    }

    public void SetProcessSampleColumnFunction(TableColumn<ProcessSample, String> columnProcessSample) {
        makeHeaderWrappable(columnProcessSample);
        columnProcessSample.setCellFactory(column -> {
            return new TableCell<ProcessSample, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) { //If the cell is empty
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setGraphic(null);

                    ProcessSample row = null;

                    if(getIndex() > -1 && getIndex() < getTableView().getItems().size()) {
                        row = getTableView().getItems().get(getIndex());
                    }
                    int colorRow = 0;
                    if (row != null) {
                        String groupNum = this.getTableColumn().getId().substring(5 , 6);
                        switch(Integer.parseInt(groupNum)) {
                            case 1: colorRow = row.getGroup1Status(); break;
                            case 2: colorRow = row.getGroup2Status(); break;
                            case 3: colorRow = row.getGroup3Status(); break;
                            case 4: colorRow = row.getGroup4Status(); break;
                            case 5: colorRow = row.getGroup5Status(); break;
                            case 6: colorRow = row.getGroup6Status(); break;
                            default: colorRow = 0;
                        }
                    }
                    setStyle(setStyleByCode(colorRow));
                }
            };
        });
    }

    public void SetProcessSampleTableFunction(TableView<ProcessSample> newTableProcessSample) {
        /*newTableProcessSample.setRowFactory((TableView<ProcessSample> paramP) -> new TableRow<ProcessSample>() {
            @Override
            protected void updateItem(ProcessSample row, boolean paramBoolean) {
                 if (row != null) {
                     String newText = row.getMainText().substring(3 , 4);
                     int x = Integer.parseInt(newText);
                     setStyle(setStyleByCode(x));
                } else {
                    setStyle(null);
                }
                super.updateItem(row, paramBoolean);
            }
        });*/
    }

    public ObservableList<ProcessSample> getProcessSampleData() {
        return processSampleData;
    }

    public void initProcessSampleData(TableView<ProcessSample> newTableProcessSample) {
        newTableProcessSample.setItems(this.getProcessSampleData());
    }
    public void fitTable(TableView<ProcessSample> newTableProcessSample) {
        int countItems = newTableProcessSample.getItems().size();
        double heightRow = 24;
        double heightTable = 49+(countItems*heightRow);
        newTableProcessSample.setPrefHeight(heightTable);
        newTableProcessSample.setMaxHeight(heightTable);
        newTableProcessSample.setMinHeight(heightTable);
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
        graphSeries_Channel_1.setName("ch1");
        graphSeries_Channel_2.setName("ch2");
        lineChart.getData().add(graphSeries_Channel_1);
        lineChart.getData().add(graphSeries_Channel_2);
    }

    public void chartAdd() {
        int x = 255;
        double arr1[] = new double[x];
        double arr2[] = new double[x];
        for (int i = 0; i < x; i++) {
            arr1[i] = Math.sin(i);
            arr2[i] = Math.cos(i);
        }
        fillSeries(arr1, graphSeries_Channel_1);
        fillSeries(arr2, graphSeries_Channel_2);
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public void setLineToProcessSample(String mainText) {
        processSampleData.add(new ProcessSample(mainText));
    }

    public void clearLineProcessSample(int lineId) {
        processSampleData.clear();
    }
}
