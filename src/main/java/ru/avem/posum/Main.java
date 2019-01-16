package ru.avem.posum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.avem.posum.controllers.LoginController;
import ru.avem.posum.controllers.MainController;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {
    private static Stage PRIMARY_STAGE;

    private Scene loginScene;
    private Scene mainScene;

    private LoginController loginViewController;
    private MainController mainViewController;

    @Override
    public void init() throws IOException {
        crateLoginScene();
        createMainScene();
    }

    private void crateLoginScene() throws IOException {
        FXMLLoader loginViewLoader = new FXMLLoader();
        loginViewLoader.setLocation(Main.class.getResource("/layouts/loginView.fxml"));
        Parent loginViewParent = loginViewLoader.load();
        loginViewController = loginViewLoader.getController();
        loginViewController.setMainApp(this);

        loginScene = new Scene(loginViewParent, 300, 215);
        loginScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (!(event.getTarget() instanceof TextField)) {
                        Platform.exit();
                    }
                    break;
            }
        });
    }

    private void createMainScene() throws IOException {
        FXMLLoader mainViewLoader = new FXMLLoader();
        mainViewLoader.setLocation(Main.class.getResource("/layouts/mainView.fxml"));
        Parent mainViewParent = mainViewLoader.load();
        mainViewController = mainViewLoader.getController();
        mainViewController.setMainApp(this);

        mainScene = new Scene(mainViewParent, 1280, 720);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        PRIMARY_STAGE = primaryStage;
        showLoginView();
        PRIMARY_STAGE.show();
    }

    private void showLoginView() {
        PRIMARY_STAGE.setTitle("Авторизация");
        PRIMARY_STAGE.setScene(loginScene);
    }
}
