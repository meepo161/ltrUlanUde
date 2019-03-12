package ru.avem.posum;

import javafx.util.Pair;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.models.ExperimentModel;

import java.util.List;

public interface ControllerManager {
    void loadItemsForMainTableView();

    void loadItemsForModulesTableView();

    void loadDefaultSettings();

    void toggleSettingsSceneButtons(boolean isDisable);

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

    void showTestProgram(TestProgram testProgram);

    void setEditMode(boolean editMode);

    void hideRequiredFieldsSymbols();

    LTR24 getLTR24Instance();

    LTR212 getLTR212Instance();

    void loadDefaultCalibrationSettings(CrateModel.Moudules moduleType, int channel);

    boolean isClosed();

    void setClosed(boolean closed);
}
