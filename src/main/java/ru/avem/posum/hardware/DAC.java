package ru.avem.posum.hardware;

public abstract class DAC extends Module {
    private int[] amplitudes;
    private int[] frequencies;
    private int[] phases;
    private int checkedChannelsCounter;

    public DAC() {
        channelsCount = 8; // 8 каналов, поскольку в проекте используется LTR34-8
        checkedChannels = new boolean[channelsCount];
        amplitudes = new int[channelsCount];
        frequencies = new int[channelsCount];
        phases = new int[channelsCount];
    }

    @Override
    public void openConnection() {
        status = open(crate, slot);
        checkStatus();
    }

    protected native String open(String crate, int slot);

    public abstract void initModule();

    protected native void generate(double[] signal);

    public native String start();

    public void closeConnection() {
        stop();
        close();
    }

    public native String stop();

    public native String close();

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

    public void setCheckedChannelsCounter(int checkedChannelsCounter) {
        this.checkedChannelsCounter = checkedChannelsCounter;
    }
}
