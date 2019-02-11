package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR212Module;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR212ModuleRepository extends DataBaseRepository {
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

    public static void insertLTR212Module(LTR212Module ltr212Module) {
        sendAction((ltr212ModuleDao) -> ltr212ModuleDao.create(ltr212Module));
    }

    public static void updateLTR212Module(LTR212Module ltr212Module) {
        sendAction((ltr212ModuleDao) -> ltr212ModuleDao.update(ltr212Module));
    }

    public static void deleteLTR212Module(LTR212Module ltr212Module) {
        sendAction((ltr212ModuleDao) -> ltr212ModuleDao.delete(ltr212Module));
    }

    public static List<LTR212Module> getAllLTR212Modules(){
        final List[] ltr212Modules = {null};
        sendAction((ltr212ModulesDao -> ltr212Modules[0] = ltr212ModulesDao.queryForAll()));
        return (List<LTR212Module>) ltr212Modules[0];
    }

    public static LTR212Module getLTR212Module(long id) {
        final LTR212Module[] ltr212Module = {null};
        sendAction((ltr212ModulesDao -> ltr212Module[0] = ltr212ModulesDao.queryForId(id)));
        return ltr212Module[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR212Module, Long> ltr212ModulesDao) throws SQLException;
    }

    private static void sendAction(LTR212ModuleRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR212Module, Long> ltr212ModuleDao = DaoManager.createDao(connectionSource, LTR212Module.class);

            actionable.onAction(ltr212ModuleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
