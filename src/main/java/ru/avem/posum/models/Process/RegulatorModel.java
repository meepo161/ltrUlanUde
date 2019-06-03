package ru.avem.posum.models.Process;

public class RegulatorModel {
    private double bufferedError;
    private double bufferedIValue;
    private int calculationCount = 1;
    private double dCoefficient;
    private double dValue;
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

    public double getAmplitude() {
        return calculateRegulator(neededAmplitude, responseAmplitude);
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

        System.out.printf("\nNeeded: %f, response: %f. Error: %f\n", neededParameter, response, neededParameter - response);
        System.out.printf("Needed: %f, response: %f. New: %f\n", neededParameter, response, (response + pValue));
        return response + pValue + iValue + dValue;
    }

    public double getDc() {
        return calculateRegulator(neededDc, responseDc);
    }

    public double getFrequency() {
        return calculateRegulator(neededFrequency, responseFrequency);
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
