package ru.avem.posum;

import ru.avem.posum.db.models.Protocol;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    int getSelectedCrate();

    int getSelectedModule();

    CrateModel getCrateModelInstance();

    void refreshLTR24Settings();

    void refreshLTR212Settings();

    void createListModulesControllers(List<String> modulesNames);

    void showChannelData(CrateModel.Moudules moduleType, int slot, int channel);

    ExperimentModel getExperimentModel();

    boolean isClosed();

    void setClosed(boolean closed);

    void clearSettingsView();

    void setupProtocol(Protocol protocol);

    void setEditMode(boolean editMode);
}
