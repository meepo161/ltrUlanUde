package ru.avem.posum.controllers.Process;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;

import java.util.List;

public interface LinkingManager {
    ObservableList<CheckBox> getChosenChannels();

    ObservableList<Modules> getChosenModules();

    ObservableList<Pair<CheckBox, CheckBox>> getLinkedChannels();

    List<Modules> getLinkedModules();

    void initListViews();

    void setGraphModel();
}
