package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public abstract class Module {
    int channelsCount;
    boolean[] checkedChannels;
    private boolean connectionOpen;
    String crateSerialNumber;
    double[] data;
    String[] descriptions;
    private long moduleId;
    private int slot;
    protected String status;
    private TextEncoder textEncoder = new TextEncoder();

    public boolean checkStatus() {
        if (status.equals("Операция успешно выполнена")) {
            return true;
        } else if (status.equals("Потеряно соединение с крейтом")) {
            return false;
        } else {
            status = textEncoder.cp2utf(status);
            return false;
        }
    }

    public String encode(String string) {
        return textEncoder.cp2utf(string);
    }

    public abstract void openConnection();

    public abstract void checkConnection();

    public abstract void initializeModule();

    public abstract void defineFrequency();

    public abstract void start();

    public abstract void stop();

    public abstract void closeConnection();


    public int getChannelsCount() {
        return channelsCount;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public double[] getData() {
        return data;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public long getModuleId() {
        return moduleId;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isConnectionOpen() {
        return connectionOpen;
    }

    public String getStatus() {
        return status;
    }

    public void setConnectionOpen(boolean connectionOpen) {
        this.connectionOpen = connectionOpen;
    }

    public void setChannelsCount(int channelsCount) {
        this.channelsCount = channelsCount;
    }

    public void setCrateSerialNumber(String crateSerialNumber) {
        this.crateSerialNumber = crateSerialNumber;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
