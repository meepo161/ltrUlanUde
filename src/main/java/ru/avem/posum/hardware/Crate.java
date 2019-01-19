package ru.avem.st49.hardware;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.controllers.IMainController;
import ru.avem.posum.utils.TextEncoder;

public class Crate {
    private final int LTR_CRATES_MAX = 16;
    private final int LTR_MODULES_PER_CRATE_MAX = 16;
    private String[] crates = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private String[] names = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private String[] connectionInterfaces = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private String[][] modules = new String[LTR_CRATES_MAX][LTR_MODULES_PER_CRATE_MAX];
    private boolean wasError;
    private String status;
    private String error;
    private TextEncoder textEncoder = new TextEncoder();
    private IMainController iMainController;

    public Crate(IMainController iMainController) {
        this.iMainController = iMainController;

        status = fillCratesList(crates);
        checkStatus();

        if (!wasError) {
            status = getCratesInfo(names, connectionInterfaces);
            checkStatus();
        }

        if (!wasError) {
            initModulesList();

            for (int i = 0; i < crates.length; i++) {
                if (!crates[i].isEmpty()) {
                    status = fillListOfModules(crates[i], modules[i]);
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
                iMainController.setMainStatusBarText(error);
            });
        }
    }

    public ObservableList<String> getCratesNames() {
        ObservableList<String> cratesNames = FXCollections.observableArrayList();

        for (int i = 0; i < crates.length; i++) {
            if (!names[i].isEmpty()) {
                cratesNames.add(names[i] + " (" + crates[i] + "), " + connectionInterfaces[i]);
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

    public String[][] getModules() {
        return modules;
    }

    static {
        System.loadLibrary("CrateLibrary");
    }
}
