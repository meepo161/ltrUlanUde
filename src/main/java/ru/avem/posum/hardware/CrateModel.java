package ru.avem.posum.hardware;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.utils.TextEncoder;

import java.util.ArrayList;
import java.util.List;

public class CrateModel {
    public enum Moudules {
        LTR24, LTR34, LTR212
    }

    public static final String LTR24 = "LTR24";
    public static final String LTR34 = "LTR34";
    public static final String LTR212 = "LTR212";

    private final int LTR_CRATES_MAX = 16;
    private final int LTR_MODULES_PER_CRATE_MAX = 16;
    private String[][] crates = new String[LTR_CRATES_MAX][LTR_CRATES_MAX]; // массив хранит серийные номера, имена и интерфейс подключения крейтов
    private String[][] modules = new String[LTR_CRATES_MAX][LTR_MODULES_PER_CRATE_MAX];
    private boolean wasError;
    private String status;
    private String error;
    private TextEncoder textEncoder = new TextEncoder();
    private List<LTR24> ltr24ModulesList = new ArrayList<>();
    private List<LTR34> ltr34modules = new ArrayList<>();
    private List<LTR212> ltr212modules = new ArrayList<>();

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

    private void checkStatus() {
        if (wasError) {
            status = textEncoder.cp2utf(status);
            error = status;
            Platform.runLater(() -> {
//                baseController.setMainStatusBarText(error);
            });
        }
    }

    public ObservableList<String> getCratesNames() {
        ObservableList<String> cratesNames = FXCollections.observableArrayList();

        for (int i = 0; i < crates[1].length; i++) {
            if (!crates[1][i].isEmpty()) {
                cratesNames.add(crates[1][i] + " (" + crates[0][i] + "), " + crates[2][i]);
            }
        }

        return cratesNames;
    }

    public ObservableList<String> getModulesNames(int crate) {
        ObservableList<String> modulesNames = FXCollections.observableArrayList();

        for (String name : modules[crate]) {
            if (!name.isEmpty()) {
                modulesNames.addAll(name);
            }
        }

        return modulesNames;
    }

    public String[][] getCrates() {
        return crates;
    }

    public String[][] getModules() {
        return modules;
    }

    public List<LTR24> getLtr24ModulesList() {
        return ltr24ModulesList;
    }

    public List<LTR34> getLtr34ModulesList() {
        return ltr34modules;
    }

    public List<LTR212> getLtr212ModulesList() {
        return ltr212modules;
    }

    static {
        System.loadLibrary("CrateLibrary");
    }
}
