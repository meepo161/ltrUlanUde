package ru.avem.posum.hardware;

public class LTR34 extends DAC {
    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                setCheckedChannelsCounter(i + 1);
            }
        }
    }

    public void openConnection() {
        status = open(crate, getSlot());
        checkStatus();
    }

    public void initModule() {
        status = initialize(getCheckedChannelsCounter());
        checkStatus();
    }

    public void closeConnection() {
        stop();
        close();
    }

    public void stop() {
        stop(getSlot());
    }

    public native String open(String crate, int slot);

    public native String initialize(int channelsCounter);

    public native void generate(double[] signal);

    public native String start();

    public native String stop(int slot);

    public native String close();

    static {
        System.loadLibrary("LTR34Library");
    }
}
