package ru.avem.posum.controllers.Settings;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ru.avem.posum.hardware.Crate;

public class HardwareSettings extends Settings {
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
                Platform.runLater(() -> settings.getStatusBarLine().setStatus
                        ("     Устанавливается соединение с модулями", settings.getStatusBar()));

                cratesListView.setDisable(true);
                chooseCrateButton.setDisable(true);
                saveSettingsButton.setDisable(true);
                initSettingsModel();
                crate.initialize(crateSerialNumber);
                toggleUiElements(true);
                saveSettingsButton.setDisable(false);
            } else {
                Platform.runLater(() -> settings.getStatusBarLine().setStatus("Крейт не выбран",
                        settings.getStatusBar()));
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
            settings.getStatusBarLine().setStatus("Ошибка сохранения настроек: необходимо выбрать крейт",
                    settings.getStatusBar());
        } else {
            isCrateChosen = true;
        }

        return isCrateChosen;
    }

    public void selectCrate() {
        for (int i = 0; i < crate.getCratesNames().size(); i++) {
            String crateName = crate.getCratesNames().get(i);
            crateSerialNumber = settings.getTestProgram().getCrateSerialNumber();
            int notCrate = 0;

            if (crateName.contains(crateSerialNumber)) {
                selectedCrate = i;
                toggleUiElements(true);
            } else {
                notCrate++;
            }

            if (notCrate == crate.getCratesNames().size()) {
                settings.getStatusBarLine().setStatus
                        ("Ошибка загрузки настроек: крейт с указанным серийным номером не найден.",
                                settings.getStatusBar());
            }

            cratesListView.getSelectionModel().select(selectedCrate);
            modulesListView.getSelectionModel().clearSelection();
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
