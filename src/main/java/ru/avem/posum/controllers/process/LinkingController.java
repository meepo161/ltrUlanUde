package ru.avem.posum.controllers.process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.process.LinkingModel;
import ru.avem.posum.models.process.ChannelModel;
import ru.avem.posum.models.process.GraphModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LinkingController implements BaseController, LinkingManager {
    @FXML
    private Button addChannelButton;
    @FXML
    private ListView<CheckBox> adcChannelsListView;
    @FXML
    private Label checkIcon;
    @FXML
    private Button chooseAllButton;
    @FXML
    private ListView<CheckBox> dacChannelsListView;
    @FXML
    private Button linkButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label warningIcon;

    private GraphModel graphModel;
    private LinkingModel linkingModel = new LinkingModel();
    private ProcessController processController;
    private StatusBarLine statusBarLine;
    private TestProgram testProgram;
    private WindowsManager wm;

    // Инициализирует внешний вид
    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);

        listen(addChannelButton);
        listen(linkButton);
    }

    // Меняет состояние кнопки "Выбрать все"
    private void listen(Button button) {
        button.pressedProperty().addListener(observable -> checkItems(adcChannelsListView));
    }

    // Меняет состояние кнопки "Выбрать все"
    private void checkItems(ListView<CheckBox> listView) {
        new Thread(() -> {
            Utils.sleep(250);
            chooseAllButton.setDisable(listView.getItems().isEmpty());
        }).start();
    }

    // Возвращает список добавленных каналов
    @Override
    public ObservableList<CheckBox> getChosenChannels() {
        return linkingModel.getChosenChannels();
    }

    // Возвращает список добавленных модулей
    @Override
    public ObservableList<Modules> getChosenModules() {
        return linkingModel.getChosenModules();
    }

    // Возвращает список связянных каналов
    @Override
    public ObservableList<Pair<CheckBox, CheckBox>> getLinkedChannels() {
        return linkingModel.getLinkedChannels();
    }

    // Возвращает список связанных модулей
    @Override
    public List<Modules> getLinkedModules() {
        return linkingModel.getLinkedModules();
    }

    // Инициализирует списки
    @Override
    public void initListViews() {
        linkingModel.setTestProgram(testProgram);

        Platform.runLater(() -> {
            dacChannelsListView.getItems().clear();
            adcChannelsListView.getItems().clear();

            dacChannelsListView.setItems(linkingModel.getDescriptionsOfChannels(Crate.LTR34));
            adcChannelsListView.setItems(linkingModel.getDescriptionsOfChannels(Crate.LTR212, Crate.LTR24, Crate.LTR27));

            listenChannels(adcChannelsListView.getItems());
            listenChannels(dacChannelsListView.getItems());

            checkItems(adcChannelsListView);

            processController.getCalibrationModel().loadCalibrations(processController.getTableController().getChannels(), processController.getProcessModel().getModules());
        });
    }

    // Задает модель
    @Override
    public void setGraphModel() {
        setGraphModel(processController.getGraphController().getGraphModel());
    }

    // Меняет состояние GUI в зависимости от количества выбранных каналов и их типа
    private void listenChannels(ObservableList<CheckBox> checkBoxes) {
        ObservableList<CheckBox> adcCheckBoxes = adcChannelsListView.getItems();
        ObservableList<CheckBox> dacCheckBoxes = dacChannelsListView.getItems();

        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(observable -> {
                boolean isAdcChannelSelected = isChannelSelected(adcCheckBoxes);
                boolean isDacChannelSelected = isChannelSelected(dacCheckBoxes);
                boolean isOneAndOnlyAdcChannelSelected = isOneAndOnlyChannelSelected(adcCheckBoxes);
                boolean isOneAndOnlyDacChannelSelected = isOneAndOnlyChannelSelected(dacCheckBoxes);

                if (isAdcChannelSelected && !isDacChannelSelected) {
                    addChannelButton.setDisable(false);
                    linkButton.setDisable(true);
                } else if (isOneAndOnlyAdcChannelSelected && isOneAndOnlyDacChannelSelected) {
                    addChannelButton.setDisable(true);
                    linkButton.setDisable(false);
                } else {
                    addChannelButton.setDisable(true);
                    linkButton.setDisable(true);
                }
            });
        }
    }

    // Возвращает логическое значение выбран ли хотя бы один канал
    private boolean isChannelSelected(ObservableList<CheckBox> checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return true;
            }
        }

        return false;
    }

    // Возвращает логическое значение выбран ли только один канал
    private boolean isOneAndOnlyChannelSelected(ObservableList<CheckBox> checkBoxes) {
        int selectedChannelsCount = 0;

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedChannelsCount++;
            }
        }

        return selectedChannelsCount == 1;
    }

    // Добавляет канал(ы)
    public void handleAddChannel() {
        List<CheckBox> chosenChannels = new ArrayList<>();
        ObservableList<CheckBox> adcChannels = adcChannelsListView.getItems();

        for (CheckBox channel : adcChannels) {
            if (channel.isSelected()) {
                chosenChannels.add(channel);
                Platform.runLater(() -> adcChannels.remove(channel));

                ChannelModel newChannelModel = createChannel(channel);
                graphModel.add(processController.getTestProgramId(), newChannelModel);
            }
        }

        linkingModel.saveChosen(chosenChannels);
        addChannelButton.setDisable(true);
    }

    // Связывает каналы
    public void handleLink() {
        Optional<CheckBox> selectedDacChannel = getSelectedCheckBox(dacChannelsListView);
        Optional<CheckBox> selectedAdcChannel = getSelectedCheckBox(adcChannelsListView);

        if (!selectedDacChannel.isPresent()) {
            statusBarLine.setStatus("Не выбран канал ЦАП", false);
            return;
        }

        if (!selectedAdcChannel.isPresent()) {
            statusBarLine.setStatus("Не выбран канал АЦП", false);
            return;
        }

        dacChannelsListView.getItems().remove(selectedDacChannel.get());
        adcChannelsListView.getItems().remove(selectedAdcChannel.get());
        linkingModel.saveLinked(new Pair<>(selectedDacChannel.get(), selectedAdcChannel.get()));

        ChannelModel newChannelModel = createChannel(selectedDacChannel.get(), selectedAdcChannel.get());
        graphModel.add(processController.getTestProgramId(), newChannelModel);

        enableChooseOfChannels(dacChannelsListView);
        enableChooseOfChannels(adcChannelsListView);

        statusBarLine.setStatus("Операция успешно выполнена", true);
        linkButton.setDisable(true);
    }

    // Возвращает модель связанных каналов
    private ChannelModel createChannel(CheckBox selectedDacChannel, CheckBox selectedAdcChannel) {
        String pairName = selectedDacChannel.getText() + " => " +  selectedAdcChannel.getText();
        return new ChannelModel(pairName);
    }

    // Возвращает модель добавленного канала
    private ChannelModel createChannel(CheckBox selectedAdcChannel) {
        String channelName = selectedAdcChannel.getText();
        return new ChannelModel(channelName);
    }

    // Возвращает выбранный канал
    private Optional<CheckBox> getSelectedCheckBox(ListView<CheckBox> listView) {
        ObservableList<CheckBox> checkBoxes = listView.getItems();

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return Optional.of(checkBox);
            }
        }

        return Optional.empty();
    }

    // Меняет состояние GUI
    private void enableChooseOfChannels(ListView<CheckBox> listView) {
        ObservableList<CheckBox> channels = listView.getItems();

        for (CheckBox checkBox : channels) {
            checkBox.setDisable(false);
        }
    }

    // Выбирает все каналы АЦП
    public void handleChooseAll() {
        for (CheckBox checkBox : adcChannelsListView.getItems()) {
            checkBox.setSelected(true);
        }
    }

    // Возвращает пользователя в окно процесса испытаний
    public void handleBackButton() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Загрузка программы испытаний");

        new Thread(() -> {
            addChannelButton.setDisable(true);
            processController.getTableController().toggleResponseUiElements(true);
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clear();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.EXPERIMENT_SCENE));
        }).start();
    }

    // Добавляет выбранные и связанные каналы
    public void add(ChannelModel channelModel) {
        String name = channelModel.getName();
        if (name.contains(" => ")) {
            String dacName = name.split(" => ")[0];
            String adcName = name.split(" => ")[1];
            CheckBox dacCheckBox = new CheckBox(dacName);
            CheckBox adcCheckBox = new CheckBox(adcName);
            linkingModel.getLinkedChannels().add(new Pair<>(dacCheckBox, adcCheckBox));
        } else {
            CheckBox adcCheckBox = new CheckBox(name);
            linkingModel.getChosenChannels().add(adcCheckBox);
        }
    }

    // Инициализирует списки каналов
    public void initModulesList() {
        linkingModel.getDescriptionsOfChannels(Crate.LTR34);
        linkingModel.getDescriptionsOfChannels(Crate.LTR212, Crate.LTR24, Crate.LTR27);
    }

    // Задает делегат WindowManager
    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    // Задает модель графика
    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
    }

    // Задает контроллер процесса испытаний
    public void setProcessController(ProcessController processController) {
        this.processController = processController;
    }

    // Задает модель программы испытаний
    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
        linkingModel.setTestProgram(testProgram);
    }
}
