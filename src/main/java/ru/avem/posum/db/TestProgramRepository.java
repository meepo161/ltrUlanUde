package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.TestProgram;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TestProgramRepository extends DataBaseRepository {
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

    public static void insertTestProgram(TestProgram testProgram) {
        sendAction((testProgramDao) -> testProgramDao.create(testProgram));
    }

    public static void updateTestProgram(TestProgram testProgram) {
        sendAction((testProgramDao) -> testProgramDao.update(testProgram));
    }

    public static void updateTestProgramId() {
        List<TestProgram> testPrograms = getAllTestPrograms();

        for (int i = 0; i < testPrograms.size(); i++) {
            testPrograms.get(i).setIndex(i + 1);
            updateTestProgram(testPrograms.get(i));
        }
    }

    public static void deleteTestProgram(TestProgram testProgram) {
        sendAction((testProgramDao) -> testProgramDao.delete(testProgram));
    }

    public static List<TestProgram> getAllTestPrograms() {
        final List[] testPrograms = {null};
        sendAction((testProgramDao) -> testPrograms[0] = testProgramDao.queryForAll());
        return (List<TestProgram>) testPrograms[0];
    }

    public static TestProgram getTestProgramm(long id) {
        final TestProgram[] testProgram = {null};
        sendAction((testProgramDao) -> testProgram[0] = testProgramDao.queryForId(id));
        return testProgram[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<TestProgram, Long> testProgramDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<TestProgram, Long> testProgramDao =
                    DaoManager.createDao(connectionSource, TestProgram.class);

            actionable.onAction(testProgramDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
