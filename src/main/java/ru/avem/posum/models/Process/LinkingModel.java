package ru.avem.posum.models.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import ru.avem.posum.db.ModulesRepository;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.Crate;

import java.util.ArrayList;
import java.util.List;

public class LinkingModel {
    private TestProgram testProgram;

    public ObservableList<CheckBox> getChannelsDescriptions(String... moduleTypes) {
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


    public void setTestProgram(TestProgram testProgram) {
        this.testProgram = testProgram;
    }
}
