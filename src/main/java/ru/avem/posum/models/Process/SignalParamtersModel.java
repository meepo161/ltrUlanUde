package ru.avem.posum.models.Process;

import ru.avem.posum.db.models.Modules;

import javax.swing.plaf.SliderUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class SignalParamtersModel {
    private final int SLOTS = 16; // максимальное количество слотов
    private final int CHANNELS = 4; // количество каналов АЦП

    private double[] adcFrequencies;
    private double[][] amplitudes = new double[SLOTS][];
    private int[][] bufferedSamplesPerSemiPeriods = new int[SLOTS][];
    private double[][] data = new double[SLOTS][];
    private double[][] dc = new double[SLOTS][];
    private double[][] frequencies = new double[SLOTS][];
    private double[][] loadsCounter = new double[SLOTS][];
    private double[][] minSignalValues = new double[SLOTS][];
    private double[][] maxSignalValues = new double[SLOTS][];
    private int[][] periods = new int[SLOTS][];
    private double[][] rms = new double[SLOTS][];
    private double[][] samplesPerSemiPeriod = new double[SLOTS][];
    private int[][] samplesPerSemiPeriods = new int[SLOTS][];
    private double[][] zeroTransitionCounter = new double[SLOTS][];

    public void setFields(double[][] data) {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            adcFrequencies = new double[SLOTS];
            amplitudes[moduleIndex] = new double[CHANNELS];
            bufferedSamplesPerSemiPeriods[moduleIndex] = new int[CHANNELS];
            dc[moduleIndex] = new double[CHANNELS];
            frequencies[moduleIndex] = new double[CHANNELS];
            loadsCounter[moduleIndex] = new double[CHANNELS];
            minSignalValues[moduleIndex] = new double[CHANNELS];
            maxSignalValues[moduleIndex] = new double[CHANNELS];
            periods[moduleIndex] = new int[CHANNELS];
            rms[moduleIndex] = new double[CHANNELS];
            samplesPerSemiPeriod[moduleIndex] = new double[CHANNELS];
            samplesPerSemiPeriods[moduleIndex] = new int[CHANNELS];
            zeroTransitionCounter[moduleIndex] = new double[CHANNELS];
        }

        this.data = data;
    }

    public void setAdcFrequencies(List<Modules> modules) {
        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            adcFrequencies[moduleIndex] = modules.get(moduleIndex).getDataLength() / CHANNELS; // TODO: change this shit
        }
    }

    public void calculateParameters() {
        calculateMinAndMaxValues();
        calculateAmplitudes();
        calculateDC();
        calculateRms();
        calculateFrequencies();
        calculateLoadsCounters();
    }

    private void calculateMinAndMaxValues() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                double min = Integer.MAX_VALUE;
                double max = Integer.MIN_VALUE;

                for (int i = channelIndex; i < data.length; i += CHANNELS) {
                    if (data[moduleIndex][i] > max) {
                        max = data[channelIndex][i];
                    }

                    if (data[moduleIndex][i] < min) {
                        min = data[moduleIndex][i];
                    }
                }

                maxSignalValues[moduleIndex][channelIndex] = max;
                minSignalValues[moduleIndex][channelIndex] = min;

                System.out.printf("Slot: %d. Channel: %d. Max: %f, min: %f\n", moduleIndex + 1, channelIndex + 1, maxSignalValues[moduleIndex][channelIndex], minSignalValues[moduleIndex][channelIndex]);

                if (frequencies[moduleIndex][channelIndex] > 2) {
                    calculateAverageMinAndMaxValues(moduleIndex, channelIndex);
                }
            }

        }
    }

    private void calculateAverageMinAndMaxValues(int moduleIndex, int channelIndex) {
        double[] channelData = new double[data[moduleIndex].length / CHANNELS];
        List<Double> maxs = new ArrayList<>();
        List<Double> mins = new ArrayList<>();
        int pieces;
        double frequency = frequencies[moduleIndex][channelIndex];

        if (frequency < 5) {
            pieces = 2;
        } else if (frequency > 5 && frequency < 10) {
            pieces = 4;
        } else if (frequency > 10 && frequency < 20) {
            pieces = 5;
        } else {
            pieces = 10;
        }

        for (int i = channelIndex, j = 0; i < data[moduleIndex].length; i += CHANNELS) {
            channelData[j++] = data[moduleIndex][i];
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

        maxSignalValues[moduleIndex][channelIndex] = max;
        minSignalValues[moduleIndex][channelIndex] = min;
    }

    private void calculateAmplitudes() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                double amplitude = (maxSignalValues[moduleIndex][channelIndex] - minSignalValues[moduleIndex][channelIndex]) / 2;
                amplitudes[moduleIndex][channelIndex] = amplitude < 0 ? 0 : amplitude;
            }
        }
    }

    private void calculateDC() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                double dc = (maxSignalValues[moduleIndex][channelIndex] + minSignalValues[moduleIndex][channelIndex]) / 2;
                this.dc[moduleIndex][channelIndex] = dc;
            }
        }
    }

    private void calculateRms() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                double summ = 0;

                for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
                    summ += (data[moduleIndex][i] - dc[moduleIndex][channelIndex]) * (data[moduleIndex][i] - dc[moduleIndex][channelIndex]);
                }

                double rms = Math.sqrt(summ / data[moduleIndex].length * CHANNELS);
                this.rms[moduleIndex][channelIndex] = rms < 0 ? 0 : rms;
            }
        }
    }

    private void calculateFrequencies() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                double frequency;
                int accuracyCoefficient = 10; // коэффициент для переключения алгоритмов

                if (estimateFrequency(moduleIndex, channelIndex) < accuracyCoefficient) {
                    frequency = defineFrequencyFirstAlgorithm(moduleIndex, channelIndex);
                } else {
                    frequency = defineFrequencySecondAlgorithm(moduleIndex, channelIndex);
                }

                frequencies[moduleIndex][channelIndex] = amplitudes[moduleIndex][channelIndex] < getLowerLimitOfAmplitude() ? 0 : frequency;
            }
        }
    }

    private double getLowerLimitOfAmplitude() {
        return 0.001;
    }

    private double estimateFrequency(int moduleIndex, int channelIndex) {
        boolean positivePartOfSignal = false;
        double frequency = 0;
        double lowerLimitOfAmplitude = getLowerLimitOfAmplitude();

        for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
            if (data[moduleIndex][i] >= dc[moduleIndex][channelIndex] + lowerLimitOfAmplitude && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (data[moduleIndex][i] < dc[moduleIndex][channelIndex] - lowerLimitOfAmplitude && positivePartOfSignal) {
                positivePartOfSignal = false;
            }
        }

        return frequency;
    }

    private double defineFrequencyFirstAlgorithm(int moduleIndex, int channelIndex) {
        int shift = 1_000;
        double firstValue = data[moduleIndex][channelIndex] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc[moduleIndex][channelIndex] + shift));
        double minSamples = dc[moduleIndex][channelIndex] + getLowerLimitOfAmplitude();
        samplesPerSemiPeriod[moduleIndex][channelIndex] = zeroTransitionCounter[moduleIndex][channelIndex] = 0;

        for (int index = channelIndex; index < data.length; index += CHANNELS) {
            double value = data[moduleIndex][index] + shift;
            double centerOfSignal = dc[moduleIndex][channelIndex] + shift;

            countSamplesFirstAlgorithm(moduleIndex, channelIndex);

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= CHANNELS * minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] == 0)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value > centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] > minSamples) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index >= CHANNELS * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value >= centerOfSignal && !positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] == 0)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] >= minSamples) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = false;
                }
            }
        }

        return (samplesPerSemiPeriod[moduleIndex][channelIndex] == 0 ? 0 : (adcFrequencies[moduleIndex] / (samplesPerSemiPeriod[moduleIndex][channelIndex] * 2)));
    }

    private void countSamplesFirstAlgorithm(int moduleIndex, int channelIndex) {
        if (zeroTransitionCounter[moduleIndex][channelIndex] == 1) {
            samplesPerSemiPeriod[moduleIndex][channelIndex]++;
        }
    }

    private double defineFrequencySecondAlgorithm(int moduleIndex, int channelIndex) {
        int shift = 1_000;
        double firstValue = data[moduleIndex][0] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc[moduleIndex][channelIndex] + shift));
        double minSamples = dc[moduleIndex][channelIndex] + getLowerLimitOfAmplitude();
        bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] = 0;
        periods[moduleIndex][channelIndex] = 0;
        samplesPerSemiPeriods[moduleIndex][channelIndex] = 0;
        zeroTransitionCounter[moduleIndex][channelIndex] = 0;

        for (int index = channelIndex; index < data.length; index += CHANNELS) {
            double value = data[moduleIndex][index] + shift;
            double centerOfSignal = dc[moduleIndex][channelIndex] + shift;

            countSamplesSecondAlgorithm(moduleIndex, channelIndex);

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (index >= CHANNELS * minSamples)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal)) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && samplesPerSemiPeriods[moduleIndex][channelIndex] >= minSamples) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (index > CHANNELS * minSamples)) {
                    positivePartOfSignal = false;
                } else if ((value >= centerOfSignal && !positivePartOfSignal)) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && samplesPerSemiPeriods[moduleIndex][channelIndex] > minSamples) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] == 0 ? 0 : bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] / periods[moduleIndex][channelIndex];
        return (samplesPerPeriod == 0 ? 0 : (adcFrequencies[moduleIndex] / samplesPerPeriod));
    }

    private void countSamplesSecondAlgorithm(int moduleIndex, int channelIndex) {
        if (zeroTransitionCounter[moduleIndex][channelIndex] >= 1) {
            samplesPerSemiPeriods[moduleIndex][channelIndex]++;
        }
    }

    private void countPeriods(int moduleIndex, int channelIndex) {
        zeroTransitionCounter[moduleIndex][channelIndex]++;
        if (zeroTransitionCounter[moduleIndex][channelIndex] % 2 != 0 && zeroTransitionCounter[moduleIndex][channelIndex] > 2) {
            bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
            periods[moduleIndex][channelIndex]++;
        }
    }

    private void calculateLoadsCounters() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                loadsCounter[moduleIndex][channelIndex] += frequencies[moduleIndex][channelIndex];
            }
        }
    }

    public double getAmplitude(int moduleIndex, int channelIndex) {
        return amplitudes[moduleIndex][channelIndex];
    }

    public double getDc(int moduleIndex, int channelIndex) {
        return dc[moduleIndex][channelIndex];
    }

    public double getFrequency(int moduleIndex, int channelIndex) {
        return frequencies[moduleIndex][channelIndex];
    }

    public double getLoadsCounter(int moduleIndex, int channelIndex) {
        return loadsCounter[moduleIndex][channelIndex];
    }

    public double getRms(int moduleIndex, int channelIndex) {
        return rms[moduleIndex][channelIndex];
    }
}
