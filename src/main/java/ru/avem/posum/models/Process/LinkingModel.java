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
import java.util.HashMap;
import java.util.List;

public class LinkingModel {
    private HashMap<String, List<Pair<Integer, String>>> channelsHashMap = new HashMap<>();
    private List<Pair<Integer, String>> descriptions;
    private HashMap<String, Modules> modulesHashMap = new HashMap<>();
    private ObservableList<Pair<CheckBox, CheckBox>> removedDescriptions = FXCollections.observableArrayList();
    private TestProgram testProgram;

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
                String moduleName = Modules.getModuleName(module);
                descriptions = Modules.getChannelsDescriptions(module);

                channelsHashMap.put(moduleName, descriptions);

                for (Pair<Integer, String> description : descriptions) {
                    channels.add(new CheckBox(description.getValue()));
                    modulesHashMap.put(description.getValue(), module); // сохранение описания канала с объектом базы данных
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

    public HashMap<String, List<Pair<Integer, String>>> getChannelsHashMap() {
        return channelsHashMap;
    }

    public Modules getModule(String channelDescription) {
        return modulesHashMap.get(channelDescription);
    }

    public ObservableList<Pair<CheckBox, CheckBox>> getRemovedDescriptions() {
        return removedDescriptions;
    }

    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
