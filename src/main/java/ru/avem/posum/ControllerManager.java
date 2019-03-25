package ru.avem.posum;

import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    void checkCalibration();

    void createListModulesControllers(List<String> modulesNames);

    int getDecimalFormatScale();

    String getCrate();

    CrateModel getCrateModelInstance();

    ExperimentModel getExperimentModel();

    boolean getICPMode();

    String getValueName();

    double getZeroShift();

    void hideRequiredFieldsSymbols();

    boolean isClosed();

    void loadDefaultCalibrationSettings(ADC adc, String moduleType, int channel);

    void loadDefaultSettings();

    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadModuleSettings(int selectedModuleIndex, String moduleName);

    void setStopped(boolean closed);

    void setEditMode(boolean editMode);

    void showChannelData(String moduleType, int slot, int channel);

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void toggleSettingsSceneButtons(boolean isDisable);
}
