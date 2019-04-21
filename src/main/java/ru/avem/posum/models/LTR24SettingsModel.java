package ru.avem.posum.models;

import ru.avem.posum.hardware.LTR24;

public class LTR24SettingsModel {
    private String[] channelsDescriptions;
    private int[] channelsTypes;
    private boolean[] checkedChannels;
    private int disabledChannels;
    private boolean connectionOpen = true;
    private LTR24 ltr24;
    private int[] measuringRanges;
    private String moduleName;
    private int slot;

    public void initModule() {
        if (!connectionOpen) {
            ltr24.openConnection();
            connectionOpen = true;
        }

        ltr24.initializeModule();
    }

    public String[] getChannelsDescriptions() {
        return channelsDescriptions;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public int getDisabledChannels() {
        return disabledChannels;
    }

    public LTR24 getLTR24Instance() {
        return ltr24;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isConnectionOpen() {
        return connectionOpen;
    }

    public void setConnectionOpen(boolean connectionOpen) {
        this.connectionOpen = connectionOpen;
    }

    public void setDisabledChannels(int disabledChannels) {
        this.disabledChannels = disabledChannels;
    }

    public void setLTR24Instance(LTR24 ltr24) {
        this.ltr24 = ltr24;
        this.checkedChannels = ltr24.getCheckedChannels();
        this.channelsTypes = ltr24.getChannelsTypes();
        this.measuringRanges = ltr24.getMeasuringRanges();
        this.channelsDescriptions = ltr24.getChannelsDescription();
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
