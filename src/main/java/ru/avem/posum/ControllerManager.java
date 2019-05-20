package ru.avem.posum;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.ExperimentModel;
import ru.avem.posum.models.Signal.SignalModel;

import java.util.List;

public interface ControllerManager {

    void checkCalibration();

    void createListModulesControllers(List<String> modulesNames);

    String getCrateSerialNumber();

    Crate getCrateModelInstance();

    int getDecimalFormatScale();

    ExperimentModel getExperimentModel();

    List<Modules> getLinkedModules();

    ObservableList<Pair<CheckBox, CheckBox>> getRemovedDescriptions();

    String getValueName();

    double getDc();

    void giveChannelInfo(int channel, String moduleType, int slot);

    void hideRequiredFieldsSymbols();

    void initializeSignalGraphView();

    void initListViews();

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

    void setTestProgram();

    void setStopped(boolean stopped);

    void setEditMode(boolean editMode);

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void stopAllModules();
}
