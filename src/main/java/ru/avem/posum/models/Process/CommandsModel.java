package ru.avem.posum.models.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.db.CommandsRepository;

public class CommandsModel {
    private ObservableList<Command> commands = FXCollections.observableArrayList();

    public void addCommand(long testProgramId, String type, String description) {
        ru.avem.posum.db.models.Command command = new ru.avem.posum.db.models.Command(testProgramId, description, type.toString());
        CommandsRepository.insertCommand(command);
        commands.add(new Command(command.getType(), command.getDescription()));
    }

    public ObservableList<Command> getCommands() {
        return commands;
    }
}
