package ru.avem.posum.controllers.settings.LTR27;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.hardware.LTR27;
import ru.avem.posum.models.settings.LTR27SettignsModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class LTR27Settings implements BaseController {
    @FXML
    private Label checkIcon;
    @FXML
    private ComboBox<String> frequencyComboBox;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label sceneTitleLabel;
    @FXML
    private StatusBar statusBar;
    @FXML
    private CheckBox submoduleOneCheckBox;
    @FXML
    private Label subModuleOneChannelOneLabel;
    @FXML
    private Label subModuleOneChannelTwoLabel;
    @FXML
    private TextField subModuleOneChannelOneTextField;
    @FXML
    private TextField subModuleOneChannelTwoTextField;
    @FXML
    private CheckBox submoduleTwoCheckBox;
    @FXML
    private Label subModuleTwoChannelOneLabel;
    @FXML
    private Label subModuleTwoChannelTwoLabel;
    @FXML
    private TextField subModuleTwoChannelOneTextField;
    @FXML
    private TextField subModuleTwoChannelTwoTextField;
    @FXML
    private CheckBox submoduleThreeCheckBox;
    @FXML
    private Label subModuleThreeChannelOneLabel;
    @FXML
    private Label subModuleThreeChannelTwoLabel;
    @FXML
    private TextField subModuleThreeChannelOneTextField;
    @FXML
    private TextField subModuleThreeChannelTwoTextField;
    @FXML
    private CheckBox submoduleFourCheckBox;
    @FXML
    private Label subModuleFourChannelOneLabel;
    @FXML
    private Label subModuleFourChannelTwoLabel;
    @FXML
    private TextField subModuleFourChannelOneTextField;
    @FXML
    private TextField subModuleFourChannelTwoTextField;
    @FXML
    private CheckBox submoduleFiveCheckBox;
    @FXML
    private Label subModuleFiveChannelOneLabel;
    @FXML
    private Label subModuleFiveChannelTwoLabel;
    @FXML
    private TextField subModuleFiveChannelOneTextField;
    @FXML
    private TextField subModuleFiveChannelTwoTextField;
    @FXML
    private CheckBox submoduleSixCheckBox;
    @FXML
    private Label subModuleSixChannelOneLabel;
    @FXML
    private Label subModuleSixChannelTwoLabel;
    @FXML
    private TextField subModuleSixChannelOneTextField;
    @FXML
    private TextField subModuleSixChannelTwoTextField;
    @FXML
    private CheckBox submoduleSevenCheckBox;
    @FXML
    private Label subModuleSevenChannelOneLabel;
    @FXML
    private Label subModuleSevenChannelTwoLabel;
    @FXML
    private TextField subModuleSevenChannelOneTextField;
    @FXML
    private TextField subModuleSevenChannelTwoTextField;
    @FXML
    private CheckBox submoduleEightCheckBox;
    @FXML
    private Label subModuleEightChannelOneLabel;
    @FXML
    private Label subModuleEightChannelTwoLabel;
    @FXML
    private TextField subModuleEightChannelOneTextField;
    @FXML
    private TextField subModuleEightChannelTwoTextField;
    @FXML
    private Label warningIcon;

    private ControllerManager cm;
    private LTR27 ltr27 = new LTR27();
    private LTR27SettignsModel ltr27SettingsModel;
    private LTR27SubmodulesSettings ltr27SubmodulesSettings;
    private String[][] submodulesDescription;
    private StatusBarLine statusBarLine;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar,
                warningIcon);
        ltr27SettingsModel = new LTR27SettignsModel();
        ltr27SubmodulesSettings = new LTR27SubmodulesSettings(this);
    }

    public void loadSettings(String moduleName) {
        sceneTitleLabel.setText(String.format("Настройки модуля %s", moduleName));
        ltr27SettingsModel.setModuleName(moduleName);
        ltr27SettingsModel.setSlot(Utils.parseSlotNumber(moduleName));
        ltr27SettingsModel.setModuleInstance(cm.getCrateModelInstance().getModulesList());
        ltr27SubmodulesSettings.setSubmodulesNames();
        ltr27SubmodulesSettings.enableUielements();
    }

    public void handleInitialize() {

    }

    public void handleBack() {
        wm.setScene(WindowsManager.Scenes.SETTINGS_SCENE);
    }

    public String[][] getSubmodulesDescriptions() {
        return ltr27SettingsModel.getDescriptions();
    }

    public CheckBox getSubmoduleOneCheckBox() {
        return submoduleOneCheckBox;
    }

    public Label getSubModuleOneChannelOneLabel() {
        return subModuleOneChannelOneLabel;
    }

    public Label getSubModuleOneChannelTwoLabel() {
        return subModuleOneChannelTwoLabel;
    }

    public TextField getSubModuleOneChannelOneTextField() {
        return subModuleOneChannelOneTextField;
    }

    public TextField getSubModuleOneChannelTwoTextField() {
        return subModuleOneChannelTwoTextField;
    }

    public CheckBox getSubmoduleTwoCheckBox() {
        return submoduleTwoCheckBox;
    }

    public Label getSubModuleTwoChannelOneLabel() {
        return subModuleTwoChannelOneLabel;
    }

    public Label getSubModuleTwoChannelTwoLabel() {
        return subModuleTwoChannelTwoLabel;
    }

    public TextField getSubModuleTwoChannelOneTextField() {
        return subModuleTwoChannelOneTextField;
    }

    public TextField getSubModuleTwoChannelTwoTextField() {
        return subModuleTwoChannelTwoTextField;
    }

    public CheckBox getSubmoduleThreeCheckBox() {
        return submoduleThreeCheckBox;
    }

    public Label getSubModuleThreeChannelOneLabel() {
        return subModuleThreeChannelOneLabel;
    }

    public Label getSubModuleThreeChannelTwoLabel() {
        return subModuleThreeChannelTwoLabel;
    }

    public TextField getSubModuleThreeChannelOneTextField() {
        return subModuleThreeChannelOneTextField;
    }

    public TextField getSubModuleThreeChannelTwoTextField() {
        return subModuleThreeChannelTwoTextField;
    }

    public CheckBox getSubmoduleFourCheckBox() {
        return submoduleFourCheckBox;
    }

    public Label getSubModuleFourChannelOneLabel() {
        return subModuleFourChannelOneLabel;
    }

    public Label getSubModuleFourChannelTwoLabel() {
        return subModuleFourChannelTwoLabel;
    }

    public TextField getSubModuleFourChannelOneTextField() {
        return subModuleFourChannelOneTextField;
    }

    public TextField getSubModuleFourChannelTwoTextField() {
        return subModuleFourChannelTwoTextField;
    }

    public CheckBox getSubmoduleFiveCheckBox() {
        return submoduleFiveCheckBox;
    }

    public Label getSubModuleFiveChannelOneLabel() {
        return subModuleFiveChannelOneLabel;
    }

    public Label getSubModuleFiveChannelTwoLabel() {
        return subModuleFiveChannelTwoLabel;
    }

    public TextField getSubModuleFiveChannelOneTextField() {
        return subModuleFiveChannelOneTextField;
    }

    public TextField getSubModuleFiveChannelTwoTextField() {
        return subModuleFiveChannelTwoTextField;
    }

    public CheckBox getSubmoduleSixCheckBox() {
        return submoduleSixCheckBox;
    }

    public Label getSubModuleSixChannelOneLabel() {
        return subModuleSixChannelOneLabel;
    }

    public Label getSubModuleSixChannelTwoLabel() {
        return subModuleSixChannelTwoLabel;
    }

    public TextField getSubModuleSixChannelOneTextField() {
        return subModuleSixChannelOneTextField;
    }

    public TextField getSubModuleSixChannelTwoTextField() {
        return subModuleSixChannelTwoTextField;
    }

    public CheckBox getSubmoduleSevenCheckBox() {
        return submoduleSevenCheckBox;
    }

    public Label getSubModuleSevenChannelOneLabel() {
        return subModuleSevenChannelOneLabel;
    }

    public Label getSubModuleSevenChannelTwoLabel() {
        return subModuleSevenChannelTwoLabel;
    }

    public TextField getSubModuleSevenChannelOneTextField() {
        return subModuleSevenChannelOneTextField;
    }

    public TextField getSubModuleSevenChannelTwoTextField() {
        return subModuleSevenChannelTwoTextField;
    }

    public CheckBox getSubmoduleEightCheckBox() {
        return submoduleEightCheckBox;
    }

    public Label getSubModuleEightChannelOneLabel() {
        return subModuleEightChannelOneLabel;
    }

    public Label getSubModuleEightChannelTwoLabel() {
        return subModuleEightChannelTwoLabel;
    }

    public TextField getSubModuleEightChannelOneTextField() {
        return subModuleEightChannelOneTextField;
    }

    public TextField getSubModuleEightChannelTwoTextField() {
        return subModuleEightChannelTwoTextField;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
