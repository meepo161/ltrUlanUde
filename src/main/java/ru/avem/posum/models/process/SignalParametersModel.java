package ru.avem.posum.models.process;

import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import uk.me.berndporr.iirj.Butterworth;

import java.util.*;

public class SignalParametersModel {
    private final int SLOTS = 16; // максимальное количество слотов
    private final int CHANNELS = 4; // количество каналов АЦП

    private double[] adcFrequencies; // частоты дискретизации модулей
    private double[][] amplitudes = new double[SLOTS][]; // амплитуды каналов модулей
    private double[][] bufferedFrequency = new double[SLOTS][]; // сохраненные частоты каналов модулей
    private double[][] bufferedRms = new double[SLOTS][]; // сохраненные действующие значения модулей
    private int[][] bufferedSamplesPerSemiPeriods = new int[SLOTS][]; // сохраненные значения количества сэмплов в полупериоде
    private int[][] frequencyCalculationCounters = new int[SLOTS][CHANNELS]; // счетчик для перезапуска расчета частоты
    private double[][] data = new double[SLOTS][]; // данные модулей
    private double[][] dc = new double[SLOTS][]; // постоянные составляющие каналов модулей
    private double[][] frequencies = new double[SLOTS][]; // частоты каналов модулей
    private Butterworth iir = new Butterworth(); // фильтр
    private double[][] loadsCounter = new double[SLOTS][]; // счетчик нагружений каналов модулей
    private double[][] maxSignalValues = new double[SLOTS][]; // максимальные значения на каналах модулей
    private double[][] minSignalValues = new double[SLOTS][]; // минимальные значения на каналах модулей
    private int[][] periods = new int[SLOTS][]; // количество периодов каналов модулей
    private double[][] rms = new double[SLOTS][]; // действующие значения каналов модулей
    private double[][] samplesPerSemiPeriod = new double[SLOTS][]; // количество сэмплов в полупериоде каналов модулей
    private int[][] samplesPerSemiPeriods = new int[SLOTS][]; // количество сэмплов в полупериодах каналов модулей
    private String[] typesOfModules = new String[SLOTS]; // список модулей
    private double[][] zeroTransitionCounter = new double[SLOTS][]; // количество переходов через 0 на каналах  модулей

    public SignalParametersModel() {
        initArrays();
    }

