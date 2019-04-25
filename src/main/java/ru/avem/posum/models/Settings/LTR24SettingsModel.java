package ru.avem.posum.models.Settings;

import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

public class LTR24SettingsModel {
    private String[] descriptions;
    private boolean[] checkedChannels;
    private boolean connectionOpen = true;
    private LTR24 ltr24;
    private int[] measuringRanges;
    private String moduleName;
    private int slot;
    private int[] typesOfChannels;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr24 = (LTR24) instancesOfModules.get(slot);
        this.checkedChannels = ltr24.getCheckedChannels();
        this.descriptions = ltr24.getDescriptions();
        this.measuringRanges = ltr24.getMeasuringRanges();
        this.typesOfChannels = ltr24.getTypeOfChannels();
    }

    public void initModule() {
        if (!connectionOpen) {
            ltr24.openConnection();
            connectionOpen = true;
        }

        ltr24.initializeModule();
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

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
