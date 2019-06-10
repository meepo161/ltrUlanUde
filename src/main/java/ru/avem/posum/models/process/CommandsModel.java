package ru.avem.posum.models.process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.db.CommandsRepository;

public class CommandsModel {
    private ObservableList<Command> commands = FXCollections.observableArrayList();

    public void addCommand(long testProgramId, String type, String description) {
        ru.avem.posum.db.models.Command command = new ru.avem.posum.db.models.Command(testProgramId, description, type);
        CommandsRepository.insertCommand(command);
        commands.add(new Command(command.getId(), command.getType(), command.getDescription()));
    }

    public void deleteCommand(Command command) {
        for (ru.avem.posum.db.models.Command dbCommand : CommandsRepository.getAllCommands()) {
            if (dbCommand.getId() == command.getId()) {
                CommandsRepository.deleteCommand(dbCommand);
            }
        }
    }

    public void loadCommand(ru.avem.posum.db.models.Command command) {
        commands.add(new Command(command.getId(), command.getType(), command.getDescription()));
    }

    public ObservableList<Command> getCommands() {
        return commands;
    }
}
