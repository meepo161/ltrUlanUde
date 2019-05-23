package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.ProcessModel;
import ru.avem.posum.models.Process.ProgramModel;
import ru.avem.posum.utils.StatusBarLine;

public class ProgramController {
    private CheckBox amplitudeCheckBox;
    private Label amplitudeLabel;
    private TextField amplitudeTextField;
    private Label calibratedAmplitudeLabel;
    private TextField calibratedAmplitudeTextField;
    private Slider amplitudeSlider;
    private CheckBox dcCheckBox;
    private Label dcLabel;
    private TextField dcTextField;
    private Label calibratedDcLabel;
    private TextField calibratedDcTextField;
    private Slider dcSlider;
    private CheckBox rmsCheckBox;
    private Label rmsLabel;
    private TextField rmsTextField;
    private Label calibratedRmsLabel;
    private TextField calibratedRmsTextField;
    private Slider rmsSlider;
    private CheckBox frequencyCheckBox;
    private Label frequencyLabel;
    private TextField frequencyTextField;
    private Slider frequencySlider;
    private Label pLabel;
    private Slider pSlider;
    private TextField pTextField;
    private Label iLabel;
    private Slider iSlider;
    private TextField iTextField;
    private Label dLabel;
    private Slider dSlider;
    private TextField dTextField;
    private AnchorPane mainPanel;
    private ProgramModel programModel = new ProgramModel();
    private ToolBar toolbarSettings;
    private VBox topPanel;
    private TableView<ChannelModel> tableView;

    private ContextMenu contextMenu = new ContextMenu();
    private StatusBarLine statusBarLine;
    private ControllerManager cm;

    public ProgramController(CheckBox amplitudeCheckBox, Label amplitudeLabel, TextField amplitudeTextField,
                             Label calibratedAmplitudeLabel, TextField calibratedAmplitudeTextField,
                             Slider amplitudeSlider, CheckBox dcCheckBox, Label dcLabel, TextField dcTextField,
                             Label calibratedDcLabel, TextField calibratedDcTextField, Slider dcSlider,
                             CheckBox rmsCheckBox, Label rmsLabel, TextField rmsTextField, Label calibratedRmsLabel,
                             TextField calibratedRmsTextField, Slider rmsSlider, CheckBox frequencyCheckBox,
                             Label frequencyLabel, TextField frequencyTextField, Slider frequencySlider, Label pLabel,
                             Slider pSlider, TextField pTextField, Label iLabel, Slider iSlider, TextField iTextField,
                             Label dLabel, Slider dSlider, TextField dTextField, AnchorPane mainPanel,
                             ToolBar toolbarSettings, VBox topPanel, TableView<ChannelModel> tableView,
                             StatusBarLine statusBarLine) {

        this.amplitudeCheckBox = amplitudeCheckBox;
        this.amplitudeLabel = amplitudeLabel;
        this.amplitudeTextField = amplitudeTextField;
        this.calibratedAmplitudeLabel = calibratedAmplitudeLabel;
        this.calibratedAmplitudeTextField = calibratedAmplitudeTextField;
        this.amplitudeSlider = amplitudeSlider;
        this.dcCheckBox = dcCheckBox;
        this.dcLabel = dcLabel;
        this.dcTextField = dcTextField;
        this.calibratedDcLabel = calibratedDcLabel;
        this.calibratedDcTextField = calibratedDcTextField;
        this.dcSlider = dcSlider;
        this.rmsCheckBox = rmsCheckBox;
        this.rmsLabel = rmsLabel;
        this.rmsTextField = rmsTextField;
        this.calibratedRmsLabel = calibratedRmsLabel;
        this.calibratedRmsTextField = calibratedRmsTextField;
        this.rmsSlider = rmsSlider;
        this.frequencyCheckBox = frequencyCheckBox;
        this.frequencyLabel = frequencyLabel;
        this.frequencyTextField = frequencyTextField;
        this.frequencySlider = frequencySlider;
        this.pLabel = pLabel;
        this.pSlider = pSlider;
        this.pTextField = pTextField;
        this.iLabel = iLabel;
        this.iSlider = iSlider;
        this.iTextField = iTextField;
        this.dLabel = dLabel;
        this.dSlider = dSlider;
        this.dTextField = dTextField;
        this.mainPanel = mainPanel;
        this.toolbarSettings = toolbarSettings;
        this.topPanel = topPanel;
        this.tableView = tableView;
        this.statusBarLine = statusBarLine;

        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        toolbarSettings.setVisible(false);

        initSliders();
        initContextMenu();
        listen(tableView);
    }

