package ru.avem.posum.models;

import ru.avem.posum.hardware.LTR24;

public class LTR24SettingsModel {
    private String[] channelsDescriptions;
    private int[] channelsTypes;
    private boolean[] checkedChannels;
    private int disabledChannels;
    private boolean icpMode;
    private boolean connectionOpen = true;
    private LTR24 ltr24;
    private int[] measuringRanges;
    private String moduleName;
    private int slot;

    public int getDisabledChannels() {
        return disabledChannels;
    }

    public LTR24 getLtr24() {
        return ltr24;
    }

    public void setDisabledChannels(int disabledChannels) {
        this.disabledChannels = disabledChannels;
    }

    public void setLtr24(LTR24 ltr24) {
        this.ltr24 = ltr24;
        this.checkedChannels = ltr24.getCheckedChannels();
        this.channelsTypes = ltr24.getChannelsTypes();
        this.measuringRanges = ltr24.getMeasuringRanges();
        this.channelsDescriptions = ltr24.getChannelsDescription();
    }

    public void initModule() {
        if (!connectionOpen) {
            ltr24.openConnection();
        }

        ltr24.initializeModule();
    }

    public String[] getChannelsDescriptions() {
        return channelsDescriptions;
    }

    public void setChannelsDescriptions(String[] channelsDescriptions) {
        this.channelsDescriptions = channelsDescriptions;
    }

    public int[] getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(int[] channelsTypes) {
        this.channelsTypes = channelsTypes;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(boolean[] checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public boolean isIcpMode() {
        return icpMode;
    }

    public void setIcpMode(boolean icpMode) {
        this.icpMode = icpMode;
    }

    public boolean isConnectionOpen() {
        return connectionOpen;
    }

    public void setConnectionOpen(boolean connectionOpen) {
        this.connectionOpen = connectionOpen;
    }

    public int[] getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(int[] measuringRanges) {
        this.measuringRanges = measuringRanges;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
