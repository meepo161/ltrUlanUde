package ru.avem.posum.models.settings;

import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

public class LTR27SettignsModel {
    private String[][] descriptions;
    private LTR27 ltr27;
    private String moduleName;
    private int slot;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr27 = (LTR27) instancesOfModules.get(slot);
        ltr27.setSlot(slot);
        this.descriptions = ltr27.getInfo();
    }

    public boolean initModule(int frequencyIndex) {
        ltr27.checkConnection();

        if (ltr27.checkStatus()) {
            setFrequency(frequencyIndex);
            ltr27.initializeModule();
            return true;
        } else {
            ltr27.openConnection();
            ltr27.setStatus("Потеряно соединение с крейтом.");
            return false;
        }
    }

    private void setFrequency(int frequencyIndex) {
        ltr27.getSettingsOfModule().put(ADC.Settings.FREQUENCY, frequencyIndex);
    }

    public String[][] getDescriptions() {
        return descriptions;
    }

    public LTR27 getModuleInstance() { return ltr27; }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
