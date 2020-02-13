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
import ru.avem.posum.communication.CommunicationModel;
import ru.avem.posum.communication.ModbusConnection;
import ru.avem.posum.controllers.*;
import ru.avem.posum.controllers.calibration.CalibrationController;
import ru.avem.posum.controllers.calibration.LTR27CalibrationController;
import ru.avem.posum.controllers.process.LinkingController;
import ru.avem.posum.controllers.process.ProcessController;
import ru.avem.posum.controllers.settings.HardwareSettings;
import ru.avem.posum.controllers.settings.LTR212.LTR212Settings;
import ru.avem.posum.controllers.settings.LTR24.LTR24Settings;
import ru.avem.posum.controllers.settings.LTR27.LTR27SettingsController;
import ru.avem.posum.controllers.settings.LTR34.LTR34Settings;
import ru.avem.posum.controllers.settings.Settings;
import ru.avem.posum.controllers.signal.SignalController;
import ru.avem.posum.db.DataBaseRepository;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.models.signal.SignalModel;
import ru.avem.posum.utils.ExtView;
import ru.avem.posum.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tornadofx.*;

import static ru.avem.posum.controllers.process.RegulatorController.isError;

public class Main extends Application implements WindowsManager, ControllerManager {
    private CalibrationController calibrationController;
    private LTR27CalibrationController ltr27CalibrationController;
    private Scene calibrationScene;
    private Scene ltr27CalibrationScene;
    private volatile boolean closed;
    private LoginController loginController;
    private LinkingController linkingController;
    private Scene linkingScene;
    private Scene loginScene;
    private Stage loginStage;
    private LTR24Settings ltr24Settings;
    private Scene ltr24Scene;
    private LTR27SettingsController ltr27SettingsController;
    private Scene ltr27Scene;
    private LTR34Settings ltr34Settings;
    private Scene ltr34Scene;
    private LTR212Settings ltr212Settings;
    private Scene ltr212Scene;
    private Scene mainScene;
    private MainController mainController;
    private List<Pair<BaseController, Scene>> modulesPairs = new ArrayList<>();
    private Parent parent;
    private Stage primaryStage;
    private ProcessController processController;
    private Scene processScene;
    private Settings settings;
    private Scene settingsScene;
    private SignalController signalController;
    private Scene signalGraphScene;
    private boolean stopped;


    // Инициализация окон
    @Override
    public void init() throws IOException {


        DataBaseRepository.init(false);

        new Thread(() -> {
            while (!isClosed()) {
                CommunicationModel.INSTANCE.getMU110Controller().onKM3();
            }
            System.exit(0);
        }).start();

        crateLoginScene();
        createMainScene();
        createSettingsScene();
        createLinkingScene();
        createProcessScene();
        createSignalGraphScene();
        createCalibrationScene();
        createLtr27CalibrationScene();
        initializeSingletons();
    }

    private void initializeSingletons() {
        ModbusConnection svetlana = ModbusConnection.INSTANCE;
        CommunicationModel serega = CommunicationModel.INSTANCE;
    }

    // Создание внешнего вида окна авторизации
    private void crateLoginScene() throws IOException {

        loginController = (LoginController) getController("/layouts/loginView.fxml");
        loginController.setMainApp(this);

        loginScene = createScene(300, 215);
        setKeyListener();
    }

    // Загружает внешний вид, возвращает контроллер
    private BaseController getController(String layoutPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(layoutPath));
        parent = loader.load();
        BaseController baseController = loader.getController();
        baseController.setWindowManager(this);
        baseController.setControllerManager(this);

