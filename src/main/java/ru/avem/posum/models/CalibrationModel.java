package ru.avem.posum.models;

public class CalibrationModel {
    private double loadValue;
    private double channelValue;
    private String valueName;

    public CalibrationModel(double loadValue, double channelValue, String valueName) {
        this.loadValue = loadValue;
        this.channelValue = channelValue;
        this.valueName = valueName;
    }

    public double getLoadValue() {
        return loadValue;
    }

    public double getChannelValue() {
        return channelValue;
    }

    public String getValueName() {
        return valueName;
    }
}
