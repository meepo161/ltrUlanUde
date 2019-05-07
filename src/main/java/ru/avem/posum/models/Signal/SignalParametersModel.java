package ru.avem.posum.models.Signal;

import com.j256.ormlite.stmt.query.In;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Calibration.CalibrationPointModel;

import java.net.Inet4Address;
import java.util.*;

public class SignalParametersModel {
    private double accuracyCoefficient;
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
    private double bufferedSamplesPerSemiPeriods;
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
    private double loadsCounter;
    private double lowerBound;
    private double maxSignalValue;
    private int minSamples = 20;
    private double minSignalValue;
    private double rms;
    private int samplesPerSemiPeriods;
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
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int i = channel; i < data.length; i += channels) {
            if (data[i] > max) {
                max = data[i];
            }

            if (data[i] < min) {
                min = data[i];
            }
        }

        minSignalValue = min;
        maxSignalValue = max;

        if (signalFrequency > 2) {
            calculateAverageMinAndMaxValues();
        }
    }

    private void calculateAverageMinAndMaxValues() {
        double[] channelData = new double[data.length / channels];
        List<Double> maxs = new ArrayList<>();
        List<Double> mins = new ArrayList<>();
        int pieces;

        if (signalFrequency < 5) {
            pieces = 2;
        } else if (signalFrequency > 5 && signalFrequency < 10) {
            pieces = 4;
        } else if (signalFrequency > 10 && signalFrequency < 20) {
            pieces = 5;
        } else {
            pieces = 10;
        }

        for (int i = channel, j = 0; i < data.length; i += channels) {
            channelData[j++] = data[i];
        }

        for (int pieceIndex = 0; pieceIndex < pieces; pieceIndex++) {
            double[] pieceOfDate = new double[channelData.length / pieces];
            System.arraycopy(channelData, pieceIndex * pieceOfDate.length, pieceOfDate, 0, pieceOfDate.length);
            DoubleSummaryStatistics statistics = Arrays.stream(pieceOfDate).summaryStatistics();
            maxs.add(statistics.getMax());
            mins.add(statistics.getMin());
        }

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int indexOfMax = 0;
        int indexOfMin = 0;
        for (int i = 0; i < maxs.size(); i++) {
            if (max < maxs.get(i)) {
                max = maxs.get(i);
                indexOfMax = i;
            }
            if (min > mins.get(i)) {
                min = mins.get(i);
                indexOfMin = i;
            }
        }

        maxs.remove(indexOfMax);
        mins.remove(indexOfMin);
        max = min = 0;

        for (int i = 0; i < maxs.size(); i++) {
            max += maxs.get(i) / maxs.size();
            min += mins.get(i) / mins.size();
        }

        minSignalValue = min;
        maxSignalValue = max;
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
        return defineFrequency(estimatedFrequency);
    }

    private double estimateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;
        double filteringCoefficient = 1.05;

        for (int i = channel; i < data.length; i += channels) {
            if (amplitude + zeroShift > 0) {
                if (zeroShift > 0) {
                    if (data[i] > zeroShift * filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < zeroShift / filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                } else {
                    if (data[i] > zeroShift / filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < zeroShift * filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                }
            } else if (amplitude + zeroShift < 0 && zeroShift < 0) {
                if (data[i] < zeroShift * filteringCoefficient && !positivePartOfSignal) {
                    frequency++;
                    positivePartOfSignal = true;
                } else if (data[i] > zeroShift / filteringCoefficient && positivePartOfSignal) {
                    positivePartOfSignal = false;
                }
            }
        }

        return frequency;
    }

    private double defineFrequency(double estimatedFrequency) {
        int shift = 1_000;
        double firstValue = data[0] + shift;
        boolean firstPeriod = true;
        double periods = 0;
        boolean positivePartOfSignal = !(firstValue > (zeroShift + shift));
        bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods = 0;
        int zeroTransitionCounter = 0;

        for (int index = channel; index < data.length; index += channels) {
            double value = data[index] + shift;
            double centerOfSignal = zeroShift + shift;

            countSamples(zeroTransitionCounter);

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= channels * minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal)) {
                    zeroTransitionCounter++;
                    if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
                        bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
                        periods++;
                    }
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriods >= minSamples) {
                    zeroTransitionCounter++;
                    if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
                        bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
                        periods++;
                    }
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index > channels * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value >= centerOfSignal && !positivePartOfSignal)) {
                    zeroTransitionCounter++;
                    if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
                        bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
                        periods++;
                    }
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriods > minSamples) {
                    zeroTransitionCounter++;
                    if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
                        bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
                        periods++;
                    }
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods == 0 ? 0 : bufferedSamplesPerSemiPeriods / periods;
        double signalFrequency = (samplesPerPeriod == 0 ? 0 : (adc.getFrequency() / samplesPerPeriod));

        System.out.println("Samples per period: " + samplesPerPeriod);

        if (signalFrequency < 5) {
            return signalFrequency;
        } else if (signalFrequency < estimatedFrequency / 2.5 || signalFrequency > estimatedFrequency * 2.5) {
            return estimatedFrequency;
        } else {
            return signalFrequency;
        }
    }

    private void countSamples(double zeroTransitionCounter) {
        if (zeroTransitionCounter >= 1) {
            samplesPerSemiPeriods++;
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

    public void setAccuracyCoefficient(double accuracyCoefficient) {
        this.accuracyCoefficient = accuracyCoefficient;
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

    public void setMinSamples(int minSamples) {
        this.minSamples = minSamples;
    }

    public void setRMS(int rms) {
        this.rms = rms;
    }

    public void setZeroShift(int zeroShift) {
        this.zeroShift = zeroShift;
    }
}
