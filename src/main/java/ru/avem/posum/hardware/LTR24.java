package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR24 {
    private int[] checkedChannels = new int[8];
    private String[] descriptionOfChannels = new String[8];
    private int[] channelsTypes = new int[8];
    private int[] measuringRanges = new int[8];
    private String crate;
    private int slot;
    private String status;
    private TextEncoder textEncoder = new TextEncoder();

    public void start() {
        status = initialize(crate, slot, channelsTypes, measuringRanges);
        status = textEncoder.cp2utf(status);
    }

    public native String initialize(String crate, int slot, int[] channelsTypes, int[] measuringRanges);

    public native String fillArray(double[] data);

    public native String stop();

    public int[] getCheckedChannels() {
        return checkedChannels;
    }

    public String[] getDescriptionOfChannels() {
        return descriptionOfChannels;
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

    public void setSlot(int slot) {
        this.slot = slot;
    }

    static {
        System.loadLibrary("LTR24Library");
    }
}
