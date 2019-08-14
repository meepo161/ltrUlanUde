package ru.avem.posum.hardware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.utils.TextEncoder;

import java.util.HashMap;
import java.util.Optional;

/**
 * Класс крейта
 */

public class Crate {
    public static final String LTR24 = "LTR24";
    public static final String LTR27 = "LTR27";
    public static final String LTR34 = "LTR34";
    public static final String LTR212 = "LTR212";
    private static final int LTR_CRATES_MAX = 16;
    private static final int LTR_MODULES_PER_CRATE_MAX = 16;

    private String[][] crates = new String[LTR_CRATES_MAX][LTR_CRATES_MAX]; // массив хранит серийные номера, имена и интерфейс подключения крейтов
    private Optional<ObservableList<String>> cratesNames; // названия крейтов
    private String[][] modules = new String[LTR_CRATES_MAX][LTR_MODULES_PER_CRATE_MAX]; // информация о модулях крейта
    private HashMap<Integer, Module> modulesList = new HashMap<>(); // список модулей
    private String status; // результат выполнения команды
    private TextEncoder textEncoder = new TextEncoder(); // расшифровывает текст, полученный от крейта
    private boolean wasError; // значение поля устанавливается из библиотеки dll, не удалять!

    public Crate() {
        initCratesList();
    }

    public void initCratesList() {
        initEmptyCratesList();
        fillCratesList(crates[0]);
        setCratesInfo();
        setModulesList();
        closeConnection();
        fillCratesNames();
    }

    private void initEmptyCratesList() {
        for (int i = 0; i < crates.length; i++) {
            for (int j = 0; j < crates[i].length; j++) {
                crates[i][j] = "";
            }
        }
    }

    // Проверяет наличие ошибки
    private void checkStatus() {
        if (wasError) {
            status = textEncoder.cp2utf(status);
        }
    }

    // Получает данные о крейте и модулях
    private void setCratesInfo() {
        if (!wasError) {
            status = getCratesInfo(crates[1], crates[2]);
            checkStatus();
        }
    }

    // Заполняет список модулей
    private void setModulesList() {
        initEmptyModulesList();

        for (int i = 0; i < crates[0].length; i++) {
            if (!crates[0][i].isEmpty()) {
                status = fillModulesList(crates[0][i], modules[i]);
                checkStatus();
            }
        }
    }

    private void initEmptyModulesList() {
        for (int i = 0; i < modules.length; i++) {
            for (int j = 0; j < modules.length; j++) {
                modules[i][j] = "";
            }
        }
    }

    // Закрывает соединение с крейтом
    private void closeConnection() {
        status = close();
        checkStatus();
    }

    public native String close();

    // Заполняет список крейтов
    private void fillCratesNames() {
        cratesNames = Optional.of(FXCollections.observableArrayList());

        for (int i = 0; i < crates[1].length; i++) {
            if (!crates[1][i].isEmpty()) {
                cratesNames.get().add(crates[1][i] + " (" + crates[0][i] + "), " + crates[2][i]);
            }
        }
    }

    public ObservableList<String> getModulesNames(int crate) {
        ObservableList<String> modulesNames = FXCollections.observableArrayList();
        String[] names = modules[crate];

        for (int i = 0; i < names.length; i++) {
            if (!names[i].isEmpty()) {
                modulesNames.addAll(names[i] + " (Слот " + (i + 1) + ")");
            }
        }

        return modulesNames;
    }

    public static String[][] getLTR27Info() {
        LTR27 ltr27 = new LTR27();
        return ltr27.getInfo();
    }

    public native String initialize(String crateSN);

    public native String fillCratesList(String[] crates);

    public native String getCratesInfo(String[] names, String[] connectionInterfaces);

    public native String fillModulesList(String crate, String[] modules);

    public HashMap<Integer, Module> getModulesList() {
        return modulesList;
    }

    public String[][] getCrates() {
        return crates;
    }

    public Optional<ObservableList<String>> getCratesNames() {
        return cratesNames;
    }

    static {
        System.loadLibrary("CrateLibrary");
    }
}
