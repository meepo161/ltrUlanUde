package ru.avem.posum.hardware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.utils.TextEncoder;

import java.util.ArrayList;
import java.util.List;

public class CrateModel {
    public enum Moudules {
        LTR24, LTR212
    }

    public static final String LTR24 = "LTR24";
    public static final String LTR34 = "LTR34";
    public static final String LTR212 = "LTR212";

    private final int LTR_CRATES_MAX = 16;
    private final int LTR_MODULES_PER_CRATE_MAX = 16;
    private String[][] crates = new String[LTR_CRATES_MAX][LTR_CRATES_MAX]; // массив хранит серийные номера, имена и интерфейс подключения крейтов
    private String[][] modules = new String[LTR_CRATES_MAX][LTR_MODULES_PER_CRATE_MAX];
    private String status;
    private TextEncoder textEncoder = new TextEncoder();
    private List<Pair<Integer, LTR24>> ltr24ModulesList = new ArrayList<>();
    private List<Pair<Integer, LTR34>> ltr34ModulesList = new ArrayList<>();
    private List<Pair<Integer, LTR212>> ltr212ModulesList = new ArrayList<>();
    private ObservableList<String> modulesNames = FXCollections.observableArrayList();
    private boolean wasError; // значение поля устанавливается из библиотеки dll, не удалять!

    public CrateModel() {
        initCratesList();

        status = fillCratesList(crates[0]);
        checkStatus();

        if (!wasError) {
            status = getCratesInfo(crates[1], crates[2]);
            checkStatus();
        }

        if (!wasError) {
            initModulesList();

            for (int i = 0; i < crates[0].length; i++) {
                if (!crates[0][i].isEmpty()) {
                    status = fillListOfModules(crates[0][i], modules[i]);
                    checkStatus();
                }
            }
        }

        if (!wasError) {
            status = stop();
            checkStatus();
        }

        getCratesNames();
    }

    private void initCratesList() {
        for (int i = 0; i < crates.length; i++) {
            for (int j = 0; j < crates[i].length; j++) {
                crates[i][j] = "";
            }
        }
    }

    public native String fillCratesList(String[] crates);

    private void checkStatus() {
        if (wasError) {
            status = textEncoder.cp2utf(status);
        }
    }

    public native String getCratesInfo(String[] names, String[] connectionInterfaces);

    private void initModulesList() {
        for (int i = 0; i < modules.length; i++) {
            for (int j = 0; j < modules.length; j++) {
                modules[i][j] = "";
            }

        }
    }

    public native String fillListOfModules(String crate, String[] modules);

    public native String stop();

    public ObservableList<String> getCratesNames() {
        ObservableList<String> cratesNames = FXCollections.observableArrayList();

        for (int i = 0; i < crates[1].length; i++) {
            if (!crates[1][i].isEmpty()) {
                cratesNames.add(crates[1][i] + " (" + crates[0][i] + "), " + crates[2][i]);
            }
        }

        return cratesNames;
    }

    public ObservableList<String> fillModulesNames(int crate) {
        modulesNames = FXCollections.observableArrayList();
        String[] names = modules[crate];

        for (int i = 0; i < names.length; i++) {
            if (!names[i].isEmpty()) {
                modulesNames.addAll(names[i] + " (Слот " + (i + 1) + ")");
            }
        }

        return modulesNames;
    }

    public ObservableList<String> getModulesNames() {
        return modulesNames;
    }

    public String[][] getCrates() {
        return crates;
    }

    public List<Pair<Integer, LTR24>> getLtr24ModulesList() {
        return ltr24ModulesList;
    }

    public List<Pair<Integer, LTR34>> getLtr34ModulesList() {
        return ltr34ModulesList;
    }

    public List<Pair<Integer, LTR212>> getLtr212ModulesList() {
        return ltr212ModulesList;
    }

    static {
        System.loadLibrary("CrateLibrary");
    }
}
