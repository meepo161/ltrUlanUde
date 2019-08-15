package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

/**
 * Класс модуля крейта
 */

public abstract class Module {
    int channelsCount; // количество каналов модуля
    boolean[] checkedChannels; // задействованные каналы
    private boolean connectionOpen; // состояние соединения с модулем
    String crateSerialNumber; // серийный номер крейта
    double[] data; // данные модуля
    String[] descriptions; // описание каналов модуля
    private long moduleId; // id модуля
    private int slot; // слот модуля
    protected String status; // результат выполнения операции модуля
    private TextEncoder textEncoder = new TextEncoder(); // расшифровывает текст, полученный от модуля

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

    public void setCheckedChannels(boolean[] checkedChannels) {
        this.checkedChannels = checkedChannels;
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

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
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
