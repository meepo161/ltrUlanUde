package ru.avem.posum.models.settings;

import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

/**
 * Модель настроек модуля LTR24
 */

public class LTR24SettingsModel {
    private String[] descriptions; // Описания каналов
    private boolean[] checkedChannels; // Задействованные каналы
    private LTR24 ltr24; // Инстанс модуля
    private int[] measuringRanges; // Диапазоны имзерений каналов
    private String moduleName; // Название модуля
    private int slot; // Номер слота
    private int[] typesOfChannels; // Режимы работы модулей

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr24 = (LTR24) instancesOfModules.get(slot);
        ltr24.setSlot(slot);
        this.checkedChannels = ltr24.getCheckedChannels();
        this.descriptions = ltr24.getDescriptions();
        this.measuringRanges = ltr24.getMeasuringRanges();
        this.typesOfChannels = ltr24.getTypeOfChannels();
    }

    public void initModule() {
        ltr24.checkConnection();

        if (ltr24.checkStatus()) {
            ltr24.initializeModule();
        } else {
            ltr24.openConnection();
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

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
