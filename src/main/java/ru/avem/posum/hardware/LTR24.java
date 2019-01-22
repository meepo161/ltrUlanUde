package ru.avem.posum.hardware;

public class LTR24 {
    private int[] checkedChannels = new int[8];
    private int[] channelsTypes = new int[8];
    private int[] measuringRanges = new int[8];

    public LTR24() {
        setDefaultParameters();
    }

    /**
     * Для каналов измерения виброускорения выбраны:
     * 0 - Режим ICP-вход
     * 1 - ~5 В
     *
     * Для каналов измерения перемещения выбраны:
     * 0 - Дифференциальный вход без отсечки постоянной составляющей
     * 1 - -10 В/+10 В
     */
    private void setDefaultParameters() {
        for (int i = 0; i < channelsTypes.length; i++) {
            channelsTypes[i] = 0;
            measuringRanges[i] = 1;
        }
    }

    public native String initialize(int slot, int[] selectedBridgeTypes, int[] selectedMeasuringRanges);

    public native String fillArray(double[] data);

    public native String stop();

    public int[] getCheckedChannels() {
        return checkedChannels;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    static {
        System.loadLibrary("LTR24Library");
    }
}