    public void toggleSettingsPanel() {
        int TOOLBAR_HEIGHT = 110;
        boolean hide = programModel.checkToProgramClicksCounter();
        double neededHeight = hide ? mainPanel.getMaxHeight() : mainPanel.getMaxHeight() + TOOLBAR_HEIGHT;

        toolbarSettings.setVisible(!hide);
        topPanel.setPrefHeight(neededHeight);
        topPanel.maxHeight(neededHeight);
        topPanel.minHeight(neededHeight);
    }

    private void initSliders() {
        init(amplitudeSlider, amplitudeTextField);
        init(dcSlider, dcTextField);
        init(frequencySlider, frequencyTextField);
        init(rmsSlider, rmsTextField);
        init(pSlider, pTextField);
        init(iSlider, iTextField);
        init(dSlider, dTextField);
    }

    private void init(Slider slider, TextField textField) {
        slider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> textField.textProperty().setValue(
                String.valueOf((int) slider.getValue())));

        setDigitFilter(textField);
        listen(textField, slider);
    }



    private void setDigitFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void listen(TextField textField, Slider slider) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = textField.getText();

            if (!value.equals("-") && !value.isEmpty()) {
                slider.setValue((int) Double.parseDouble(textField.getText()));
            }
        });
    }

    private void listen(TableView<ChannelModel> tableView) {
        tableView.setRowFactory(tv -> {
            TableRow<ChannelModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }

                if (event.getButton() == MouseButton.PRIMARY && (!row.isEmpty())) {
                    ChannelModel pair = tableView.getSelectionModel().getSelectedItem();
                    toggleCheckBoxes(false);

                    amplitudeTextField.setText(pair.getAmplitude());
                    dcTextField.setText(pair.getDc());
                    rmsTextField.setText(pair.getRms());
                    frequencyTextField.setText(pair.getFrequency());
                    pTextField.setText(pair.getpValue());
                    iTextField.setText(pair.getiValue());
                    dcTextField.setText(pair.getdValue());
                }
            });
            return row;
        });
    }

    private void toggleCheckBoxes(boolean isDisable) {
        amplitudeCheckBox.setDisable(isDisable);
        dcCheckBox.setDisable(isDisable);
        rmsCheckBox.setDisable(isDisable);
        frequencyCheckBox.setDisable(isDisable);
    }

    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deletePairModel());
        menuItemClear.setOnAction(event -> clearPairModels());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deletePairModel() {
        ChannelModel selectedChannelModel = tableView.getSelectionModel().getSelectedItem();
        tableView.getItems().remove(selectedChannelModel);
        deleteDescriptions(selectedChannelModel);
        statusBarLine.setStatus("Канал успешно удален", true);
        check(tableView);
    }

    private void check(TableView<ChannelModel> tableView) {
        if (tableView.getItems().isEmpty()) {
            toggleCheckBoxes(true);
        }
    }

    private void deleteDescriptions(ChannelModel channelModel) {
        if (channelModel.getName().contains("->")) { // удаление связанных каналов
            String descriptionOfDacChannel = channelModel.getName().split(" -> ")[0];
            String descriptionOfAdcChannel = channelModel.getName().split(" -> ")[1];

            for (Pair<CheckBox, CheckBox> channels : cm.getLinkedChannels()) {
                if (channels.getKey().getText().split(" \\(")[0].equals(descriptionOfDacChannel) ||
                        channels.getValue().getText().split(" \\(")[0].equals(descriptionOfAdcChannel)) {
                    Platform.runLater(() -> cm.getLinkedChannels().remove(channels));
                }
            }
        } else { // удаление выбранных каналов
            String descriptionOfChannel = channelModel.getName();

            for (CheckBox channel : cm.getChosenChannels()) {
                if (channel.getText().split(" \\(")[0].equals(descriptionOfChannel)) {
                    Platform.runLater(() -> cm.getChosenChannels().remove(channel));
                }
            }
        }
    }

    private void clearPairModels() {
        ObservableList<ChannelModel> channelModels = tableView.getItems();
        tableView.getItems().removeAll(channelModels);
        clearDescriptions();
        statusBarLine.setStatus("Все каналы успешно удалены", true);
        check(tableView);
    }

    private void clearDescriptions() {
        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = cm.getLinkedChannels();
        cm.getLinkedChannels().removeAll(linkedChannels); // удаление всех связанных каналов

        ObservableList<CheckBox> chosenChannels = cm.getChosenChannels();
        cm.getChosenChannels().removeAll(chosenChannels); // удаление всех выбранных каналов
    }

    public void clear() {
        toolbarSettings.setVisible(false);
        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        topPanel.maxHeight(mainPanel.getMaxHeight());
        topPanel.minHeight(mainPanel.getMaxHeight());
        programModel.resetProgramClickCounter();
    }

    public void setCm(ControllerManager cm) {
        this.cm = cm;
    }
}
