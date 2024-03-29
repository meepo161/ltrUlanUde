package ru.avem.posum.models.signal;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.models.calibration.CalibrationPoint;
import ru.avem.posum.utils.MovingAverage;
import uk.me.berndporr.iirj.Butterworth;

import java.util.*;

public class SignalParametersModel {
    private boolean accurateFrequencyCalculation = true; // флаг выполнения точных расчетов
    private ADC adc; // инстанс модуля АЦП
    private int averageIterator; // счетчик для усреднеия значений
    private double peakValue; // пиковое значение
    private double bufferedPeakValue; // сохраненное пиковое значение
    private double bufferedFrequency; // сохраненное значение частоты
    private double bufferedLoadsCounter; // сохраненное значение количества нагружений
    private double bufferedRms; // сохраненное действующее значение
    private double bufferedDC; // сохраненное значение постоянной составляющей
    private double bufferedCalibratedAmplitude; // сохраненное значение градуированной амплитуды
    private double bufferedCalibratedRms; // сохраненное значение градуированного действующего значения
    private double bufferedCalibratedZeroShift; // сохраненное значение градуированного смещения нуля
    private double bufferedSamplesPerSemiPeriods; // сохранненое значение количества сэмплов в полупериоде
    private double calibratedAmplitude; // градуированная амплитуда
    private double calibratedRms; // градуированное действующее значение
    private double calibratedDC; // градуированная постоянная составляющая
    private double calibratedValue; // градуированное значение
    private double firstLoadValue; // первое значение нагрузки
    private double firstChannelValue; // первое значение канала
    private int frequencyCalculationCounter; // счетчик расчета частоты
    private Butterworth iir = new Butterworth(); // фильтр
    private String calibratedValueName; // название физической величины
    private int channel; // номер канала
    private int channels; // количество каналов
    private double[] data; // сигнал
    private double dc; // постоянная составляющая
    private double loadsCounter; // счетчик количества нагружений
    private double lowerBound; // нижняя граница вертикальной оси графика
    private double maxSignalValue; // максимальное значение
    private int minSamples = 50; // минимальное количество сэмплов в полупериоде
    private double minSignalValue; // минимальное значение
    private double rms; // действующее значение
    private int periods; // количество периодов
    private int samplesPerSemiPeriods; // количество сэмплов в полупериодах
    private int samplesPerSemiPeriod; // количество сэмплов в полупериоде
    private double savedFrequency; // сохнаренное значение частоты
    private double savedRms; // сохраненное действующее значение
    private double secondLoadValue; // второе значение нагрузки
    private double secondChannelValue; // второе значение канала
    private double shift; // смещение сигнала
    private double signalFrequency; // частота
    private double tickUnit;
    private double upperBound; // верхняя граница вертикальной оси графика
    private int zeroTransitionCounter; // счетчик переходов через ноль

    public void setFields(ADC adc, int channel) {
        this.adc = adc;
        this.channel = channel;
        this.channels = adc.getChannelsCount();
    }

    // Расчитывает параметры сигнала
    public void calculateParameters(double[] signal, double averageCount, boolean isCalibrationExists) {
        setFields(signal, channel);
        calculateMinAndMaxValues();
        calculateParameters(averageCount);
        checkCalibration(isCalibrationExists, averageCount);
    }

    private void setFields(double[] rawData, int channel) {
        MovingAverage ma = new MovingAverage(10);
        this.data = ma.exponentialMovingAverage(rawData, channel);
        this.data = rawData;
        this.channel = channel;
    }

    // Расчитывает минимальное и максимальное значения
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

    // Более точный расчет минимального и максимального значений
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

    // Рассчитывает параметры сигнала
    private void calculateParameters(double averageCount) {
        if (averageCount == 1) {
            peakValue = rms = dc = 0;
            bufferedPeakValue = peakValue = calculatePeakValue();
            bufferedDC = dc = calculateDC();
            bufferedRms = rms = calculateRms();
            bufferedFrequency = signalFrequency = accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            loadsCounter += calculateLoadsCounter();
        } else if (averageIterator < averageCount) {
            bufferedPeakValue += calculatePeakValue();
            bufferedRms += calculateRms();
            bufferedDC += calculateDC();
            bufferedFrequency += accurateFrequencyCalculation ? calculateFrequency() : estimateFrequency();
            bufferedLoadsCounter += calculateLoadsCounter();
            averageIterator++;
        } else {
            peakValue = bufferedPeakValue / averageCount;
            rms = bufferedRms / averageCount;
            dc = bufferedDC / averageCount;
            signalFrequency = bufferedFrequency / averageCount;
            loadsCounter += bufferedLoadsCounter / averageCount;
            bufferedPeakValue = bufferedFrequency = bufferedLoadsCounter = bufferedRms = bufferedDC = 0;
            averageIterator = 0;
        }
    }

