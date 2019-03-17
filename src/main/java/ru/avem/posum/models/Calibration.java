package ru.avem.posum.models;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.utils.GaussianElimination;

import java.util.ArrayList;
import java.util.List;

public class Calibration {
    private ADC adc;
    private int channel;
    private List<Double> calibrationCoefficients = new ArrayList<>();

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

    // Рассчитывает значение функции от x
    public static double applyCalibration(double x, int coefficient, List<Double> calibrationCoefficients) {
        return x * calibrationCoefficients.get(coefficient);
    }

    public List<Double> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }
}
