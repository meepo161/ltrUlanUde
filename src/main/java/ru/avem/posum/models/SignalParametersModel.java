package ru.avem.posum.models;

import ru.avem.posum.hardware.ADC;

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
    private double calibrationFirstPointLoadValue;
    private double calibrationFirstPointChannelValue;
    private double calibrationSecondPointLoadValue;
    private double calibrationSecondPointChannelValue;
    private String calibrationValueName;
    private int channel;
    private int channels;
    private double[] data;
    private double lowerBound;
    private double maxSignalValue;
    private double minSignalValue;
    private double rms;
    private int samplesPerSemiPeriod;
    private double signalFrequency;
    private double tickUnit;
    private double upperBound;
    private double zeroShift;

    void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
        this.channels = adc.getChannelsCount();
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
        maxSignalValue = -999_999_999;
        minSignalValue = 999_999_999;

        for (int i = channel; i < data.length; i += channels) {
            if (data[i] > maxSignalValue) {
                maxSignalValue = data[i];
            }

            if (data[i] < minSignalValue) {
                minSignalValue = data[i];
            }
        }
    }

    private void calculateParameters(double averageCount) {
        if (averageCount == 1) {
            bufferedAmplitude = amplitude = calculateAmplitude();
            bufferedFrequency = signalFrequency = calculateFrequency();
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
            signalFrequency = bufferedFrequency / averageCount;
            rms = bufferedRms / averageCount;
            zeroShift = bufferedZeroShift / averageCount;
            averageIterator = 0;
            bufferedAmplitude = bufferedFrequency = bufferedRms = bufferedZeroShift = 0;
        }
    }

    private double calculateAmplitude() {
        return (maxSignalValue - minSignalValue) / 2;
    }

    private double calculateFrequency() {
        int shift = 1_000;
        double firstValue = data[channel] + shift;
        double zeroTransitionCounter = 0;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (zeroShift + shift));
        samplesPerSemiPeriod = 0;

        for (int index = channel; index < data.length; index += channels) {
            double value = data[index] + shift;
            double centerOfSignal = zeroShift + shift;

            countSamples(zeroTransitionCounter);

            if (firstValue > centerOfSignal) {
                if (value > centerOfSignal && firstPeriod && (index > channels * 10)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal && samplesPerSemiPeriod == 0)) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value > centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriod > 10) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index > channels * 10)) {
                    positivePartOfSignal = false;
                } else if ((value > centerOfSignal && !positivePartOfSignal && samplesPerSemiPeriod == 0)) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriod > 10) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = false;
                }
            }
        }

        double frequency = (samplesPerSemiPeriod == 0 ? 0 : (adc.getFrequency() / (samplesPerSemiPeriod * 2)));
        if (frequency > 50) {
            frequency = calculateFrequencyBeyond50Hz();
        }
        return frequency;
    }

    private void countSamples(double frequency) {
        if (frequency == 1) {
            samplesPerSemiPeriod++;
        }
    }

    private double calculateFrequencyBeyond50Hz() {
        boolean positivePartOfSignal = false;
        double frequency = 0;

        for (int i = channel; i < data.length; i += channels) {
            if ((data[i] > zeroShift * 1.9) && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (data[i] < zeroShift * 1.9){
                positivePartOfSignal = false;
            }
        }

        return frequency;
    }

    private double calculateRms() {
        double squaresSum = 0;

        for (double datum : data) {
            squaresSum += datum * datum;
        }

        return Math.sqrt(squaresSum / data.length);
    }

    private double calculateZeroShift() {
        return (maxSignalValue + minSignalValue) / 2;
    }

    private void checkCalibration(boolean isCalibrationExists) {
        if (isCalibrationExists) {
            if (lowerBound < 0 & calibrationFirstPointLoadValue >= 0) {
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
        return calibratedValue = value / (Math.abs(lowerBound) + Math.abs(upperBound)) * calibrationSecondPointLoadValue;
    }

    private void defineBounds() {
        if (calibrationFirstPointChannelValue > calibrationSecondPointChannelValue) {
            double loadValueBuffer = calibrationFirstPointLoadValue;
            double channelValueBuffer = calibrationFirstPointChannelValue;
            calibrationFirstPointLoadValue = calibrationSecondPointLoadValue;
            calibrationFirstPointChannelValue = calibrationSecondPointChannelValue;
            calibrationSecondPointLoadValue = loadValueBuffer;
            calibrationSecondPointChannelValue = channelValueBuffer;
        }
    }

    private void setBounds() {
        lowerBound = calibrationFirstPointChannelValue;
        upperBound = calibrationSecondPointChannelValue;
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
        calibrationFirstPointChannelValue = CalibrationPoint.parseChannelValue(firstCalibrationPoint);
        calibrationFirstPointLoadValue = CalibrationPoint.parseLoadValue(firstCalibrationPoint);
        calibrationSecondPointChannelValue = CalibrationPoint.parseChannelValue(secondCalibrationPoint);
        calibrationSecondPointLoadValue = CalibrationPoint.parseLoadValue(secondCalibrationPoint);
    }

    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (calibrationSecondPointLoadValue - calibrationFirstPointLoadValue) / (calibrationSecondPointChannelValue - calibrationFirstPointChannelValue);
            double b = calibrationFirstPointLoadValue - k * calibrationFirstPointChannelValue;
            calibratedValue = k * value + b;
        }
    }

    void defineCalibratedBounds(ADC adc) {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        calibrationValueName = CalibrationPoint.parseValueName(calibrationSettings.get(0));
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

    double getSignalFrequency() {
        return signalFrequency;
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

    String getCalibrationValueName() {
        return calibrationValueName;
    }

    double getZeroShift() {
        return zeroShift;
    }
}
