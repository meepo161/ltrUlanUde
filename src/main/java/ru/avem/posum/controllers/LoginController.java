package ru.avem.posum.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.Main;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.db.AccountRepository;
import ru.avem.posum.db.models.Account;
import ru.avem.posum.utils.Toast;

import java.util.List;

public class LoginController implements BaseController {
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField loginTextField;

    private ControllerManager cm;
    private Main main;
    private WindowsManager wm;

    @FXML
    private void handleLogIn() {
        authenticateUser();
    }

    private void authenticateUser() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        if (login.isEmpty()) {
            Toast.makeText("Введите имя пользователя").show(Toast.ToastType.WARNING);
        } else if (password.isEmpty()) {
            Toast.makeText("Введите пароль").show(Toast.ToastType.WARNING);
        } else {
            check(login, password);
        }
    }

    private void check(String login, String password) {
        List<Account> accounts = AccountRepository.getAllAccounts();
        for (int accountIndex = 0; accountIndex < accounts.size(); accountIndex++) {
            boolean isLoginsEquals = login.equals(accounts.get(accountIndex).getUserName());
            boolean isPasswordsEquals = password.equals(accounts.get(accountIndex).getUserPassword());
            boolean isLastAccount = (accountIndex == accounts.size() - 1);

            if (isLoginsEquals && isPasswordsEquals) {
                cm.setAdministration(login.equals("admin"));
                main.setMainView();
                break;
            } else if (isLastAccount) {
                Toast.makeText("Неверное имя пользователя или пароль").show(Toast.ToastType.ERROR);
            }
        }
    }

    public void setMainApp(Main main) {
        this.main = main;
    }

    public void showScene() {
        wm.setScene(WindowsManager.Scenes.LOGIN_SCENE);
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) { this.cm = cm; }
}

