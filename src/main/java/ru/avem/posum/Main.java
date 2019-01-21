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

public class Main extends Application implements WindowsManager, ControllerManager {
    private Stage loginStage;
    private Stage primaryStage;

    private Scene loginScene;
    private Scene mainScene;
    private Scene settingsScene;
    private Scene processScene;
    private Scene ltr24Scene;
    private Scene ltr34Scene;
    private Scene ltr212Scene;

    private LoginController loginController;
    private MainController mainController;
    private SettingsController settingsController;
    private ProcessController processController;
    private LTR24SettingController ltr24SettingController;
    private LTR34SettingController ltr34SettingController;
    private LTR212SettingController ltr212SettingController;

    private Parent parent;

    @Override
    public void init() throws IOException {
        DataBaseRepository.init(false);

        crateLoginScene();
        createMainScene();
        createSettingsScene();
        createProcessScene();
        createLTR24Scene();
        createLTR34Scene();
        createLTR212Scene();
    }

    private void crateLoginScene() throws IOException {
        loginController = (LoginController) getController("/layouts/loginView.fxml");
        loginController.setMainApp(this);

        loginScene = createScene(300, 215);
        setKeyListener();
    }

    private BaseController getController(String layoutPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(layoutPath));
        parent = loader.load();
        BaseController baseController = loader.getController();
        baseController.setWindowManager(this);
        baseController.setControllerManager(this);

        return baseController;
    }

    private Scene createScene(int width, int heigh) {
        return new Scene(parent, width, heigh);
    }

    private void setKeyListener() {
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
        mainController = (MainController) getController("/layouts/mainView.fxml");
        mainScene = createScene(1280, 720);
    }

    private void createSettingsScene() throws IOException {
        settingsController = (SettingsController) getController("/layouts/settingsView.fxml");
        settingsScene = createScene(1280, 720);
    }

    private void createProcessScene() throws IOException {
        processController = (ProcessController) getController("/layouts/processView.fxml");
        processScene = createScene(1280, 720);
    }

    private void createLTR24Scene() throws IOException {
        ltr24SettingController = (LTR24SettingController) getController("/layouts/LTR24SettingView.fxml");
        ltr24Scene = createScene(1280, 720);
    }

    private void createLTR34Scene() throws IOException {
        ltr34SettingController = (LTR34SettingController) getController("/layouts/LTR34SettingView.fxml");
        ltr34Scene = createScene(1280, 720);
    }

    private void createLTR212Scene() throws IOException {
        ltr212SettingController = (LTR212SettingController) getController("/layouts/LTR212SettingView.fxml");
        ltr212Scene = createScene(1280, 720);
    }

    @Override
    public void start(Stage loginStage) {
        this.loginStage = loginStage;
        setLoginStageSize();
        setCentreOfStage(this.loginStage);
        this.loginStage.show();
        loginController.showScene();
    }

    private void setLoginStageSize() {
        this.loginStage.setMinWidth(300);
        this.loginStage.setMinHeight(250);
        this.loginStage.setWidth(300);
        this.loginStage.setHeight(250);
        this.loginStage.setMaxWidth(300);
        this.loginStage.setMaxHeight(250);
        this.loginStage.setResizable(false);
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
        setMainStageSize();
        setCentreOfStage(primaryStage);
        primaryStage.show();
        loginStage.close();
    }

    private void setMainStageSize() {
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true);
    }

    @Override
    public void setScene(WindowsManager.Scenes scene) {
        switch (scene) {
            case LOGIN_SCENE:
                loginStage.setTitle("Авторизация");
                loginStage.setScene(loginScene);
                break;
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
            case LTR24_SCENE:
                primaryStage.setTitle("Настройки модуля LTR24");
                primaryStage.setScene(ltr24Scene);
                break;
            case LTR34_SCENE:
                primaryStage.setTitle("Настройки модуля LTR34");
                primaryStage.setScene(ltr34Scene);
                break;
            case LTR212_SCENE:
                primaryStage.setTitle("Настройки модуля LTR212");
                primaryStage.setScene(ltr212Scene);
                break;
            case SIGNAL_GRPAH_SCENE:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void loadItemsForTableView() {
        mainController.showPotocols();
    }
}
