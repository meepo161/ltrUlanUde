package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class LTR212 extends ADC {
    private String firFilePath;
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!
    private String iirFilePath;

    public LTR212() {
        initializeModuleSettings();
    }

    @Override
    public void openConnection() {
        status = openConnection(crateSerialNumber, getSlot(), System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    @Override
    public void checkConnection() {
        Crate crate = new Crate();
        if (!crate.getCratesNames().isEmpty()) {
            status = checkConnection(getSlot());
        } else {
            status = "Потеряно соединение с крейтом";
        }
    }

    public void testEEPROM() {
        status = testEEPROM(getSlot());
        checkStatus();
    }

    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getTypeOfChannels(), getMeasuringRanges(), getLTR212ModuleSettings(), firFilePath, iirFilePath);
        checkStatus();
    }

    @Override
    public void defineFrequency() {
        status = getFrequency(getSlot());
        checkStatus();
    }

    @Override
    public void start() {
        status = start(getSlot());
        checkStatus();
    }

    @Override
    public void write(double[] data, double[] timeMarks) {
        status = write(getSlot(), data, timeMarks, data.length);
    }

    @Override
    public void stop() {
        status = stop(getSlot());
        checkStatus();
    }

    @Override
    public void closeConnection() {
        closeConnection(getSlot());
        checkStatus();
    }

    public native String openConnection(String crate, int slot, String path);

    public native String checkConnection(int slot);

    public native String testEEPROM(int slot);

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings, String firFilePath, String iirFilePath);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int size);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR212Library");
    }

    private void initializeModuleSettings() {
        getModuleSettings().put(Settings.ADC_MODE, 0); // режим работы каналов
        getModuleSettings().put(Settings.CALIBRATION_COEFFICIENTS, 0); // использование калибровочных коэффициентов
        getModuleSettings().put(Settings.FACTORY_CALIBRATION_COEFFICIENTS, 0); // использование заводских калибровочных коэфффициентов
        getModuleSettings().put(Settings.LOGIC_CHANNELS_COUNT, channelsCount); // количество логичческих каналов
        getModuleSettings().put(Settings.IIR, 0); // использование программного фильтра
        getModuleSettings().put(Settings.FIR, 0); // использование программного фильтра
        getModuleSettings().put(Settings.DECIMATION, 0); // использование децимации
        getModuleSettings().put(Settings.TAP, 0); // порядок фильтра
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE, 1); // опорное напряжение
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE_TYPE, 1); // тип опорного напряжения
    }

    private int[] getLTR212ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getModuleSettings().get(Settings.ADC_MODE));
        settingsList.add(getModuleSettings().get(Settings.CALIBRATION_COEFFICIENTS));
        settingsList.add(getModuleSettings().get(Settings.FACTORY_CALIBRATION_COEFFICIENTS));
        settingsList.add(getModuleSettings().get(Settings.LOGIC_CHANNELS_COUNT));
        settingsList.add(getModuleSettings().get(Settings.IIR));
        settingsList.add(getModuleSettings().get(Settings.FIR));
        settingsList.add(getModuleSettings().get(Settings.DECIMATION));
        settingsList.add(getModuleSettings().get(Settings.TAP));
        settingsList.add(getModuleSettings().get(Settings.REFERENCE_VOLTAGE));
        settingsList.add(getModuleSettings().get(Settings.REFERENCE_VOLTAGE_TYPE));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();

        settings.append(moduleSettings.get(Settings.ADC_MODE)).append(", ")
                .append(moduleSettings.get(Settings.CALIBRATION_COEFFICIENTS)).append(", ")
                .append(moduleSettings.get(Settings.FACTORY_CALIBRATION_COEFFICIENTS)).append(", ")
                .append(moduleSettings.get(Settings.LOGIC_CHANNELS_COUNT)).append(", ")
                .append(moduleSettings.get(Settings.IIR)).append(", ")
                .append(moduleSettings.get(Settings.FIR)).append(", ")
                .append(moduleSettings.get(Settings.DECIMATION)).append(", ")
                .append(moduleSettings.get(Settings.TAP)).append(", ")
                .append(moduleSettings.get(Settings.REFERENCE_VOLTAGE)).append(", ")
                .append(moduleSettings.get(Settings.REFERENCE_VOLTAGE_TYPE));

        return settings;
    }

    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");

        moduleSettings.put(Settings.ADC_MODE, Integer.valueOf(separatedSettings[0]));
        moduleSettings.put(Settings.CALIBRATION_COEFFICIENTS, Integer.valueOf(separatedSettings[1]));
        moduleSettings.put(Settings.FACTORY_CALIBRATION_COEFFICIENTS, Integer.valueOf(separatedSettings[2]));
        moduleSettings.put(Settings.LOGIC_CHANNELS_COUNT, Integer.valueOf(separatedSettings[3]));
        moduleSettings.put(Settings.IIR, Integer.valueOf(separatedSettings[4]));
        moduleSettings.put(Settings.FIR, Integer.valueOf(separatedSettings[5]));
        moduleSettings.put(Settings.DECIMATION, Integer.valueOf(separatedSettings[6]));
        moduleSettings.put(Settings.TAP, Integer.valueOf(separatedSettings[7]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE, Integer.valueOf(separatedSettings[8]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE_TYPE, Integer.valueOf(separatedSettings[9]));
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    public void setFirFilePath(String firFilePath) {
        this.firFilePath = firFilePath;
    }

    public void setIirFilePath(String iirFilePath) {
        this.iirFilePath = iirFilePath;
    }
}