    private void initArrays() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            adcFrequencies = new double[SLOTS];
            amplitudes[moduleIndex] = new double[CHANNELS];
            bufferedFrequency[moduleIndex] = new double[CHANNELS];
            bufferedRms[moduleIndex] = new double[CHANNELS];
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
    }

    // Задает данные модулей
    public void setData(double[][] data) {
        this.data = data;
    }

    // Задает частоты дискеризации модулей
    public void setAdcFrequencies(List<Modules> modules) {
        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            adcFrequencies[moduleIndex] = modules.get(moduleIndex).getDataLength() / CHANNELS; // TODO: change this shit
        }
    }

    // Рассчитывает параметры измеренного сигнала на каналах модулей
    public void calculateParameters() {
        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            if (!typesOfModules[moduleIndex].equals(Crate.LTR34)) {
                calculateMinAndMaxValues(moduleIndex);
                calculateAmplitudes(moduleIndex);
                calculateDC(moduleIndex);
                calculateRms(moduleIndex);
                calculateFrequencies(moduleIndex);
                calculateLoadsCounters(moduleIndex);
            }
        }
    }

    // Рассчитывает минимальное и максимальное значения
    private void calculateMinAndMaxValues(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            double min = Integer.MAX_VALUE;
            double max = Integer.MIN_VALUE;

            iir.lowPass(1, data[moduleIndex].length / CHANNELS, 50);
            for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
                if (data[moduleIndex][i] > max) {
                    max = data[moduleIndex][i];
                }

                if (data[moduleIndex][i] < min) {
                    min = data[moduleIndex][i];
                }
            }

            maxSignalValues[moduleIndex][channelIndex] = max;
            minSignalValues[moduleIndex][channelIndex] = min;

            if (frequencies[moduleIndex][channelIndex] > 2) {
                calculateAverageMinAndMaxValues(moduleIndex, channelIndex);
            }
        }
    }

    // Более точный расчет минимального и максимального значений
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

    // Рассчитывает аплитуду
    private void calculateAmplitudes(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            double amplitude = (maxSignalValues[moduleIndex][channelIndex] - minSignalValues[moduleIndex][channelIndex]) / 2;
            amplitudes[moduleIndex][channelIndex] = amplitude < 0 ? 0 : amplitude;
        }
    }

    // Рассчитывает постоянную составляющую
    private void calculateDC(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            double dc = (maxSignalValues[moduleIndex][channelIndex] + minSignalValues[moduleIndex][channelIndex]) / 2;
            this.dc[moduleIndex][channelIndex] = dc;
        }
    }

    // Рассчитывает действующее значение
    private void calculateRms(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            double summ = 0;

            for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
                summ += (data[moduleIndex][i] - dc[moduleIndex][channelIndex]) * (data[moduleIndex][i] - dc[moduleIndex][channelIndex]);
            }

            double rms = Math.sqrt(summ / data[moduleIndex].length * CHANNELS);

            double buffer = bufferedRms[moduleIndex][channelIndex];
            if (!(rms - buffer > 0.05) && (rms > buffer)) {
                rms = buffer;
            }

            bufferedRms[moduleIndex][channelIndex] = this.rms[moduleIndex][channelIndex];
            this.rms[moduleIndex][channelIndex] = rms < 0 ? 0 : rms;
        }
    }

    // Рассчитывает частоту
    private void calculateFrequencies(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            int estimatedFrequency = estimateFrequency(moduleIndex, channelIndex);
            int accuracyCoefficient = 5; // коэффициент для переключения алгоритмов
            double frequency;
            int upperLimitOfFrequency = 1000;

            frequency = defineFrequencySecondAlgorithm(moduleIndex, channelIndex, estimatedFrequency * 2);
            double buffer = bufferedFrequency[moduleIndex][channelIndex];

            if (!(frequency - buffer > 1) && (frequency > bufferedFrequency[moduleIndex][channelIndex])) {
                frequency = bufferedFrequency[moduleIndex][channelIndex];
            }

            if (frequencyCalculationCounters[moduleIndex][channelIndex] == 10) {
                frequency = defineFrequencySecondAlgorithm(moduleIndex, channelIndex, estimatedFrequency * 2);
                frequencyCalculationCounters[moduleIndex][channelIndex] = 0;
            }

            if (frequency <= accuracyCoefficient) {
                frequency = defineFrequencyFirstAlgorithm(moduleIndex, channelIndex);
            }

            frequencies[moduleIndex][channelIndex] = amplitudes[moduleIndex][channelIndex] < getLowerLimitOfAmplitude(moduleIndex) ? 0 : frequency;
            frequencies[moduleIndex][channelIndex] = frequencies[moduleIndex][channelIndex] > upperLimitOfFrequency ? bufferedFrequency[moduleIndex][channelIndex] : frequencies[moduleIndex][channelIndex];
            bufferedFrequency[moduleIndex][channelIndex] = frequencies[moduleIndex][channelIndex];
        }
    }

    // Возвращает минимальное значение амплитуды
    private double getLowerLimitOfAmplitude(int moduleIndex) {
        switch (typesOfModules[moduleIndex]) {
            case Crate.LTR24:
                return 0.05;
            case Crate.LTR212:
                return 0.001;
//            case Crate.LTR27:
//                return 0.01;
            default:
                return 0;
        }
    }

    // Оценивает частоту
    private int estimateFrequency(int moduleIndex, int channelIndex) {
        boolean positivePartOfSignal = false;
        int frequency = 0;
        double lowerLimitOfAmplitude = getLowerLimitOfAmplitude(moduleIndex);
        iir.lowPass(10, data[moduleIndex].length / CHANNELS, 50);

        for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
            double value = iir.filter(data[moduleIndex][i]);
            if (value >= dc[moduleIndex][channelIndex] + lowerLimitOfAmplitude && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (value < dc[moduleIndex][channelIndex] - lowerLimitOfAmplitude && positivePartOfSignal) {
                positivePartOfSignal = false;
            }
        }

        return frequency;
    }

    // Возвращает значение частоты
    private double defineFrequencyFirstAlgorithm(int moduleIndex, int channelIndex) {
        int shift = 1_000;
        double firstValue = data[moduleIndex][channelIndex] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc[moduleIndex][channelIndex] + shift));
        double minAmplitude = getLowerLimitOfAmplitude(moduleIndex);
        samplesPerSemiPeriod[moduleIndex][channelIndex] = zeroTransitionCounter[moduleIndex][channelIndex] = 0;

        for (int index = channelIndex; index < data[moduleIndex].length; index += CHANNELS) {
            double value = data[moduleIndex][index] + shift;
            double centerOfSignal = dc[moduleIndex][channelIndex] + shift;

            countSamplesFirstAlgorithm(moduleIndex, channelIndex);

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (value >= minAmplitude)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal && positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] == 0)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && (value >= minAmplitude)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value <= centerOfSignal && firstPeriod && (value >= minAmplitude)) {
                    positivePartOfSignal = false;
                } else if ((value > centerOfSignal && !positivePartOfSignal && samplesPerSemiPeriod[moduleIndex][channelIndex] == 0)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value <= centerOfSignal && !firstPeriod && positivePartOfSignal && (value >= minAmplitude)) {
                    zeroTransitionCounter[moduleIndex][channelIndex]++;
                    positivePartOfSignal = false;
                }
            }
        }

        return (samplesPerSemiPeriod[moduleIndex][channelIndex] == 0 ? 0 : (adcFrequencies[moduleIndex] / (samplesPerSemiPeriod[moduleIndex][channelIndex] * 2)));
    }

    // Считает количество сэмплов в полупериоде
    private void countSamplesFirstAlgorithm(int moduleIndex, int channelIndex) {
        if (zeroTransitionCounter[moduleIndex][channelIndex] == 1) {
            samplesPerSemiPeriod[moduleIndex][channelIndex]++;
        }
    }

    // Возвращает значение частоты
    private double defineFrequencySecondAlgorithm(int moduleIndex, int channelIndex, int cutoffFrequency) {
        int shift = 1_000;
        double firstValue = data[moduleIndex][0] + shift;
        boolean firstPeriod = true;
        boolean positivePartOfSignal = !(firstValue > (dc[moduleIndex][channelIndex] + shift));
        double minAmplitude = getLowerLimitOfAmplitude(moduleIndex);
        bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] = 0;
        periods[moduleIndex][channelIndex] = 0;
        samplesPerSemiPeriods[moduleIndex][channelIndex] = 0;
        zeroTransitionCounter[moduleIndex][channelIndex] = 0;

        iir.lowPass(1, data[moduleIndex].length / CHANNELS, cutoffFrequency);
        for (int index = channelIndex; index < data[moduleIndex].length; index += CHANNELS) {
            double value = iir.filter(data[moduleIndex][index] + shift);
            double centerOfSignal = dc[moduleIndex][channelIndex] + shift;

            countSamplesSecondAlgorithm(moduleIndex, channelIndex);

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (value >= minAmplitude)) {
                    positivePartOfSignal = true;
                } else if ((value < centerOfSignal) && positivePartOfSignal) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value <= centerOfSignal && firstPeriod && (value >= minAmplitude)) {
                    positivePartOfSignal = false;
                } else if ((value > centerOfSignal && !positivePartOfSignal)) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value <= centerOfSignal && !firstPeriod && positivePartOfSignal) {
                    countPeriods(moduleIndex, channelIndex);
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] == 0 ? 0 : (double) bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] / periods[moduleIndex][channelIndex];
        return (samplesPerPeriod == 0 ? 0 : (adcFrequencies[moduleIndex] / samplesPerPeriod));
    }

    // Считает количество сэмплов в полупериоде
    private void countSamplesSecondAlgorithm(int moduleIndex, int channelIndex) {
        if (zeroTransitionCounter[moduleIndex][channelIndex] >= 1) {
            samplesPerSemiPeriods[moduleIndex][channelIndex]++;
        }
    }

    // Считает количество периодов
    private void countPeriods(int moduleIndex, int channelIndex) {
        zeroTransitionCounter[moduleIndex][channelIndex]++;
        if (zeroTransitionCounter[moduleIndex][channelIndex] % 2 != 0 && zeroTransitionCounter[moduleIndex][channelIndex] > 2) {
            bufferedSamplesPerSemiPeriods[moduleIndex][channelIndex] = samplesPerSemiPeriods[moduleIndex][channelIndex];
            periods[moduleIndex][channelIndex]++;
        }
    }

    // Считает количество нагружений
    private void calculateLoadsCounters(int moduleIndex) {
        for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
            loadsCounter[moduleIndex][channelIndex] += frequencies[moduleIndex][channelIndex];
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

    public void setTypesOfModules(String[] typesOfModules) {
        this.typesOfModules = typesOfModules;
    }
}
