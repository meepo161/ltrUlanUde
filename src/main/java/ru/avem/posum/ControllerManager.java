package ru.avem.posum;

import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {

    void checkCalibration();

    void createListModulesControllers(List<String> modulesNames);

    String getCrate();

    CrateModel getCrateModelInstance();

    int getDecimalFormatScale();

    ExperimentModel getExperimentModel();

    boolean getICPMode();

    String getValueName();

    double getZeroShift();

    void giveChannelInfo(int channel, String moduleType, int slot);

    void hideRequiredFieldsSymbols();

    void initializeSignalGraphView();

    boolean isClosed();

    void loadDefaultCalibrationSettings(ADC adc, String moduleType, int channel);

    void loadDefaultSettings();

    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadModuleSettings(int selectedModuleIndex, String moduleName);

    void setStopped(boolean closed);

    void setEditMode(boolean editMode);

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void toggleSettingsSceneButtons(boolean isDisable);
}
