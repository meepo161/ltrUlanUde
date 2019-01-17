package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import ru.avem.posum.Main;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField userLogin;
    @FXML
    private TextField userPassword;

    private Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleLogIn() {
        
    }
}
