package ru.avem.posum.hardware;

public class LTR34 extends DAC {
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!

    public void countChannels() {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                setCheckedChannelsCounter(i + 1);
            }
        }
    }

    @Override
    public void openConnection() {
        status = openConnection(crate, getSlot());
        checkStatus();
    }

    @Override
    public void checkConnection() {
        CrateModel crateModel = new CrateModel();
        if (!crateModel.getCratesNames().isEmpty()) {
            status = checkConnection(getSlot());
            checkStatus();
        } else {
            openConnection();
            status = "Потеряно соединение с крейтом";
        }
    }

    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getCheckedChannelsCounter());
    }

    @Override
    public void defineFrequency() {
        getFrequency(getSlot());
    }

    @Override
    public void start() {
        status = start(getSlot());
    }

    @Override
    public void generate(double[] signal) {
        status = generate(getSlot(), signal, signal.length);
    }

    @Override
    public void stop() {
        status = stop(getSlot());
        checkStatus();
    }

    @Override
    public void closeConnection() {
        status = closeConnection(getSlot());
        checkStatus();
    }

    public native String openConnection(String crate, int slot);

    public native String checkConnection(int slot);

    public native String initialize(int slot, int channelsCounter);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String generate(int slot, double[] signal, int length);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR34Library");
    }

    @Override
    public double getFrequency() {
        return frequency;
    }
}
