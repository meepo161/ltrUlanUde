package ru.avem.posum.hardware;

public class LTR24 extends ADC {
    @Override
    public void openConnection() {
        status = open(crate, slot);
        checkStatus();
    }

    public native String open(String crate, int slot);

    static {
        System.loadLibrary( "LTR24Library");
    }
}
