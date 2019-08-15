package ru.avem.posum.models.settings;

import ru.avem.posum.controllers.settings.LTR27.LTR27SettingsController;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.utils.Utils;

import java.util.HashMap;

/**
 * Модель настроек модуля LTR27
 */

public class LTR27SettingsModel {
    private double[] data; // Данные модуля
    private String[][] descriptions; // Информация о субмодулях
    private LTR27 ltr27; // Инстанс модуля
    private LTR27SettingsController ltr27SettingsController; // Инстанс контроллера
    private String moduleName;  // Название модуля
    private int slot; // Номер слота
    private double[] timeMarks; // Синхрометки

    public LTR27SettingsModel(LTR27SettingsController ltr27SettingsController) {
        this.ltr27SettingsController = ltr27SettingsController;
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

    // Записывает данные, полученные от модуля
    public void receiveData() {
        new Thread(() -> {
            data = new double[LTR27.MAX_FREQUENCY * LTR27.MAX_SUBMODULES];
            timeMarks = new double[data.length];
            while (!ltr27SettingsController.isStopped()) {
                ltr27.write(data, timeMarks);

                Utils.sleep(100);
            }
        }).start();
    }

    // Останавливает модуль
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
