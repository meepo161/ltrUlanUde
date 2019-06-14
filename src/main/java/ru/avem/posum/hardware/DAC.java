package ru.avem.posum.hardware;

import java.util.HashMap;

public abstract class DAC extends Module {
    public enum Settings {
        DAC_MODE("DAC mode"), FACTORY_CALIBRATION_COEFFICIENTS("Factory calibration coefficients"),
        FREQUENCY("Frequency"), SIGNAL_TYPE("SignalModel type");

        private String settingName;

        Settings(String settingName) {
            this.settingName = settingName;
        }

        public String getSettingName() {
            return settingName;
        }
    }

    private double[] amplitudes;
    private int checkedChannelsCounter;
    private double[] dc;
    private double[] frequencies;
    HashMap<String, Integer> moduleSettings;
    private int[] phases;

    DAC() {
        channelsCount = 8; // 8 каналов, поскольку в проекте используется LTR34-8
        amplitudes = new double[channelsCount];
        checkedChannels = new boolean[channelsCount];
        dc = new double[channelsCount];
        descriptions = new String[channelsCount];
        frequencies = new double[channelsCount];
        moduleSettings = new HashMap<>();
        phases = new int[channelsCount];
    }

    public abstract void generate(double[] signal);

    public abstract double getFrequency();

    public double[] getAmplitudes() {
        return amplitudes;
    }

    public int getCheckedChannelsCounter() {
        return checkedChannelsCounter;
    }

    public double[] getDc() {
        return dc;
    }

    public double[] getFrequencies() {
        return frequencies;
    }

    public HashMap<String, Integer> getModuleSettings() {
        return moduleSettings;
    }

    public int[] getPhases() {
        return phases;
    }

    void setCheckedChannelsCounter(int checkedChannelsCounter) {
        this.checkedChannelsCounter = checkedChannelsCounter;
    }
}
