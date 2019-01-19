package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.avem.posum.Main;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.AccountRepository;
import ru.avem.posum.db.models.Account;
import ru.avem.posum.utils.Toast;

import java.util.List;

public class LoginController implements BaseController {
    @FXML
    private TextField userLogin;
    @FXML
    private PasswordField userPassword;

    private Main main;
    private WindowsManager wm;


    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public void showScene() {
        wm.setScene(WindowsManager.Scenes.LOGIN_SCENE);
    }

    public void handleLogIn() {
        authenticateUser();
    }

    private void authenticateUser() {
        List<Account> allAccounts = AccountRepository.getAllAccounts();
        String login = userLogin.getText();
        String password = userPassword.getText();

        if (login.isEmpty()) {
            Toast.makeText("Введите имя пользователя").show(Toast.ToastType.WARNING);
        } else if (password.isEmpty()) {
            Toast.makeText("Введите пароль").show(Toast.ToastType.WARNING);
        } else {
            checkLoginAndPassword(allAccounts, login, password);
        }
    }

    private void checkLoginAndPassword(List<Account> allAccounts, String login, String password) {
        for (int i = 0; i < allAccounts.size(); i++) {
            if (login.equals(allAccounts.get(i).getUserName()) && password.equals((allAccounts.get(i).getUserPassword()))) {
                main.setMainView();
                break;
            } else {
                if (i == allAccounts.size() - 1) {
                    Toast.makeText("Неверное имя пользователя или пароль").show(Toast.ToastType.ERROR);
                }
            }
        }
    }

    public void setMainApp(Main main) {
        this.main = main;
    }
}

