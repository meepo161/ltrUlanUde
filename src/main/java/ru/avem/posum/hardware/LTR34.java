package ru.avem.posum.hardware;

public class LTR34 extends DAC {
    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                setCheckedChannelsCounter(i + 1);
            }
        }
    }

    @Override
    public void initModule() {
        status = initialize(getCheckedChannelsCounter());
        checkStatus();
    }

    public native String initialize(int channelsCounter);

    static {
        System.loadLibrary("LTR34Library");
    }
}
