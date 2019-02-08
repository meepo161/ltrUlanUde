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

    public void initModule() {
        status = initialize(crate, slot, channelsCounter, checkedChannels);
        checkStatus();
    }

    public native String initialize(String crateSN, int slot, int channelsCounter, boolean[] checkedChannels);

    private void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public void closeConnection() {
        try {
            close();
            initModule();
            dataSend(new double[500_000]);
            start();
            Thread.sleep(100);
            close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public native void dataSend(double[] data);

    public native String start();

    public native String close();

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public int[][] getChannelsParameters() {
        return channelsParameters;
    }

    public int getChannelsCounter() {
        return channelsCounter;
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

    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channelsCounter = i + 1;
            }
        }
    }

    static {
        System.load( System.getProperty("user.dir") + "\\src\\main\\resources\\libs\\LTR34Library.dll");
    }
}
