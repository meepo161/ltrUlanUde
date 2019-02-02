package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.TestingSample;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TestingSampleRepository extends DataBaseRepository{
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

    public static void insertTestingSample(TestingSample testingSample) {
        sendAction((testItemDao) -> testItemDao.create(testingSample));
    }

    public static void updateTestingSample(TestingSample testingSample) {
        sendAction((testItemDao) -> testItemDao.update(testingSample));
    }

    public static void deleteTestingSample(TestingSample testingSample) {
        sendAction((testItemDao) -> testItemDao.delete(testingSample));
    }

    public static List<TestingSample> getAllTestingSamples() {
        final List[] testingSamples = {null};
        sendAction((testItemDao) -> testingSamples[0] = testItemDao.queryForAll());
        return (List<TestingSample>) testingSamples[0];
    }

    public static TestingSample getTestingSample(long id) {
        final TestingSample[] testingSample = {null};
        sendAction((testItemDao) -> testingSample[0] = testItemDao.queryForId(id));
        return testingSample[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<TestingSample, Long> testingSamplesDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<TestingSample, Long> testingSampleDao =
                    DaoManager.createDao(connectionSource, TestingSample.class);

            actionable.onAction(testingSampleDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
