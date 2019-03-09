package ru.avem.posum.hardware;

public class LTR212 extends ADC {
    @Override
    public void openConnection() {
        status = open(crate, slot, System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio");
        checkStatus();
    }

    public native String open(String crate, int slot, String path);

    static {
        System.loadLibrary( "LTR212Library");
    }
}
