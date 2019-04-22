package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class LTR34 extends DAC {
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!

    public LTR34() {
        initializeModuleSettings();
    }

    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                setCheckedChannelsCounter(i + 1);
            }
        }
    }

    @Override
    public void openConnection() {
        status = openConnection(crate, getSlot());
        checkStatus();
    }

    @Override
    public void checkConnection() {
        CrateModel crateModel = new CrateModel();
        if (!crateModel.getCratesNames().isEmpty()) {
            status = checkConnection(getSlot());
            checkStatus();
        } else {
            openConnection();
            status = "Потеряно соединение с крейтом";
        }
    }

    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getCheckedChannelsCounter(), getLTR34ModuleSettings());
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
    public void generate(double[] signal) {
        status = generate(getSlot(), signal, signal.length);
    }

    @Override
    public void stop() {
        status = stop(getSlot());
        checkStatus();
    }

    @Override
    public void closeConnection() {
        status = closeConnection(getSlot());
        checkStatus();
    }

    public native String openConnection(String crate, int slot);

    public native String checkConnection(int slot);

    public native String initialize(int slot, int channelsCounter, int[] moduleSettings);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String generate(int slot, double[] signal, int length);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR34Library");
    }

    private void initializeModuleSettings() {
        getModuleSettings().put(DAC.Settings.DAC_MODE.getSettingName(), 0); // режим работы каналов
        getModuleSettings().put(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), 1); // использование заводских калибровочных коэфффициентов
        getModuleSettings().put(Settings.SIGNAL_TYPE.getSettingName(), 0); // тип генерируемого сигнала
    }

    private int[] getLTR34ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getModuleSettings().get(DAC.Settings.DAC_MODE.getSettingName()));
        settingsList.add(getModuleSettings().get(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.SIGNAL_TYPE.getSettingName()));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();

        settings.append(moduleSettings.get(DAC.Settings.DAC_MODE.getSettingName())).append(", ")
                .append(moduleSettings.get(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName())).append(", ")
                .append(moduleSettings.get(DAC.Settings.SIGNAL_TYPE.getSettingName())).append(", ");

        return settings;
    }

    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");

        moduleSettings.put(DAC.Settings.DAC_MODE.getSettingName(), Integer.valueOf(separatedSettings[0]));
        moduleSettings.put(DAC.Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), Integer.valueOf(separatedSettings[2]));
        moduleSettings.put(Settings.SIGNAL_TYPE.getSettingName(), Integer.valueOf(separatedSettings[3]));
    }

    @Override
    public double getFrequency() {
        return frequency;
    }
}
