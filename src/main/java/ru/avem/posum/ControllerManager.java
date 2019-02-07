package ru.avem.posum;

import ru.avem.posum.db.models.TestProgramm;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void refreshLTR24Settings();

    void loadDefaultSettings();

    void createListModulesControllers(List<String> modulesNames);

    void showChannelData(CrateModel.Moudules moduleType, int slot, int channel);

    int getSelectedCrate();

    int getSelectedModule();

    int getSlot();

    CrateModel getCrateModelInstance();

    ExperimentModel getExperimentModel();

    void setupProtocol(TestProgramm testProgramm);

    void setEditMode(boolean editMode);

    boolean isClosed();

    void setClosed(boolean closed);
}
