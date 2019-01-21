package ru.avem.posum;

public interface WindowsManager {
     enum Scenes {
        LOGIN_SCENE, MAIN_SCENE, SETTINGS_SCENE, PROCESS_SCENE, SIGNAL_GRPAH_SCENE, LTR34_SCENE, LTR212_SCENE, LTR24_SCENE
    }

    void setScene(Scenes scene);
}
