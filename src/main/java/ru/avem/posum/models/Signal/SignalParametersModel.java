package ru.avem.posum.models.Signal;

import ru.avem.posum.db.models.Calibration;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.Calibration.CalibrationPoint;

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
    private double shift;
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
//        MovingAverage ma = new MovingAverage(10);
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
        return (maxSignalValue - minSignalValue) / 2 + shift;
    }

    private double calculateDC() {
        double dc = (maxSignalValue + minSignalValue) / 2;
        double upperBound = ADC.MeasuringRangeOfChannel.UPPER_BOUND.getBoundValue();
        double lowerBound = ADC.MeasuringRangeOfChannel.LOWER_BOUND.getBoundValue();

        if (dc != upperBound || dc != lowerBound) {
            dc -= shift;
        }

        return dc;
    }

    private double calculateRms() {
        double summ = 0;
        for (int i = channel; i < data.length; i += channels) {
            summ += (data[i] - dc) * (data[i] - dc);
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

        return amplitude < getLowerLimitOfAmplitude() ? 0 : frequency;
    }


    private double getLowerLimitOfAmplitude() {
        return ((Math.abs(ADC.MeasuringRangeOfChannel.LOWER_BOUND.getBoundValue()) +
                Math.abs(ADC.MeasuringRangeOfChannel.UPPER_BOUND.getBoundValue())) / 2) * 0.001;
    }

    private double estimateFrequency() {
        boolean positivePartOfSignal = false;
        double frequency = 0;
        double lowerLimitOfAmplitude = getLowerLimitOfAmplitude();

        for (int i = channel; i < data.length; i += channels) {
            if (data[i] >= dc + lowerLimitOfAmplitude && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (data[i] < dc - lowerLimitOfAmplitude && positivePartOfSignal) {
                positivePartOfSignal = false;
            }
        }

//        System.out.printf("Estimated frequency: %f\n", frequency);

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
        firstChannelValue = CalibrationPoint.parseChannelValue(firstCalibrationPoint);
        firstLoadValue = CalibrationPoint.parseLoadValue(firstCalibrationPoint);
        secondChannelValue = CalibrationPoint.parseChannelValue(secondCalibrationPoint);
        secondLoadValue = CalibrationPoint.parseLoadValue(secondCalibrationPoint);
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
            double minLoadValue = Double.MAX_VALUE;
            double maxLoadValue = Double.MIN_VALUE;
            int GRAPH_SCALE = 5;

            defineValueName();

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
    }

    private void defineValueName() {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);

        for (String calibration : calibrationSettings) {
            calibratedValueName = CalibrationPoint.parseValueName(calibration);

            if (!calibratedValueName.isEmpty()) {
                calibratedValueName = "В";
            } else {
                System.out.println("Value name: " + calibratedValueName);
                break;
            }
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

    public void setDc(int dc) {
        this.dc = dc;
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

    public void setShift(double shift) {
        this.shift = shift;
    }
}
