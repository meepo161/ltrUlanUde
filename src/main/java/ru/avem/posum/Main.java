package ru.avem.posum;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import ru.avem.posum.controllers.Calibration.CalibrationController;
import ru.avem.posum.controllers.Settings.LTR212.LTR212Settings;
import ru.avem.posum.controllers.Settings.LTR24.LTR24Settings;
import ru.avem.posum.controllers.Settings.LTR27.LTR27Settings;
import ru.avem.posum.controllers.Settings.LTR34.LTR34Settings;
import ru.avem.posum.controllers.Settings.Settings;
import ru.avem.posum.controllers.Signal.SignalGraph;
import ru.avem.posum.db.DataBaseRepository;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.models.ExperimentModel;
import ru.avem.posum.models.Signal.SignalModel;
import ru.avem.posum.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends Application implements WindowsManager, ControllerManager {
    private volatile boolean closed;
    private LoginController loginController;
    private List<Pair<BaseController, Scene>> modulesPairs = new ArrayList<>();
    private LTR24Settings ltr24Settings;
    private LTR27Settings ltr27Settings;
    private LTR34Settings ltr34Settings;
    private LTR212Settings ltr212Settings;
    private CalibrationController calibrationController;
    private MainController mainController;
    private Parent parent;
    private ProcessController processController;
    private Settings settings;
    private Scene mainScene;
    private Scene loginScene;
    private Scene settingsScene;
    private Scene processScene;
    private Scene ltr24Scene;
    private Scene ltr27Scene;
    private Scene ltr34Scene;
    private Scene ltr212Scene;
    private Scene signalGraphScene;
    private Scene calibrationScene;
    private SignalGraph signalGraph;
    private Stage loginStage;
    private Stage primaryStage;
    private boolean stopped;

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
        settings = (Settings) getController("/layouts/settingsView.fxml");
        settingsScene = createScene(1280, 720);
    }

    private void createProcessScene() throws IOException {
        processController = (ProcessController) getController("/layouts/processView.fxml");
        processScene = createScene(1280, 720);
    }

    private void createLTR24Scene() throws IOException {
        ltr24Settings = (LTR24Settings) getController("/layouts/LTR24SettingView.fxml");
        ltr24Scene = createScene(1280, 720);
    }

    private void createLTR34Scene() throws IOException {
        ltr34Settings = (LTR34Settings) getController("/layouts/LTR34SettingView.fxml");
        ltr34Scene = createScene(1280, 720);
    }

    private void createLTR212Scene() throws IOException {
        ltr212Settings = (LTR212Settings) getController("/layouts/LTR212SettingView.fxml");
        ltr212Scene = createScene(1280, 720);
    }

    private void createSignalGraphScene() throws IOException {
        signalGraph = (SignalGraph) getController("/layouts/signalGraphView.fxml");
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
    public void checkCalibration() {
        Utils.sleep(500);
        signalGraph.checkCalibration();
    }

    @Override
    public void createListModulesControllers(List<String> modulesNames) {
        modulesPairs.clear();
        for (String module : modulesNames) {
            String layoutPath = null;
            switch (Utils.parseModuleType(module)) {
                case Crate.LTR24:
                    layoutPath = "/layouts/LTR24SettingView.fxml";
                    break;
                case Crate.LTR27:
                    layoutPath = "/layouts/LTR27SettingView.fxml";
                    break;
                case Crate.LTR34:
                    layoutPath = "/layouts/LTR34SettingView.fxml";
                    break;
                case Crate.LTR212:
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        closed = true;
        stopAllModules();
        System.out.println("Version_1.0 closed");
    }

    private void stopAllModules() {
        ObservableList<String> modulesNames = settings.getSettingsModel().getModulesNames();
        HashMap<Integer, Module> modules = getCrateModelInstance().getModulesList();

        if (!modules.isEmpty()) {
            for (int index = 0; index < modulesNames.size(); index++) {
                int slot = settings.getSettingsModel().parseSlotNumber(index);
                if (slot != 16) { // TODO: delete this, because LTR27Module is absentee
                    Module module = modules.get(slot);
                    module.checkConnection();
                    module.checkStatus();
                    if (module.getStatus().equals("Операция успешно выполнена")) {
                        module.stop();
                        module.closeConnection();
                    }
                }
            }
        }
    }

    @Override
    public String getCrateSerialNumber() {
        return settings.getHardwareSettings().getCrateSerialNumber();
    }

    @Override
    public Crate getCrateModelInstance() {
        return settings.getHardwareSettings().getCrate();
    }

    @Override
    public int getDecimalFormatScale() {
        return signalGraph.getDecimalFormatScale();
    }

    @Override
    public ExperimentModel getExperimentModel() {
        return processController.getExperimentModel();
    }

    @Override
    public String getValueName() {
        return signalGraph.getSignalModel().getValueName();
    }

    @Override
    public double getZeroShift() {
        return signalGraph.getSignalModel().getZeroShift();
    }

    @Override
    public void giveChannelInfo(int channel, String moduleType, int slot) {
        signalGraph.getSignalModel().setFields(moduleType, slot, channel);
    }

    @Override
    public void hideRequiredFieldsSymbols() {
        settings.hideRequiredFieldsSymbols();
    }

    @Override
    public void initializeSignalGraphView() {
        signalGraph.initializeView();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void loadDefaultCalibrationSettings(SignalModel signalModel) {
        calibrationController.loadDefaultCalibrationSettings(signalModel);
    }

    @Override
    public void loadDefaultSettings() {
        settings.loadDefaultSettings();
    }

    @Override
    public void loadItemsForMainTableView() {
        mainController.getTestPrograms();
        mainController.showTestPrograms();
    }

    @Override
    public void loadItemsForModulesTableView() {
        settings.refreshModulesList();
    }

    @Override
    public void loadModuleSettings(int id, String moduleName) {
        String moduleType = (moduleName + " ").substring(0, 6).trim();

        switch (moduleType) {
            case Crate.LTR24:
                ltr24Settings = (LTR24Settings) modulesPairs.get(id).getKey();
                ltr24Settings.loadSettings(moduleName);
                break;
            case Crate.LTR34:
                ltr34Settings = (LTR34Settings) modulesPairs.get(id).getKey();
                ltr34Settings.loadSettings(moduleName);
                break;
            case Crate.LTR212:
                ltr212Settings = (LTR212Settings) modulesPairs.get(id).getKey();
                ltr212Settings.loadSettings(moduleName);
                break;
        }
    }

    @Override
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public void setEditMode(boolean editMode) {
        settings.setEditMode(editMode);
    }

    @Override
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public void showChannelValue() {
        calibrationController.showChannelValue();
    }

    @Override
    public void showTestProgram(TestProgram testProgram) {
        settings.showTestProgram(testProgram);
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
}
