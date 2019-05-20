package ru.avem.posum.hardware;

import javafx.collections.ObservableList;

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
        status = openConnection(crateSerialNumber, getSlot(), getBioPath());
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

    public void testEEPROM() {
        status = testEEPROM(getSlot());
    }

    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getTypeOfChannels(), getMeasuringRanges(), getLTR212ModuleSettings(), firFilePath, iirFilePath);
    }

    @Override
    public void defineFrequency() {
        status = getFrequency(getSlot());
    }

    @Override
    public void start() {
        status = start(getSlot());
    }

    @Override
    public void write(double[] data, double[] timeMarks) {
        status = write(getSlot(), data, timeMarks, data.length);
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

    public native String openConnection(String crate, int slot, String path);

    public native String checkConnection(int slot);

    public native String testEEPROM(int slot);

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings, String firFilePath, String iirFilePath);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int dataLength);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR212Library");
    }

    private void initializeModuleSettings() {
        getSettingsOfModule().put(Settings.ADC_MODE, 0); // режим работы каналов
        getSettingsOfModule().put(Settings.CALIBRATION_COEFFICIENTS, 0); // использование калибровочных коэффициентов
        getSettingsOfModule().put(Settings.FACTORY_CALIBRATION_COEFFICIENTS, 0); // использование заводских калибровочных коэфффициентов
        getSettingsOfModule().put(Settings.LOGIC_CHANNELS_COUNT, channelsCount); // количество логичческих каналов
        getSettingsOfModule().put(Settings.IIR, 0); // использование программного фильтра
        getSettingsOfModule().put(Settings.FIR, 0); // использование программного фильтра
        getSettingsOfModule().put(Settings.DECIMATION, 0); // использование децимации
        getSettingsOfModule().put(Settings.TAP, 0); // порядок фильтра
        getSettingsOfModule().put(Settings.REFERENCE_VOLTAGE, 1); // опорное напряжение
        getSettingsOfModule().put(Settings.REFERENCE_VOLTAGE_TYPE, 1); // тип опорного напряжения
    }

    private int[] getLTR212ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getSettingsOfModule().get(Settings.ADC_MODE));
        settingsList.add(getSettingsOfModule().get(Settings.CALIBRATION_COEFFICIENTS));
        settingsList.add(getSettingsOfModule().get(Settings.FACTORY_CALIBRATION_COEFFICIENTS));
        settingsList.add(getSettingsOfModule().get(Settings.LOGIC_CHANNELS_COUNT));
        settingsList.add(getSettingsOfModule().get(Settings.IIR));
        settingsList.add(getSettingsOfModule().get(Settings.FIR));
        settingsList.add(getSettingsOfModule().get(Settings.DECIMATION));
        settingsList.add(getSettingsOfModule().get(Settings.TAP));
        settingsList.add(getSettingsOfModule().get(Settings.REFERENCE_VOLTAGE));
        settingsList.add(getSettingsOfModule().get(Settings.REFERENCE_VOLTAGE_TYPE));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();

        settings.append(settingsOfModule.get(Settings.ADC_MODE)).append(", ")
                .append(settingsOfModule.get(Settings.CALIBRATION_COEFFICIENTS)).append(", ")
                .append(settingsOfModule.get(Settings.FACTORY_CALIBRATION_COEFFICIENTS)).append(", ")
                .append(settingsOfModule.get(Settings.LOGIC_CHANNELS_COUNT)).append(", ")
                .append(settingsOfModule.get(Settings.IIR)).append(", ")
                .append(settingsOfModule.get(Settings.FIR)).append(", ")
                .append(settingsOfModule.get(Settings.DECIMATION)).append(", ")
                .append(settingsOfModule.get(Settings.TAP)).append(", ")
                .append(settingsOfModule.get(Settings.REFERENCE_VOLTAGE)).append(", ")
                .append(settingsOfModule.get(Settings.REFERENCE_VOLTAGE_TYPE));

        return settings;
    }

    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");

        settingsOfModule.put(Settings.ADC_MODE, Integer.valueOf(separatedSettings[0]));
        settingsOfModule.put(Settings.CALIBRATION_COEFFICIENTS, Integer.valueOf(separatedSettings[1]));
        settingsOfModule.put(Settings.FACTORY_CALIBRATION_COEFFICIENTS, Integer.valueOf(separatedSettings[2]));
        settingsOfModule.put(Settings.LOGIC_CHANNELS_COUNT, Integer.valueOf(separatedSettings[3]));
        settingsOfModule.put(Settings.IIR, Integer.valueOf(separatedSettings[4]));
        settingsOfModule.put(Settings.FIR, Integer.valueOf(separatedSettings[5]));
        settingsOfModule.put(Settings.DECIMATION, Integer.valueOf(separatedSettings[6]));
        settingsOfModule.put(Settings.TAP, Integer.valueOf(separatedSettings[7]));
        settingsOfModule.put(Settings.REFERENCE_VOLTAGE, Integer.valueOf(separatedSettings[8]));
        settingsOfModule.put(Settings.REFERENCE_VOLTAGE_TYPE, Integer.valueOf(separatedSettings[9]));
    }

    public static String getBioPath() {
        return System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio";
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
