package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public abstract class Module {
    int channelsCount;
    String crateSerialNumber;
    private int slot;
    private boolean connectionOpen;
    private long moduleId;
    String[] descriptions;
    boolean[] checkedChannels;
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
