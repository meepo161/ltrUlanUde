package ru.avem.posum.hardware;

public class LTR212 extends ADC {
    public void openConnection() {
        clearStatus();
        status = open(crate, slot, System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    private void clearStatus() {
        status = "";
    }

    public native String open(String crate, int slot, String path);

    public void initModule() {
        clearStatus();
        status = initialize(slot, channelsTypes, measuringRanges, moduleSettings);
        checkStatus();
    }

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings);

    public void receive(double[] data) {
        clearStatus();
        status = fillArray(slot, data, timeMarks);
        checkStatus();
    }

    public native String fillArray(int slot, double[] data, double[] timeMarks);

    public void closeConnection() {
        close(slot);
        checkStatus();
    }

    public native String close(int slot);

    static {
        System.loadLibrary( "LTR212Library");
    }
}
