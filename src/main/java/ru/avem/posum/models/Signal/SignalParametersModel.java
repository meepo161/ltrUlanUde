package ru.avem.posum.models.Signal;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Calibration.CalibrationPointModel;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;

public class SignalParametersModel {
    private boolean accurateFrequencyCalculation = true;
    private ADC adc;
    private int averageIterator;
    private double amplitude;
    private double bufferedAmplitude;
    private double bufferedFrequency;
    private double bufferedLoadsCounter;
    private double bufferedRms;
    private double bufferedZeroShift;
    private double bufferedCalibratedAmplitude;
    private double bufferedCalibratedRms;
    private double bufferedCalibratedZeroShift;
    private double calibratedAmplitude;
    private double calibratedRms;
    private double calibratedZeroShift;
    private double calibratedValue;
    private double calibrationFirstPointLoadValue;
    private double calibrationFirstPointChannelValue;
    private double calibrationSecondPointLoadValue;
    private double calibrationSecondPointChannelValue;
    private String calibrationValueName;
    private int channel;
    private int channels;
    private double[] data;
    private int loadsCounter;
    private double lowerBound;
    private double maxSignalValue;
    private int minSamples = 20;
    private double minSignalValue;
    private double rms;
    private int samplesPerSemiPeriod;
    private double signalFrequency;
    private double tickUnit;
    private double upperBound;
    private double zeroShift;

