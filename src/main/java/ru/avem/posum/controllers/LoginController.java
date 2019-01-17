package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.avem.posum.Main;
import ru.avem.posum.db.AccountRepository;
import ru.avem.posum.db.models.Account;
import ru.avem.posum.utils.Toast;

import java.util.List;

public class LoginController {
    @FXML
    private TextField userLogin;
    @FXML
    private PasswordField userPassword;

    private Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleLogIn() {
        authenticateUser();
    }

    public void authenticateUser() {
        List<Account> allAccounts = AccountRepository.getAllAccounts();
        String login = userLogin.getText();
        String password = userPassword.getText();

        if (login.isEmpty()) {
            Toast.makeText("Введите имя пользователя").show(Toast.ToastType.WARNING);
        } else if (password.isEmpty()) {
            Toast.makeText("Введите пароль").show(Toast.ToastType.WARNING);
        } else {
            for (int i = 0; i < allAccounts.size(); i++) {
                if (login.equals(allAccounts.get(i).getUserName()) && password.equals((allAccounts.get(i).getUserPassword()))) {
                    main.showMainView();
                    break;
                } else {
                    if (i == allAccounts.size() - 1) {
                        Toast.makeText("Неверное имя пользователя или пароль").show(Toast.ToastType.ERROR);
                    }
                }
            }
        }
    }
}

