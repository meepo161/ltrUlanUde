package ru.avem.posum.models.process;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ChannelModel {
    private long id;
    private String initialValue = "0.0";
    private StringProperty name;
    private StringProperty amplitude;
    private StringProperty responseAmplitude;
    private StringProperty relativeResponseAmplitude;
    private StringProperty dc;
    private StringProperty responseDc;
    private StringProperty relativeResponseDc;
    private StringProperty frequency;
    private StringProperty responseFrequency;
    private StringProperty relativeResponseFrequency;
    private StringProperty loadsCounter;
    private StringProperty responseLoadsCounter;
    private StringProperty rms;
    private StringProperty responseRms;
    private StringProperty relativeResponseRms;
    private StringProperty pCoefficient;
    private StringProperty iCoefficient;
    private StringProperty dCoefficient;
    private StringProperty chosenParameterIndex;
    private CheckBox responseCheckBox;
    private ColorPicker colorPicker;
    private StringProperty responseColor;

    public ChannelModel(String name) {
        this.name = new SimpleStringProperty(name);
        initResponseFields();
        pCoefficient = new SimpleStringProperty(initialValue);
        iCoefficient = new SimpleStringProperty(initialValue);
        dCoefficient = new SimpleStringProperty(initialValue);
        chosenParameterIndex = new SimpleStringProperty("-1");
        responseColor = new SimpleStringProperty(String.format("rgba(%d, %d, %d, %d);", 139, 0, 0, 1));
        responseCheckBox = createResponseCheckBox();
        colorPicker = createColorPicker();
    }

    private CheckBox createResponseCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        checkBox.setDisable(true);
        return checkBox;
    }

    private ColorPicker createColorPicker() {
        double red = Double.parseDouble(responseColor.get().split("rgba\\(")[1].split(", ")[0]);
        double green = Double.parseDouble(responseColor.get().split(", ")[1].split(", ")[0]);
        double blue = Double.parseDouble(responseColor.get().split(", ")[2].split(", ")[0]);
        double opacity = Double.parseDouble(responseColor.get().split(", ")[3].split("\\);")[0]);
        Color color = Color.color(red / 255.0, green / 255.0, blue / 255.0, opacity);
        ColorPicker colorPicker = new ColorPicker(color);

        colorPicker.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        colorPicker.setStyle("-fx-color-label-visible: false;");

        return colorPicker;
    }

    private void initResponseFields() {
        amplitude = new SimpleStringProperty(initialValue);
        responseAmplitude = new SimpleStringProperty(initialValue);
        relativeResponseAmplitude = new SimpleStringProperty(initialValue);
        dc = new SimpleStringProperty(initialValue);
        responseDc = new SimpleStringProperty(initialValue);
        relativeResponseDc = new SimpleStringProperty(initialValue);
        frequency = new SimpleStringProperty(initialValue);
        responseFrequency = new SimpleStringProperty(initialValue);
        relativeResponseFrequency = new SimpleStringProperty(initialValue);
        loadsCounter = new SimpleStringProperty("0");
        responseLoadsCounter = new SimpleStringProperty("0");
        rms = new SimpleStringProperty(initialValue);
        responseRms = new SimpleStringProperty(initialValue);
        relativeResponseRms = new SimpleStringProperty(initialValue);
    }

    public ChannelModel(long id, String name, String pCoefficient, String iCoefficient, String dCoefficient,
                        String chosenParameterIndex, String chosenParameterValue, String responseColor) {

        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.pCoefficient = new SimpleStringProperty(pCoefficient);
        this.iCoefficient = new SimpleStringProperty(iCoefficient);
        this.dCoefficient = new SimpleStringProperty(dCoefficient);
        this.chosenParameterIndex = new SimpleStringProperty(chosenParameterIndex);
        this.responseColor = new SimpleStringProperty(responseColor);
        initResponseFields();
        setChosenParameterValue(chosenParameterValue);
        responseCheckBox = createResponseCheckBox();
        colorPicker = createColorPicker();
    }

    private void setChosenParameterValue(String value) {
        switch (Integer.parseInt(chosenParameterIndex.get())) {
            case 0:
                setAmplitude(value);
                break;
            case 1:
                setDc(value);
                break;
            case 2:
                setFrequency(value);
                break;
        }
    }

    public void clearResponse() {
        setResponseAmplitude(initialValue);
        setRelativeResponseAmplitude(initialValue);
        setDc(initialValue);
        setResponseDc(initialValue);
        setRelativeResponseDc(initialValue);
        setResponseFrequency(initialValue);
        setRelativeResponseFrequency(initialValue);
        setLoadsCounter("0");
        setResponseLoadsCounter("0");
        setResponseRms(initialValue);
        setRelativeResponseRms(initialValue);
    }

    public StringProperty amplitudeProperty() {
        return amplitude;
    }

    public StringProperty dcProperty() {
        return dc;
    }

    public StringProperty frequencyProperty() {
        return frequency;
    }

    public String getAmplitude() {
        return amplitude.get().isEmpty() ? initialValue : amplitude.get();
    }

    public String getChosenParameterIndex() {
        return chosenParameterIndex.get();
    }

    public String getChosenParameterValue() {
        switch (Integer.parseInt(chosenParameterIndex.get())) {
            case 0:
                return amplitude.get();
            case 1:
                return dc.get();
            case 2:
                return frequency.get();
            default:
                return "0";
        }
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public String getDc() {
        return dc.get().isEmpty() ? initialValue : dc.get();
    }

    public String getDCoefficient() {
        return dCoefficient.get().isEmpty() ? "0" : dCoefficient.get();
    }

    public String getFrequency() {
        return frequency.get().isEmpty() ? initialValue : frequency.get();
    }

    public String getICoefficient() {
        return iCoefficient.get().isEmpty() ? "0" : iCoefficient.get();
    }

    public long getId() {
        return id;
    }

    public String getLoadsCounter() {
        return loadsCounter.get();
    }

    public String getName() {
        return name.get();
    }

    public String getPCoefficient() {
        return pCoefficient.get().isEmpty() ? "0" : pCoefficient.get();
    }

    public String getRelativeResponseAmplitude() {
        return relativeResponseAmplitude.get();
    }

    public String getRelativeResponseDc() {
        return relativeResponseDc.get();
    }

    public String getRelativeResponseFrequency() {
        return relativeResponseFrequency.get();
    }

    public String getResponseAmplitude() {
        return responseAmplitude.get();
    }

    public String getResponseDc() {
        return responseDc.get();
    }

    public String getResponseLoadsCounter() {
        return responseLoadsCounter.get();
    }

    public CheckBox getResponseCheckBox() {
        return responseCheckBox;
    }

    public String getResponseColor() {
        responseColor = new SimpleStringProperty(String.format("rgba(%d, %d, %d, %s);",
                (int) (255 * colorPicker.getValue().getRed()),
                (int) (255 * colorPicker.getValue().getGreen()),
                (int) (255 * colorPicker.getValue().getBlue()),
                String.valueOf(colorPicker.getValue().getOpacity()).replace(",", ".")));

        return responseColor.getValue();
    }

    public String getResponseFrequency() {
        return responseFrequency.get();
    }

    public String getResponseRms() {
        return responseRms.get();
    }

    public String getRms() {
        return rms.get().isEmpty() ? initialValue : rms.get();
    }

    public int getSlot() {
        return Integer.parseInt(this.name.getValue().split("слот ")[1].split("\\)")[0]);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty relativeAmplitudeProperty() {
        return relativeResponseAmplitude;
    }

    public StringProperty relativeDcProperty() {
        return relativeResponseDc;
    }

    public StringProperty relativeFrequencyProperty() {
        return relativeResponseFrequency;
    }

    public StringProperty responseAmplitudeProperty() {
        return responseAmplitude;
    }

    public StringProperty responseDcProperty() {
        return responseDc;
    }

    public StringProperty responseFrequencyProperty() {
        return responseFrequency;
    }

    public StringProperty responseLoadsCounterProperty() {
        return responseLoadsCounter;
    }

    public StringProperty responseRmsProperty() {
        return responseRms;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude.set(amplitude);
    }

    public void setChosenParameterIndex(String chosenParameterIndex) {
        this.chosenParameterIndex.set(chosenParameterIndex);
    }

    public void setDc(String dc) {
        this.dc.set(dc);
    }

    public void setDCoefficient(String dValue) {
        this.dCoefficient.set(dValue);
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public void setICoefficient(String iValue) {
        this.iCoefficient.set(iValue);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLoadsCounter(String value) {
        loadsCounter.set(value);
    }

    public void setName(String description) {
        this.name.set(description);
    }

    public void setPCoefficient(String pValue) {
        this.pCoefficient.set(pValue);
    }

    public void setRelativeResponseAmplitude(String relativeResponseAmplitude) {
        this.relativeResponseAmplitude.set(relativeResponseAmplitude);
    }

    public void setRelativeResponseDc(String relativeResponseDc) {
        this.relativeResponseDc.set(relativeResponseDc);
    }

    public void setRelativeResponseFrequency(String relativeResponseFrequency) {
        this.relativeResponseFrequency.set(relativeResponseFrequency);
    }

    public void setRelativeResponseRms(String relativeResponseRms) {
        this.relativeResponseRms.set(relativeResponseRms);
    }

    public void setResponseAmplitude(String responseAmplitude) {
        this.responseAmplitude.set(responseAmplitude);
    }

    public void setResponseColor(String responseColor) {
        this.responseColor.set(responseColor);
    }

    public void setResponseDc(String responseDc) {
        this.responseDc.set(responseDc);
    }

    public void setResponseLoadsCounter(String responseLoadsCounter) {
        this.responseLoadsCounter.set(responseLoadsCounter);
    }

    public void setResponseFrequency(String responseFrequency) {
        this.responseFrequency.set(responseFrequency);
    }

    public void setResponseRms(String responseRms) {
        this.responseRms.set(responseRms);
    }

    public void setRms(String rms) {
        this.rms.set(rms);
    }

    public ObservableValue<HBox> getResponse() {
        HBox hBox = new HBox();

        hBox.getChildren().add(responseCheckBox);
        hBox.getChildren().add(colorPicker);
        hBox.setMaxHeight(22); // ограничение высоты в 22px необходимо для нормального отображения в ячейке

        return new ReadOnlyObjectWrapper<>(hBox);
    }
}
