package ru.avem.posum.models.calibration;

import javafx.collections.ObservableList;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;

public class CalibrationPoint {
    private int channelNumber;
    private String channelValue;
    private String loadValue;
    private String valueName;

    public CalibrationPoint(int channelNumber, CalibrationModel calibrationModel) {
        double loadValue = calibrationModel.getLoadValue();
        double channelValue = calibrationModel.getChannelValue();
        int decimalFormatScale = calibrationModel.getDecimalFormatScale();

        this.channelNumber = channelNumber;
        this.loadValue = Utils.convertFromExponentialFormat(loadValue, decimalFormatScale);
        this.channelValue = Utils.convertFromExponentialFormat(channelValue, decimalFormatScale);
        this.valueName = calibrationModel.getValueName();
    }

    public CalibrationPoint(String loadValue, String channelValue, String valueName) {
        this.loadValue = loadValue;
        this.channelValue = channelValue;
        this.valueName = valueName;
    }

    public static ArrayList<String> toString(ObservableList<CalibrationPoint> points) {
        ArrayList<String> convertedList = new ArrayList<>();

        for (CalibrationPoint point : points) {
            String settings = String.format("Channel: %d, load value: %s, channel value: %s, value name: %s",
                    point.getChannelNumber(), point.getLoadValue(), point.getChannelValue(), point.getValueName());

            convertedList.add(settings);
        }

        return convertedList;
    }

    public String toString() {
        return String.format("Channel: %d, load value: %s, channel value: %s, value name: %s",
                this.getChannelNumber(), this.getLoadValue(), this.getChannelValue(), this.getValueName());
    }

    public static CalibrationPoint fromString(String rawValue) {
        int channelNumber = Integer.parseInt(rawValue.split(", ")[0].split("Channel: ")[1]);
        String loadValue = rawValue.split(", ")[1].split("load value: ")[1];
        String channelValue = rawValue.split(", ")[2].split("channel value: ")[1];
        String valueName = rawValue.split(", ")[3].split("value name: ")[1];
        CalibrationPoint calibrationPoint = new CalibrationPoint(loadValue, channelValue, valueName);
        calibrationPoint.channelNumber = channelNumber;
        return calibrationPoint;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public String getChannelValue() {
        return channelValue.replaceAll(",", ".");
    }

    public double getValueOfChannel() {
        return Double.parseDouble(channelValue.replaceAll(",", "."));
    }

    public String getLoadValue() {
        return loadValue.replaceAll(",", ".");
    }

    public double getLoadOfChannel() {
        return Double.parseDouble(loadValue.replaceAll(",", "."));
    }

    public String getValueName() {
        if (valueName.equals("Ноль")) {
            return "";
        } else {
            return valueName;
        }
    }

    public static double parseChannelValue(String settings) {
        return Double.parseDouble(settings.split(", ")[2].split("channel value: ")[1]);
    }

    public static double parseLoadValue(String settings) {
        return Double.parseDouble(settings.split(", ")[1].split("load value: ")[1]);
    }

    public static String parseValueName(String settings) {
        String[] splittedSettings = settings.split(", ")[3].split("value name: ");

        if (splittedSettings.length == 2) {
            return splittedSettings[1];
        } else {
            return "";
        }
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }
}
