package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class LTR34 {
    private boolean[] checkedChannels = new boolean[8];
    private int[][] channelsParameters = new int[8][8];
    private int channelsCounter;
    private String crate;
    private int slot;
    private String status;
    private TextEncoder textEncoder = new TextEncoder();
    private boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

    public void openConnection() {
        status = open(crate, slot);
        checkStatus();
    }

    public native String open(String crate, int slot);

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public void initModule() {
        status = initialize(channelsCounter);
        checkStatus();
    }

    public native String initialize(int channelsCounter);

    public void closeConnection() {
        stop();
        close();
    }

    public native void generate(double[] data);

    public native String start();

    public native String stop();

    public native String close();

    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channelsCounter = i + 1;
            }
        }
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(boolean[] checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public int[][] getChannelsParameters() {
        return channelsParameters;
    }

    public void setChannelsParameters(int[][] channelsParameters) {
        this.channelsParameters = channelsParameters;
    }

    public int getChannelsCounter() {
        return channelsCounter;
    }

    public void setChannelsCounter(int channelsCounter) {
        this.channelsCounter = channelsCounter;
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

    static {
        System.loadLibrary("LTR34Library");
    }
}
