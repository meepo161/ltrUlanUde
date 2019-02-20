package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR24 {
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

    public LTR24() {
        String defaultCalibrationSettings = "0.0, 0.0, 0.0, 0.0, В";
        status = "";

        for (int i = 0; i < CHANNELS; i++) {
            channelsDescription[i] = "";
            calibrationSettings[i] = defaultCalibrationSettings;
        }
    }

    public void openConnection() {
        status = open(crate, slot);
        checkStatus();
    }

    public native String open(String crate, int slot);

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
        close(slot);
    }

    public native String close(int slot);

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(boolean[] checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(int[] channelsTypes) {
        this.channelsTypes = channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = measuringRanges;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public void setChannelsDescription(String[] channelsDescription) {
        this.channelsDescription = channelsDescription;
    }

    public String[] getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(String[] calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TextEncoder getTextEncoder() {
        return textEncoder;
    }

    public void setTextEncoder(TextEncoder textEncoder) {
        this.textEncoder = textEncoder;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    static {
        System.loadLibrary( "LTR24Library");
    }
}