        return baseController;
    }

    // Создание внешнего вида окна
    private Scene createScene(int width, int height) {
        return new Scene(parent, width, height);
    }

    // Закрывает приложение, если пользователь нажал Esc в окне авторизации
    private void setKeyListener() {
        loginScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!(event.getTarget() instanceof TextField)) {
                    Platform.exit();
                }
            }
        });
    }

    // Создание внешнего вида главного окна
    private void createMainScene() throws IOException {
        mainController = (MainController) getController("/layouts/mainView.fxml");
        mainScene = createScene(1280, 720);

    }

    // Создание внешнего виде окна настроек
    private void createSettingsScene() throws IOException {
        settings = (Settings) getController("/layouts/settingsView.fxml");
        settingsScene = createScene(1280, 720);
    }

    // Создание внешнего виде окна добавления и связывания каналов
    private void createLinkingScene() throws IOException {
        linkingController = (LinkingController) getController("/layouts/linkingView.fxml");
        linkingScene = createScene(1280, 720);
    }

    // Создание внешнего вида окна процесса испытаний
    private void createProcessScene() throws IOException {
        processController = (ProcessController) getController("/layouts/processView.fxml");
        processScene = createScene(1280, 720);


    }

    // Создание внешнего вида окна текущей нагрузки на каналах
    private void createSignalGraphScene() throws IOException {
        signalController = (SignalController) getController("/layouts/signalGraphView.fxml");
        signalGraphScene = createScene(1280, 720);
    }

    // Создание внешнего вида окна градуировки канала
    private void createCalibrationScene() throws IOException {
        calibrationController = (CalibrationController) getController("/layouts/calibrationView.fxml");
        calibrationScene = createScene(1280, 720);
    }

    // Создание внешнего вида окна градуировки каналов модуля LTR27
    private void createLtr27CalibrationScene() throws IOException {
        ltr27CalibrationController = (LTR27CalibrationController) getController("/layouts/ltr27CalibrationView.fxml");
        ltr27CalibrationScene = createScene(1280, 720);
    }

    @Override
    public void start(Stage loginStage) {
        this.loginStage = loginStage;
        setLoginStageSize();
        setCentreOfStage(this.loginStage);
        this.loginStage.show();
        loginController.showScene();
    }

    // Задает размеры окна
    private void setLoginStageSize() {
        this.loginStage.setMinWidth(300);
        this.loginStage.setMinHeight(230);
        this.loginStage.setWidth(300);
        this.loginStage.setHeight(230);
        this.loginStage.setMaxWidth(300);
        this.loginStage.setMaxHeight(230);
        this.loginStage.setResizable(false);
    }

    // Располагает окно по центру экрана
    private void setCentreOfStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    // Отображает главное окно программы
    public void setMainView() {
        initPrimaryStage();
        setCentreOfStage(primaryStage);
        showMainScene();
        // FX.registerApplication(this, primaryStage);
    }

    // Инициализирует главное окно программы
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

    // Отображает главное окно программы
    private void showMainScene() {
        primaryStage.show();
        loginStage.close();
        mainController.getOpenExperimentButton().requestFocus();

    }

    // Проверяет наличие калибровочных коэффициентов
    @Override
    public void checkCalibration() {
        signalController.checkCalibration();

        new Thread(() -> {
            Utils.sleep(2000); // пауза для отрисовки ненулевого сигнала
            signalController.getGraphController().restartOfShow();
        }).start();
    }

    // Создает окна и контроллеры настроек модулей
    @Override
    public void createListModulesControllers(List<String> modulesNames) {
        modulesPairs.clear();
        for (String module : modulesNames) {
            String layoutPath = null;
            switch (Utils.parseModuleType(module)) {
                case Crate.LTR24:
                    layoutPath = "/layouts/ltr24SettingView.fxml";
                    break;
                case Crate.LTR27:
                    layoutPath = "/layouts/ltr27SettingView.fxml";
                    break;
                case Crate.LTR34:
                    layoutPath = "/layouts/ltr34SettingView.fxml";
                    break;
                case Crate.LTR212:
                    layoutPath = "/layouts/ltr212SettingView.fxml";
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

    // Загружает внешний вид окна
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
        System.out.println("Version_1.1 closed");
    }

    // Останавливает все модули и закрывает соединение с ними
    @Override
    public void stopAllModules() {
        ObservableList<String> modulesNames = settings.getSettingsModel().getModulesNames();
        HashMap<Integer, Module> modules = getCrateModelInstance().getModulesList();

        if (!modules.isEmpty()) {
            for (int index = 0; index < modulesNames.size(); index++) {
                int slot = settings.getSettingsModel().parseSlotNumber(index);
                Module module = modules.get(slot);

                if (module.isConnectionOpen()) {
                    module.closeConnection();
                }
            }
        }
    }

    // Возвращает серийный номер крейта
    @Override
    public String getCrateSerialNumber() {
        return settings.getHardwareSettings().getCrateSerialNumber();
    }

    // Возвращает объект крейта
    @Override
    public Crate getCrateModelInstance() {
        return settings.getHardwareSettings().getCrate();
    }

    // Возвращает значение постоянной составляющей сигнала
    @Override
    public double getDc() {
        return signalController.getSignalModel().getDc();
    }

    // Возвращает количество знаков после запятой
    @Override
    public int getDecimalFormatScale() {
        return signalController.getDecimalFormatScale();
    }

    // Возвращает контроллер окна добавления и связывания каналов
    @Override
    public LinkingController getLinkingController() {
        return linkingController;
    }

    // Возвращает путь к файлу стилей
    @Override
    public String getStyleSheet() {
        return settingsScene.getStylesheets().get(0);
    }

    // Передает номер канала, тип модуля и слот, в котором он расположен в модель
    @Override
    public void giveChannelInfo(int channel, String moduleType, int slot) {
        signalController.getSignalModel().setFields(moduleType, slot, channel);
    }

    // Скрывает символы перед обязательными полями общей информации
    @Override
    public void hideRequiredFieldsSymbols() {
        settings.hideRequiredFieldsSymbols();
    }

    // Инициализирует окно текущей нагрузки на канале
    @Override
    public void initializeSignalGraphView() {
        signalController.initializeView();
    }

    // Возвращает состояние приложения
    @Override
    public boolean isClosed() {
        return closed;
    }

    // Возвращает состояние процесса измерений
    @Override
    public boolean isStopped() {
        return stopped;
    }

    // Устанавливает начальное состояние интерфейса окна градуировки канала
    @Override
    public void loadDefaultCalibrationSettings(SignalModel signalModel) {
        signalController.getGraphController().setShowFinished(true);
        calibrationController.loadDefaultCalibrationSettings(signalModel);
    }

    // Устанавливае начальное состояние интерфейса окна настрокий программы испытаний
    @Override
    public void loadDefaultSettings() {
        settings.loadDefaultSettings();
    }

    // Загружает список программ испытаний
    @Override
    public void loadItemsForMainTableView() {
        mainController.getTestPrograms();
        mainController.showTestPrograms();
    }

    // Загружает список модулей в окне настройки программы испытаний
    @Override
    public void loadItemsForModulesTableView() {
        settings.refreshModulesList();
    }

    // Загружает настройки модуля
    @Override
    public void loadModuleSettings(int id, String moduleName) {
        String moduleType = (moduleName + " ").substring(0, 6).trim();

        switch (moduleType) {
            case Crate.LTR24:
                ltr24Settings = (LTR24Settings) modulesPairs.get(id).getKey();
                ltr24Settings.loadSettings(moduleName);
                break;
            case Crate.LTR27:
                ltr27SettingsController = (LTR27SettingsController) modulesPairs.get(id).getKey();
                ltr27CalibrationController.setManagers();
                ltr27SettingsController.loadSettings(moduleName);
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

    // Выбирает вкладку общих данных в окне настроек программы испытаний 
    @Override
    public void selectGeneralSettingsTab() {
        settings.selectGeneralSettingsTab();
    }

    // Выдает права доступа администратора
    @Override
    public void setAdministration(boolean administration) {
        mainController.setAdministration(administration);
        mainController.initMenu();
    }

    // Задает состояние приложения
    @Override
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    // Включает режим редактирования в окне настроек программы испытаний
    @Override
    public void setEditMode(boolean editMode) {
        settings.setEditMode(editMode);
    }

    // Передает модель программы испытаний в контроллер окна процесса испытаний
    @Override
    public void setTestProgram() {
        TestProgram testProgram = mainController.getSelectedTestProgram();
        processController.setTestProgram(testProgram);
    }

    // Задает состояние процесса измерений
    @Override
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    // Отображает значение нагрузки на канале в окне градуировки
    @Override
    public void showChannelValue() {
        calibrationController.showChannelValue();
    }

    // Передает модель программы испытаний в окно настроек программы испытаний
    @Override
    public void showTestProgram(TestProgram testProgram) {
        settings.showTestProgram(testProgram);
    }

    // Задает внешний вид окна
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
            case LINKING_SCENE:
                primaryStage.setTitle("Добавление каналов ЦАП и АЦП");
                primaryStage.setScene(linkingScene);
                break;
            case LTR24_SCENE:
                primaryStage.setTitle("Настройки модуля LTR24");
                primaryStage.setScene(ltr24Scene);
                break;
            case LTR27_SCENE:
                primaryStage.setTitle("Настройки модуля LTR27");
                primaryStage.setScene(ltr27Scene);
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
            case LTR27_CALIBRATION_SCENE:
                primaryStage.setTitle("Градуировка каналов");
                primaryStage.setScene(ltr27CalibrationScene);
                break;
        }
    }

    // Задает внешний вид окна натроек модуля
    @Override
    public void setModuleScene(String moduleName, int id) {
        primaryStage.setTitle("Настройки модуля " + moduleName);
        primaryStage.setScene(modulesPairs.get(id).getValue());
    }

    // Возвращает контроллер окна настроек программы испытаний
    @Override
    public BaseController getSettingsController() {
        return modulesPairs.get(settings.getHardwareSettings().getSelectedModuleIndex()).getKey();
    }

    // Возвращает модель настроек крейта
    @Override
    public HardwareSettings getHardwareSettings() {
        return settings.getHardwareSettings();
    }
}
