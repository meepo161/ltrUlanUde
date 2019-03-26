package ru.avem.posum.hardware;

import java.util.ArrayList;
import java.util.List;

public class LTR212 extends ADC {
    public LTR212() {
        initModuleSettings();
    }

    private void initModuleSettings() {
        getModuleSettings().put(Settings.ADC_MODE.getSettingName(), 0); // режим работы каналов
        getModuleSettings().put(Settings.CALIBRATION_COEFFICIENTS.getSettingName(), 0); // использование калибровочных коэффициентов
        getModuleSettings().put(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName(), 0); // использование заводских калибровочных коэфффициентов
        getModuleSettings().put(Settings.LOGIC_CHANNELS_COUNT.getSettingName(), channelsCount); // количество логичческих каналов
        getModuleSettings().put(Settings.IIR_FILTER.getSettingName(), 0); // использование программного фильтра
        getModuleSettings().put(Settings.FIR_FILTER.getSettingName(), 0); // использование программного фильтра
        getModuleSettings().put(Settings.DECIMATION.getSettingName(), 0); // использование децимации
        getModuleSettings().put(Settings.TAP.getSettingName(), 0); // порядок фильтра
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE.getSettingName(), 0); // опорное напряжение
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), 0); // тип опорного напряжения
    }

    public void openConnection() {
        clearStatus();
        status = open(crate, getSlot(), System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    private void clearStatus() {
        status = "";
    }

    public native String open(String crate, int slot, String path);

    public void initModule() {
        clearStatus();
        status = initialize(getSlot(), getChannelsTypes(), getMeasuringRanges(), getLTR212ModuleSettings());
        checkStatus();
    }

    private int[] getLTR212ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getModuleSettings().get(Settings.ADC_MODE.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.CALIBRATION_COEFFICIENTS.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.LOGIC_CHANNELS_COUNT.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.IIR_FILTER.getSettingName()));
        settingsList.add(getModuleSettings().get(Settings.FIR_FILTER.getSettingName()));
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

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings);

    public void receive(double[] data) {
        clearStatus();
        status = fillArray(getSlot(), data, getTimeMarks());
        checkStatus();
    }

    public native String fillArray(int slot, double[] data, double[] timeMarks);

    public void closeConnection() {
        close(getSlot());
        checkStatus();
    }

    public native String close(int slot);

    static {
        System.loadLibrary( "LTR212Library");
    }
}
