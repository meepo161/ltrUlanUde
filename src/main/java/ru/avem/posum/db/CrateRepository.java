package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Crate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CrateRepository extends DataBaseRepository {
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

    public static void insertCrate(Crate crate) {
        sendAction((crateDao) -> crateDao.create(crate));
    }

    public static void updateCrate(Crate crate) {
        sendAction((crateDao) -> crateDao.update(crate));
    }

    public static void deleteCrate(Crate crate) {
        sendAction((crateDao) -> crateDao.delete(crate));
    }

    public static List<Crate> getAllCrates(){
        final List[] crates = {null};
        sendAction((cratesDao -> crates[0] = cratesDao.queryForAll()));
        return (List<Crate>) crates[0];
    }

    public static Crate getCrate(long id) {
        final Crate[] crate = {null};
        sendAction((cratesDao -> crate[0] = cratesDao.queryForId(id)));
        return crate[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Crate, Long> accountsDao) throws SQLException;
    }

    private static void sendAction(CrateRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Crate, Long> crateDao = DaoManager.createDao(connectionSource, Crate.class);

            actionable.onAction(crateDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
