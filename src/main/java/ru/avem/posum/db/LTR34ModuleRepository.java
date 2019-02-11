package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR34Module;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR34ModuleRepository extends DataBaseRepository {
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

    public static void insertLTR34Module(LTR34Module ltr34Module) {
        sendAction((ltr34ModuleDao) -> ltr34ModuleDao.create(ltr34Module));
    }

    public static void updateLTR34Module(LTR34Module ltr34Module) {
        sendAction((ltr34ModuleDao) -> ltr34ModuleDao.update(ltr34Module));
    }

    public static void deleteLTR34Module(LTR34Module ltr34Module) {
        sendAction((ltr34ModuleDao) -> ltr34ModuleDao.delete(ltr34Module));
    }

    public static List<LTR34Module> getAllLTR34Modules(){
        final List[] ltr34Modules = {null};
        sendAction((ltr34ModulesDao -> ltr34Modules[0] = ltr34ModulesDao.queryForAll()));
        return (List<LTR34Module>) ltr34Modules[0];
    }

    public static LTR34Module getLTR34Module(long id) {
        final LTR34Module[] ltr34Module = {null};
        sendAction((ltr34ModulesDao -> ltr34Module[0] = ltr34ModulesDao.queryForId(id)));
        return ltr34Module[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR34Module, Long> ltr34ModulesDao) throws SQLException;
    }

    private static void sendAction(LTR34ModuleRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR34Module, Long> ltr34ModuleDao = DaoManager.createDao(connectionSource, LTR34Module.class);

            actionable.onAction(ltr34ModuleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
