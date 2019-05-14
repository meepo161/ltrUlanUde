package ru.avem.posum.models.Process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class ProcessSampleModel {
    private TableView<ProcessSample> tableSample;
    private ObservableList<ProcessSample> processSampleData = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> graphSeries_Channel[] = new XYChart.Series[24];
    private Boolean graphSeries_Channel_Enabled[] = new Boolean[24];
    private int  currentIndex = 0;

    private LineChart<Number, Number> pLineChart;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public ProcessSampleModel() {
    }

    public void resetData() {
        processSampleData.clear();
    }

    public void loadData(long idTest) {
        int index = processSampleData.size() + 1;
        processSampleData.add(new ProcessSample("АЦП"+String.valueOf(index) +" ЦАП"+String.valueOf(index)));
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
//                    setStyle(setStyleByCode(colorRow));
                }
            };
        });
    }

    public void SetProcessSampleColumnColorFunction(TableColumn<ProcessSample, Void> columnProcessSample) {
        makeHeaderWrappable(columnProcessSample);
        Callback<TableColumn<ProcessSample, Void>, TableCell<ProcessSample, Void>> cellFactory = new Callback<TableColumn<ProcessSample, Void>, TableCell<ProcessSample, Void>>() {
            @Override
            public TableCell<ProcessSample, Void> call(final TableColumn<ProcessSample, Void> param) {
                final TableCell<ProcessSample, Void> cell = new TableCell<ProcessSample, Void>() {
                    private final CheckBox chBox = new CheckBox("");
                    {
                        chBox.setMaxHeight(20);
                        chBox.setOnAction((ActionEvent event) -> {
                            String groupNum = this.getTableColumn().getId().substring(5 , 6);
                            ProcessSample row = getTableView().getItems().get(getIndex());
                            //System.out.println("CheckBox: " + row + " groupNum:" + groupNum + " isSelected:" + chBox.isSelected());
                            switch(Integer.parseInt(groupNum)) {
                                case 1: row.setGroup1Enable(chBox.isSelected());
                                        if(row.getGroup1GraphNum() < 0) {
                                            row.setGroup1GraphNum(chartsAdd());
                                            chartSetColor(row.getGroup1GraphNum(), row.getGroup1Color());
                                        }
                                        chartSetEnabled(row.getGroup1GraphNum(), chBox.isSelected());
                                        break;
                                case 2: row.setGroup2Enable(chBox.isSelected());
                                    if(row.getGroup2GraphNum() < 0) {
                                        row.setGroup2GraphNum(chartsAdd());
                                        chartSetColor(row.getGroup2GraphNum(), row.getGroup2Color());
                                    }
                                    chartSetEnabled(row.getGroup2GraphNum(), chBox.isSelected());
                                    break;
                                case 3: row.setGroup3Enable(chBox.isSelected());
                                    if(row.getGroup3GraphNum() < 0) {
                                        row.setGroup3GraphNum(chartsAdd());
                                        chartSetColor(row.getGroup3GraphNum(), row.getGroup3Color());
                                    }
                                    chartSetEnabled(row.getGroup3GraphNum(), chBox.isSelected());
                                    break;
                                case 4: row.setGroup4Enable(chBox.isSelected());
                                    if(row.getGroup4GraphNum() < 0) {
                                        row.setGroup4GraphNum(chartsAdd());
                                        chartSetColor(row.getGroup4GraphNum(), row.getGroup4Color());
                                    }
                                    chartSetEnabled(row.getGroup4GraphNum(), chBox.isSelected());
                                    break;
                                case 5: row.setGroup5Enable(chBox.isSelected());
                                    if(row.getGroup5GraphNum() < 0) {
                                        row.setGroup5GraphNum(chartsAdd());
                                        chartSetColor(row.getGroup5GraphNum(), row.getGroup5Color());
                                    }
                                    chartSetEnabled(row.getGroup5GraphNum(), chBox.isSelected());
                                    break;
                                case 6: row.setGroup6Enable(chBox.isSelected());
                                    if(row.getGroup6GraphNum() < 0) {
                                        row.setGroup6GraphNum(chartsAdd());
                                        chartSetColor(row.getGroup6GraphNum(), row.getGroup6Color());
                                    }
                                    chartSetEnabled(row.getGroup6GraphNum(), chBox.isSelected());
                                    break;
                                default:
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


                            ProcessSample row = getTableView().getItems().get(getIndex());
                            System.out.println("ColorPicker: " + row + " groupNum:" + groupNum + " isColor:" + colorValue);
                            switch(Integer.parseInt(groupNum)) {
                                case 1: row.setGroup1Color(colorValue);
                                        chartSetColor(row.getGroup1GraphNum(), row.getGroup1Color());break;
                                case 2: row.setGroup2Color(colorValue);
                                        chartSetColor(row.getGroup2GraphNum(), row.getGroup2Color());break;
                                case 3: row.setGroup3Color(colorValue);
                                        chartSetColor(row.getGroup3GraphNum(), row.getGroup3Color());break;
                                case 4: row.setGroup4Color(colorValue);
                                        chartSetColor(row.getGroup4GraphNum(), row.getGroup4Color());break;
                                case 5: row.setGroup5Color(colorValue);
                                        chartSetColor(row.getGroup5GraphNum(), row.getGroup5Color());break;
                                case 6: row.setGroup6Color(colorValue);
                                        chartSetColor(row.getGroup6GraphNum(), row.getGroup6Color());break;
                                default: break;
                            }
                        });
                    }
                    private final HBox hBox = new HBox();
                    {
                        hBox.setMaxHeight(22);
                        hBox.getChildren().add(chBox);
                        hBox.getChildren().add(colorPicker);
//                        hBox.setStyle(setStyleByCode(0));
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
//                            setStyle(setStyleByCode(0));
                        }
                    }
                };
                return cell;
            }
        };

        columnProcessSample.setCellFactory(cellFactory);
    }

    public ObservableList<ProcessSample> getProcessSampleData() {
        return processSampleData;
    }

    public void initProcessSampleData(TableView<ProcessSample> newTableProcessSample) {
        tableSample = newTableProcessSample;
        tableSample.setItems(this.getProcessSampleData());
    }

    public void fitTable() {
        if(tableSample != null) {
            int countItems = tableSample.getItems().size();
            double heightRow = 24;
            double heightTable = 49 + (countItems * heightRow);
//            tableSample.setPrefHeight(heightTable);
//            tableSample.setMaxHeight(heightTable);
//            tableSample.setMinHeight(heightTable);
        }
    }

    public void setXAxis(double xLength) {
        if(pLineChart != null) {
            NumberAxis pNumAxis = (NumberAxis)pLineChart.getXAxis();
            pNumAxis.setAutoRanging(false);
            pNumAxis.setUpperBound(xLength);
            pNumAxis.setTickUnit(xLength/10);

            //pLineChart.getXAxis().ra

            //(xLength);
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
        pLineChart = lineChart;
        pLineChart.setLegendVisible(false);
        pLineChart.setAnimated(false);
    }
    // метод для получения номера линии с графика, и инициализация ее.
    private int chartsAdd() {
        graphSeries_Channel[currentIndex] = new XYChart.Series<>();
        graphSeries_Channel[currentIndex].setName("ch"+String.valueOf(currentIndex));
        graphSeries_Channel_Enabled[currentIndex] = true;
        pLineChart.getData().add(graphSeries_Channel[currentIndex]);
        currentIndex++;
        return (currentIndex-1);
    }

    private void chartSetEnabled(int index, Boolean Status) {
        if(!Status) {
            graphSeries_Channel[index].getData().clear();
            graphSeries_Channel_Enabled[index] = false;
        } else {
            graphSeries_Channel_Enabled[index] = true;
        }
    }

    private void chartSetColor(int index, String color) {
        if(index >= 0) {
            graphSeries_Channel[index].getNode().setStyle("-fx-stroke: rgb(" + color + ");");
        }
    }

    public void chartClearData(int index){
        if(graphSeries_Channel_Enabled[index]) {
            graphSeries_Channel[index].getData().clear();
        }
    }

    public void chartSetData(int index, double array[]){
        if(graphSeries_Channel_Enabled[index]) {
            fillSeries(array, graphSeries_Channel[index]);
        }
    }

    public void chartAddData(int index, double value, double time ){
        if(graphSeries_Channel_Enabled[index]) {
            if(graphSeries_Channel[index].getData() != null) {
                Platform.runLater(() -> {
                    graphSeries_Channel[index].getData().add(new XYChart.Data<>(time, value));
                });

            } else {
                List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
                intermediateList.add(new XYChart.Data<>(time, value));
                Platform.runLater(() -> {
                    graphSeries_Channel[index].getData().clear();
                    graphSeries_Channel[index].getData().addAll(intermediateList);
                });
           }
        }
    }


    public void chartAdd() {
        double arr1[] = new double[255];
        for (int j = 0; j < currentIndex; j++) {
            for (int i = 0; i < 255; i++) {
                arr1[i] = Math.sin((i + j)*0.1);
            }
            chartSetData(j, arr1);
        }
    }

    public void setLineToProcessSample(String mainText) {
        processSampleData.add(new ProcessSample(mainText));
    }

    public void clearLineProcessSample(int lineId) {
        processSampleData.clear();
    }
}
