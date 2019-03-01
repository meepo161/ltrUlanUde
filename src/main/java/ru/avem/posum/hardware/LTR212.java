package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR212 implements ADC {
    private String crate;
    private int slot;
    private final int CHANNELS = 4;
    private boolean[] checkedChannels = new boolean[CHANNELS];
    private int[] channelsTypes = new int[CHANNELS];
    private int[] measuringRanges = new int[CHANNELS];
    private String[] channelsDescription = new String[CHANNELS];
    private String[] calibrationSettings = new String[CHANNELS];
    private String status;
    private TextEncoder textEncoder = new TextEncoder();
    private boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

    public LTR212() {
        String defaultCalibrationSettings = "notSetted, 0.0, 0.0, 0.0, 0.0, В";
        status = "";

        for (int i = 0; i < CHANNELS; i++) {
            channelsDescription[i] = "";
            calibrationSettings[i] = defaultCalibrationSettings;
        }
    }

    public void openConnection() {
        status = open(crate, slot, System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    public native String open(String crate, int slot, String path);

    public void initModule() {
        status = initialize(slot, channelsTypes, measuringRanges);
        checkStatus();
    }

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges);

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public void receiveData(double[] data) {
        status = fillArray(slot, data);
        checkStatus();
    }

    public native String fillArray(int slot, double[] data);

    public void closeConnection() {
        status = close(slot);
        checkStatus();
    }

    public native String close(int slot);

    @Override
    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    @Override
    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    @Override
    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    @Override
    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    @Override
    public String[] getCalibrationSettings() {
        return calibrationSettings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBusy() {
        return busy;
    }

    static {
        System.loadLibrary( "LTR212Library");
    }
}
