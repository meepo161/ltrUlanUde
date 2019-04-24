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
        status = openConnection(crate, getSlot(), System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
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
        getModuleSettings().put(Settings.ADC_MODE.getSettingName(), 0); // режим работы каналов
        getModuleSettings().put(Settings.CALIBRATION_COEFFICIENTS.getSettingName(), 0); // использование калибровочных коэффициентов
        getModuleSettings().put(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), 0); // использование заводских калибровочных коэфффициентов
        getModuleSettings().put(Settings.LOGIC_CHANNELS_COUNT.getSettingName(), channelsCount); // количество логичческих каналов
        getModuleSettings().put(Settings.IIR.getSettingName(), 0); // использование программного фильтра
        getModuleSettings().put(Settings.FIR.getSettingName(), 0); // использование программного фильтра
        getModuleSettings().put(Settings.DECIMATION.getSettingName(), 0); // использование децимации
        getModuleSettings().put(Settings.TAP.getSettingName(), 0); // порядок фильтра
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE.getSettingName(), 1); // опорное напряжение
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), 1); // тип опорного напряжения
    }

    private int[] getLTR212ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getModuleSettings().get(Settings.ADC_MODE.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.CALIBRATION_COEFFICIENTS.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.LOGIC_CHANNELS_COUNT.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.IIR.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.FIR.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.DECIMATION.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.TAP.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.REFERENCE_VOLTAGE.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName()));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();

        settings.append(moduleSettings.get(Settings.ADC_MODE.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.CALIBRATION_COEFFICIENTS.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.LOGIC_CHANNELS_COUNT.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.IIR.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.FIR.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.DECIMATION.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.TAP.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.REFERENCE_VOLTAGE.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName()));

        return settings;
    }

    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");

        moduleSettings.put(Settings.ADC_MODE.getSettingName(), Integer.valueOf(separatedSettings[0]));
        moduleSettings.put(Settings.CALIBRATION_COEFFICIENTS.getSettingName(), Integer.valueOf(separatedSettings[1]));
        moduleSettings.put(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), Integer.valueOf(separatedSettings[2]));
        moduleSettings.put(Settings.LOGIC_CHANNELS_COUNT.getSettingName(), Integer.valueOf(separatedSettings[3]));
        moduleSettings.put(Settings.IIR.getSettingName(), Integer.valueOf(separatedSettings[4]));
        moduleSettings.put(Settings.FIR.getSettingName(), Integer.valueOf(separatedSettings[5]));
        moduleSettings.put(Settings.DECIMATION.getSettingName(), Integer.valueOf(separatedSettings[6]));
        moduleSettings.put(Settings.TAP.getSettingName(), Integer.valueOf(separatedSettings[7]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE.getSettingName(), Integer.valueOf(separatedSettings[8]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), Integer.valueOf(separatedSettings[9]));
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    public String getFirFilePath() {
        return firFilePath;
    }

    public String getIirFilePath() {
        return iirFilePath;
    }

    public void setFirFilePath(String firFilePath) {
        this.firFilePath = firFilePath;
    }

    public void setIirFilePath(String iirFilePath) {
        this.iirFilePath = iirFilePath;
    }
}
