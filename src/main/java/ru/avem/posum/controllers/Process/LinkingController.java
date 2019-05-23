package ru.avem.posum.controllers.Process;

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
import ru.avem.posum.models.Process.LinkingModel;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.ProcessModel;
import ru.avem.posum.utils.StatusBarLine;

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
    private ProcessModel processModel;
    private StatusBarLine statusBarLine;
    private TestProgram testProgram;
    private WindowsManager wm;

    @FXML
    private void initialize() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon);
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
                processModel.getProcessData().add(newChannelModel);
            }
        }

        linkingModel.saveChosen(chosenChannels);
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
        processModel.getProcessData().add(newChannelModel);

        enableChooseOfChannels(dacChannelsListView);
        enableChooseOfChannels(adcChannelsListView);

        statusBarLine.setStatus("Операция успешно выполнена", true);
        linkButton.setDisable(true);
    }

    private ChannelModel createChannel(CheckBox selectedDacChannel, CheckBox selectedAdcChannel) {
        Modules dacModule = linkingModel.getModule(selectedDacChannel.getText());
        int dacChannel = getChannelNumber(Modules.getModuleName(dacModule), selectedDacChannel.getText());
        double amplitude = Modules.getAmplitude(dacModule, dacChannel);
        double dc = Modules.getDc(dacModule, dacChannel);
        int frequency = Modules.getFrequency(dacModule, dacChannel);
        int phase = Modules.getPhase(dacModule, dacChannel);
        String pairName = selectedDacChannel.getText().split(" \\(")[0] + " -> " +
                selectedAdcChannel.getText().split(" \\(")[0];

        ChannelModel channelModel = new ChannelModel(pairName);
        channelModel.setAmplitude(String.valueOf(amplitude));
        channelModel.setDc(String.valueOf(dc));
        channelModel.setFrequency(String.valueOf(frequency));
        channelModel.setPhase(String.valueOf(phase)); // TODO: change this shit
        channelModel.setPvalue("0");
        channelModel.setIvalue("0");
        channelModel.setDvalue("0");

        return channelModel;
    }

    private ChannelModel createChannel(CheckBox selectedAdcChannel) {
        String channelName = selectedAdcChannel.getText().split(" \\(")[0];

        ChannelModel channelModel = new ChannelModel(channelName);
        channelModel.setAmplitude("0");
        channelModel.setDc("0");
        channelModel.setFrequency("0");
        channelModel.setPhase("0"); // TODO: change this shit
        channelModel.setPvalue("0");
        channelModel.setIvalue("0");
        channelModel.setDvalue("0");

        return channelModel;
    }

    private int getChannelNumber(String moduleName, String channelDescription) {
        HashMap<String, List<Pair<Integer, String>>> channelsHashMap = linkingModel.getChannelsHashMap();
        List<Pair<Integer, String>> descriptions = channelsHashMap.get(moduleName);
        int channel = -1;

        for (Pair<Integer, String> description : descriptions) {
            if (description.getValue().equals(channelDescription)) {
                channel = description.getKey();
            }
        }

        return channel;
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
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public LinkingModel getLinkingModel() {
        return linkingModel;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
    }
}
