package ru.avem.posum;

import ru.avem.posum.hardware.CrateModel;

import java.util.List;

public interface ControllerManager {
    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    int getSelectedCrate();

    int getSelectedModule();

    CrateModel getCrateModelInstance();

    void refreshLTR24Settings();

    void createListModulesControllers(List<String> modulesNames);
}
