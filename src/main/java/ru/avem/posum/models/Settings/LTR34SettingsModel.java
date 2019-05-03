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
    private int[] amplitudes;
    private boolean[] checkedChannels;
    private boolean connectionOpen = true;
    private String[] descriptions;
    private int[] frequencies;
    private LTR34 ltr34 = new LTR34();
    private String moduleName;
    private int[] phases;
    private Random random = new Random();
    private double[] signal = new double[31_250]; // массив данных для генерации сигнала для каждого канала
    private int slot;
    private boolean stopped;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr34 = (LTR34) instancesOfModules.get(slot);
        this.checkedChannels = ltr34.getCheckedChannels();
        this.amplitudes = ltr34.getAmplitudes();
        this.descriptions = ltr34.getDescriptions();
        this.frequencies = ltr34.getFrequencies();
        this.phases = ltr34.getPhases();
    }

    public void initModule() {
        if (!connectionOpen) {
            ltr34.openConnection();
            connectionOpen = true;
        }

        ltr34.countChannels();
        ltr34.initializeModule();
    }

    public void calculateSignal(int signalType) {
        List<double[]> channelsData = new ArrayList<>();
        int channels = (ltr34.getCheckedChannelsCounter() <= 4) ? 4 : 8;

        for (int channelIndex = 0; channelIndex < channels; channelIndex++) {
            switch (signalType) {
                case 0:
                    channelsData.add(createSinSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 1:
                    channelsData.add(createSquareSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 2:
                    channelsData.add(createTriangleSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex]));
                    break;
                case 3:
                    channelsData.add(createSawtoothSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex], -2));
                    break;
                case 4:
                    channelsData.add(createSawtoothSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex], 2));
                    break;
                case 5:
                    channelsData.add(createNoiseSignal(signal.length / channels, amplitudes[channelIndex],
                            frequencies[channelIndex], phases[channelIndex]));
            }
        }

        signal = mergeArrays(channelsData);
    }

    private double[] createSinSignal(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / length + channelPhase);
        }

        return data;
    }

    private double[] createSquareSignal(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = amplitude * Math.signum(Math.sin(2 * Math.PI * frequency * i / length + channelPhase));
        }

        return data;
    }

    private double[] createTriangleSignal(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = (2 * amplitude) / Math.PI * Math.asin(Math.sin(2 * Math.PI * frequency * i / length + channelPhase));
        }

        return data;
    }

    private double[] createSawtoothSignal(int length, int amplitude, int frequency, int phase, double coefficient) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = (coefficient * amplitude) / Math.PI * Math.atan(1.0 / Math.tan((double) i / length * Math.PI *
                    frequency + channelPhase));
        }

        return data;
    }


    private double[] createNoiseSignal(int length, int amplitude, int frequency, int phase) {
        double[] data = new double[length];
        double channelPhase = Math.toRadians(phase);

        for (int i = 0; i < length; i++) {
            data[i] = amplitude * random.nextDouble() * Math.sin(2 * Math.PI * (frequency + random.nextDouble()) *
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

    public void generate(boolean isAutogenerationMode) {
        ltr34.generate(signal);
        ltr34.start();
        stopped = false;

        if (!isAutogenerationMode) {
            new Thread(() -> {
                while (!stopped && ltr34.checkStatus()) {
                    ltr34.generate(signal);
                    ltr34.checkConnection();
                    Utils.sleep(1000);
                }
            }).start();
        }
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
        if (connectionOpen) {
            ltr34.stop();
            stopped = true;
            ltr34.closeConnection();
            connectionOpen = false;
        }
    }

    public int[] getAmplitudes() {
        return amplitudes;
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

    public int getSlot() {
        return slot;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
