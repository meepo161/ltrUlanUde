package ru.avem.posum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.avem.posum.controllers.LoginController;
import ru.avem.posum.controllers.MainController;
import ru.avem.posum.db.DataBaseRepository;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {
    private static Stage LOGIN_STAGE;
    private static Stage PRIMARY_STAGE;

    private Scene loginScene;
    private Scene mainScene;

    private LoginController loginViewController;
    private MainController mainViewController;


    @Override
    public void init() throws IOException {
        DataBaseRepository.init(true);

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
                case ENTER:
                    loginViewController.handleLogIn();
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
    public void start(Stage loginStage) throws Exception {
        LOGIN_STAGE = loginStage;
        setLoginView();
        LOGIN_STAGE.show();
    }

    private void setLoginView() {
        LOGIN_STAGE.setTitle("Авторизация");
        LOGIN_STAGE.setScene(loginScene);
        LOGIN_STAGE.setMinWidth(300);
        LOGIN_STAGE.setMinHeight(250);
        LOGIN_STAGE.setWidth(300);
        LOGIN_STAGE.setHeight(250);
        LOGIN_STAGE.setMaxWidth(300);
        LOGIN_STAGE.setMaxHeight(250);
        LOGIN_STAGE.setResizable(false);

        setCentreOfStage(LOGIN_STAGE);
    }

    private void setCentreOfStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void setMainView() {
        PRIMARY_STAGE = new Stage();
        PRIMARY_STAGE.setTitle("ПО Система управления многоканальная");
        PRIMARY_STAGE.setScene(mainScene);
        PRIMARY_STAGE.setMinWidth(1280);
        PRIMARY_STAGE.setMinHeight(720);
        PRIMARY_STAGE.setWidth(1280);
        PRIMARY_STAGE.setHeight(720);
        PRIMARY_STAGE.setResizable(true);

        setCentreOfStage(PRIMARY_STAGE);

        PRIMARY_STAGE.show();
        LOGIN_STAGE.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
