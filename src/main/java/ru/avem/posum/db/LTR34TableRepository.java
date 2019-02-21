package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR34Table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR34TableRepository extends DataBaseRepository {
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

    public static void insertLTR34Module(LTR34Table ltr34Table) {
        sendAction((ltr34TableDao) -> ltr34TableDao.create(ltr34Table));
    }

    public static void updateLTR34Table(LTR34Table ltr34Table) {
        sendAction((ltr34TableDao) -> ltr34TableDao.update(ltr34Table));
    }

    public static void deleteLTR34Table(LTR34Table ltr34Table) {
        sendAction((ltr34TableDao) -> ltr34TableDao.delete(ltr34Table));
    }

    public static List<LTR34Table> getAllLTR34Tables(){
        final List[] ltr34Tables = {null};
        sendAction((ltr34TablesDao -> ltr34Tables[0] = ltr34TablesDao.queryForAll()));
        return (List<LTR34Table>) ltr34Tables[0];
    }

    public static LTR34Table getLTR34Module(long id) {
        final LTR34Table[] ltr34Table = {null};
        sendAction((ltr34ModulesDao -> ltr34Table[0] = ltr34ModulesDao.queryForId(id)));
        return ltr34Table[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR34Table, Long> ltr34ModulesDao) throws SQLException;
    }

    private static void sendAction(LTR34TableRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR34Table, Long> ltr34ModuleDao = DaoManager.createDao(connectionSource, LTR34Table.class);

            actionable.onAction(ltr34ModuleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
