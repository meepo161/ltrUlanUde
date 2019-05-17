package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Modules;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModulesRepository extends DataBaseRepository {
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

    public static void insertModule(Modules modules) {
        sendAction((modulesDao) -> modulesDao.create(modules));
    }

    public static void updateModules(Modules modules) {
        sendAction((modulesDao) -> modulesDao.update(modules));
    }

    public static void deleteModule(Modules modules) {
        sendAction((modulesDao) -> modulesDao.delete(modules));
    }

    public static List<Modules> getAllModules(){
        final List[] modules = {null};
        sendAction((modulesDao -> modules[0] = modulesDao.queryForAll()));
        return (List<Modules>) modules[0];
    }

    public static List<Modules> getModules(long id) {
        List<Modules> allModules = getAllModules();
        List<Modules> modulesOfTestProgram = new ArrayList<>();

        for (Modules module : allModules) {
            if (module.getTestProgramId() == id) {
                modulesOfTestProgram.add(module);
            }
        }

        return modulesOfTestProgram;
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Modules, Long> modulesDao) throws SQLException;
    }

    private static void sendAction(ModulesRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Modules, Long> modulesDao = DaoManager.createDao(connectionSource, Modules.class);

            actionable.onAction(modulesDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
