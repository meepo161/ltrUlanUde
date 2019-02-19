package ru.avem.posum;

import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadDefaultSettings();

    void loadLTR24Settings(int id);

    void loadLTR34Settings(int id);

    void loadLTR212Settings(int id);

    void createListModulesControllers(List<String> modulesNames);

    void showChannelData(CrateModel.Moudules moduleType, int slot, int channel);

    int getSelectedCrate();

    int getSelectedModule();

    int getSlot();

    CrateModel getCrateModelInstance();

    ExperimentModel getExperimentModel();

    double getMaxValue();

    void showChannelValue();

    void setCalibrationStopped();

    void showTestProgram(TestProgram testProgram);

    void setEditMode(boolean editMode);

    void hideRequiredFieldsSymbols();

    boolean isClosed();

    void setClosed(boolean closed);
}
