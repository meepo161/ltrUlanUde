package ru.avem.posum.models.Process;

import javafx.beans.property.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PairModel {
    private final StringProperty name;
    private final StringProperty amplitudeColor;
    private final IntegerProperty amplitudeGraphNum;
    private final BooleanProperty amplitudeEnable;
    private final IntegerProperty amplitudeStatus;
    private final StringProperty responseAmplitude;
    private final StringProperty amplitude;
    private final StringProperty relativeResponseAmplitude;
    private final StringProperty frequencyColor;
    private final IntegerProperty frequencyGraphNum;
    private final BooleanProperty frequencyEnable;
    private final IntegerProperty frequencyStatus;
    private final StringProperty responseFrequency;
    private final StringProperty frequency;
    private final StringProperty relativeResponseFrequency;
    private final StringProperty phaseColor;
    private final IntegerProperty phaseGraphNum;
    private final BooleanProperty phaseEnable;
    private final IntegerProperty phaseStatus;
    private final StringProperty responsePhase;
    private final StringProperty phase;
    private final StringProperty relativeResponsePhase;
    private final StringProperty pValue;
    private final StringProperty iValue;
    private final StringProperty dValue;

    private List<StringProperty> properties = new ArrayList<>();

    public PairModel(String name, Color amplitudeColor, Color frequencyColor, Color phaseColor, Color pColor, Color group5Color, Color group6Color) {
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
        this.phaseColor = new SimpleStringProperty(String.format("%d, %d, %d",
                (phaseColor.getRed() * 255),
                (phaseColor.getGreen() * 255),
                (phaseColor.getBlue() * 255)));
        this.phaseGraphNum = new SimpleIntegerProperty(-1);
        this.phaseEnable = new SimpleBooleanProperty( false );
        this.phaseStatus = new SimpleIntegerProperty(3);
        this.responsePhase = new SimpleStringProperty("0");
        this.phase = new SimpleStringProperty("0");
        this.relativeResponsePhase = new SimpleStringProperty("0");
        this.pValue = new SimpleStringProperty("0");
        this.iValue = new SimpleStringProperty("0");
        this.dValue = new SimpleStringProperty("0");
        properties.addAll(Arrays.asList(this.name, this.amplitudeColor, this.responseAmplitude, this.amplitude,
                this.relativeResponseAmplitude, this.frequencyColor, this.responseFrequency, this.frequency,
                this.relativeResponseFrequency, this.phaseColor, this.responsePhase, this.phase,
                this.relativeResponsePhase, this.pValue, this.iValue, this.dValue));
    }

    public PairModel(String name) {
        this(name,Color.white, Color.BLACK, Color.white, Color.white, Color.white, Color.white);
    }
    public PairModel() {
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

    public String getPhaseColor() { return phaseColor.get(); }
    public StringProperty phaseColorProperty() { return phaseColor; }
    public void setPhaseColor(String phaseColor) { this.phaseColor.set(phaseColor); }

    public Integer getPhaseGraphNum() { return phaseGraphNum.get(); }
    public IntegerProperty phaseGraphNumProperty() { return phaseGraphNum; }
    public void setPhaseGraphNum(Integer graphNum) { this.phaseGraphNum.set(graphNum); }

    public Boolean getPhaseEnable() { return phaseEnable.get(); }
    public BooleanProperty phaseEnableProperty() { return phaseEnable; }
    public void setPhaseEnable(Boolean phaseEnable) { this.phaseEnable.set(phaseEnable); }

    public Integer getPhaseStatus() { return phaseStatus.get(); }
    public IntegerProperty phaseStatusProperty() { return phaseStatus; }
    public void setPhaseStatus(Integer phaseStatus) { this.phaseStatus.set(phaseStatus); }

    public String getResponsePhase() { return responsePhase.get(); }
    public StringProperty responsePhaseProperty() { return responsePhase; }
    public void setResponsePhase(String responsePhase) { this.responsePhase.set(responsePhase); }

    public String getPhase() { return phase.get(); }
    public StringProperty phaseProperty() { return phase; }
    public void setPhase(String phase) { this.phase.set(phase); }

    public String getRelativeResponsePhase() { return relativeResponsePhase.get(); }
    public StringProperty relativeResponsePhaseProperty() { return relativeResponsePhase; }
    public void setRelativeResponsePhase(String relativeResponsePhase) { this.relativeResponsePhase.set(relativeResponsePhase); }

    public String getpValue() { return pValue.get(); }
    public StringProperty pValueProperty() { return pValue; }
    public void setPvalue(String pValue) { this.pValue.set(pValue); }

    public String getiValue() { return iValue.get(); }
    public StringProperty iValueProperty() { return iValue; }
    public void setIvalue(String iValue) { this.iValue.set(iValue); }

    public String getdValue() { return dValue.get(); }
    public StringProperty dValueProperty() { return dValue; }
    public void setDvalue(String dValue) { this.dValue.set(dValue); }
}
