package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.LTR212Table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LTR212TablesRepository extends DataBaseRepository {
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

    public static void insertLTR212Table(LTR212Table ltr212Table) {
        sendAction((ltr212TableDao) -> ltr212TableDao.create(ltr212Table));
    }

    public static void updateLTR212Table(LTR212Table ltr212Table) {
        sendAction((ltr212TableDao) -> ltr212TableDao.update(ltr212Table));
    }

    public static void deleteLTR212Table(LTR212Table ltr212Table) {
        sendAction((ltr212TableDao) -> ltr212TableDao.delete(ltr212Table));
    }

    public static List<LTR212Table> getAllLTR212Tables(){
        final List[] ltr212Tables = {null};
        sendAction((ltr212TablesDao -> ltr212Tables[0] = ltr212TablesDao.queryForAll()));
        return (List<LTR212Table>) ltr212Tables[0];
    }

    public static LTR212Table getLTR212Table(long id) {
        final LTR212Table[] ltr212Table = {null};
        sendAction((ltr212TablesDao -> ltr212Table[0] = ltr212TablesDao.queryForId(id)));
        return ltr212Table[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<LTR212Table, Long> ltr212TablesDao) throws SQLException;
    }

    private static void sendAction(LTR212TablesRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<LTR212Table, Long> ltr212TableDao = DaoManager.createDao(connectionSource, LTR212Table.class);

            actionable.onAction(ltr212TableDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
