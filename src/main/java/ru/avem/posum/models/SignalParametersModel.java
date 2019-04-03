package ru.avem.posum.models;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.utils.Complex;

import java.util.List;

public class SignalParametersModel {
    private ADC adc;
    private int averageIterator;
    private double amplitude;
    private double bufferedAmplitude;
    private double bufferedFrequency;
    private double bufferedRms;
    private double bufferedZeroShift;
    private double calibratedValue;
    private int channel;
    private final int CHANNELS = 4;
    private double[] data;
    private double firstPointLoadValue;
    private double firstPointChannelValue;
    private double frequency;
    private double lowerBound;
    private double maxValue;
    private double minValue;
    private double rms;
    private double secondPointLoadValue;
    private double secondPointChannelValue;
    private double tickUnit;
    private double upperBound;
    private String valueName;
    private double zeroShift;

    void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
    }

    void calculateParameters(double[] signal, double averageCount, boolean isCalibrationExists) {
        setFields(signal, channel);
        calculateMinAndMaxValues();
        calculateParameters(averageCount);
        checkCalibration(isCalibrationExists);
    }

    private void setFields(double[] rawData, int channel) {
        this.data = rawData;
        this.channel = channel;
    }

    private void calculateMinAndMaxValues() {
        maxValue = -999_999_999;
        minValue = 999_999_999;

        for (int i = channel; i < data.length; i += CHANNELS) {
            if (data[i] > maxValue) {
                maxValue = data[i];
            }

            if (data[i] < minValue) {
                minValue = data[i];
            }
        }
    }

    private void calculateParameters(double averageCount) {
        if (averageCount == 1) {
            bufferedAmplitude = amplitude = calculateAmplitude();
            bufferedFrequency = frequency = calculateFrequency();
            bufferedRms = rms = calculateRms();
            bufferedZeroShift = zeroShift = calculateZeroShift();
        } else if (averageIterator < averageCount) {
            bufferedAmplitude += calculateAmplitude();
            bufferedFrequency += calculateFrequency();
            bufferedRms += calculateRms();
            bufferedZeroShift += calculateZeroShift();
            averageIterator++;
        } else {
            amplitude = bufferedAmplitude / averageCount;
            frequency = bufferedFrequency / averageCount;
            rms = bufferedRms / averageCount;
            zeroShift = bufferedZeroShift / averageCount;
            averageIterator = 0;
            bufferedAmplitude = bufferedFrequency = bufferedRms = bufferedZeroShift = 0;
        }
    }

    private double calculateAmplitude() {
        return (maxValue - minValue) / 2;
    }

    private double calculateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;

        for (int i = channel; i < data.length; i += CHANNELS) {
            if ((data[i] > amplitude / 1.1) && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (data[i] < amplitude / 2) {
                positivePartOfSignal = false;
            }
        }

        return frequency;
    }

    private double calculateRms() {
        double squaresSum = 0;
        int samplesPerPeriod = (int) (data.length / frequency);

        for (int i = 0; i < samplesPerPeriod; i++) {
            squaresSum += data[i] * data[i];
        }

        return Math.sqrt(squaresSum / samplesPerPeriod);
    }

    private double calculateZeroShift() {
        return (maxValue + minValue) / 2;
    }

    private void checkCalibration(boolean isCalibrationExists) {
        if (isCalibrationExists) {
            if (lowerBound < 0 & firstPointLoadValue >= 0) {
                amplitude = applyCalibration(amplitude);
            } else {
                amplitude = applyCalibration(adc, amplitude);
            }
            zeroShift = applyCalibration(adc, zeroShift);
        }
    }

    private double applyCalibration(double value) {
        defineBounds();
        setBounds();
        return calibratedValue = value / (Math.abs(lowerBound) + Math.abs(upperBound)) * secondPointLoadValue;
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

    double applyCalibration(ADC adc, double value) {
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

    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (secondPointLoadValue - firstPointLoadValue) / (secondPointChannelValue - firstPointChannelValue);
            double b = firstPointLoadValue - k * firstPointChannelValue;
            calibratedValue = k * value + b;
        }
    }

    void defineCalibratedBounds(ADC adc) {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        valueName = CalibrationPoint.parseValueName(calibrationSettings.get(0));
        double minLoadValue = 999_999_999;
        double maxLoadValue = -999_999_999;
        int GRAPH_SCALE = 5;

        for (String calibrationSetting : calibrationSettings) {
            double loadValue = CalibrationPoint.parseLoadValue(calibrationSetting);

            if (minLoadValue > loadValue) {
                minLoadValue = loadValue;
            }
            if (maxLoadValue < loadValue) {
                maxLoadValue = loadValue;
            }
        }

        lowerBound = minLoadValue;
        upperBound = maxLoadValue;
        tickUnit = maxLoadValue / GRAPH_SCALE;
    }

    double getAmplitude() {
        return amplitude;
    }

    double getFrequency() {
        return frequency;
    }

    double getLowerBound() {
        return lowerBound;
    }

    double getRms() {
        return rms;
    }

    double getTickUnit() {
        return tickUnit;
    }

    double getUpperBound() {
        return upperBound;
    }

    String getValueName() {
        return valueName;
    }

    double getZeroShift() {
        return zeroShift;
    }
}
