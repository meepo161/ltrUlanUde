package ru.avem.posum.models;

import javafx.collections.ObservableList;
import ru.avem.posum.controllers.CalibrationController;
import ru.avem.posum.db.models.Calibration;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;

public class CalibrationPoint {
    private int channel;
    private String loadValue;
    private String channelValue;
    private String valueName;

    public CalibrationPoint(int channel, CalibrationModel calibrationModel) {
        double loadValue = calibrationModel.getLoadValue();
        double channelValue = calibrationModel.getChannelValue();
        String valueName = calibrationModel.getValueName();
        int decimalFormatScale = calibrationModel.getDecimalFormatScale();

        this.channel = channel;
        this.loadValue = Utils.convertFromExponentialFormat(loadValue, decimalFormatScale);
        this.channelValue = Utils.convertFromExponentialFormat(channelValue, decimalFormatScale);
        this.valueName = valueName;
    }

    public static ArrayList<String> toString(ObservableList<CalibrationPoint> points) {
        ArrayList<String> convertedList = new ArrayList<>();

        for (CalibrationPoint point : points) {
            String channel = String.valueOf(point.getChannel());
            String loadValue = String.valueOf(point.getLoadValue());
            String channelValue = String.valueOf(point.getChannelValue());
            String valueName = String.valueOf(point.getValueName());

            String settings = "Channel: " + channel +
                    ", load value: " + loadValue +
                    ", channel value: " + channelValue +
                    ", value name: " + valueName;
            convertedList.add(settings);
        }

        return convertedList;
    }

    public int getChannel() {
        return channel;
    }

    public String getChannelValue() {
        return channelValue.replaceAll(",", ".");
    }

    public String getLoadValue() {
        return loadValue.replaceAll(",", ".");
    }

    private String getValueName() {
        return valueName;
    }

    static double parseChannelValue(String settings) {
        return Double.parseDouble(settings.split(", ")[2].split("channel value: ")[1]);
    }

    static double parseLoadValue(String settings) {
        return Double.parseDouble(settings.split(", ")[1].split("load value: ")[1]);
    }

    static String parseValueName(String settings) {
        return settings.split(", ")[3].split("value name: ")[1];
    }
}
