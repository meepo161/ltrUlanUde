package ru.avem.posum.models.Settings;

import javafx.scene.chart.XYChart;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LTR34SettingsModel {
    private double[] amplitudes = {2, 2, 2, 2};
    private boolean[] checkedChannels;
    private boolean connectionOpen = true;
    private double[] dc = {0, 0, 0, 0};
    private String[] descriptions;
    private int[] frequencies = {8, 8, 8, 8};
    private LTR34 ltr34 = new LTR34();
    private String moduleName;
    private int[] phases = {0, 0, 0, 0};
    private Random random = new Random();
    private double[] signal = new double[31_250]; // массив данных для генерации сигнала для каждого канала
    private int slot;
    private boolean stopped = true;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr34 = (LTR34) instancesOfModules.get(slot);
        this.checkedChannels = ltr34.getCheckedChannels();
        this.amplitudes = ltr34.getAmplitudes();
        this.dc = ltr34.getDc();
        this.descriptions = ltr34.getDescriptions();
        this.frequencies = ltr34.getFrequencies();
        this.phases = ltr34.getPhases();
    }

    public void initModule() {
        if (!ltr34.isConnectionOpen()) {
            ltr34.openConnection();
        }

        ltr34.countChannels();
        ltr34.initializeModule();
    }

    public void calculateSignal(int signalType) {
        List<double[]> channelsData = new ArrayList<>();
        int channels = 4;
//        int channels = (ltr34.getCheckedChannelsCounter() <= 4) ? 4 : 8;

        for (int channelIndex = 0; channelIndex < channels; channelIndex++) {
            switch (signalType) {
                case 0:
                    channelsData.add(createSinSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 1:
                    channelsData.add(createSquareSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 2:
                    channelsData.add(createTriangleSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 3:
                    channelsData.add(createSawtoothSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex], -2));
                    break;
                case 4:
                    channelsData.add(createSawtoothSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex], 2));
                    break;
                case 5:
                    channelsData.add(createNoiseSignal(signal.length / channels, amplitudes[channelIndex],
                            dc[channelIndex], frequencies[channelIndex], phases[channelIndex]));
            }
        }

        signal = mergeArrays(channelsData);
    }

    private double[] createSinSignal(int length, double amplitude, double dc, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = dc + amplitude * Math.sin(2 * Math.PI * frequency * i / length + channelPhase);
        }

        return data;
    }

    private double[] createSquareSignal(int length, double amplitude, double dc, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = dc + amplitude * Math.signum(Math.sin(2 * Math.PI * frequency * i / length + channelPhase));
        }

        return data;
    }

    private double[] createTriangleSignal(int length, double amplitude, double dc, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = dc + (2 * amplitude) / Math.PI * Math.asin(Math.sin(2 * Math.PI * frequency * i / length + channelPhase));
        }

        return data;
    }

    private double[] createSawtoothSignal(int length, double amplitude, double dc, int frequency, int phase, double coefficient) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = dc + (coefficient * amplitude) / Math.PI * Math.atan(1.0 / Math.tan((double) i / length * Math.PI *
                    frequency + channelPhase));
        }

        return data;
    }


    private double[] createNoiseSignal(int length, double amplitude, double dc, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = dc + amplitude * random.nextDouble() * Math.sin(2 * Math.PI * (frequency + random.nextDouble()) *
                    i / length + channelPhase);
        }

        return data;
    }

    private static double[] mergeArrays(List<double[]> channelsData) {
        int resultArraySize = 0;
        int numberOfArrays = 0;

        for (double[] array : channelsData) {
            resultArraySize += array.length;
            numberOfArrays++;
        }

        double[] resultArray = new double[resultArraySize];
        int[] countsOfArrays = new int[numberOfArrays];

        for (int i = 0; i < resultArraySize; ) {
            double[] currentArray = channelsData.get(i % numberOfArrays);
            int countsOfArray = countsOfArrays[i % numberOfArrays]++;
            resultArray[i++] = currentArray[countsOfArray];
        }

        return resultArray;
    }

    public XYChart.Series<Number, Number> createSeries(int channelNumber) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(String.format("Канал %d", channelNumber + 1));
        int channels = ltr34.getCheckedChannelsCounter() <= 4 ? 4 : 8;

        for (int i = channelNumber; i < signal.length; i += channels * 10) { // коэффициент 10 введен для того, чтобы не отрисовывать все точки
            series.getData().add(new XYChart.Data<>((double) i / (signal.length - channels * 10 + channelNumber), signal[i]));
        }

        return series;
    }

    public void stopModule() {
        if (!stopped) {
            ltr34.stop();
            stopped = true;
        }
    }

    public double[] getAmplitudes() {
        return amplitudes;
    }

    public double[] getDc() {
        return dc;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public LTR34 getLTR34Instance() {
        return ltr34;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int[] getPhases() {
        return phases;
    }

    public double[] getSignal() {
        return signal;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
