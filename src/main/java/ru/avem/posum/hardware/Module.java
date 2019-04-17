package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public abstract class Module {
    int channelsCount;
    String crate;
    private int slot;
    private long moduleId;
    String[] channelsDescription;
    boolean[] checkedChannels;
    protected volatile String status;
    private TextEncoder textEncoder = new TextEncoder();

    void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public abstract void stop();

    public int getChannelsCount() {
        return channelsCount;
    }

    public String[] getChannelsDescription() {
        return channelsDescription;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
