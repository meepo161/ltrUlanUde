package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class LTR24 extends ADC {
    private double frequency;
    private boolean busy;

    public LTR24() {
        initializeModuleSettings();
    }

    public void openConnection() {
        status = openConnection(crate, getSlot());
        checkStatus();
    }

    public void initializeModule() {
        status = initialize(getSlot(), getChannelsTypes(), getMeasuringRanges(), getLTR24ModuleSettings());
        checkStatus();
    }

    public void defineFrequency() {
        status = getFrequency(getSlot());
    }

    public void start() {
        status = start(getSlot());
        checkStatus();
    }

    public void write(double[] data, double[] timeMarks) {
        status = write(getSlot(), data, timeMarks, data.length, 2000);
        checkStatus();
    }

    @Override
    public void stop() {
        status = stop(getSlot());
        checkStatus();
    }

    public void closeConnection() {
        closeConnection(getSlot());
        checkStatus();
    }

    public native String openConnection(String crate, int slot);

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int length, int timeout);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary( "LTR24Library");
    }

    private void initializeModuleSettings() {
        getModuleSettings().put(Settings.FREQUENCY.getSettingName(), 7); // частота дискретизации 9.7 кГц
    }

    private int[] getLTR24ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getModuleSettings().get(Settings.FREQUENCY.getSettingName()));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();
        settings.append(moduleSettings.get(Settings.FREQUENCY.getSettingName())).append(", ");
        return settings;
    }

    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");
        moduleSettings.put(Settings.FREQUENCY.getSettingName(), Integer.valueOf(separatedSettings[0]));
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    public boolean isBusy() {
        return busy;
    }
}
