package ru.avem.posum.hardware;

import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

public class LTR212 {
    private boolean[] checkedChannels = new boolean[4];
    private int[] channelsTypes = new int[4];
    private int[] measuringRanges = new int[4];
    private String[] channelsDescription = new String[4];
    private String crate;
    private int slot;
    private double[] data = new double[128];
    private RingBuffer ringBuffer = new RingBuffer(data.length * 10);
    private String status;
    private TextEncoder textEncoder = new TextEncoder();

    public void initModule() {
        status = initialize(crate, slot, channelsTypes, measuringRanges);
        checkStatus();
    }

    public native String initialize(String crate, int slot, int[] channelsTypes, int[] measuringRanges);

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public native String fillArray(int slot, double[] data);

    public void stop() {
        status = closeConnection(slot);
        checkStatus();
    }

    public native String closeConnection(int slot);

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

    public RingBuffer getRingBuffer() {
        return ringBuffer;
    }

    public String getStatus() {
        return status;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    static {
        System.loadLibrary("LTR212Library");
    }
}
