package ru.avem.posum;

import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.controllers.process.LinkingController;
import ru.avem.posum.controllers.settings.HardwareSettings;
import ru.avem.posum.controllers.settings.LTR27.LTR27SettingsController;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.signal.SignalModel;

import java.util.List;

public interface ControllerManager {

    void checkCalibration();

    void createListModulesControllers(List<String> modulesNames);

    double getDc();

    String getCrateSerialNumber();

    Crate getCrateModelInstance();

    int getDecimalFormatScale();

    LinkingController getLinkingController();

    String getStyleSheet();

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

    void setTestProgram();

    void setStopped(boolean stopped);

    void setEditMode(boolean editMode);

    void showChannelValue();

    void showTestProgram(TestProgram testProgram);

    void stopAllModules();

    void initLtr27CalibrationView(String title, int submoduleIndex);

    BaseController getSettingsController();

    HardwareSettings getHardwareSettings();
}
