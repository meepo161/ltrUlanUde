package ru.avem.posum.models.settings;

import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.hardware.Module;

import java.util.HashMap;

public class LTR27SettignsModel {
    private String[][] descriptions;
    private boolean[] checkedSubmodules;
    private LTR27 ltr27;
    private String moduleName;
    private int slot;

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr27 = (LTR27) instancesOfModules.get(slot);
        ltr27.setSlot(slot);
//        this.checkedSubmodules = ltr27.getCheckedSubmodules();
        this.descriptions = ltr27.getInfo();
    }

    public void initModule() {
        ltr27.checkConnection();

        if (ltr27.checkStatus()) {
            ltr27.initializeModule();
        } else {
            ltr27.openConnection();
        }
    }

    public String[][] getDescriptions() {
        return descriptions;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
