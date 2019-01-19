package ru.avem.posum.hardware;

public class LTR34 {
    public native String initialize(int slot);

    public native void dataSend(double[] data);

    public native String start();

    public native String stop();

    static {
        System.loadLibrary("LTR34Library");
    }
}
