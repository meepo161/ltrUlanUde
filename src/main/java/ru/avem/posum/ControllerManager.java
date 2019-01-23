package ru.avem.posum;

import ru.avem.posum.hardware.Crate;

public interface ControllerManager {
    void loadItemsForTableView();

    int getSelectedCrate();

    int getSelectedModule();

    Crate getCrateInstance();
}
