package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR212 {
    private boolean[] checkedChannels = new boolean[4];
    private int[] channelsTypes = new int[4];
    private int[] measuringRanges = new int[4];
    private String[] channelsDescription = new String[4];
    private String crate;
    private int slot;
    private String status = "";
    private TextEncoder textEncoder = new TextEncoder();
    private boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

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
        System.loadLibrary( "LTR212Library");
    }
}
