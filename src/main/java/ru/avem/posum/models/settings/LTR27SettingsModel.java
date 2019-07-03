package ru.avem.posum.models.settings;

import ru.avem.posum.controllers.settings.LTR27.LTR27Settings;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.Utils;

import java.util.HashMap;

public class LTR27SettingsModel {
    private double[] data;
    private String[][] descriptions;
    private LTR27 ltr27;
    private LTR27Settings ltr27Settings;
    private String moduleName;
    private int slot;
    private double[] timeMarks;

    public LTR27SettingsModel(LTR27Settings ltr27Settings) {
        this.ltr27Settings = ltr27Settings;
    }

    public void setModuleInstance(HashMap<Integer, Module> instancesOfModules) {
        this.ltr27 = (LTR27) instancesOfModules.get(slot);
        ltr27.setSlot(slot);
        this.descriptions = ltr27.getInfo();
    }

    public boolean initModule(int frequencyIndex) {
        ltr27.openConnection();
        ltr27.checkConnection();

        if (ltr27.checkStatus()) {
            setFrequency(frequencyIndex);
            ltr27.initializeModule();
            ltr27.start();
            return true;
        } else {
            return false;
        }
    }

    public void receiveData() {
        new Thread(() -> {
            data = new double[(int) ltr27.getFrequency() * LTR27.MAX_SUBMODULES];
            timeMarks = new double[data.length];
            while (!ltr27Settings.isStopped()) {
                ltr27.write(data, timeMarks);
                Utils.sleep(100);
            }
        }).start();
    }

    public void stop() {
        ltr27.stop();
        ltr27.closeConnection();
    }

    public double[] getData() {
        return data;
    }

    public String[][] getDescriptions() {
        return descriptions;
    }

    public LTR27 getModuleInstance() {
        return ltr27;
    }

    private void setFrequency(int frequencyIndex) {
        ltr27.getSettingsOfModule().put(ADC.Settings.FREQUENCY, frequencyIndex);
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
