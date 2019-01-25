package ru.avem.posum.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class ProcessSampleModel {

    private ObservableList<ProcessSample> processSampleData = FXCollections.observableArrayList();
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
