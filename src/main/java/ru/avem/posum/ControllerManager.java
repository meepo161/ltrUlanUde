package ru.avem.posum;

import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    String getCrate();

    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadDefaultSettings();

    void toggleSettingsSceneButtons(boolean isDisable);

    void createListModulesControllers(List<String> modulesNames);

    void showChannelData(String moduleType, int slot, int channel);

    CrateModel getCrateModelInstance();

    ExperimentModel getExperimentModel();

    double getMaxValue();

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void setEditMode(boolean editMode);

    void hideRequiredFieldsSymbols();

    void loadDefaultCalibrationSettings(ADC adc, int channel);

    boolean isClosed();

    void setClosed(boolean closed);

    void loadModuleSettings(int selectedModuleIndex, String moduleName);
}
