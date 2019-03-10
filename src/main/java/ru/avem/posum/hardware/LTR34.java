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
        status = open(crate, slot);
        checkStatus();
    }

    protected native String open(String crate, int slot);

    public void initModule() {
        status = initialize(getCheckedChannelsCounter());
        checkStatus();
    }

    public native String initialize(int channelsCounter);

    public native void generate(double[] signal);

    public native String start();

    public void closeConnection() {
        stop();
        close();
    }

    public native String stop();

    public native String close();

    static {
        System.loadLibrary("LTR34Library");
    }
}
