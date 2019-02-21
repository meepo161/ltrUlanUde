package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR24Table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR24TablesRepository extends DataBaseRepository {
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

    public static void insertLTR24Table(LTR24Table ltr24Table) {
        sendAction((ltr24TableDao) -> ltr24TableDao.create(ltr24Table));
    }

    public static void updateLTR24Table(LTR24Table ltr24Table) {
        sendAction((ltr24TableDao) -> ltr24TableDao.update(ltr24Table));
    }

    public static void deleteLTR24Table(LTR24Table ltr24Table) {
        sendAction((ltr24TableDao) -> ltr24TableDao.delete(ltr24Table));
    }

    public static List<LTR24Table> getAllLTR24Tables(){
        final List[] ltr24Tables = {null};
        sendAction((ltr24TablesDao -> ltr24Tables[0] = ltr24TablesDao.queryForAll()));
        return (List<LTR24Table>) ltr24Tables[0];
    }

    public static LTR24Table getLTR24Table(long id) {
        final LTR24Table[] ltr24Table = {null};
        sendAction((ltr24TablesDao -> ltr24Table[0] = ltr24TablesDao.queryForId(id)));
        return ltr24Table[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR24Table, Long> ltr24TablesDao) throws SQLException;
    }

    private static void sendAction(LTR24TablesRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR24Table, Long> ltr24TableDao = DaoManager.createDao(connectionSource, LTR24Table.class);

            actionable.onAction(ltr24TableDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
