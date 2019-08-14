package ru.avem.posum.models.settings;

import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

public class LTR212SettingsModel {
    private String[] descriptions; // Описания каналов
    private boolean[] checkedChannels; // Задействованные каналы
    private LTR212 ltr212; // Инстанс модуля
    private int[] measuringRanges; // Диапазоны имзерений каналов
    private String moduleName; // Название модуля
    private int slot; // Номер слота
    private int[] typesOfChannels; // Режимы работы модулей

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr212 = (LTR212) instancesOfModules.get(slot);
        this.checkedChannels = ltr212.getCheckedChannels();
        this.descriptions = ltr212.getDescriptions();
        this.measuringRanges = ltr212.getMeasuringRanges();
        this.typesOfChannels = ltr212.getTypeOfChannels();
    }

    public void initModule() {
        ltr212.checkConnection();

        if (ltr212.checkStatus()) {
            ltr212.initializeModule();
        } else {
            ltr212.openConnection();
        }
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

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
