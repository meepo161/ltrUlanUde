package ru.avem.posum.models.Process;

public class RegulatorModel {
    private double bufferedError;
    private double bufferedIValue;
    private int calculationCount = 1;
    private double dCoefficient;
    private double dValue;
    private double error;
    private double iCoefficient;
    private double iValue;
    private double neededAmplitude;
    private double neededDc;
    private double neededFrequency;
    private double neededRms;
    private double pCoefficient;
    private double pValue;
    private double responseAmplitude;
    private double responseDc;
    private double responseFrequency;
    private double responseRms;

    public double getAmplitude() {
        return calculateRegulator(responseAmplitude, neededAmplitude);
    }

    private double calculateRegulator(double neededParameter, double response) {
        double error = neededParameter - response;
        pValue = pCoefficient * error;
        iValue = iCoefficient * error;

        if (calculationCount % 2 != 0) {
            bufferedError = error;
            bufferedIValue = iValue;
        } else {
            iValue = bufferedIValue + iCoefficient * error;
            dValue = dCoefficient * (error - bufferedError);
            calculationCount = 1;
        }

        return neededParameter + pValue + iValue + dValue;
    }

    public double getDc() {
        return calculateRegulator(responseDc, neededDc);
    }

    public double getFrequency() {
        return calculateRegulator(responseFrequency, neededFrequency);
    }

    public double getRms() {
        return neededRms;
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

    public void setResponseRms(double responseRms) {
        this.responseRms = responseRms;
    }

    public void setPCoefficient(double pValue) {
        this.pCoefficient = pValue;
    }

    public void setICoefficient(double iValue) {
        this.iCoefficient = iValue;
    }

    public void setCoefficient(double dValue) {
        this.dCoefficient = dValue;
    }
}
