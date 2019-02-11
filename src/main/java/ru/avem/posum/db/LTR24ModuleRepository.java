package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR24Module;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR24ModuleRepository extends DataBaseRepository {
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

    public static void insertLTR24Module(LTR24Module ltr24Module) {
        sendAction((ltr24ModuleDao) -> ltr24ModuleDao.create(ltr24Module));
    }

    public static void updateLTR24Module(LTR24Module ltr24Module) {
        sendAction((ltr24ModuleDao) -> ltr24ModuleDao.update(ltr24Module));
    }

    public static void deleteLTR24Module(LTR24Module ltr24Module) {
        sendAction((ltr24ModuleDao) -> ltr24ModuleDao.delete(ltr24Module));
    }

    public static List<LTR24Module> getAllLTR24Modules(){
        final List[] ltr24Modules = {null};
        sendAction((ltr24ModulesDao -> ltr24Modules[0] = ltr24ModulesDao.queryForAll()));
        return (List<LTR24Module>) ltr24Modules[0];
    }

    public static LTR24Module getLTR24Module(long id) {
        final LTR24Module[] ltr24Module = {null};
        sendAction((ltr24ModulesDao -> ltr24Module[0] = ltr24ModulesDao.queryForId(id)));
        return ltr24Module[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR24Module, Long> ltr24ModulesDao) throws SQLException;
    }

    private static void sendAction(LTR24ModuleRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR24Module, Long> ltr24ModuleDao = DaoManager.createDao(connectionSource, LTR24Module.class);

            actionable.onAction(ltr24ModuleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
