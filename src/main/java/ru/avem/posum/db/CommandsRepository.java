package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsRepository extends DataBaseRepository {
    public static void createTable(Class dataClass) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            TableUtils.dropTable(connectionSource, dataClass, true);
            TableUtils.createTableIfNotExists(connectionSource, dataClass);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Добавляет запланированные команды в базу данных
    public static void insertCommand(Command command) {
        sendAction((commandDao) -> commandDao.create(command));
    }

    // Обновляет запланированные команды в базе данных
    public static void updateCommand(Command command) {
        sendAction((commandDao) -> commandDao.update(command));
    }

    // Удаляет запланированные команды из базы данных
    public static void deleteCommand(Command command) {
        sendAction((commandDao) -> commandDao.delete(command));
    }

    // Возвращает список всех запланированных команд
    public static List<Command> getAllCommands() {
        final List[] command = {null};
        sendAction((commandDao) -> command[0] = commandDao.queryForAll());
        return (List<Command>) command[0];
    }

    // Возвращает запланированные команды с указанным id
    public static List<Command> getCommandsByTestId(long idTest) {
        final List[] command = {null};
        Map<String, Object> map = new HashMap<>();
        map.put("idTest", idTest);
        sendAction((commandDao) -> command[0] = commandDao.queryForFieldValues(map));
        return (List<Command>) command[0];
    }

    public static Command getCommand(long id) {
        final Command[] command = {null};
        sendAction((commandDao) -> command[0] = commandDao.queryForId(id));
        return command[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Command, Long> commandDao) throws SQLException;
    }

    private static void sendAction(CommandsRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Command, Long> commandDao =
                    DaoManager.createDao(connectionSource, Command.class);

            actionable.onAction(commandDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
