package ru.avem.posum.models.Process;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;

import java.awt.*;

public class ChannelModel {
    private StringProperty name;
    private StringProperty amplitude;
    private StringProperty responseAmplitude;
    private StringProperty relativeResponseAmplitude;
    private StringProperty dc;
    private StringProperty frequency;
    private StringProperty responseFrequency;
    private StringProperty relativeResponseFrequency;
    private StringProperty rms;
    private StringProperty responseRms;
    private StringProperty relativeResponseRms;
    private StringProperty pValue;
    private StringProperty iValue;
    private StringProperty dValue;
    private StringProperty chosenParameterIndex;
    private CheckBox responseCheckBox;
    private ColorPicker colorPicker;
    private StringProperty responseColor;

    public ChannelModel(String name) {
        this.name = new SimpleStringProperty(name);
        responseAmplitude = new SimpleStringProperty("0");
        amplitude = new SimpleStringProperty("0");
        relativeResponseAmplitude = new SimpleStringProperty("0");
        dc = new SimpleStringProperty("0");
        responseFrequency = new SimpleStringProperty("0");
        frequency = new SimpleStringProperty("0");
        relativeResponseFrequency = new SimpleStringProperty("0");
        responseRms = new SimpleStringProperty("0");
        rms = new SimpleStringProperty("0");
        relativeResponseRms = new SimpleStringProperty("0");
        pValue = new SimpleStringProperty("0");
        iValue = new SimpleStringProperty("0");
        dValue = new SimpleStringProperty("0");
        chosenParameterIndex = new SimpleStringProperty("-1");
        responseCheckBox = createResponseCheckBox();
        colorPicker = createColorPicker();
        responseColor = new SimpleStringProperty(String.format("%d, %d, %d",
                Color.BLACK.getRed() * 255,
                Color.BLACK.getGreen() * 255,
                Color.BLACK.getBlue() * 255));
    }

    private CheckBox createResponseCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        return checkBox;
    }

    private ColorPicker createColorPicker() {
        ColorPicker colorPicker = new ColorPicker();

        colorPicker.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        colorPicker.setStyle("-fx-color-label-visible: false;");
        colorPicker.setOnAction((ActionEvent event) -> responseColor = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (colorPicker.getValue().getRed() * 255),
                (int) (colorPicker.getValue().getGreen() * 255),
                (int) (colorPicker.getValue().getBlue() * 255))));

        return colorPicker;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String description) {
        this.name.set(description);
    }

    public String getAmplitude() {
        return amplitude.get();
    }

    public StringProperty amplitudeProperty() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude.set(amplitude);
    }

    public String getResponseAmplitude() {
        return responseAmplitude.get();
    }

    public StringProperty responseAmplitudeProperty() {
        return responseAmplitude;
    }

    public void setResponseAmplitude(String responseAmplitude) {
        this.responseAmplitude.set(responseAmplitude);
    }

    public String getRelativeResponseAmplitude() {
        return relativeResponseAmplitude.get();
    }

    public StringProperty relativeResponseAmplitudeProperty() {
        return relativeResponseAmplitude;
    }

    public void setRelativeResponseAmplitude(String relativeResponseAmplitude) {
        this.relativeResponseAmplitude.set(relativeResponseAmplitude);
    }

    public String getDc() {
        return dc.get();
    }

    public void setDc(String dc) {
        this.dc.set(dc);
    }

    public String getFrequency() {
        return frequency.get();
    }

    public StringProperty frequencyProperty() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public String getResponseFrequency() {
        return responseFrequency.get();
    }

    public StringProperty responseFrequencyProperty() {
        return responseFrequency;
    }

    public void setResponseFrequency(String responseFrequency) {
        this.responseFrequency.set(responseFrequency);
    }

    public String getRelativeResponseFrequency() {
        return relativeResponseFrequency.get();
    }

    public StringProperty relativeResponseFrequencyProperty() {
        return relativeResponseFrequency;
    }

    public void setRelativeResponseFrequency(String relativeResponseFrequency) {
        this.relativeResponseFrequency.set(relativeResponseFrequency);
    }

    public String getRms() {
        return rms.get();
    }

    public StringProperty rmsProperty() {
        return rms;
    }

    public void setRms(String rms) {
        this.rms.set(rms);
    }

    public String getResponseRms() {
        return responseRms.get();
    }

    public StringProperty responseRmsProperty() {
        return responseRms;
    }

    public void setResponseRms(String responseRms) {
        this.responseRms.set(responseRms);
    }

    public String getRelativeResponseRms() {
        return relativeResponseRms.get();
    }

    public StringProperty relativeResponseRmsProperty() {
        return relativeResponseRms;
    }

    public void setRelativeResponseRms(String relativeResponseRms) {
        this.relativeResponseRms.set(relativeResponseRms);
    }

    public String getPValue() {
        return pValue.get();
    }

    public void setPvalue(String pValue) {
        this.pValue.set(pValue);
    }

    public String getIValue() {
        return iValue.get();
    }

    public void setIvalue(String iValue) {
        this.iValue.set(iValue);
    }

    public String getDValue() {
        return dValue.get();
    }

    public void setDvalue(String dValue) {
        this.dValue.set(dValue);
    }

    public String getChosenParameterIndex() {
        return chosenParameterIndex.get();
    }

    public void setChosenParameterIndex(String chosenParameterIndex) {
        this.chosenParameterIndex.set(chosenParameterIndex);
    }

    public CheckBox getResponseCheckBox() {
        return responseCheckBox;
    }

    public void setResponseCheckBox(CheckBox checkBox) {
        responseCheckBox = checkBox;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public String getResponseColor() {
        return String.valueOf(responseColor);
    }

    public void setResponseColor(StringProperty color) {
        responseColor = color;
    }

    public ObservableValue<HBox> getResponse() {
        HBox hBox = new HBox();

        hBox.getChildren().add(responseCheckBox);
        hBox.getChildren().add(colorPicker);
        hBox.setMaxHeight(22); // ограничение высоты в 22px необходимо для нормального отображения в ячейке

        return new ReadOnlyObjectWrapper<>(hBox);
    }
}
