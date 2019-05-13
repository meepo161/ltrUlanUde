package ru.avem.posum;

import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.ExperimentModel;
import ru.avem.posum.models.Signal.SignalModel;

import java.util.List;

public interface ControllerManager {

    void checkCalibration();

    void createListModulesControllers(List<String> modulesNames);

    String getCrateSerialNumber();

    Crate getCrateModelInstance();

    int getDecimalFormatScale();

    ExperimentModel getExperimentModel();

    String getValueName();

    double getDc();

    void giveChannelInfo(int channel, String moduleType, int slot);

    void hideRequiredFieldsSymbols();

    void initializeSignalGraphView();

    boolean isClosed();

    boolean isStopped();

    void loadDefaultCalibrationSettings(SignalModel signalModel);

    void loadDefaultSettings();

    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadModuleSettings(int selectedModuleIndex, String moduleName);

    void selectGeneralSettingsTab();

    void setAdministration(boolean administration);

    void setClosed(boolean closed);

    void setStopped(boolean stopped);

    void setEditMode(boolean editMode);

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void stopAllModules();
}
