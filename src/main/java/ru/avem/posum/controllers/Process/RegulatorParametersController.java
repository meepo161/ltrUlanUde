package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import ru.avem.posum.hardware.Process;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.RegulatorParametersModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RegulatorParametersController {
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
    private RegulatorParametersModel regulatorParametersModel = new RegulatorParametersModel();
    private ToolBar toolbarSettings;
    private VBox topPanel;
    private TableView<ChannelModel> tableView;
    private Button saveButton;

    private List<Node> amplitudeUiElements = new ArrayList<>();
    private ContextMenu contextMenu = new ContextMenu();
    private List<Node> dcUiElements = new ArrayList<>();
    private List<Node> frequencyUiElements = new ArrayList<>();
    private LinkingManager lm;
    private List<CheckBox> pidParameters = new ArrayList<>();
    private Process process;
    private ProcessController processController;
    private List<Node> rmsUiElements = new ArrayList<>();
    private StatusBarLine statusBarLine;

    public RegulatorParametersController(CheckBox amplitudeCheckBox, Label amplitudeLabel, TextField amplitudeTextField,
                                         Label calibratedAmplitudeLabel, TextField calibratedAmplitudeTextField,
                                         Slider amplitudeSlider, CheckBox dcCheckBox, Label dcLabel, TextField dcTextField,
                                         Label calibratedDcLabel, TextField calibratedDcTextField, Slider dcSlider,
                                         CheckBox frequencyCheckBox, Label frequencyLabel, TextField frequencyTextField, Slider frequencySlider, Label pLabel,
                                         Slider pSlider, TextField pTextField, Label iLabel, Slider iSlider, TextField iTextField,
                                         Label dLabel, Slider dSlider, TextField dTextField, AnchorPane mainPanel,
                                         ToolBar toolbarSettings, VBox topPanel, TableView<ChannelModel> tableView,
                                         StatusBarLine statusBarLine, Button saveButton, Process process,
                                         ProcessController processController) {

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
        this.saveButton = saveButton;
        this.process = process;
        this.processController = processController;

        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        toolbarSettings.setVisible(false);

        fillListOfPidParameters();
        fillListOfAmplitudeUiElements();
        fillListOfDcUiElements();
        fillListOfFrequencyUiElements();

        initSliders();
        initContextMenu();
        listen(tableView);
        listenCheckBoxes();
    }

    private void fillListOfPidParameters() {
        pidParameters.add(amplitudeCheckBox);
        pidParameters.add(dcCheckBox);
        pidParameters.add(frequencyCheckBox);
    }

    private void fillListOfAmplitudeUiElements() {
        amplitudeUiElements.add(amplitudeLabel);
        amplitudeUiElements.add(amplitudeTextField);
        amplitudeUiElements.add(calibratedAmplitudeLabel);
        amplitudeUiElements.add(calibratedAmplitudeTextField);
        amplitudeUiElements.add(amplitudeSlider);
    }

    private void fillListOfDcUiElements() {
        dcUiElements.add(dcLabel);
        dcUiElements.add(dcTextField);
        dcUiElements.add(calibratedDcLabel);
        dcUiElements.add(calibratedDcTextField);
        dcUiElements.add(dcSlider);
    }

    private void fillListOfFrequencyUiElements() {
        frequencyUiElements.add(frequencyLabel);
        frequencyUiElements.add(frequencyTextField);
        frequencyUiElements.add(frequencySlider);
    }

    private void listenCheckBoxes() {
        for (CheckBox checkBox : pidParameters) {
            listen(checkBox);
        }
    }

    private void listen(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                unselectCheckBoxes(checkBox);
                highlightPidParameters();
            } else {
                clearParameters();
                saveButton.setDisable(false);
            }
        });
    }

    private void unselectCheckBoxes(CheckBox checkBox) {
        for (int checkBoxIndex = 0; checkBoxIndex < pidParameters.size(); checkBoxIndex++) {
            if (checkBox != pidParameters.get(checkBoxIndex)) {
                pidParameters.get(checkBoxIndex).setSelected(false);
            }
        }
    }

    private void highlightPidParameters() {
        for (int parameterIndex = 0; parameterIndex < pidParameters.size(); parameterIndex++) {
            if (pidParameters.get(parameterIndex).isSelected()) {
                highlight(parameterIndex);
            }
        }
    }

    private void highlight(int parameterIndex) {
        switch (parameterIndex) {
            case 0:
                toggleUiElements(amplitudeUiElements, false);
                toggleUiElements(dcUiElements, true);
                toggleUiElements(rmsUiElements, true);
                toggleUiElements(frequencyUiElements, true);
                break;
            case 1:
                toggleUiElements(amplitudeUiElements, true);
                toggleUiElements(dcUiElements, false);
                toggleUiElements(rmsUiElements, true);
                toggleUiElements(frequencyUiElements, true);
                break;
            case 2:
                toggleUiElements(amplitudeUiElements, true);
                toggleUiElements(dcUiElements, true);
                toggleUiElements(rmsUiElements, true);
                toggleUiElements(frequencyUiElements, false);
                break;
        }

        toggleUiElements(false);
        saveButton.setDisable(false);
    }

    private void toggleUiElements(List<Node> elements, boolean isDisable) {
        for (Node element : elements) {
            element.setDisable(isDisable);
        }
    }

    private void toggleUiElements(boolean isDisable) {
        pLabel.setDisable(isDisable);
        pSlider.setDisable(isDisable);
        pTextField.setDisable(isDisable);
        iLabel.setDisable(isDisable);
        iSlider.setDisable(isDisable);
        iTextField.setDisable(isDisable);
        dLabel.setDisable(isDisable);
        dSlider.setDisable(isDisable);
        dTextField.setDisable(isDisable);
    }

    public void showRegulationPanel() {
        int TOOLBAR_HEIGHT = 110;
        boolean hide = regulatorParametersModel.checkToProgramClicksCounter();
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
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty() && process.isStopped())) {
                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }

                if (event.getButton() == MouseButton.PRIMARY && (!row.isEmpty())) {
                    selectParameters(tableView);
                }
            });

            row.selectedProperty().addListener(observable -> {
                if (!tableView.getItems().isEmpty()) {
                    selectParameters(tableView);
                }
            });
            return row;
        });
    }

    private void selectParameters(TableView<ChannelModel> tableView) {
        ChannelModel selectedChannel = tableView.getSelectionModel().getSelectedItem();

        toggleCheckBoxes(!selectedChannel.getName().contains("=>"));
        selectParameters(selectedChannel);
        setParameters(selectedChannel);
    }

    private void toggleCheckBoxes(boolean isDisable) {
        for (CheckBox checkBox : pidParameters) {
            checkBox.setDisable(isDisable);
        }
    }

    private void selectParameters(ChannelModel channelModel) {
        int chosenParameterIndex = Integer.parseInt(channelModel.getChosenParameterIndex());

        if (chosenParameterIndex != -1) {
            for (int parametersIndex = 0; parametersIndex < pidParameters.size(); parametersIndex++) {
                if (parametersIndex != chosenParameterIndex) {
                    pidParameters.get(parametersIndex).setSelected(false);
                }
            }

            pidParameters.get(chosenParameterIndex).setSelected(true);
        } else {
            unselectAllCheckBoxes();
        }
    }

    private void setParameters(ChannelModel channelModel) {
        amplitudeTextField.setText(channelModel.getAmplitude());
        dcTextField.setText(channelModel.getDc());
        frequencyTextField.setText(channelModel.getFrequency());
        pTextField.setText(channelModel.getPcoefficient());
        iTextField.setText(channelModel.getICoefficient());
        dTextField.setText(channelModel.getDcoefficient());
    }

    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteChannelModel());
        menuItemClear.setOnAction(event -> clearPairModels());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deleteChannelModel() {
        ChannelModel selectedChannelModel = tableView.getSelectionModel().getSelectedItem();
        tableView.getItems().remove(selectedChannelModel);
        deleteDescriptions(selectedChannelModel);
        statusBarLine.setStatus("Канал успешно удален", true);
        check(tableView);
    }

    private void check(TableView<ChannelModel> tableView) {
        if (tableView.getItems().isEmpty()) {
            toggleCheckBoxes(true);
            unselectAllCheckBoxes();
            clearParameters();
        } else {
            ChannelModel selectedChannel = tableView.getSelectionModel().getSelectedItem();
            setParameters(selectedChannel);
        }
    }

    private void unselectAllCheckBoxes() {
        for (CheckBox checkBox : pidParameters) {
            checkBox.setSelected(false);
        }
    }

    private void clearParameters() {
        amplitudeTextField.setText("0");
        dcTextField.setText("0");
        frequencyTextField.setText("0");
        pTextField.setText("0");
        iTextField.setText("0");
        dTextField.setText("0");

        toggleUiElements(amplitudeUiElements, true);
        toggleUiElements(dcUiElements, true);
        toggleUiElements(rmsUiElements, true);
        toggleUiElements(frequencyUiElements, true);
        toggleUiElements(true);
        saveButton.setDisable(true);
    }

    private void deleteDescriptions(ChannelModel channelModel) {
        if (channelModel.getName().contains("=>")) { // удаление связанных каналов
            String descriptionOfDacChannel = channelModel.getName().split(" => ")[0];
            String descriptionOfAdcChannel = channelModel.getName().split(" => ")[1];

            for (Pair<CheckBox, CheckBox> channels : lm.getLinkedChannels()) {
                if (channels.getKey().getText().equals(descriptionOfDacChannel) ||
                        channels.getValue().getText().equals(descriptionOfAdcChannel)) {
                    Platform.runLater(() -> lm.getLinkedChannels().remove(channels));
                }
            }
        } else { // удаление выбранных каналов
            String descriptionOfChannel = channelModel.getName();

            for (CheckBox channel : lm.getChosenChannels()) {
                if (channel.getText().equals(descriptionOfChannel)) {
                    Platform.runLater(() -> lm.getChosenChannels().remove(channel));
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
        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = lm.getLinkedChannels();
        lm.getLinkedChannels().removeAll(linkedChannels); // удаление всех связанных каналов

        ObservableList<CheckBox> chosenChannels = lm.getChosenChannels();
        lm.getChosenChannels().removeAll(chosenChannels); // удаление всех выбранных каналов
    }

    public void clear() {
        toolbarSettings.setVisible(false);
        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        topPanel.maxHeight(mainPanel.getMaxHeight());
        topPanel.minHeight(mainPanel.getMaxHeight());
        regulatorParametersModel.resetProgramClickCounter();
    }

    public int getChosenParameterIndex() {
        for (int parameterIndex = 0; parameterIndex < pidParameters.size(); parameterIndex++) {
            if (pidParameters.get(parameterIndex).isSelected()) {
                return parameterIndex;
            }
        }

        return -1;
    }

    public void save(ChannelModel selectedChannel) {
        selectedChannel.setAmplitude(amplitudeTextField.getText());
        selectedChannel.setDc(dcTextField.getText());
        selectedChannel.setFrequency(frequencyTextField.getText());
        selectedChannel.setPcoefficient(pTextField.getText());
        selectedChannel.setIcoefficient(iTextField.getText());
        selectedChannel.setDcoefficient(dTextField.getText());
        selectedChannel.setChosenParameterIndex(String.valueOf(getChosenParameterIndex()));

        removeColumns();
        addColumns(getChosenIndexesOfParameters());
    }

    private void removeColumns() {
        int normalCountOfColumns = 7; // количество колонок в таблице, если регулировка отсутвует
        int realCountOfColumns = tableView.getColumns().size();

        while (realCountOfColumns > normalCountOfColumns) {
            tableView.getColumns().remove(tableView.getColumns().size() - 1);
            realCountOfColumns = tableView.getColumns().size();
        }
    }

    private void addColumns(List<Integer> chosenIndexesOfParamters) {
        for (int value : chosenIndexesOfParamters) {
            switch (value) {
                case 0:
                    TableColumn<ChannelModel, String> amplitudeColumn = createColumn("Амплитуда норма");
                    TableColumn<ChannelModel, String> relativeResponseAmplitudeColumn = createColumn("Амплитуда отклик, %");

                    amplitudeColumn.setCellValueFactory(cellData -> cellData.getValue().amplitudeProperty());
                    relativeResponseAmplitudeColumn.setCellValueFactory(cellData -> cellData.getValue().relativeAmplitudeProperty());

                    tableView.getColumns().add(amplitudeColumn);
                    tableView.getColumns().add(relativeResponseAmplitudeColumn);
                    break;
                case 1:
                    TableColumn<ChannelModel, String> dcColumn = createColumn("Статика норма");
                    TableColumn<ChannelModel, String> relativeResponseDcColumn = createColumn("Статика отклик, %");

                    dcColumn.setCellValueFactory(cellData -> cellData.getValue().dcProperty());
                    relativeResponseDcColumn.setCellValueFactory(cellData -> cellData.getValue().relativeDcProperty());

                    tableView.getColumns().add(dcColumn);
                    tableView.getColumns().add(relativeResponseDcColumn);
                    break;
                case 2:
                    TableColumn<ChannelModel, String> frequencyColumn = createColumn("Частота норма");
                    TableColumn<ChannelModel, String> relativeResponseFrequencyColumn = createColumn("Частота отклик, %");

                    frequencyColumn.setCellValueFactory(cellData -> cellData.getValue().frequencyProperty());
                    relativeResponseFrequencyColumn.setCellValueFactory(cellData -> cellData.getValue().relativeFrequencyProperty());

                    tableView.getColumns().add(frequencyColumn);
                    tableView.getColumns().add(relativeResponseFrequencyColumn);
                    break;
            }
        }
    }

    private List<Integer> getChosenIndexesOfParameters() {
        List<Integer> chosenParameters = new ArrayList<>();

        ObservableList<ChannelModel> channels = processController.getTableController().getChannels();
        for (ChannelModel channel : channels) {
            int chosenParameterIndex = Integer.parseInt(channel.getChosenParameterIndex());
            if (chosenParameterIndex != -1 && !isChosenParameterAdded(chosenParameterIndex, chosenParameters)) {
                chosenParameters.add(chosenParameterIndex);
            }
        }

        return chosenParameters;
    }

    private boolean isChosenParameterAdded(int chosenParameter, List<Integer> chosenParameters) {
        for (int value : chosenParameters) {
            if (value == chosenParameter) {
                return true;
            }
        }

        return false;
    }

    private TableColumn<ChannelModel, String> createColumn(String header) {
        TableColumn<ChannelModel, String> column = new TableColumn<>();

        column.setMinWidth(50);
        column.setPrefWidth(80);
        column.setMaxWidth(5000);
        column.setText(header);
        Utils.makeHeaderWrappable(column);

        return column;
    }

    public void setLm(LinkingManager lm) {
        this.lm = lm;
    }
}
