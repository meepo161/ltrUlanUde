package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.LinkingModel;
import ru.avem.posum.models.Process.PairModel;
import ru.avem.posum.models.Process.ProcessModel;
import ru.avem.posum.utils.StatusBarLine;

import java.util.Optional;

public class LinkingController implements BaseController {
    @FXML
    private ListView<CheckBox> adcChannelsListView;
    @FXML
    private Label checkIcon;
    @FXML
    private ListView<CheckBox> dacChannelsListView;
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

            dacChannelsListView.setItems(linkingModel.getChannelsDescriptions(Crate.LTR34));
            adcChannelsListView.setItems(linkingModel.getChannelsDescriptions(Crate.LTR212, Crate.LTR24));

            listen(dacChannelsListView);
            listen(adcChannelsListView);
        });
    }

    private void listen(ListView<CheckBox> listView) {
        ObservableList<CheckBox> channels = listView.getItems();

        for (CheckBox checkBox : channels) {
            checkBox.selectedProperty().addListener(observable -> {
                for (CheckBox channel : channels) {
                    if (channel != checkBox) {
                        channel.setDisable(checkBox.isSelected());
                    }
                }
            });
        }
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

        selectedDacChannel.ifPresent(checkBox -> dacChannelsListView.getItems().remove(checkBox));
        selectedAdcChannel.ifPresent(checkBox -> adcChannelsListView.getItems().remove(checkBox));

        String pairName = selectedDacChannel.get().getText().split(" \\(")[0] + " - " +
                          selectedAdcChannel.get().getText().split(" \\(")[0];
        processModel.getProcessData().add(new PairModel(pairName));

        enableChooseOfChannels(dacChannelsListView);
        enableChooseOfChannels(adcChannelsListView);

        statusBarLine.setStatus("Операция успешно выполнена", true);
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
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clearStatusBar();
            Platform.runLater(() -> wm.setScene(WindowsManager.Scenes.EXPERIMENT_SCENE));
        }).start();
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
    }
}
