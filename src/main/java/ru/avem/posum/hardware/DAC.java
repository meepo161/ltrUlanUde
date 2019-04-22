package ru.avem.posum.hardware;

import java.util.HashMap;

public abstract class DAC extends Module {
    public enum Settings {
        DAC_MODE("DAC mode"), FACTORY_CALIBRATION_COEFFICIENTS("Factory calibration coefficients"),
        SIGNAL_TYPE("Signal type");

        private String settingName;

        Settings(String settingName) {
            this.settingName = settingName;
        }

        public String getSettingName() {
            return settingName;
        }
    }

    private int[] amplitudes;
    private int checkedChannelsCounter;
    private int[] frequencies;
    HashMap<String, Integer> moduleSettings;
    private int[] phases;

    DAC() {
        amplitudes = new int[channelsCount];
        channelsCount = 8; // 8 каналов, поскольку в проекте используется LTR34-8
        checkedChannels = new boolean[channelsCount];
        channelsDescription = new String[channelsCount];
        frequencies = new int[channelsCount];
        moduleSettings = new HashMap<>();
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
