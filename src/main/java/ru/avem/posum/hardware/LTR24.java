package ru.avem.posum.hardware;

public class LTR24 extends ADC {
    private double frequency;

    public void openConnection() {
        status = openConnection(crate, getSlot());
        checkStatus();
    }

    public void initializeModule() {
        status = initialize(getSlot(), getChannelsTypes(), getMeasuringRanges());
        checkStatus();
    }

    @Override
    public double getFrequency() {
        status = getFrequency(getSlot());
        return frequency;
    }

    public void start() {
        status = start(getSlot());
        checkStatus();
    }

    public void write(double[] data, double[] timeMarks) {
        status = write(getSlot(), data, timeMarks, data.length, 1000);
        checkStatus();
    }

    public void stop() {
        status = stop(getSlot());
        checkStatus();
    }

    public void closeConnection() {
        closeConnection(getSlot());
        checkStatus();
    }

    public native String openConnection(String crate, int slot);

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int length, int timeout);

    public native String stop(int slot);

    public native String closeConnection(int slot);

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
