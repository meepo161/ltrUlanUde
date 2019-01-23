package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

public class LTR24 extends Thread {
    private boolean[] checkedChannels = new boolean[8];
    private int[] channelsTypes = new int[8];
    private int[] measuringRanges = new int[8];
    private String[] channelsDescription = new String[8];
    private String crate;
    private int slot;
    private double[] data = new double[1024];
    private RingBuffer ringBuffer = new RingBuffer(data.length * 100);
    private String status;
    private TextEncoder textEncoder = new TextEncoder();

    public void initModule() {
        status = initialize(crate, slot, channelsTypes, measuringRanges);
        checkStatus();
    }

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public native String initialize(String crate, int slot, int[] channelsTypes, int[] measuringRanges);

    public native String fillArray(double[] data);

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

    public String getStatus() {
        return status;
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

    static {
        System.loadLibrary("LTR24Library");
    }

    public void run() {
        fillArray(data);
        ringBuffer.put(data);
    }
}
