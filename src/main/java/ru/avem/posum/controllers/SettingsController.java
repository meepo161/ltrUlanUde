package ru.avem.posum.controllers;

import ru.avem.posum.Main;

public class SettingsController {
    private Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleChooseCrate() {

    }

    public void handleSetupModule() {

    }

    public void handleSaveSetup() {

    }

    public void handleSaveTestingProgramm() {

    }

    public void handleBackButton() {
        Main.getPrimaryStage().setScene(main.getMainScene());
    }
}
