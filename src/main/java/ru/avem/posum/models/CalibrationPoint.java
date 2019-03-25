package ru.avem.posum.models;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class CalibrationPoint {
    private int channel;
    private String loadValue;
    private String channelValue;
    private String valueName;

    public CalibrationPoint(int channel, double loadValue, double channelValue, String channelName) {
        this.channel = channel;
        this.loadValue = String.format("%.7f", loadValue);
        this.channelValue = String.format("%.7f", channelValue);
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

    public static double parseLoadValue(String settings) {
        return Double.parseDouble(settings.split(", ")[1].split("load value: ")[1]);
    }

    public static double parseChannelValue(String settings) {
        return Double.parseDouble(settings.split(", ")[2].split("channel value: ")[1]);
    }

    public static String parseValueName(String settings) {
        return settings.split(", ")[3].split("value name: ")[1];
    }

    public int getChannel() {
        return channel;
    }

    public String getLoadValue() {
        return loadValue.replaceAll(",", ".");
    }

    public String getChannelValue() {
        return channelValue.replaceAll(",", ".");
    }

    public String getValueName() {
        return valueName;
    }
}
