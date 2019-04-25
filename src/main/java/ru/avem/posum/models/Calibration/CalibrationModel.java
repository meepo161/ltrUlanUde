package ru.avem.posum.models.Calibration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CalibrationModel {
    private ADC adc;
    private ObservableList<CalibrationPointModel> calibrationPointModels = FXCollections.observableArrayList();
    private int channelNumber;
    private double channelValue;
    private List<Double> calibrationCoefficients = new ArrayList<>();
    private int decimalFormatScale;
    private double loadValue;
    private String valueName = "";

    public void calibrate(ADC adc, int channelNumber) {
        setFields(adc, channelNumber);
        calculateCalibrationCoefficients();
    }

    private void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channelNumber = channel;
    }

    private void calculateCalibrationCoefficients() {
        for (String settings : adc.getCalibrationSettings().get(channelNumber)) {
            double loadValue = CalibrationPointModel.parseLoadValue(settings);
            double channelValue = CalibrationPointModel.parseChannelValue(settings);
            calibrationCoefficients.add(loadValue / channelValue);
        }
    }

    public void parse(String calibration) {
        loadValue = CalibrationPointModel.parseLoadValue(calibration);
        channelValue = CalibrationPointModel.parseChannelValue(calibration);
        valueName = CalibrationPointModel.parseValueName(calibration);
    }

    public List<Double> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }

    public ObservableList<CalibrationPointModel> getCalibrationPointModels() {
        return calibrationPointModels;
    }

    public double getChannelValue() {
        return channelValue;
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

    public String getValueName() {
        return valueName;
    }

    public void setChannelValue(double channelValue) {
        this.channelValue = channelValue;
    }

    public void setDecimalFormatScale(int decimalFormatScale) {
        this.decimalFormatScale = decimalFormatScale;
    }

    public void setLoadValue(double loadValue) {
        this.loadValue = loadValue;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
