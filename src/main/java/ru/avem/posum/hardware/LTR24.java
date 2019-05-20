package ru.avem.posum.hardware;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class LTR24 extends ADC {
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!

    public LTR24() {
        initializeModuleSettings();
    }

    @Override
    public void openConnection() {
        status = openConnection(crateSerialNumber, getSlot());
        setConnectionOpen(checkStatus());
    }

    @Override
    public void checkConnection() {
        Crate crate = new Crate();

        if (crate.getCratesNames().isPresent()) {
            ObservableList<String> cratesNames = crate.getCratesNames().get();

            if (!cratesNames.isEmpty()) {
                status = checkConnection(getSlot());
            } else {
                status = "Потеряно соединение с крейтом";
                openConnection();
            }
        }
    }

    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getTypeOfChannels(), getMeasuringRanges(), getLTR24ModuleSettings());
    }

    @Override
    public void defineFrequency() {
        getFrequency(getSlot());
    }

    @Override
    public void start() {
        status = start(getSlot());
    }

    @Override
    public void write(double[] data, double[] timeMarks) {
        write(getSlot(), data, timeMarks, data.length);
    }

    @Override
    public void stop() {
        status = stop(getSlot());
    }

    @Override
    public void closeConnection() {
        if (isConnectionOpen()) {
            status = closeConnection(getSlot());
            setConnectionOpen(checkStatus());
        }
    }

    public native String openConnection(String crate, int slot);

    private native String checkConnection(int slot);

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int dataLength);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR24Library");
    }

    private void initializeModuleSettings() {
        getSettingsOfModule().put(Settings.FREQUENCY, 7); // частота дискретизации 9.7 кГц
    }

    private int[] getLTR24ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getSettingsOfModule().get(Settings.FREQUENCY));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();
        settings.append(settingsOfModule.get(Settings.FREQUENCY)).append(", ");
        return settings;
    }

    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");
        settingsOfModule.put(Settings.FREQUENCY, Integer.valueOf(separatedSettings[0]));
    }

    @Override
    public double getFrequency() {
        return frequency;
    }
}
