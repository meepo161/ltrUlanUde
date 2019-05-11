package ru.avem.posum.models.Signal;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Calibration.CalibrationPointModel;
import ru.avem.posum.utils.MovingAverage;

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
    private double bufferedDC;
    private double bufferedCalibratedAmplitude;
    private double bufferedCalibratedRms;
    private double bufferedCalibratedZeroShift;
    private double bufferedSamplesPerSemiPeriods;
    private double calibratedAmplitude;
    private double calibratedRms;
    private double calibratedDC;
    private double calibratedValue;
    private double firstLoadValue;
    private double firstChannelValue;
    private double secondLoadValue;
    private double secondChannelValue;
    private String calibratedValueName;
    private int channel;
    private int channels;
    private double[] data;
    private double dc;
    private double loadsCounter;
    private double lowerBound;
    private double maxSignalValue;
    private int minSamples = 20;
    private double minSignalValue;
    private double rms;
    private int periods;
    private int samplesPerSemiPeriods;
    private int samplesPerSemiPeriod;
    private double signalFrequency;
    private double tickUnit;
    private double upperBound;
    private int zeroTransitionCounter;

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
        //TODO
//        MovingAverage ma=new MovingAverage(10);
//        this.data = ma.exponentialMovingAverage(rawData,channel);
        this.data = rawData;
        this.channel = channel;
    }

    private void calculateMinAndMaxValues() {
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;

        for (int i = channel; i < data.length; i += channels) {
            if (data[i] > max) {
                max = data[i];
            }

            if (data[i] < min) {
                min = data[i];
            }
        }

        maxSignalValue = max;
        minSignalValue = min;

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

        maxSignalValue = max;
        minSignalValue = min;
    }

    private void calculateParameters(double averageCount) {
        if (averageCount == 1) {
            amplitude = rms = dc = 0;
            bufferedAmplitude = amplitude = calculateAmplitude();
            bufferedDC = dc = calculateDC();
            bufferedRms = rms = calculateRms();
            bufferedFrequency = signalFrequency = accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            loadsCounter += calculateLoadsCounter();
        } else if (averageIterator < averageCount) {
            bufferedAmplitude += calculateAmplitude();
            bufferedRms += calculateRms();
            bufferedDC += calculateDC();
            bufferedFrequency += accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            bufferedLoadsCounter += calculateLoadsCounter();
            averageIterator++;
        } else {
            amplitude = bufferedAmplitude / averageCount;
            rms = bufferedRms / averageCount;
            dc = bufferedDC / averageCount;
            signalFrequency = bufferedFrequency / averageCount;
            loadsCounter += bufferedLoadsCounter / averageCount;
            bufferedAmplitude = bufferedFrequency = bufferedLoadsCounter = bufferedRms = bufferedDC = 0;
            averageIterator = 0;
        }
    }

    private double calculateAmplitude() {
        return (maxSignalValue - minSignalValue) / 2;
    }

    private double calculateDC() {
        //TODO
//        double shift = -0.00049;
        double shift = -0;
        return (maxSignalValue + minSignalValue) / 2 - shift;
    }

    private double calculateRms() {
        double summ = 0;
        for (int i = channel; i < data.length; i += channels) {
            summ += (data[i] - dc) * (data[i] - dc); //TODO
//            summ += (data[i]) * (data[i]);
        }
        return Math.sqrt(summ / data.length * channels);
    }

    private double calculateFrequency() {
        double frequency;

        if (estimateFrequency() < accuracyCoefficient) {
            frequency = defineFrequencyFirstAlgorithm();
        } else {
            frequency = defineFrequencySecondAlgorithm();
        }

        double lowerLimitOfAmplitude = ((Math.abs(ADC.MeasuringRangeOfChannel.LOWER_BOUND.getBoundValue()) +
                Math.abs(ADC.MeasuringRangeOfChannel.UPPER_BOUND.getBoundValue())) / 2) * 0.01;

        return amplitude < lowerLimitOfAmplitude ? 0 : frequency;
    }

    private double estimateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;
        double filteringCoefficient = 1.05;

        for (int i = channel; i < data.length; i += channels) {
            if (amplitude + dc >= 0) {
                if (dc >= 0) {
                    if (data[i] >= dc * filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < dc / filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                } else {
                    if (data[i] >= dc / filteringCoefficient && !positivePartOfSignal) {
                        frequency++;
                        positivePartOfSignal = true;
                    } else if (data[i] < dc * filteringCoefficient && positivePartOfSignal) {
                        positivePartOfSignal = false;
                    }
                }
            } else if (amplitude + dc < 0 && dc < 0) {
                if (data[i] < dc * filteringCoefficient && !positivePartOfSignal) {
                    frequency++;
                    positivePartOfSignal = true;
                } else if (data[i] >= dc / filteringCoefficient && positivePartOfSignal) {
                    positivePartOfSignal = false;
                }
            }
        }

        return frequency;
    }

    private double defineFrequencyFirstAlgorithm() {
        int shift = 1_000;
        double firstValue = data[channel] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc + shift));
        samplesPerSemiPeriod = zeroTransitionCounter = 0;

        for (int index = channel; index < data.length; index += channels) {
            double value = data[index] + shift;
            double centerOfSignal = dc + shift;

            countSamplesFirstAlgorithm();

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= channels * minSamples)) {
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
                if (value < centerOfSignal && firstPeriod && (index >= channels * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value >= centerOfSignal && !positivePartOfSignal && samplesPerSemiPeriod == 0)) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriod >= minSamples) {
                    zeroTransitionCounter++;
                    positivePartOfSignal = false;
                }
            }
        }

        return (samplesPerSemiPeriod == 0 ? 0 : (adc.getFrequency() / (samplesPerSemiPeriod * 2)));
    }

    private void countSamplesFirstAlgorithm() {
        if (zeroTransitionCounter == 1) {
            samplesPerSemiPeriod++;
        }
    }

    private double defineFrequencySecondAlgorithm() {
        int shift = 1_000;
        double firstValue = data[0] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc + shift));
        bufferedSamplesPerSemiPeriods = periods = samplesPerSemiPeriods = zeroTransitionCounter = 0;

        for (int index = channel; index < data.length; index += channels) {
            double value = data[index] + shift;
            double centerOfSignal = dc + shift;

            countSamplesSecondAlgorithm();

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= channels * minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal)) {
                    countPeriods();
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriods >= minSamples) {
                    countPeriods();
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index > channels * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value >= centerOfSignal && !positivePartOfSignal)) {
                    countPeriods();
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriods > minSamples) {
                    countPeriods();
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods == 0 ? 0 : bufferedSamplesPerSemiPeriods / periods;
        return (samplesPerPeriod == 0 ? 0 : (adc.getFrequency() / samplesPerPeriod));
    }

    private void countPeriods() {
        zeroTransitionCounter++;
        if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
            bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
            periods++;
        }
    }

    private void countSamplesSecondAlgorithm() {
        if (zeroTransitionCounter >= 1) {
            samplesPerSemiPeriods++;
        }
    }

    private double calculateLoadsCounter() {
        return calculateFrequency();
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
        if (lowerBound < 0 & firstLoadValue >= 0) {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(amplitude);
            bufferedCalibratedRms += calibratedRms = applyCalibration(rms);
        } else {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(adc, amplitude);
            bufferedCalibratedRms += calibratedRms = applyCalibration(adc, rms);
        }
        bufferedCalibratedZeroShift += calibratedDC = applyCalibration(adc, dc);
    }

    private void calculateCalibratedParameters(double averageCount) {
        calibratedAmplitude = bufferedCalibratedAmplitude / averageCount;
        calibratedRms = bufferedCalibratedRms / averageCount;
        calibratedDC = bufferedCalibratedZeroShift / averageCount;
        bufferedCalibratedAmplitude = bufferedCalibratedRms = bufferedCalibratedZeroShift = 0;
    }

    private double applyCalibration(double value) {
        defineBounds();
        setBounds();
        return calibratedValue = value / (Math.abs(lowerBound) + Math.abs(upperBound)) * secondLoadValue;
    }

    private void defineBounds() {
        if (firstChannelValue > secondChannelValue) {
            double bufferForLoadValue = firstLoadValue;
            double bufferForChannelValue = firstChannelValue;
            firstLoadValue = secondLoadValue;
            firstChannelValue = secondChannelValue;
            secondLoadValue = bufferForLoadValue;
            secondChannelValue = bufferForChannelValue;
        }
    }

    private void setBounds() {
        lowerBound = firstChannelValue;
        upperBound = secondChannelValue;
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
        firstChannelValue = CalibrationPointModel.parseChannelValue(firstCalibrationPoint);
        firstLoadValue = CalibrationPointModel.parseLoadValue(firstCalibrationPoint);
        secondChannelValue = CalibrationPointModel.parseChannelValue(secondCalibrationPoint);
        secondLoadValue = CalibrationPointModel.parseLoadValue(secondCalibrationPoint);
    }

    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (secondLoadValue - firstLoadValue) / (secondChannelValue - firstChannelValue);
            double b = firstLoadValue - k * firstChannelValue;
            calibratedValue = k * value + b;
        }
    }

    public void defineCalibratedBounds(ADC adc) {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        if (!calibrationSettings.isEmpty()) {
            calibratedValueName = CalibrationPointModel.parseValueName(calibrationSettings.get(0));
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

    public double getCalibratedDC() {
        return calibratedDC;
    }

    public double getCalibratedRms() {
        return calibratedRms;
    }

    public String getCalibratedValueName() {
        return calibratedValueName;
    }

    public double getDc() {
        return dc;
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

    public void setDc(int dc) {
        this.dc = dc;
    }
}
