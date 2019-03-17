package ru.avem.posum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import ru.avem.posum.controllers.*;
import ru.avem.posum.db.DataBaseRepository;
import ru.avem.posum.db.models.Calibration;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.models.ExperimentModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application implements WindowsManager, ControllerManager {
    private LoginController loginController;
    private List<Pair<BaseController, Scene>> modulesPairs = new ArrayList<>();
    private LTR24SettingController ltr24SettingController;
    private LTR34SettingController ltr34SettingController;
    private LTR212SettingController ltr212SettingController;
    private CalibrationController calibrationController;
    private MainController mainController;
    private Parent parent;
    private ProcessController processController;
    private SettingsController settingsController;
    private Scene mainScene;
    private Scene loginScene;
    private Scene settingsScene;
    private Scene processScene;
    private Scene ltr24Scene;
    private Scene ltr34Scene;
    private Scene ltr212Scene;
    private Scene signalGraphScene;
    private Scene calibrationScene;
    private SignalGraphController signalGraphController;
    private Stage loginStage;
    private Stage primaryStage;
    private volatile boolean closed;

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
        createSignalGraphScene();
        createCalibrationScene();
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

    private Scene createScene(int width, int height) {
        return new Scene(parent, width, height);
    }

    private void setKeyListener() {
        loginScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!(event.getTarget() instanceof TextField)) {
                    Platform.exit();
                }
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

    private void createSignalGraphScene() throws IOException {
        signalGraphController = (SignalGraphController) getController("/layouts/signalGraphView.fxml");
        signalGraphScene = createScene(1280, 720);
    }

    private void createCalibrationScene() throws IOException {
        calibrationController = (CalibrationController) getController("/layouts/calibrationView.fxml");
        calibrationScene = createScene(1280, 720);
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
        initPrimaryStage();
        setCentreOfStage(primaryStage);
        showMainScene();
    }

    private void initPrimaryStage() {
        primaryStage = new Stage();
        primaryStage.setTitle("ПО Система управления многоканальная");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true);
    }

    private void showMainScene() {
        primaryStage.show();
        loginStage.close();
        mainController.getOpenExperimentButton().requestFocus();
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
            case EXPERIMENT_SCENE:
                primaryStage.setTitle("Процесс испытаний");
                primaryStage.setScene(processScene);
                break;
            case LTR24_SCENE:
                primaryStage.setTitle("Настройки модуля LTR24Table");
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
            case SIGNAL_GRAPH_SCENE:
                primaryStage.setTitle("Настройки канала");
                primaryStage.setScene(signalGraphScene);
                break;
            case CALIBRATION_SCENE:
                primaryStage.setTitle("Градуировка канала");
                primaryStage.setScene(calibrationScene);
                break;
        }
    }

    @Override
    public void setModuleScene(String moduleName, int id) {
        primaryStage.setTitle("Настройки модуля " + moduleName);
        primaryStage.setScene(modulesPairs.get(id).getValue());
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void loadItemsForMainTableView() {
        mainController.getTestPrograms();
        mainController.showTestPrograms();
    }

    @Override
    public void loadItemsForModulesTableView() {
        settingsController.refreshModulesList();
    }

    @Override
    public void loadDefaultSettings() {
        settingsController.loadDefaultSettings();
    }

    @Override
    public void toggleSettingsSceneButtons(boolean isDisable) {
        settingsController.toggleButtons(isDisable);
    }

    @Override
    public void loadModuleSettings(int id, String moduleName) {
        String moduleType = (moduleName + " ").substring(0, 6).trim();

        switch (moduleType) {
            case CrateModel.LTR24:
                ltr24SettingController = (LTR24SettingController) modulesPairs.get(id).getKey();
                ltr24SettingController.loadSettings(moduleName);
                break;
            case CrateModel.LTR34:
                ltr34SettingController = (LTR34SettingController) modulesPairs.get(id).getKey();
                ltr34SettingController.loadSettings(moduleName);
                break;
            case CrateModel.LTR212:
                ltr212SettingController = (LTR212SettingController) modulesPairs.get(id).getKey();
                ltr212SettingController.loadSettings(moduleName);
                break;
        }
    }

    @Override
    public boolean getICPMode() {
        return ltr24SettingController.isIcpMode();
    }

    @Override
    public void checkCalibration() {
        signalGraphController.checkCalibration();
    }

    @Override
    public void createListModulesControllers(List<String> modulesNames) {
        modulesPairs.clear();
        for (String module : modulesNames) {
            String layoutPath = null;
            switch (module.split(" ")[0]) {
                case CrateModel.LTR24:
                    layoutPath = "/layouts/LTR24SettingView.fxml";
                    break;
                case CrateModel.LTR34:
                    layoutPath = "/layouts/LTR34SettingView.fxml";
                    break;
                case CrateModel.LTR212:
                    layoutPath = "/layouts/LTR212SettingView.fxml";
                    break;
            }
            try {
                Pair<BaseController, Scene> pair = loadScene(layoutPath);
                modulesPairs.add(pair);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Pair<BaseController, Scene> loadScene(String layoutPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(layoutPath));
        Parent parent = loader.load();
        BaseController baseController = loader.getController();
        baseController.setWindowManager(this);
        baseController.setControllerManager(this);

        return new Pair<>(baseController, new Scene(parent, 1280, 720));
    }

    @Override
    public void showChannelData(String moduleType, int slot, int channel) {
        signalGraphController.initializeView(moduleType, slot, channel);
    }

    @Override
    public String getCrate() {
        return settingsController.getCrate();
    }

    @Override
    public CrateModel getCrateModelInstance() {
        return settingsController.getCrateModel();
    }

    @Override
    public ExperimentModel getExperimentModel() {
        return processController.getExperimentModel();
    }

    @Override
    public double getZeroShift() {
        return signalGraphController.getZeroShift();
    }

    @Override
    public void showChannelValue() {
        calibrationController.showChannelValue();
    }

    @Override
    public void showTestProgram(TestProgram testProgram) {
        settingsController.showTestProgram(testProgram);
    }

    @Override
    public void setEditMode(boolean editMode) {
        settingsController.setEditMode(editMode);
    }

    @Override
    public void hideRequiredFieldsSymbols() {
        settingsController.hideRequiredFieldsSymbols();
    }

    @Override
    public void loadDefaultCalibrationSettings(ADC adc, String moduleType, int channel) {
        calibrationController.loadDefaults(adc, moduleType, channel);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setClosed(boolean cl) {
        this.closed = cl;
    }

    @Override
    public void stop() {
        closed = true;
    }
}
