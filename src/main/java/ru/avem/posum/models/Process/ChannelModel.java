package ru.avem.posum.models.Process;

import javafx.beans.property.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChannelModel {
    private final StringProperty name;
    private final StringProperty amplitudeColor;
    private final IntegerProperty amplitudeGraphNum;
    private final BooleanProperty amplitudeEnable;
    private final IntegerProperty amplitudeStatus;
    private final StringProperty responseAmplitude;
    private final StringProperty amplitude;
    private final StringProperty relativeResponseAmplitude;
    private final StringProperty dc;
    private final StringProperty frequencyColor;
    private final IntegerProperty frequencyGraphNum;
    private final BooleanProperty frequencyEnable;
    private final IntegerProperty frequencyStatus;
    private final StringProperty responseFrequency;
    private final StringProperty frequency;
    private final StringProperty relativeResponseFrequency;
    private final StringProperty rmsColor;
    private final IntegerProperty rmsGraphNum;
    private final BooleanProperty rmsEnable;
    private final IntegerProperty rmsStatus;
    private final StringProperty responseRms;
    private final StringProperty rms;
    private final StringProperty relativeResponseRms;
    private final StringProperty pValue;
    private final StringProperty iValue;
    private final StringProperty dValue;
    private final StringProperty chosenParameterIndex;

    private List<StringProperty> properties = new ArrayList<>();

    public ChannelModel(String name, Color amplitudeColor, Color frequencyColor, Color rmsColor, Color pColor, Color group5Color, Color group6Color) {
        this.name = new SimpleStringProperty(name);
        this.amplitudeColor = new SimpleStringProperty(String.format("%d, %d, %d",
                (amplitudeColor.getRed() * 255),
                (amplitudeColor.getGreen() * 255),
                (amplitudeColor.getBlue() * 255)));
        this.amplitudeGraphNum = new SimpleIntegerProperty(-1);
        this.amplitudeEnable = new SimpleBooleanProperty( false );
        this.amplitudeStatus = new SimpleIntegerProperty(3);
        this.responseAmplitude = new SimpleStringProperty("0");
        this.amplitude = new SimpleStringProperty("0");
        this.relativeResponseAmplitude = new SimpleStringProperty("0");
        this.dc = new SimpleStringProperty("0");
        this.frequencyColor = new SimpleStringProperty(String.format("%d, %d, %d",
                (frequencyColor.getRed() * 255),
                (frequencyColor.getGreen() * 255),
                (frequencyColor.getBlue() * 255)));
        this.frequencyGraphNum = new SimpleIntegerProperty(-1);
        this.frequencyEnable = new SimpleBooleanProperty( false );
        this.frequencyStatus = new SimpleIntegerProperty(3);
        this.responseFrequency = new SimpleStringProperty("0");
        this.frequency = new SimpleStringProperty("0");
        this.relativeResponseFrequency = new SimpleStringProperty("0");
        this.rmsColor = new SimpleStringProperty(String.format("%d, %d, %d",
                (rmsColor.getRed() * 255),
                (rmsColor.getGreen() * 255),
                (rmsColor.getBlue() * 255)));
        this.rmsGraphNum = new SimpleIntegerProperty(-1);
        this.rmsEnable = new SimpleBooleanProperty( false );
        this.rmsStatus = new SimpleIntegerProperty(3);
        this.responseRms = new SimpleStringProperty("0");
        this.rms = new SimpleStringProperty("0");
        this.relativeResponseRms = new SimpleStringProperty("0");
        this.pValue = new SimpleStringProperty("0");
        this.iValue = new SimpleStringProperty("0");
        this.dValue = new SimpleStringProperty("0");
        this.chosenParameterIndex = new SimpleStringProperty("-1");
        properties.addAll(Arrays.asList(this.name, this.amplitudeColor, this.responseAmplitude, this.amplitude,
                this.relativeResponseAmplitude, this.frequencyColor, this.responseFrequency, this.frequency,
                this.relativeResponseFrequency, this.rmsColor, this.responseRms, this.rms,
                this.relativeResponseRms, this.pValue, this.iValue, this.dValue));
    }

    public ChannelModel(String name) {
        this(name,Color.white, Color.BLACK, Color.white, Color.white, Color.white, Color.white);
    }
    public ChannelModel() {
        this(null,Color.white, Color.white, Color.white, Color.white, Color.white, Color.white);
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

    public String getAmplitudeColor() { return amplitudeColor.get(); }
    public StringProperty amplitudeColorProperty() { return amplitudeColor; }
    public void setAmplitudeColor(String amplitudeColor) { this.amplitudeColor.set(amplitudeColor); }

    public Integer getAmplitudeGraphNum() { return amplitudeGraphNum.get(); }
    public IntegerProperty amplitudeGraphNumProperty() { return amplitudeGraphNum; }
    public void setAmplitudeGraphNum(Integer graphNum) { this.amplitudeGraphNum.set(graphNum); }

    public Boolean getAmplitudeEnable() { return amplitudeEnable.get(); }
    public BooleanProperty amplitudeEnableProperty() { return amplitudeEnable; }
    public void setAmplitudeEnable(Boolean amplitudeEnable) { this.amplitudeEnable.set(amplitudeEnable); }

    public Integer getAmplitudeStatus() { return amplitudeStatus.get(); }
    public IntegerProperty amplitudeStatusProperty() { return amplitudeStatus; }
    public void setAmplitudeStatus(Integer amplitudeStatus) { this.amplitudeStatus.set(amplitudeStatus); }

    public String getResponseAmplitude() { return responseAmplitude.get(); }
    public StringProperty responseAmplitudeProperty() { return responseAmplitude; }
    public void setResponseAmplitude(String responseAmplitude) { this.responseAmplitude.set(responseAmplitude); }

    public String getAmplitude() { return amplitude.get(); }
    public StringProperty amplitudeProperty() { return amplitude; }
    public void setAmplitude(String amplitude) { this.amplitude.set(amplitude); }

    public String getRelativeResponseAmplitude() { return relativeResponseAmplitude.get(); }
    public StringProperty relativeResponseAmplitudeProperty() { return relativeResponseAmplitude; }
    public void setRelativeResponseAmplitude(String relativeResponseAmplitude) { this.relativeResponseAmplitude.set(relativeResponseAmplitude); }

    public String getDc() { return dc.get(); }
    public StringProperty dc() { return dc; }
    public void setDc(String dc) { this.dc.set(dc); }

    public String getFrequencyColor() { return frequencyColor.get(); }
    public StringProperty frequencyColorProperty() { return frequencyColor; }
    public void setFrequencyColor(String frequencyColor) { this.frequencyColor.set(frequencyColor); }

    public Integer getFrequencyGraphNum() { return frequencyGraphNum.get(); }
    public IntegerProperty frequencyGraphNumProperty() { return frequencyGraphNum; }
    public void setFrequencyGraphNum(Integer graphNum) { this.frequencyGraphNum.set(graphNum); }

    public Boolean getFrequencyEnable() { return frequencyEnable.get(); }
    public BooleanProperty frequencyEnableProperty() { return frequencyEnable; }
    public void setFrequencyEnable(Boolean frequencyEnable) { this.frequencyEnable.set(frequencyEnable); }

    public Integer getFrequencyStatus() { return frequencyStatus.get(); }
    public IntegerProperty frequencyStatusProperty() { return frequencyStatus; }
    public void setFrequencyStatus(Integer frequencyStatus) { this.frequencyStatus.set(frequencyStatus); }

    public String getResponseFrequency() { return responseFrequency.get(); }
    public StringProperty responseFrequencyProperty() { return responseFrequency; }
    public void setResponseFrequency(String responseFrequency) { this.responseFrequency.set(responseFrequency); }

    public String getFrequency() { return frequency.get(); }
    public StringProperty frequencyProperty() { return frequency; }
    public void setFrequency(String frequency) { this.frequency.set(frequency); }

    public String getRelativeResponseFrequency() { return relativeResponseFrequency.get(); }
    public StringProperty relativeResponseFrequencyProperty() { return relativeResponseFrequency; }
    public void setRelativeResponseFrequency(String relativeResponseFrequency) { this.relativeResponseFrequency.set(relativeResponseFrequency); }

    public String getRmsColor() { return rmsColor.get(); }
    public StringProperty rmsColorProperty() { return rmsColor; }
    public void setRmsColor(String rmsColor) { this.rmsColor.set(rmsColor); }

    public Integer getRmsGraphNum() { return rmsGraphNum.get(); }
    public IntegerProperty rmsGraphNumProperty() { return rmsGraphNum; }
    public void setRmsGraphNum(Integer graphNum) { this.rmsGraphNum.set(graphNum); }

    public Boolean getRmsEnable() { return rmsEnable.get(); }
    public BooleanProperty rmsEnableProperty() { return rmsEnable; }
    public void setRmsEnable(Boolean rmsEnable) { this.rmsEnable.set(rmsEnable); }

    public Integer getRmsStatus() { return rmsStatus.get(); }
    public IntegerProperty rmsStatusProperty() { return rmsStatus; }
    public void setRmsStatus(Integer rmsStatus) { this.rmsStatus.set(rmsStatus); }

    public String getResponseRms() { return responseRms.get(); }
    public StringProperty responseRmsProperty() { return responseRms; }
    public void setResponseRms(String responseRms) { this.responseRms.set(responseRms); }

    public String getRms() { return rms.get(); }
    public StringProperty rmsProperty() { return rms; }
    public void setRms(String rms) { this.rms.set(rms); }

    public String getRelativeResponseRms() { return relativeResponseRms.get(); }
    public StringProperty relativeResponseRmsProperty() { return relativeResponseRms; }
    public void setRelativeResponseRms(String relativeResponseRms) { this.relativeResponseRms.set(relativeResponseRms); }

    public String getpValue() { return pValue.get(); }
    public StringProperty pValueProperty() { return pValue; }
    public void setPvalue(String pValue) { this.pValue.set(pValue); }

    public String getiValue() { return iValue.get(); }
    public StringProperty iValueProperty() { return iValue; }
    public void setIvalue(String iValue) { this.iValue.set(iValue); }

    public String getdValue() { return dValue.get(); }
    public StringProperty dValueProperty() { return dValue; }
    public void setDvalue(String dValue) { this.dValue.set(dValue); }

    public String getChosenParameterIndex() { return chosenParameterIndex.get(); }
    public StringProperty chosenParameterIndexProperty() { return chosenParameterIndex; }
    public void setChosenParameterIndex(String chosenParameterIndex) { this.chosenParameterIndex.set(chosenParameterIndex); }
}
