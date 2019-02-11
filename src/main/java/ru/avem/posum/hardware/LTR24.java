package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR24 {
    private boolean[] checkedChannels = new boolean[4];
    private int[] channelsTypes = {0, 0, 0, 0};
    private int[] measuringRanges = {1, 1, 1, 1};
    private String[] channelsDescription = {"", "", "", ""};
    private String crate;
    private int slot;
    private String status = "";
    private TextEncoder textEncoder = new TextEncoder();
    private boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

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

    public boolean isBusy() {
        return busy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    static {
        System.load( System.getProperty("user.dir") + "\\src\\main\\resources\\libs\\LTR24Library.dll");
    }
}
