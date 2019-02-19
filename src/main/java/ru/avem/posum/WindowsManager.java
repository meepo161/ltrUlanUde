package ru.avem.posum;

public interface WindowsManager {
    enum Scenes {
        LOGIN_SCENE, MAIN_SCENE, SETTINGS_SCENE, PROCESS_SCENE, SIGNAL_GRAPH_SCENE, LTR34_SCENE, LTR212_SCENE, LTR24_SCENE, CALIBRATION_SCENE
    }

    void setScene(Scenes scene);

    void setModuleScene(String moduleName, int id);
}
