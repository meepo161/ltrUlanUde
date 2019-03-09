package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Module;

import java.io.IOException;
import java.sql.SQLException;
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

    public static void insertModule(Module module) {
        sendAction((moduleDao) -> moduleDao.create(module));
    }

    public static void updateModule(Module module) {
        sendAction((moduleDao) -> moduleDao.update(module));
    }

    public static void deleteModule(Module module) {
        sendAction((moduleDao) -> moduleDao.delete(module));
    }

    public static List<Module> getAllModules(){
        final List[] modules = {null};
        sendAction((modulesDao -> modules[0] = modulesDao.queryForAll()));
        return (List<Module>) modules[0];
    }

    public static Module getModule(long id) {
        final Module[] module = {null};
        sendAction((modulesDao -> module[0] = modulesDao.queryForId(id)));
        return module[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Module, Long> modulesDao) throws SQLException;
    }

    private static void sendAction(ModulesRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Module, Long> moduleDao = DaoManager.createDao(connectionSource, Module.class);

            actionable.onAction(moduleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
