package ru.avem.posum.hardware;

public class LTR24 extends ADC {
    public void openConnection() {
        status = open(crate, getSlot());
        checkStatus();
    }

    public native String open(String crate, int slot);

    @Override
    public double getFrequency() {
        return 0;
    }

    public void initModule() {
        status = initialize(getSlot(), getChannelsTypes(), getMeasuringRanges());
        checkStatus();
    }

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges);

    public void receive(double[] data) {
        status = fillArray(getSlot(), data);
        checkStatus();
    }

    public native String fillArray(int slot, double[] data);

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

    }

    static {
        System.loadLibrary( "LTR24Library");
    }
}
