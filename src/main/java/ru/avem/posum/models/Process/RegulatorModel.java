package ru.avem.posum.models.Process;

import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

import java.util.List;

public class RegulatorModel {
    private double pValue;
    private double iValue;
    private double dValue;
    private double responseAmplitude;
    private double responseDc;
    private double responseFrequency;
    private double responsePhase;
    private double responseRms;
    private double dacAmplitude;
    private double dacDc;
    private double dacFrequency;
    private double dacPhase;
    private double dacRms;
    private double neededAmplitude;
    private double neededDc;
    private double neededFrequency;
    private double neededPhase;
    private double neededRms;

    public double getAmplitude() {
        return neededAmplitude;
    }

    public double getDc() {
        return neededDc;
    }

    public int getFrequency() {
        double regulationError = neededFrequency - responseFrequency;
        regulationError = (regulationError < neededFrequency / 1.05) || (regulationError > neededFrequency * 1.05) ? regulationError : 0;

        System.out.printf("Regulations error: %f\n", regulationError);

        dacFrequency += pValue * regulationError;
        System.out.printf("Dac frequency: %f\n", dacFrequency);

        System.out.printf("Dac frequency: %d\n", (int) dacFrequency);
        return (int) dacFrequency;
    }

    public double getRms() {
        return neededRms;
    }

    public double getPhase() {
        return neededPhase;
    }

    public void setNeededAmplitude(double neededAmplitude) {
        this.neededAmplitude = neededAmplitude;
    }

    public void setNeededDc(double neededDc) {
        this.neededDc = neededDc;
    }

    public void setNeededFrequency(double neededFrequency) {
        this.neededFrequency = neededFrequency;
    }

    public void setNeededRms(double neededRms) {
        this.neededRms = neededRms;
    }

    public void setResponseAmplitude(double responseAmplitude) {
        this.responseAmplitude = responseAmplitude;
    }

    public void setResponseDc(double responseDc) {
        this.responseDc = responseDc;
    }

    public void setResponseFrequency(double responseFrequency) {
        this.responseFrequency = responseFrequency;
    }

    public void setResponsePhase(double responsePhase) {
        this.responsePhase = responsePhase;
    }

    public void setResponseRms(double responseRms) {
        this.responseRms = responseRms;
    }

    public void setNeededPhase(double neededPhase) {
        this.neededPhase = neededPhase;
    }

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }

    public void setIValue(double iValue) {
        this.iValue = iValue;
    }

    public void setDValue(double dValue) {
        this.dValue = dValue;
    }
}
