package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR24 {
    private boolean[] checkedChannels = new boolean[4];
    private int[] channelsTypes = new int[4];
    private int[] measuringRanges = new int[4];
    private String[] channelsDescription = new String[4];
    private String crate;
    private int slot;
    private String status;
    private TextEncoder textEncoder = new TextEncoder();
    private boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

    public void initModule() {
        status = initialize(crate, slot, channelsTypes, measuringRanges);
        checkStatus();
    }

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public void receiveData(double[] data) {
        status = fillArray(slot, data);
        checkStatus();
    }

    public native String initialize(String crate, int slot, int[] channelsTypes, int[] measuringRanges);

    public native String fillArray(int slot, double[] data);

    public native String closeModule();

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
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

    public boolean isBusy() {
        return busy;
    }

    static {
        System.loadLibrary("LTR24Library");
    }
}
