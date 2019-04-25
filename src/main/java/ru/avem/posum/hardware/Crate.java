package ru.avem.posum.hardware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.utils.TextEncoder;

import java.util.HashMap;

public class Crate {
    public static final String LTR24 = "LTR24";
    public static final String LTR27 = "LTR27";
    public static final String LTR34 = "LTR34";
    public static final String LTR212 = "LTR212";
    private static final int LTR_CRATES_MAX = 16;
    private static final int LTR_MODULES_PER_CRATE_MAX = 16;

    private String[][] crates = new String[LTR_CRATES_MAX][LTR_CRATES_MAX]; // массив хранит серийные номера, имена и интерфейс подключения крейтов
    private ObservableList<String> cratesNames;
    private String[][] modules = new String[LTR_CRATES_MAX][LTR_MODULES_PER_CRATE_MAX];
    private HashMap<Integer, Module> modulesList = new HashMap<>();
    private String status;
    private TextEncoder textEncoder = new TextEncoder();
    private boolean wasError; // значение поля устанавливается из библиотеки dll, не удалять!

    public Crate() {
        initEmptyCratesList();
        setCratesList();
        setCratesInfo();
        initEmptyModulesList();
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

    private void setCratesList() {
        status = fillCratesList(crates[0]);
        checkStatus();
    }

    public native String fillCratesList(String[] crates);

    private void checkStatus() {
        if (wasError) {
            status = textEncoder.cp2utf(status);
        }
    }

    private void setCratesInfo() {
        if (!wasError) {
            status = getCratesInfo(crates[1], crates[2]);
            checkStatus();
        }
    }

    public native String getCratesInfo(String[] names, String[] connectionInterfaces);

    private void initEmptyModulesList() {
        for (int i = 0; i < modules.length; i++) {
            for (int j = 0; j < modules.length; j++) {
                modules[i][j] = "";
            }

        }
    }

    private void setModulesList() {
        for (int i = 0; i < crates[0].length; i++) {
            if (!crates[0][i].isEmpty()) {
                status = fillModulesList(crates[0][i], modules[i]);
                checkStatus();
            }
        }
    }

    public native String fillModulesList(String crate, String[] modules);

    private void closeConnection() {
        status = close();
        checkStatus();
    }

    public native String close();

    private void fillCratesNames() {
        cratesNames = FXCollections.observableArrayList();

        for (int i = 0; i < crates[1].length; i++) {
            if (!crates[1][i].isEmpty()) {
                cratesNames.add(crates[1][i] + " (" + crates[0][i] + "), " + crates[2][i]);
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

    public native String initialize(String crateSN);

    public HashMap<Integer, Module> getModulesList() {
        return modulesList;
    }

    public String[][] getCrates() {
        return crates;
    }

    public ObservableList<String> getCratesNames() {
        return cratesNames;
    }

    static {
        System.loadLibrary("CrateLibrary");
    }
}