    public void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
        this.channels = adc.getChannelsCount();
    }

    public void calculateParameters(double[] signal, double averageCount, boolean isCalibrationExists) {
        setFields(signal, channel);
        calculateMinAndMaxValues();
        calculateParameters(averageCount);
        checkCalibration(isCalibrationExists, averageCount);
    }

    private void setFields(double[] rawData, int channel) {
        this.data = rawData;
        this.channel = channel;
    }

    private void calculateMinAndMaxValues() {
        maxSignalValue = 0;
        minSignalValue = 0;

        for (int pieceIndex = 0; pieceIndex < 10; pieceIndex++) {
            double[] pieceOfDate = new double[data.length / 10];
            System.arraycopy(data, pieceIndex * pieceOfDate.length, pieceOfDate, 0, pieceOfDate.length);
            DoubleSummaryStatistics statistics = Arrays.stream(pieceOfDate).summaryStatistics();
            maxSignalValue += statistics.getMax() / 10;
            minSignalValue += statistics.getMin() / 10;
        }
    }

    private void calculateParameters(double averageCount) {
        if (averageCount == 1) {
            amplitude = rms = zeroShift = 0;
            bufferedAmplitude = amplitude = calculateAmplitude();
            bufferedZeroShift = zeroShift = calculateZeroShift();
            bufferedRms = rms = calculateRms();
            bufferedFrequency = signalFrequency = accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            loadsCounter += calculateLoadsCounter();
        } else if (averageIterator < averageCount) {
            bufferedAmplitude += calculateAmplitude();
            bufferedRms += calculateRms();
            bufferedZeroShift += calculateZeroShift();
            bufferedFrequency += accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            bufferedLoadsCounter += calculateLoadsCounter();
            averageIterator++;
        } else {
            amplitude = bufferedAmplitude / averageCount;
            rms = bufferedRms / averageCount;
            zeroShift = bufferedZeroShift / averageCount;
            signalFrequency = bufferedFrequency / averageCount;
            loadsCounter += bufferedLoadsCounter / averageCount;
            bufferedAmplitude = bufferedFrequency = bufferedLoadsCounter = bufferedRms = bufferedZeroShift = 0;
            averageIterator = 0;
        }
    }

    private double calculateAmplitude() {
        return (maxSignalValue - minSignalValue) / 2;
    }

    private double calculateFrequency() {
        double estimatedFrequency = estimateFrequency();

        if (estimatedFrequency < 50) {
            return defineFrequency(estimatedFrequency);
        } else {
            return estimatedFrequency;
        }

    }

    private double estimateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;

        for (int i = channel; i < data.length; i += channels) {
            if (amplitude + zeroShift > 0) {
                if (zeroShift > 0) {
                    if (data[i] > zeroShift * 1.05 && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < zeroShift / 1.05 && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                } else {
                    if (data[i] > zeroShift / 1.05 && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < zeroShift * 1.05 && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                }
            } else if (amplitude + zeroShift < 0 && zeroShift < 0) {
                if (data[i] < zeroShift * 1.05 && !positivePartOfSignal) {
                    frequency++;
                    positivePartOfSignal = true;
                } else if (data[i] > zeroShift / 1.05 && positivePartOfSignal) {
                    positivePartOfSignal = false;
                }
            }
        }

        return frequency;
    }

    private double defineFrequency(double estimatedFrequency) {
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
                if (value > centerOfSignal && firstPeriod && (index > channels * minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal && samplesPerSemiPeriod == 0)) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value > centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriod > minSamples) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index > channels * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value > centerOfSignal && !positivePartOfSignal && samplesPerSemiPeriod == 0)) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriod > minSamples) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = false;
                }
            }
        }

        double signalFrequency = (samplesPerSemiPeriod == 0 ? 0 : (adc.getFrequency() / (samplesPerSemiPeriod * 2)));

        if (signalFrequency < 5) {
            return signalFrequency;
        } else  if (signalFrequency < estimatedFrequency / 1.5 || signalFrequency > estimatedFrequency * 1.5) {
            return estimatedFrequency;
        } else {
            return signalFrequency;
        }
    }

    private void countSamples(double frequency) {
        if (frequency == 1) {
            samplesPerSemiPeriod++;
        }
    }

    private double calculateLoadsCounter() {
        return calculateFrequency();
    }

    private double calculateRms() {
        double summ = 0;
        for (int i = channel; i < data.length; i += channels) {
            summ += (data[i] - zeroShift) * (data[i] - zeroShift);
        }
        return Math.sqrt(summ / data.length * channels);
    }

    private double calculateZeroShift() {
        return (maxSignalValue + minSignalValue) / 2;
    }

    private void checkCalibration(boolean isCalibrationExists, double averageCount) {
        if (isCalibrationExists) {
            if (averageIterator < averageCount || averageCount == 1) {
                sumCalibratedParameters();
            }

            if (!(averageIterator < averageCount)) {
                calculateCalibratedParameters(averageCount);
            }
        }
    }

    private void sumCalibratedParameters() {
        if (lowerBound < 0 & calibrationFirstPointLoadValue >= 0) {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(amplitude);
            bufferedCalibratedRms += calibratedRms = applyCalibration(rms);
        } else {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(adc, amplitude);
            bufferedCalibratedRms += calibratedRms = applyCalibration(adc, rms);
        }
        bufferedCalibratedZeroShift += calibratedZeroShift = applyCalibration(adc, zeroShift);
    }

    private void calculateCalibratedParameters(double averageCount) {
        calibratedAmplitude = bufferedCalibratedAmplitude / averageCount;
        calibratedRms = bufferedCalibratedRms / averageCount;
        calibratedZeroShift = bufferedCalibratedZeroShift / averageCount;
        bufferedCalibratedAmplitude = bufferedCalibratedRms = bufferedCalibratedZeroShift = 0;
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
        calibrationFirstPointChannelValue = CalibrationPointModel.parseChannelValue(firstCalibrationPoint);
        calibrationFirstPointLoadValue = CalibrationPointModel.parseLoadValue(firstCalibrationPoint);
        calibrationSecondPointChannelValue = CalibrationPointModel.parseChannelValue(secondCalibrationPoint);
        calibrationSecondPointLoadValue = CalibrationPointModel.parseLoadValue(secondCalibrationPoint);
    }

    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (calibrationSecondPointLoadValue - calibrationFirstPointLoadValue) / (calibrationSecondPointChannelValue - calibrationFirstPointChannelValue);
            double b = calibrationFirstPointLoadValue - k * calibrationFirstPointChannelValue;
            calibratedValue = k * value + b;
        }
    }

    public void defineCalibratedBounds(ADC adc) {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        if (!calibrationSettings.isEmpty()) {
            calibrationValueName = CalibrationPointModel.parseValueName(calibrationSettings.get(0));
            double minLoadValue = Double.MAX_VALUE;
            double maxLoadValue = Double.MIN_VALUE;
            int GRAPH_SCALE = 5;

            for (String calibrationSetting : calibrationSettings) {
                double loadValue = CalibrationPointModel.parseLoadValue(calibrationSetting);

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
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getCalibratedAmplitude() {
        return calibratedAmplitude;
    }

    public double getCalibratedRms() {
        return calibratedRms;
    }

    public double getCalibratedZeroShift() {
        return calibratedZeroShift;
    }

    public String getCalibrationValueName() {
        return calibrationValueName;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getLoadsCounter() {
        return loadsCounter;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getRms() {
        return rms;
    }

    public double getSignalFrequency() {
        return signalFrequency;
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public double getZeroShift() {
        return zeroShift;
    }

    public void setAccurateFrequencyCalculation(boolean accurateFrequencyCalculation) {
        this.accurateFrequencyCalculation = accurateFrequencyCalculation;
    }

    public void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(int frequency) {
        this.signalFrequency = frequency;
    }

    public void setLoadsCounter(int loadsCounter) {
        this.loadsCounter = loadsCounter;
    }

    public void setRMS(int rms) {
        this.rms = rms;
    }

    public void setZeroShift(int zeroShift) {
        this.zeroShift = zeroShift;
    }
}
