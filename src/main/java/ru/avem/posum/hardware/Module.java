package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public abstract class Module {
    protected int channelsCount;
    protected String crate;
    protected int slot;
    protected long moduleId;
    protected boolean[] checkedChannels;
    protected String status;
    protected TextEncoder textEncoder = new TextEncoder();
    protected boolean busy; // значение переменной устанавливается из библиотеки dll, не удалять!

    public abstract void openConnection();

    public abstract void initModule();

    public abstract void closeConnection();

    public void checkStatus() {
        if (!status.equals("Операция успешно выполнена")) {
            status = textEncoder.cp2utf(status);
        }
    }

    public String getCrate() {
        return crate;
    }

    public int getChannelsCount() {
        return channelsCount;
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

    public void setCheckedChannels(boolean[] checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TextEncoder getTextEncoder() {
        return textEncoder;
    }

    public void setTextEncoder(TextEncoder textEncoder) {
        this.textEncoder = textEncoder;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
