package ru.avem.posum.controllers.Settings;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import ru.avem.posum.hardware.Crate;

public class HardwareSettings extends Settings {
    private Button backButton;
    private Button chooseCrateButton;
    private Crate crate = new Crate();
    private ListView<String> cratesListView;
    private String crateSerialNumber;
    private ObservableList<String> crates;
    private String moduleName;
    private ListView<String> modulesListView;
    private ObservableList<String> modulesNames;
    private Button saveSettingsButton;
    private int selectedCrate;
    private int selectedModuleIndex;
    private Button setupModuleButton;
    private Settings settings;

    public HardwareSettings(Settings settings) {
        this.backButton = settings.getBackButton();
        this.chooseCrateButton = settings.getChooseCrateButton();
        this.cratesListView = settings.getCratesListView();
        this.modulesListView = settings.getModulesListView();
        this.saveSettingsButton = settings.getSaveSettingsButton();
        this.settings = settings;
        this.setupModuleButton = settings.getSetupModuleButton();
    }

    public void showCrates() {
        crates = crate.getCratesNames();
        cratesListView.setItems(crates);
    }

    public void showModules() {
        cratesListView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            selectedCrate = cratesListView.getSelectionModel().getSelectedIndex();
            crateSerialNumber = crate.getCrates()[0][selectedCrate];
            modulesNames = crate.getModulesNames(selectedCrate);
            modulesListView.setItems(modulesNames);

        }));
    }

    public void initialize() {
        for (int i = 0; i < getCrates().size(); i++) {
            if (cratesListView.getSelectionModel().isSelected(i)) {
                initSettingsModel();
                crate.initialize(crateSerialNumber);
                saveSettingsButton.setDisable(false);
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

        if (!chooseCrateButton.isDisabled()) {
            settings.getStatusBarLine().setStatus("Перед сохранением настроек необходимо выбрать крейт",
                    false);
        } else {
            isCrateChosen = true;
        }

        return isCrateChosen;
    }

    public void selectCrate() {
        for (int i = 0; i < crate.getCratesNames().size(); i++) {
            String crateName = crate.getCratesNames().get(i);
            crateSerialNumber = settings.getTestProgram().getCrateSerialNumber();
            int notCrateCounter = 0;

            if (crateName.contains(crateSerialNumber)) {
                selectedCrate = i;
                toggleUiElements(true);
            } else {
                notCrateCounter++;
            }

            check(notCrateCounter);

            cratesListView.getSelectionModel().select(selectedCrate);
            modulesListView.getSelectionModel().clearSelection();
        }
    }

    private void check(int notCrateCounter) {
        if (notCrateCounter == crate.getCratesNames().size()) {
            settings.getStatusBarLine().setStatus("Ошибка загрузки настроек: крейт с указанным серийным номером не найден.",
                    false);
        } else {
            settings.getStatusBarLine().clearStatusBar();
            settings.getStatusBarLine().toggleProgressIndicator(false);
            settings.getStatusBarLine().setStatusOfProgress("Устанавливается соединение с модулями");

            modulesListView.setDisable(true);
            saveSettingsButton.setDisable(true);
            setupModuleButton.setDisable(true);
            backButton.setDisable(true);

            modulesNames = crate.getModulesNames(selectedCrate);
            modulesListView.setItems(modulesNames);
            settings.getCm().createListModulesControllers(modulesNames);
            settings.getSettingsModel().setControllerManager(settings.getCm());
            settings.getSettingsModel().createModulesInstances(modulesNames);

            modulesListView.setDisable(false);
            saveSettingsButton.setDisable(false);
            setupModuleButton.setDisable(false);
            backButton.setDisable(false);

            settings.getStatusBarLine().toggleProgressIndicator(true);
            settings.getStatusBarLine().clearStatusBar();
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