    // Расчитвает пиковое значение сигнала
    private double calculatePeakValue() {
        double peakValue = (maxSignalValue - minSignalValue);
        return peakValue < 0 ? 0 : peakValue;
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

    // Рассчитывает действующее значение сигнала
    private double calculateRms() {
        double summ = 0;

        for (int i = channel; i < data.length; i += channels) {
            summ += (data[i] - dc) * (data[i] - dc);
        }

        double rms = Math.sqrt(summ / data.length * channels);
        return rms < 0 ? 0 : rms;
    }

    // Рассчитывает частоту сигнала
    private double calculateFrequency() {
        int estimatedFrequency = estimateFrequency();
        if (estimatedFrequency > 5000) {
            return estimatedFrequency;
        }

        int filterCoefficient = 10; // значение по умолчанию
        minSamples = 200;
        int samplingRate = (int) adc.getFrequency(); // частота дискретизации

        if (samplingRate < 1000) {
            minSamples = 5;
            filterCoefficient = 2;
        } else if (samplingRate < 10_000) {
            minSamples = 20;
            filterCoefficient = 2;
        } else if (samplingRate > 10_000 && samplingRate < 50_000) {
            filterCoefficient = 5;
            minSamples = 50;
        }

        double accuracyCoefficient = 5; // коэффициент для переключения алгоритмов
        double frequency;

        frequency = defineFrequencySecondAlgorithm(estimatedFrequency * filterCoefficient);

        if (!(frequency - savedFrequency > 1) && (frequency > savedFrequency)) {
            frequency = savedFrequency;
            frequencyCalculationCounter++;
        }

        if (frequencyCalculationCounter == 10) {
            frequency = defineFrequencySecondAlgorithm(estimatedFrequency * filterCoefficient);
            frequencyCalculationCounter = 0;
        }

        if (frequency <= accuracyCoefficient) {
            frequency = defineFrequencyFirstAlgorithm();
        }

        double freq = peakValue < getLowerLimitOfAmplitude() ? 0 : frequency;
        savedFrequency = freq;
        return freq;
    }

    // Оценивает частоту сигнала
    private int estimateFrequency() {
        boolean positivePartOfSignal = false;
        int frequency = 0;
        double lowerLimitOfAmplitude = getLowerLimitOfAmplitude();

        for (int index = channel; index < data.length; index += channels) {
            if (data[index] >= dc + lowerLimitOfAmplitude && !positivePartOfSignal) {
                frequency++;
                positivePartOfSignal = true;
            } else if (data[index] < dc - lowerLimitOfAmplitude && positivePartOfSignal && (index >= channels * minSamples)) {
                positivePartOfSignal = false;
            }
        }

        return frequency;
    }

    // Возвращает минимальное значение амплитуды
    private double getLowerLimitOfAmplitude() {
        return ((Math.abs(ADC.MeasuringRangeOfChannel.LOWER_BOUND.getBoundValue()) +
                Math.abs(ADC.MeasuringRangeOfChannel.UPPER_BOUND.getBoundValue())) / 2) * 0.01;
    }

    // Алгоритм расчета частоты по количеству сэмплов в полупериоде
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

    // Считает количество сэмплов в полупериоде
    private void countSamplesFirstAlgorithm() {
        if (zeroTransitionCounter == 1) {
            samplesPerSemiPeriod++;
        }
    }

    // Алгоритм расчета частоты по количеству сэмплов в полупериодах
    private double defineFrequencySecondAlgorithm(int cutoffFrequency) {
        int shift = 1_000;
        double firstValue = data[0] + shift;
        boolean firstPeriod = true;
        double lowerLimitOfAmplitude = getLowerLimitOfAmplitude();
        boolean positivePartOfSignal = !(firstValue > (dc + shift));
        bufferedSamplesPerSemiPeriods = periods = samplesPerSemiPeriods = zeroTransitionCounter = 0;

        iir.lowPass(1, data.length / channels, cutoffFrequency);
        for (int index = channel; index < data.length; index += channels) {
            double value = iir.filter(data[index] + shift);
            double centerOfSignal = dc + shift;

            countSamplesSecondAlgorithm();

            if (firstValue >= centerOfSignal) {
                if (value >= centerOfSignal && firstPeriod && (value >= lowerLimitOfAmplitude)) {
                    positivePartOfSignal = true;
                } else if (value < centerOfSignal && positivePartOfSignal) {
                    countPeriods();
                    positivePartOfSignal = false;
                    firstPeriod = false;
                } else if (value >= centerOfSignal && !firstPeriod && !positivePartOfSignal && value >= lowerLimitOfAmplitude) {
                    countPeriods();
                    positivePartOfSignal = true;
                }
            }

            if (firstValue < centerOfSignal) {
                if (value < centerOfSignal && firstPeriod && (value >= lowerLimitOfAmplitude)) {
                    positivePartOfSignal = false;
                } else if (value >= centerOfSignal && !positivePartOfSignal) {
                    countPeriods();
                    positivePartOfSignal = true;
                    firstPeriod = false;
                } else if (value < centerOfSignal && !firstPeriod && positivePartOfSignal && value >= lowerLimitOfAmplitude) {
                    countPeriods();
                    positivePartOfSignal = false;
                }
            }
        }

        double samplesPerPeriod = bufferedSamplesPerSemiPeriods == 0 ? 0 : bufferedSamplesPerSemiPeriods / periods;
        return (samplesPerPeriod == 0 ? 0 : (adc.getFrequency() / samplesPerPeriod));
    }

    // Считает количество периодов
    private void countPeriods() {
        zeroTransitionCounter++;
        if (zeroTransitionCounter % 2 != 0 && zeroTransitionCounter > 2) {
            bufferedSamplesPerSemiPeriods = samplesPerSemiPeriods;
            periods++;
        }
    }

    // Считает количество сэмплов в полупериодах
    private void countSamplesSecondAlgorithm() {
        if (zeroTransitionCounter >= 1) {
            samplesPerSemiPeriods++;
        }
    }

    // Считает количество нагружений
    private double calculateLoadsCounter() {
        return calculateFrequency();
    }

    // Проверяет наличие градуировки
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

    // Суммирует градуированные величины для усреднения
    private void sumCalibratedParameters() {
        if (lowerBound < 0 & firstLoadValue >= 0) {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(peakValue);
            bufferedCalibratedRms += calibratedRms = applyCalibration(rms);
        } else {
            bufferedCalibratedAmplitude += calibratedAmplitude = applyCalibration(adc, peakValue);
            bufferedCalibratedRms += calibratedRms = applyCalibration(adc, rms);
        }
        bufferedCalibratedZeroShift += calibratedDC = applyCalibration(adc, dc);
    }

    // Градуирует параметры сигнала
    private void calculateCalibratedParameters(double averageCount) {
        calibratedAmplitude = bufferedCalibratedAmplitude / averageCount;
        calibratedRms = bufferedCalibratedRms / averageCount;
        calibratedDC = bufferedCalibratedZeroShift / averageCount;
        bufferedCalibratedAmplitude = bufferedCalibratedRms = bufferedCalibratedZeroShift = 0;
    }

    // Градуирует параметры сигнала
    private double applyCalibration(double value) {
        defineValueName();
        defineBounds();
        setBounds();
        return calibratedValue = value / (Math.abs(lowerBound) + Math.abs(upperBound)) * secondLoadValue;
    }

    // Определяет границы сигнала
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

	// Задает границы графика
    private void setBounds() {
        lowerBound = firstChannelValue;
        upperBound = secondChannelValue;
    }

    // Градуирует параметры сигнала
    public double applyCalibration(ADC adc, double value) {
        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);

        for (int settingsIndex = 0; settingsIndex < calibrationCoefficients.size() - 1; settingsIndex++) {
            defineValueName();
            parseCalibrationSettings(calibrationSettings, settingsIndex);
            defineBounds();
            setBounds();
            calibrate(value);
        }

        return calibratedValue;
    }

