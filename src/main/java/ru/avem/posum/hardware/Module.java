package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public abstract class Module {
    int channelsCount;
    String crate;
    private int slot;
    private long moduleId;
    String[] descriptions;
    boolean[] checkedChannels;
    protected volatile String status;
    private TextEncoder textEncoder = new TextEncoder();

    public boolean checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
            return false;
        } else {
            return true;
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

    public String getStatus() {
        return status;
    }

    public void setCrate(String crate) {
        this.crate = crate;
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
