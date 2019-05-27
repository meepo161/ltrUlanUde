package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.LinkingModel;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.GraphModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LinkingController implements BaseController {
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

    private LinkingModel linkingModel = new LinkingModel();
    private GraphModel graphModel;
    private StatusBarLine statusBarLine;
    private TestProgram testProgram;
    private ControllerManager cm;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);

        listen(addChannelButton);
        listen(linkButton);
    }

    private void listen(Button button) {
        button.pressedProperty().addListener(observable -> checkItems(adcChannelsListView));
    }

    private void checkItems(ListView<CheckBox> listView) {
        new Thread(() -> {
            Utils.sleep(250);
            chooseAllButton.setDisable(listView.getItems().isEmpty());
        }).start();
    }

    public void initListViews() {
        linkingModel.setTestProgram(testProgram);

        Platform.runLater(() -> {
            dacChannelsListView.getItems().clear();
            adcChannelsListView.getItems().clear();

            dacChannelsListView.setItems(linkingModel.getDescriptionsOfChannels(Crate.LTR34));
            adcChannelsListView.setItems(linkingModel.getDescriptionsOfChannels(Crate.LTR212, Crate.LTR24));

            listenChannels(adcChannelsListView.getItems());
            listenChannels(dacChannelsListView.getItems());

            checkItems(adcChannelsListView);
        });
    }

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

    private boolean isChannelSelected(ObservableList<CheckBox> checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return true;
            }
        }

        return false;
    }

    private boolean isOneAndOnlyChannelSelected(ObservableList<CheckBox> checkBoxes) {
        int selectedChannelsCount = 0;

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedChannelsCount++;
            }
        }

        return selectedChannelsCount == 1;
    }

    public void handleAddChannel() {
        List<CheckBox> chosenChannels = new ArrayList<>();
        ObservableList<CheckBox> adcChannels = adcChannelsListView.getItems();

        for (CheckBox channel : adcChannels) {
            if (channel.isSelected()) {
                chosenChannels.add(channel);
                Platform.runLater(() -> adcChannels.remove(channel));

                ChannelModel newChannelModel = createChannel(channel);
                graphModel.getChannels().add(newChannelModel);
            }
        }

        linkingModel.saveChosen(chosenChannels);
        addChannelButton.setDisable(true);
    }

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
        graphModel.getChannels().add(newChannelModel);

        enableChooseOfChannels(dacChannelsListView);
        enableChooseOfChannels(adcChannelsListView);

        statusBarLine.setStatus("Операция успешно выполнена", true);
        linkButton.setDisable(true);
    }

    private ChannelModel createChannel(CheckBox selectedDacChannel, CheckBox selectedAdcChannel) {
        String pairName = selectedDacChannel.getText() + " => " +  selectedAdcChannel.getText();
        return new ChannelModel(pairName);
    }

    private ChannelModel createChannel(CheckBox selectedAdcChannel) {
        String channelName = selectedAdcChannel.getText();
        return new ChannelModel(channelName);
    }

    private Optional<CheckBox> getSelectedCheckBox(ListView<CheckBox> listView) {
        ObservableList<CheckBox> checkBoxes = listView.getItems();

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return Optional.of(checkBox);
            }
        }

        return Optional.empty();
    }

    private void enableChooseOfChannels(ListView<CheckBox> listView) {
        ObservableList<CheckBox> channels = listView.getItems();

        for (CheckBox checkBox : channels) {
            checkBox.setDisable(false);
        }
    }

    public void handleChooseAll() {
        for (CheckBox checkBox : adcChannelsListView.getItems()) {
            checkBox.setSelected(true);
        }
    }

    public void handleBackButton() {
        statusBarLine.toggleProgressIndicator(false);
        statusBarLine.setStatusOfProgress("Загрузка программы испытаний");

        new Thread(() -> {
            addChannelButton.setDisable(true);
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clearStatusBar();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.EXPERIMENT_SCENE));
        }).start();
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public LinkingModel getLinkingModel() {
        return linkingModel;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }

    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
    }
}
