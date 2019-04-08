package ru.avem.posum.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.utils.GaussianElimination;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CalibrationModel {
    private ADC adc;
    private ObservableList<CalibrationPoint> calibrationPoints = FXCollections.observableArrayList();
    private int channel;
    private double channelValue;
    private double channelValueCoefficient;
    private List<Double> calibrationCoefficients = new ArrayList<>();
    private int decimalFormatScale;
    private double loadValue;
    private double loadValueCoefficient;
    private String valueName = "";

    public void calibrate(ADC adc, int channel) {
        setFields(adc, channel);
        calculateCoefficients();
    }

    private void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
    }

    private void calculateCoefficients() {
        List<String> settings = adc.getCalibrationSettings().get(channel);
        calculate(settings);
    }

    private void calculate(List<String> settings) {
        for (String setting : settings) {
            double loadValue = CalibrationPoint.parseLoadValue(setting);
            double channelValue = CalibrationPoint.parseChannelValue(setting);

            calibrationCoefficients.add(loadValue / channelValue);
        }
    }

    public void parseCalibration(String calibration) {
        loadValue = CalibrationPoint.parseLoadValue(calibration);
        channelValue = CalibrationPoint.parseChannelValue(calibration);
        valueName = CalibrationPoint.parseValueName(calibration);
    }

    public List<Double> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }

    public ObservableList<CalibrationPoint> getCalibrationPoints() {
        return calibrationPoints;
    }

    public double getChannelValue() {
        return channelValue;
    }

    public double getChannelValueCoefficient() {
        return channelValueCoefficient;
    }

    public int getDecimalFormatScale() {
        return decimalFormatScale;
    }

    public String getFormattedLoadValue() {
        double value = Utils.roundValue(loadValue, decimalFormatScale);
        return Utils.convertFromExponentialFormat(value, decimalFormatScale);
    }

    public double getLoadValue() {
        return loadValue;
    }

    public double getLoadValueCoefficient() {
        return loadValueCoefficient;
    }

    public String getValueName() {
        return valueName;
    }

    public void setChannelValue(double channelValue) {
        this.channelValue = channelValue;
    }

    public void setChannelValueCoefficient(double channelValueCoefficient) {
        this.channelValueCoefficient = channelValueCoefficient;
    }

    public void setDecimalFormatScale(int decimalFormatScale) {
        this.decimalFormatScale = decimalFormatScale;
    }

    public void setLoadValue(double loadValue) {
        this.loadValue = loadValue;
    }

    public void setLoadValueCoefficient(double loadValueCoefficient) {
        this.loadValueCoefficient = loadValueCoefficient;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
