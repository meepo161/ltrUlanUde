package ru.avem.posum.models;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Observable;

public class CalibrationPoint {
    private int channel;
    private double loadValue;
    private double channelValue;
    private String valueName;

    public CalibrationPoint(int channel, double loadValue, double channelValue, String channelName) {
        this.channel = channel;
        this.loadValue = loadValue;
        this.channelValue = channelValue;
        this.valueName = channelName;
    }

    public static ArrayList<String> toString(ObservableList<CalibrationPoint> points) {
        ArrayList<String> convertedList = new ArrayList<>();

        for (CalibrationPoint point : points) {
            String channel = String.valueOf(point.getChannel());
            String loadValue = String.valueOf(point.getLoadValue());
            String channelValue = String.valueOf(point.getChannelValue());
            String valueName = String.valueOf(point.getValueName());
            StringBuffer settings = new StringBuffer();

            settings.append("Channel: ").append(channel)
                    .append(", load value: ").append(loadValue)
                    .append(", channel value: ").append(channelValue)
                    .append(", value name: ").append(valueName);

            convertedList.add(String.valueOf(settings));
        }

        return convertedList;
    }

    public int getChannel() {
        return channel;
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
