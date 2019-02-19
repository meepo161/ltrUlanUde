package ru.avem.posum.models;

public class CalibrationModel {
    private double loadValue;
    private double channelValue;

    public CalibrationModel(double loadValue, double channelValue) {
        this.loadValue = loadValue;
        this.channelValue = channelValue;
    }

    public double getLoadValue() {
        return loadValue;
    }

    public double getChannelValue() {
        return channelValue;
    }
}
