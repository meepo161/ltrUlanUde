package ru.avem.posum.hardware;

public class DAC extends Module {
    private int[] amplitudes;
    private int[] frequencies;
    private int[] phases;
    private int checkedChannelsCounter;

    DAC() {
        channelsCount = 8; // 8 каналов, поскольку в проекте используется LTR34-8
        checkedChannels = new boolean[channelsCount];
        amplitudes = new int[channelsCount];
        frequencies = new int[channelsCount];
        phases = new int[channelsCount];
    }

    public int[] getAmplitudes() {
        return amplitudes;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public int[] getPhases() {
        return phases;
    }

    public int getCheckedChannelsCounter() {
        return checkedChannelsCounter;
    }

    void setCheckedChannelsCounter(int checkedChannelsCounter) {
        this.checkedChannelsCounter = checkedChannelsCounter;
    }
}
