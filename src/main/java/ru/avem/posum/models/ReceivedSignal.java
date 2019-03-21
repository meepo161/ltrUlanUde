package ru.avem.posum.models;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.utils.Complex;

import java.util.List;

public class ReceivedSignal {
    private double amplitude;
    private double calibratedValue;
    private int channel;
    private double[] data;
    private double firstPointLoadValue;
    private double firstPointChannelValue;
    private double lowerBound;
    private double maxValue;
    private double minValue;
    private double phase;
    private double secondPointLoadValue;
    private double secondPointChannelValue;
    private double upperBound;
    private double zeroShift;

    public void calculateBaseParameters(double[] rawData, int channel) {
        setFields(rawData, channel);
        calculateMinAndMaxValues();
        calculateAmplitude();
        calculateZeroShift();
        calculatePhase();
    }

    private void calculateAmplitude() {
        amplitude = (maxValue - minValue) / 2;
    }

    private void calculatePhase() {
        createComplexArray();
    }

    private void createComplexArray() {
        Complex complexNumber = new Complex(zeroShift, 0);
        phase = complexNumber.phase();
    }

    private void calculateZeroShift() {
        zeroShift = (maxValue + minValue) / 2;
    }

    private void setFields(double[] rawData, int channel) {
        this.data = rawData;
        this.channel = channel;
    }

    private void calculateMinAndMaxValues() {
        maxValue = -999_999_999;
        minValue = 999_999_999;

        for (int i = channel; i < data.length; i += 4) {
            if (data[i] > maxValue) {
                maxValue = data[i];
            }

            if (data[i] < minValue) {
                minValue = data[i];
            }
        }
    }

    public double applyCalibration(ADC adc, double value) {
        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);

        for (int settingsIndex = 0; settingsIndex < calibrationCoefficients.size() - 1; settingsIndex++) {
            parseCalibrationSettings(calibrationSettings, settingsIndex);
            defineBounds();
            setBounds();
            calibrate(value);
        }

        return calibratedValue;
    }

    private void parseCalibrationSettings(List<String> calibrationSettings, int i) {
        String firstCalibrationPoint = calibrationSettings.get(i);
        String secondCalibrationPoint = calibrationSettings.get(i + 1);
        firstPointChannelValue = CalibrationPoint.parseChannelValue(firstCalibrationPoint);
        firstPointLoadValue = CalibrationPoint.parseLoadValue(firstCalibrationPoint);
        secondPointChannelValue = CalibrationPoint.parseChannelValue(secondCalibrationPoint);
        secondPointLoadValue = CalibrationPoint.parseLoadValue(secondCalibrationPoint);
    }

    private void defineBounds() {
        if (firstPointChannelValue > secondPointChannelValue) {
            double loadValueBuffer = firstPointLoadValue;
            double channelValueBuffer = firstPointChannelValue;
            firstPointLoadValue = secondPointLoadValue;
            firstPointChannelValue = secondPointChannelValue;
            secondPointLoadValue = loadValueBuffer;
            secondPointChannelValue = channelValueBuffer;
        }
    }

    private void setBounds() {
        lowerBound = firstPointChannelValue;
        upperBound = secondPointChannelValue;
    }

    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (secondPointLoadValue - firstPointLoadValue) / (secondPointChannelValue - firstPointChannelValue);
            double b = firstPointLoadValue - k * firstPointChannelValue;
            calibratedValue = k * value + b;
            System.out.println("Bounds: " + lowerBound + ", " + upperBound); // TODO: delete this
            System.out.println(value + ": " + calibratedValue); // TODO: delete this
        } else {
            calibratedValue = value;
        }
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getZeroShift() {
        return zeroShift;
    }

    public double getPhase() {
        return phase;
    }
}
