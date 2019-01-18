package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ru.avem.posum.Main;

public class MainController {
    @FXML
    private TableView tableViewExperimentsMain;

    private Main main;

    @FXML
    private void initialize() {
        tableViewExperimentsMain.getColumns().addAll();
    }

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
