package ru.avem.posum.models;

public class CalibrationPoint {
    private double loadValue;
    private double channelValue;
    private String valueName;

    public CalibrationPoint(double loadValue, double channelValue, String channelName) {
        this.loadValue = loadValue;
        this.channelValue = channelValue;
        this.valueName = channelName;
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
