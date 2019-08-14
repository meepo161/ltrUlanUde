package ru.avem.posum.models.process;

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

/**
 * Модель добавленных и связанных каналов
 */

public class LinkingModel {
    private HashMap<String, List<Pair<Integer, String>>> channelsHashMap = new HashMap<>(); // список связанных каналов
    private ObservableList<CheckBox> chosenChannels = FXCollections.observableArrayList(); // список добавленных каналов
    private HashMap<String, Modules> modulesHashMap = new HashMap<>(); // список задействованных модулей
    private ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = FXCollections.observableArrayList(); // список связанных каналов
    private TestProgram testProgram; // модель программы испытаний

    // Возвращает описания каналов
    public ObservableList<CheckBox> getDescriptionsOfChannels(String... moduleTypes) {
        ObservableList<CheckBox> descriptions = loadDescriptionsOfChannels(moduleTypes);
        checkRemoved(descriptions);

        return descriptions;
    }

    // Загружает описания каналов
    private ObservableList<CheckBox> loadDescriptionsOfChannels(String[] moduleTypes) {
        ObservableList<CheckBox> channels = FXCollections.observableArrayList();

        for (String moduleType : moduleTypes) {
            List<Modules> modules = getModules(moduleType);

            for (Modules module : modules) {
                String moduleName = Modules.getModuleName(module);
                List<Pair<Integer, String>> descriptions = Modules.getChannelsDescriptions(module);

                channelsHashMap.put(moduleName, descriptions);

                for (Pair<Integer, String> description : descriptions) {
                    channels.add(new CheckBox(description.getValue()));
                    modulesHashMap.put(description.getValue(), module); // сохранение описания канала с объектом базы данных
                }
            }
        }

        return channels;
    }

    // Проверяет, какие каналы уже были связаны или добавлены
    private void checkRemoved(ObservableList<CheckBox> channels) {
        List<CheckBox> channelsToRemove = new ArrayList<>();
        // Удаляет из списка связанные каналы ЦАП - АЦП
        for (Pair<CheckBox, CheckBox> descriptions : linkedChannels) {
            String descriptionOfDacChannel = descriptions.getKey().getText();
            String descriptionOfAdcChannel = descriptions.getValue().getText();

            for (CheckBox description : channels) {
                if (description.getText().equals(descriptionOfDacChannel) || description.getText().equals(descriptionOfAdcChannel)) {
                    channelsToRemove.add(description);
                }
            }
        }

        // Удаляет из списка выбранные каналы АЦП
        for (CheckBox channel : chosenChannels) {
            String descriptionOfChannel = channel.getText();

            for (CheckBox description : channels) {
                if (description.getText().equals(descriptionOfChannel)) {
                    channelsToRemove.add(description);
                }
            }
            channels.removeAll(channelsToRemove);
        }
    }

    // Возвращает список задействованных модулей
    private List<Modules> getModules(String modulesType) {
        List<Modules> modules = ModulesRepository.getModules(testProgram.getId());
        List<Modules> outputList = new ArrayList<>();

        for (Modules module : modules) {
            if (module.getModuleType().equals(modulesType) && module.getTestProgramId() == testProgram.getId()) {
                outputList.add(module);
            }
        }

        return outputList;
    }

    // Добавляет в список связанные каналы
    public void saveLinked(Pair<CheckBox, CheckBox> channels) {
        if (!linkedChannels.contains(channels)) {
            linkedChannels.add(channels);
        }
    }

    // Доабвляет в список выбранные каналы
    public void saveChosen(List<CheckBox> channels) {
        for (CheckBox channel : channels) {
            if (!chosenChannels.contains(channel)) {
                chosenChannels.add(channel);
            }
        }
    }

    // Возвращает список модулей, каналы которых были связаны
    public List<Modules> getLinkedModules() {
        List<Modules> modules = new ArrayList<>();

        for (Pair<CheckBox, CheckBox> description : linkedChannels) {
            int dacSlot = Integer.parseInt(description.getKey().getText().split("слот ")[1].split("\\)")[0]);
            int adcSlot = Integer.parseInt(description.getValue().getText().split("слот ")[1].split("\\)")[0]);

            Modules dac = modulesHashMap.get(description.getKey().getText());
            Modules adc = modulesHashMap.get(description.getValue().getText());

            if (!isPresent(dacSlot, modules)) {
                modules.add(dac);
            }

            if (!isPresent(adcSlot, modules)) {
                modules.add(adc);
            }
        }

        return modules;
    }

    // Проверяет, есть ли модуль в списке
    private boolean isPresent(int slot, List<Modules> modules) {
        for (Modules module : modules) {
            if (module.getSlot() == slot) {
                return true;
            }
        }

        return false;
    }

    // Возвращает список модулей, каналы которых были добавлены
    public ObservableList<Modules> getChosenModules() {
        ObservableList<Modules> modules = FXCollections.observableArrayList();

        for (CheckBox channel : chosenChannels) {
            int adcSlot = Integer.parseInt(channel.getText().split("слот ")[1].split("\\)")[0]);
            Modules adc = modulesHashMap.get(channel.getText());

            if (!isPresent(adcSlot, modules)) {
                modules.add(adc);
            }
        }

        return modules;
    }

    // Возвращает список добавленных каналов
    public ObservableList<CheckBox> getChosenChannels() {
        return chosenChannels;
    }

    // Возвращает список связанных каналов
    public ObservableList<Pair<CheckBox, CheckBox>> getLinkedChannels() {
        return linkedChannels;
    }

    // Задает модель программы испытаний
    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
