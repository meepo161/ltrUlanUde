package ru.avem.posum.hardware;

public abstract class DAC extends Module {
    private int[] amplitudes;
    private int[] frequencies;
    private int[] phases;
    private int checkedChannelsCounter;
    private int useCalibration; // 1 - использовать заводскую калибровку, 0

    DAC() {
        channelsCount = 8; // 8 каналов, поскольку в проекте используется LTR34-8
        checkedChannels = new boolean[channelsCount];
        amplitudes = new int[channelsCount];
        channelsDescription = new String[channelsCount];
        frequencies = new int[channelsCount];
        phases = new int[channelsCount];
    }

    public abstract void generate(double[] signal);

    public abstract double getFrequency();

    public int[] getAmplitudes() {
        return amplitudes;
    }

    public int getCheckedChannelsCounter() {
        return checkedChannelsCounter;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public int[] getPhases() {
        return phases;
    }

    public int getUseCalibration() {
        return useCalibration;
    }

    public void isUseCalibration(int useCalibration) {
        this.useCalibration = useCalibration;
    }

    void setCheckedChannelsCounter(int checkedChannelsCounter) {
        this.checkedChannelsCounter = checkedChannelsCounter;
    }
}
