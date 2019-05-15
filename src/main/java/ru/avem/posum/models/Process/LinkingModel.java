package ru.avem.posum.models.Process;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import ru.avem.posum.db.ModulesRepository;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;

import java.util.ArrayList;
import java.util.List;

public class LinkingModel {
    private TestProgram testProgram;
    private ObservableList<Pair<CheckBox, CheckBox>> removedDescriptions = FXCollections.observableArrayList();

    public ObservableList<CheckBox> getDescriptionsOfChannels(String... moduleTypes) {
        ObservableList<CheckBox> descriptions = loadDescriptionsOfChannels(moduleTypes);
        checkRemoved(descriptions);

        return descriptions;
    }

    private ObservableList<CheckBox> loadDescriptionsOfChannels(String[] moduleTypes) {
        ObservableList<CheckBox> channels = FXCollections.observableArrayList();

        for (String moduleType : moduleTypes) {
            List<Modules> modules = getModules(moduleType);

            for (Modules module : modules) {
                List<String> descriptions = Modules.getChannelsDescriptions(module);

                for (String description : descriptions) {
                    channels.add(new CheckBox(description));
                }
            }
        }

        return channels;
    }

    private void checkRemoved(ObservableList<CheckBox> channels) {
        for (Pair<CheckBox, CheckBox> descriptions : removedDescriptions) {
            String descriptionOfDacChannel = descriptions.getKey().getText();
            String descriptionOfAdcChannel = descriptions.getValue().getText();

            for (CheckBox description : channels) {
                if (description.getText().equals(descriptionOfDacChannel) || description.getText().equals(descriptionOfAdcChannel)) {
                    Platform.runLater(() -> channels.remove(description));
                }
            }
        }
    }

    private List<Modules> getModules(String modulesType) {
        List<Modules> modules = ModulesRepository.getModules(testProgram.getId());
        List<Modules> outputList = new ArrayList<>();

        for (Modules module : modules) {
            if (module.getModuleType().equals(modulesType)) {
                outputList.add(module);
            }
        }

        return outputList;
    }

    public void removeChannelsDescriptions(Pair<CheckBox, CheckBox> descriptions) {
        if (!removedDescriptions.contains(descriptions)) {
            removedDescriptions.add(descriptions);
        }
    }

    public ObservableList<Pair<CheckBox, CheckBox>> getRemovedDescriptions() {
        return removedDescriptions;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