    // Считывает параметры градуировки
    private void parseCalibrationSettings(List<String> calibrationSettings, int i) {
        if (CalibrationPoint.parseValueName(calibrationSettings.get(i)).isEmpty()) { // если ноль градуирован
            i++;
        }

        String firstCalibrationPoint = calibrationSettings.get(i);
        String secondCalibrationPoint = calibrationSettings.get(i + 1);
        firstChannelValue = CalibrationPoint.parseChannelValue(firstCalibrationPoint);
        firstLoadValue = CalibrationPoint.parseLoadValue(firstCalibrationPoint);
        secondChannelValue = CalibrationPoint.parseChannelValue(secondCalibrationPoint);
        secondLoadValue = CalibrationPoint.parseLoadValue(secondCalibrationPoint);
    }

    // Градуирует величину
    private void calibrate(double value) {
        if (value > lowerBound * 1.2 & value <= upperBound * 1.2) {
            double k = (secondLoadValue - firstLoadValue) / (secondChannelValue - firstChannelValue);
            double b = firstLoadValue - k * firstChannelValue;
            calibratedValue = k * value + b;
        }
    }

    // Определяет границы градуированного сигнала
    public void defineCalibratedBounds(ADC adc) {
        this.adc = adc;
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        if (!calibrationSettings.isEmpty()) {
            double minLoadValue = Double.MAX_VALUE;
            double maxLoadValue = Double.MIN_VALUE;
            int GRAPH_SCALE = 5;

            defineValueName();

            for (String calibrationSetting : calibrationSettings) {
                String calibratedValueName = CalibrationPoint.parseValueName(calibrationSetting);
                if (calibratedValueName.isEmpty()) {
                    continue;
                }

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

    // Определяет название физической величины
    private void defineValueName() {
        List<String> calibrationSettings = adc.getCalibrationSettings().get(channel);
        calibratedValueName = CalibrationPoint.parseValueName(calibrationSettings.get(calibrationSettings.size() - 1));
    }

    public double getPeakValue() {
        return peakValue;
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

    public void setAccurateFrequencyCalculation(boolean accurateFrequencyCalculation) {
        this.accurateFrequencyCalculation = accurateFrequencyCalculation;
    }

    public void setPeakValue(int peakValue) {
        this.peakValue = peakValue;
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
