package ru.avem.posum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.avem.posum.controllers.*;
import ru.avem.posum.db.DataBaseRepository;

import java.awt.*;
import java.io.IOException;

public class Main extends Application implements WindowsManager {
    private Stage loginStage;
    private Stage primaryStage;

    private Scene loginScene;
    private Scene mainScene;
    private Scene settingsScene;
    private Scene processScene;

    private LoginController loginController;

    @Override
    public void init() throws IOException {
        DataBaseRepository.init(true);

        crateLoginScene();
        createMainScene();
        createSettingsScene();
        createProcessScene();
    }

    private void crateLoginScene() throws IOException {
        FXMLLoader loginViewLoader = new FXMLLoader();
        loginViewLoader.setLocation(Main.class.getResource("/layouts/loginView.fxml"));
        Parent loginViewParent = loginViewLoader.load();
        loginController = loginViewLoader.getController();
        loginController.setMainApp(this);

        loginScene = new Scene(loginViewParent, 300, 215);
        loginScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    if (!(event.getTarget() instanceof TextField)) {
                        Platform.exit();
                    }
                    break;
                case ENTER:
                    loginController.handleLogIn();
                    break;
            }
        });
    }

    private void createMainScene() throws IOException {
        mainScene = createScene("/layouts/mainView.fxml");
    }

    private Scene createScene(String layoutPath) throws IOException {
        FXMLLoader mainViewLoader = new FXMLLoader();
        mainViewLoader.setLocation(Main.class.getResource(layoutPath));
        Parent mainViewParent = mainViewLoader.load();
        BaseController baseController = mainViewLoader.getController();
        baseController.setWindowManager(this);

        return new Scene(mainViewParent, 1280, 720);
    }

    private void createSettingsScene() throws IOException {
        settingsScene = createScene("/layouts/settingsView.fxml");
    }

    private void createProcessScene() throws IOException {
        processScene = createScene("/layouts/processView.fxml");
    }

    @Override
    public void start(Stage loginStage) {
        this.loginStage = loginStage;
        this.loginStage.setTitle("Авторизация");
        this.loginStage.setScene(loginScene);
        this.loginStage.setMinWidth(300);
        this.loginStage.setMinHeight(250);
        this.loginStage.setWidth(300);
        this.loginStage.setHeight(250);
        this.loginStage.setMaxWidth(300);
        this.loginStage.setMaxHeight(250);
        this.loginStage.setResizable(false);
        setCentreOfStage(this.loginStage);
        this.loginStage.show();
    }

    private void setCentreOfStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void setMainView() {
        primaryStage = new Stage();
        primaryStage.setTitle("ПО Система управления многоканальная");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true);
        setCentreOfStage(primaryStage);
        primaryStage.show();
        loginStage.close();
    }

    @Override
    public void setScene(WindowsManager.Scenes scene) {
        switch (scene) {
            case MAIN_SCENE:
                primaryStage.setTitle("Программа испытаний");
                primaryStage.setScene(mainScene);
                break;
            case SETTINGS_SCENE:
                primaryStage.setTitle("Настрока программы испытаний");
                primaryStage.setScene(settingsScene);
                break;
            case PROCESS_SCENE:
                primaryStage.setTitle("Процесс испытаний");
                primaryStage.setScene(processScene);
                break;
            case SIGNAL_GRPAH_SCENE:
                break;
            case LTR34_SCENE:
                break;
            case LTR212_SCENE:
                break;
            case LTR24_SCENE:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
