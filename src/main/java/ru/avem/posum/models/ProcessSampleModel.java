package ru.avem.posum.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class ProcessSampleModel {

    private ObservableList<ProcessSample> processSampleData = FXCollections.observableArrayList();
    private long testId = 0;

    public ProcessSampleModel() {
        this.testId = 0;
        loadData(this.testId);
    }

    public void loadData(long idTest) {
        processSampleData.add(new ProcessSample("ADC1 DAC1"));
        processSampleData.add(new ProcessSample("ADC2 DAC2"));
        processSampleData.add(new ProcessSample("ADC3 DAC3"));
        processSampleData.add(new ProcessSample("ADC4 DAC4"));
        processSampleData.add(new ProcessSample("ADC5 DAC5"));
        processSampleData.add(new ProcessSample("ADC6 DAC6"));
    }

    public String setStyleByCode(int code) {
        switch (code) {
            case 1: return "-fx-background-color: white; -fx-text-background-color: black;";
            case 2: return "-fx-background-color: yellow; -fx-text-background-color: black;";
            case 3: return "-fx-background-color: skyblue; -fx-text-background-color: black;";
            case 4: return "-fx-background-color: green; -fx-text-background-color: black;";
            case 5: return "-fx-background-color: red; -fx-text-background-color: black;";
            case 6: return "-fx-background-color: black; -fx-text-background-color: white;";
            default: return null;
        }
    }

    public void SetProcessSampleTableFunction(TableView<ProcessSample> newTableProcessSample) {
        newTableProcessSample.setRowFactory((TableView<ProcessSample> paramP) -> new TableRow<ProcessSample>() {
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
        });

    }

    public ObservableList<ProcessSample> getProcessSampleData() {
        return processSampleData;
    }

    public void initProcessSampleData(TableView<ProcessSample> newTableProcessSample) {
        newTableProcessSample.setItems(this.getProcessSampleData());
    }
    public void fitTable(TableView<ProcessSample> newTableProcessSample) {
        int countItems = newTableProcessSample.getItems().size();
        double heightRow = 49;

        newTableProcessSample.setPrefHeight((countItems)*heightRow);
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
