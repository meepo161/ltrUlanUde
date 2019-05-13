package ru.avem.posum.controllers.Settings;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import ru.avem.posum.hardware.Crate;

public class HardwareSettings extends Settings {
    private Button backButton;
    private final Button backButtonGeneralTab;
    private Button chooseCrateButton;
    private Crate crate = new Crate();
    private ListView<String> cratesListView;
    private String crateSerialNumber;
    private ObservableList<String> crates;
    private String moduleName;
    private ObservableList<String> modulesNames;
    private ListView<String> modulesListView;
    private Button saveSettingsButton;
    private int selectedCrate;
    private int selectedModuleIndex;
    private Button setupModuleButton;
    private Settings settings;

    public HardwareSettings(Settings settings) {
        this.backButton = settings.getBackButton();
        this.backButtonGeneralTab = settings.getBackButtonGeneralTab();
        this.chooseCrateButton = settings.getChooseCrateButton();
        this.cratesListView = settings.getCratesListView();
        this.modulesListView = settings.getModulesListView();
        this.saveSettingsButton = settings.getSaveSettingsButton();
        this.settings = settings;
        this.setupModuleButton = settings.getSetupModuleButton();
    }

    public void showCrates() {
        crate.initCratesList();

        if (crate.getCratesNames().isPresent()) {
            if (crate.getCratesNames().get().isEmpty()) {
                settings.getStatusBarLine().setStatus("Не найдены подключенные крейты", false);
            } else {
                crates = crate.getCratesNames().get();
                cratesListView.setItems(crates);
            }
        }
    }

    public void showModules() {
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            if (selectedCrate != -1) {
                crateSerialNumber = crate.getCrates()[0][selectedCrate];
                modulesNames = crate.getModulesNames(selectedCrate);
                modulesListView.setItems(modulesNames);
            }
        }));
    }

    public void clear() {
        cratesListView.getSelectionModel().clearSelection();
        cratesListView.getItems().clear();
        modulesListView.getSelectionModel().clearSelection();
        modulesListView.getItems().clear();
    }

    public void initialize() {
        for (int i = 0; i < getCrates().size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                initSettingsModel();
                crate.initialize(crateSerialNumber);
                saveSettingsButton.setDisable(false);
                backButton.setDisable(false);
                backButtonGeneralTab.setDisable(false);
                toggleUiElements(true);
            } else {
                settings.getStatusBarLine().setStatus("Крейт не выбран", false);
            }
        }
    }

    private void initSettingsModel() {
        settings.getSettingsModel().setControllerManager(settings.getCm());
        settings.getSettingsModel().createModulesInstances(modulesNames);
    }

    public void toggleUiElements(boolean isDisable) {
        cratesListView.setDisable(isDisable);
        chooseCrateButton.setDisable(isDisable);
        modulesListView.setDisable(!isDisable);
        setupModuleButton.setDisable(!isDisable);
    }

    public void loadModuleSettings() {
        selectedModuleIndex = modulesListView.getSelectionModel().getSelectedIndex();
        moduleName = modulesNames.get(selectedModuleIndex);
        settings.getCm().loadModuleSettings(selectedModuleIndex, moduleName);
    }

    public boolean checkHardwareSettings() {
        boolean isCrateChosen = false;
        if (crate.getCratesNames().isPresent()) {
            isCrateChosen = !crate.getCratesNames().get().isEmpty() && chooseCrateButton.isDisabled();
        }

        if (!isCrateChosen) {
            settings.getStatusBarLine().setStatus("Перед сохранением настроек необходимо выбрать крейт",
                    false);
        }

        return isCrateChosen;
    }

    public void selectCrate() {
        if (crate.getCratesNames().isPresent()) {
            if (crate.getCratesNames().get().isEmpty()) {
                settings.getStatusBarLine().setStatus("Не найдены подключенные крейты", false);
                cratesListView.setDisable(true);
                modulesListView.setDisable(true);
                chooseCrateButton.setDisable(true);
                setupModuleButton.setDisable(true);
                saveSettingsButton.setDisable(true);
            } else {
                findCrate();
            }
        }
    }

    private void findCrate() {
        ObservableList<String> cratesNames = crate.getCratesNames().get();

        for (int i = 0; i < cratesNames.size(); i++) {
            String crateName = cratesNames.get(i);
            crateSerialNumber = settings.getTestProgram().getCrateSerialNumber();
            int notCrateCounter = 0;

            if (crateName.contains(crateSerialNumber)) {
                selectedCrate = i;
                toggleUiElements(true);
            } else {
                notCrateCounter++;
            }

            check(cratesNames, notCrateCounter);
        }
    }

    private void check(ObservableList<String> cratesNames, int notCrateCounter) {
        if (notCrateCounter == cratesNames.size()) {
            settings.getStatusBarLine().setStatus("Ошибка загрузки настроек: крейт с указанным серийным номером не найден.",
                    false);
        } else {
            cratesListView.getSelectionModel().select(selectedCrate);
            modulesListView.getSelectionModel().clearSelection();
            modulesListView.setDisable(true);
            setupModuleButton.setDisable(true);
            settings.handleChooseCrate();
        }
    }

    public Crate getCrate() {
        return crate;
    }

    public ObservableList<String> getCrates() {
        return crates;
    }

    public String getCrateSerialNumber() {
        return crateSerialNumber;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ObservableList<String> getModulesNames() {
        return modulesNames;
    }

    public int getSelectedCrate() {
        return selectedCrate;
    }

    public int getSelectedModuleIndex() {
        return selectedModuleIndex;
    }
}
