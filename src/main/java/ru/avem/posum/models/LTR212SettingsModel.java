package ru.avem.posum.models;

import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

public class LTR212SettingsModel {
    private String[] descriptions;
    private boolean[] checkedChannels;
    private boolean connectionOpen = true;
    private LTR212 ltr212;
    private int[] measuringRanges;
    private String moduleName;
    private int slot;
    private int[] typesOfChannels;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr212 = (LTR212) instancesOfModules.get(slot);
        this.checkedChannels = ltr212.getCheckedChannels();
        this.descriptions = ltr212.getDescriptions();
        this.measuringRanges = ltr212.getMeasuringRanges();
        this.typesOfChannels = ltr212.getTypeOfChannels();
    }

    public void initModule() {
        if (!connectionOpen) {
            ltr212.openConnection();
            connectionOpen = true;
        }

        ltr212.initializeModule();
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public int[] getTypesOfChannels() {
        return typesOfChannels;
    }

    public boolean[] getCheckedChannels() {
        return checkedChannels;
    }

    public LTR212 getLTR212Instance() {
        return ltr212;
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

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
