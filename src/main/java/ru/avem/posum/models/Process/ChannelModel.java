package ru.avem.posum.models.Process;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import ru.avem.posum.controllers.Process.RegulatorParameters;

import java.util.Optional;

public class ChannelModel {
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
    private StringProperty relativeResponseLoadsCounter;
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
    private Optional<RegulatorParameters> regulatorEnabled;

    public ChannelModel(String name) {
        this.name = new SimpleStringProperty(name);
        amplitude = new SimpleStringProperty("0");
        responseAmplitude = new SimpleStringProperty("0");
        relativeResponseAmplitude = new SimpleStringProperty("0");
        dc = new SimpleStringProperty("0");
        responseDc = new SimpleStringProperty("0");
        relativeResponseDc = new SimpleStringProperty("0");
        frequency = new SimpleStringProperty("0");
        responseFrequency = new SimpleStringProperty("0");
        relativeResponseFrequency = new SimpleStringProperty("0");
        loadsCounter = new SimpleStringProperty("0");
        responseLoadsCounter = new SimpleStringProperty("0");
        relativeResponseLoadsCounter = new SimpleStringProperty("0");
        rms = new SimpleStringProperty("0");
        responseRms = new SimpleStringProperty("0");
        relativeResponseRms = new SimpleStringProperty("0");
        pCoefficient = new SimpleStringProperty("0");
        iCoefficient = new SimpleStringProperty("0");
        dCoefficient = new SimpleStringProperty("0");
        chosenParameterIndex = new SimpleStringProperty("-1");
        responseCheckBox = createResponseCheckBox();
        colorPicker = createColorPicker();
        responseColor = new SimpleStringProperty(String.format("rgba(%d, %d, %d, 1.0);",
                (int) Color.RED.getRed() * 255,
                (int) Color.RED.getGreen() * 255,
                (int) Color.RED.getBlue() * 255));
    }

    private CheckBox createResponseCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        return checkBox;
    }

    private ColorPicker createColorPicker() {
        ColorPicker colorPicker = new ColorPicker(Color.DARKRED);

        colorPicker.setMaxHeight(20); // ограничение высоты в 20px для нормального отображения в ячейке таблицы
        colorPicker.setStyle("-fx-color-label-visible: false;");

        return colorPicker;
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
        return amplitude.get();
    }

    public String getChosenParameterIndex() {
        return chosenParameterIndex.get();
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public String getDc() {
        return dc.get();
    }

    public String getDcoefficient() {
        return dCoefficient.get();
    }

    public String getFrequency() {
        return frequency.get();
    }

    public String getICoefficient() {
        return iCoefficient.get();
    }

    public String getLoadsCounter() {
        return loadsCounter.get();
    }

    public String getName() {
        return name.get();
    }

    public String getPcoefficient() {
        return pCoefficient.get();
    }

    public Optional<RegulatorParameters> isRegulatorEnabled() {
        return regulatorEnabled;
    }

    public String getResponseAmplitude() {
        return responseAmplitude.get();
    }

    public String getResponseDc() {
        return responseDc.get();
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
        return rms.get();
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

    public void setDcoefficient(String dValue) {
        this.dCoefficient.set(dValue);
    }

    public void setFrequency(String frequency) {
        this.frequency.set(frequency);
    }

    public void setIcoefficient(String iValue) {
        this.iCoefficient.set(iValue);
    }

    public void setName(String description) {
        this.name.set(description);
    }

    public void setPcoefficient(String pValue) {
        this.pCoefficient.set(pValue);
    }

    public void setRegulatorEnabled(Optional<RegulatorParameters> regulatorEnabled) {
        this.regulatorEnabled = regulatorEnabled;
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
