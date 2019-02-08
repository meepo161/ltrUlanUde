package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.TestProgramm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TestProgrammRepository extends DataBaseRepository {
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

    public static void insertTestProgramm(TestProgramm testProgramm) {
        sendAction((testProgrammDao) -> testProgrammDao.create(testProgramm));
    }

    public static void updateTestProgramm(TestProgramm testProgramm) {
        sendAction((testProgrammDao) -> testProgrammDao.update(testProgramm));
    }

    public static void updateTestProgrammId() {
        List<TestProgramm> testProgramms = getAllTestProgramms();

        for (int i = 0; i < testProgramms.size(); i++) {
            testProgramms.get(i).setTestProgrammId(i + 1);
            updateTestProgramm(testProgramms.get(i));
        }
    }

    public static void deleteTestProgramm(TestProgramm testProgramm) {
        sendAction((testProgrammDao) -> testProgrammDao.delete(testProgramm));
    }

    public static List<TestProgramm> getAllTestProgramms() {
        final List[] testProgramms = {null};
        sendAction((testProgrammDao) -> testProgramms[0] = testProgrammDao.queryForAll());
        return (List<TestProgramm>) testProgramms[0];
    }

    public static TestProgramm getTestProgramm(long id) {
        final TestProgramm[] testProgramm = {null};
        sendAction((testProgrammDao) -> testProgramm[0] = testProgrammDao.queryForId(id));
        return testProgramm[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<TestProgramm, Long> testProgrammDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<TestProgramm, Long> testProgrammDao =
                    DaoManager.createDao(connectionSource, TestProgramm.class);

            actionable.onAction(testProgrammDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
