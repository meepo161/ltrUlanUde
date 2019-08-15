package ru.avem.posum.models.process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.db.CommandsRepository;

/**
 * Класс для работы с запланированными командами
 */

public class CommandsModel {
    private ObservableList<Command> commands = FXCollections.observableArrayList();

    // Доабавляет команду в список и записывает ее в базу данных
    public void addCommand(long testProgramId, String type, String description) {
        ru.avem.posum.db.models.Command command = new ru.avem.posum.db.models.Command(testProgramId, description, type);
        CommandsRepository.insertCommand(command);
        commands.add(new Command(command.getId(), command.getType(), command.getDescription()));
    }

    // Удаляет команду из базы данных
    public void deleteCommand(Command command) {
        for (ru.avem.posum.db.models.Command dbCommand : CommandsRepository.getAllCommands()) {
            if (dbCommand.getId() == command.getId()) {
                CommandsRepository.deleteCommand(dbCommand);
            }
        }
    }

    // Загружает команду из базы данных
    public void loadCommand(ru.avem.posum.db.models.Command command) {
        commands.add(new Command(command.getId(), command.getType(), command.getDescription()));
    }

    // Возвращает список команд
    public ObservableList<Command> getCommands() {
        return commands;
    }
}
