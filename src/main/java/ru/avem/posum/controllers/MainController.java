package ru.avem.posum.controllers;

import ru.avem.posum.Main;

public class MainController {
    private Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleMenuItemExit() {
    }

    public void handleMenuItemAdd() {
        main.setSettingsView();
        Main.getPrimaryStage().setScene(main.getSettingsScene());
    }

    public void handleMenuItemSettings() {
    }

    public void handleMenuItemDelete() {
    }

    public void handleMenuItemAboutUs() {
    }
}
