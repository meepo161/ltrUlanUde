package ru.avem.posum;

/**
* Интерфейс смены внешнего вида окна
*/

public interface WindowsManager {
    enum Scenes {
        LOGIN_SCENE, MAIN_SCENE, SETTINGS_SCENE, EXPERIMENT_SCENE, LINKING_SCENE, SIGNAL_GRAPH_SCENE, LTR34_SCENE,
        LTR212_SCENE, LTR24_SCENE, LTR27_SCENE, CALIBRATION_SCENE, LTR27_CALIBRATION_SCENE
    }

    void setScene(Scenes scene);

    void setModuleScene(String moduleName, int id);
}
