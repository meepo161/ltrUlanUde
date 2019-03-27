package ru.avem.posum.hardware;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import javax.lang.model.element.NestingKind;
import java.util.ArrayList;
import java.util.HashMap;
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
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE.getSettingName(), 1); // опорное напряжение
        getModuleSettings().put(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), 1); // тип опорного напряжения
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

    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();

        settings.append(moduleSettings.get(Settings.ADC_MODE.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.CALIBRATION_COEFFICIENTS.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.FACTORY_CALIBRATION_COEFFICIENTS.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.LOGIC_CHANNELS_COUNT.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.IIR_FILTER.getSettingName())).append(", ")
                .append(moduleSettings.get(Settings.FIR_FILTER.getSettingName())).append(", ")
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
        moduleSettings.put(Settings.IIR_FILTER.getSettingName(), Integer.valueOf(separatedSettings[4]));
        moduleSettings.put(Settings.FIR_FILTER.getSettingName(), Integer.valueOf(separatedSettings[5]));
        moduleSettings.put(Settings.DECIMATION.getSettingName(), Integer.valueOf(separatedSettings[6]));
        moduleSettings.put(Settings.TAP.getSettingName(), Integer.valueOf(separatedSettings[7]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE.getSettingName(), Integer.valueOf(separatedSettings[8]));
        moduleSettings.put(Settings.REFERENCE_VOLTAGE_TYPE.getSettingName(), Integer.valueOf(separatedSettings[9]));
    }

    static {
        System.loadLibrary("LTR212Library");
    }
}
