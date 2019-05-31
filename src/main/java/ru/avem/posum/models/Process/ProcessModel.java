package ru.avem.posum.models.Process;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import ru.avem.posum.controllers.Process.LinkingManager;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;

import java.util.ArrayList;
import java.util.List;

public class ProcessModel {
    private List<Integer> channelsCounts;
    private String crateSerialNumber;
    private List<String> firPath;
    private List<String> iirPath;
    private LinkingManager lm;
    private List<int[]> measuringRanges;
    private List<String> modulesTypes;
    private List<int[]> settingsOfModules;
    private List<Integer> slots;
    private List<int[]> typesOfChannels;

    public void parseSettings() {
        channelsCounts = new ArrayList<>();
        crateSerialNumber = "";
        firPath = new ArrayList<>();
        iirPath = new ArrayList<>();
        measuringRanges = new ArrayList<>();
        modulesTypes = new ArrayList<>();
        settingsOfModules = new ArrayList<>();
        slots = new ArrayList<>();
        typesOfChannels = new ArrayList<>();

        for (Modules module : getModules()) {
            modulesTypes.add(module.getModuleType());
            slots.add(module.getSlot());
            channelsCounts.add(module.getChannelsCount());

            if (!module.getModuleType().equals(Crate.LTR34)) {
                typesOfChannels.add(Modules.getTypesOfChannels(module));
                measuringRanges.add(Modules.getMeasuringRanges(module));
                settingsOfModules.add(Modules.getSettingsOfModule(module));
                firPath.add(module.getFirPath());
                iirPath.add(module.getIirPath());
            } else {
                typesOfChannels.add(new int[8]);
                measuringRanges.add(new int[8]);
                settingsOfModules.add(new int[8]);
                firPath.add("");
                iirPath.add("");
            }
        }

        List<TestProgram> testPrograms = TestProgramRepository.getAllTestPrograms();

        for (TestProgram testProgram : testPrograms) {
            if (getModules().get(0).getTestProgramId() == testProgram.getId()) {
                crateSerialNumber = testProgram.getCrateSerialNumber();
                break;
            }
        }
    }

    public ObservableList<Modules> getModules() {
        List<Modules> linkedModules = lm.getLinkedModules();
        ObservableList<Modules> chosenModules = lm.getChosenModules();

        for (Modules module : linkedModules) {
            if (!chosenModules.contains(module)) {
                chosenModules.add(module);
            }
        }

        return chosenModules;
    }


    public String[] getTypesOfModules() {
        ObservableList<Modules> modules = getModules();
        int SLOTS = 16; // количество слотов в крейте
        String[] typesOfModules = new String[SLOTS];

        for (int typeIndex = 0; typeIndex < typesOfModules.length; typeIndex++) {
            typesOfModules[typeIndex] = "";
        }

        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            typesOfModules[moduleIndex] = modules.get(moduleIndex).getModuleType();
        }

        return typesOfModules;
    }

    public void initListViews() {
        lm.initListViews();
        lm.setGraphModel();
    }

    public void clear() {
        lm.getChosenChannels().clear();
        lm.getLinkedChannels().clear();
        lm.getChosenModules().clear();
    }

    public Alert createExitAlert(ButtonType ok, ButtonType cancel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ok, cancel);
        alert.setTitle("Подтвердите действие");
        alert.setHeaderText("Вернуться в главное окно?");
        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 14px;\n" + "-fx-background-radius: 5px;\n" +
                "\t-fx-border-radius: 5px;"));

        return alert;
    }

    public List<Integer> getChannelsCounts() {
        return channelsCounts;
    }

    public String getCrateSerialNumber() {
        return crateSerialNumber;
    }

    public List<String> getFirPath() {
        return firPath;
    }

    public List<String> getIirPath() {
        return iirPath;
    }

    public List<int[]> getMeasuringRanges() {
        return measuringRanges;
    }

    public List<String> getModulesTypes() {
        return modulesTypes;
    }

    public List<int[]> getSettingsOfModules() {
        return settingsOfModules;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public List<int[]> getTypesOfChannels() {
        return typesOfChannels;
    }

    public void setLm(LinkingManager lm) {
        this.lm = lm;
    }
}
