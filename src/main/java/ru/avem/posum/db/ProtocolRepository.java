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

public class ProtocolRepository extends DataBaseRepository {
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

    public static void insertProtocol(TestProgramm testProgramm) {
        sendAction((protocolDao) -> protocolDao.create(testProgramm));
    }

    public static void updateProtocol(TestProgramm testProgramm) {
        sendAction((protocolDao) -> protocolDao.update(testProgramm));
    }

    public static void updateProtocolIndex() {
        List<TestProgramm> testProgramms = getAllProtocols();

        for (int i = 0; i < testProgramms.size(); i++) {
            testProgramms.get(i).setIndex(i + 1);
            updateProtocol(testProgramms.get(i));
        }
    }

    public static void deleteProtocol(TestProgramm testProgramm) {
        sendAction((protocolDao) -> protocolDao.delete(testProgramm));
    }

    public static List<TestProgramm> getAllProtocols() {
        final List[] protocols = {null};
        sendAction((protocolDao) -> protocols[0] = protocolDao.queryForAll());
        return (List<TestProgramm>) protocols[0];
    }

    public static TestProgramm getProtocol(long id) {
        final TestProgramm[] testProgramm = {null};
        sendAction((protocolDao) -> testProgramm[0] = protocolDao.queryForId(id));
        return testProgramm[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<TestProgramm, Long> protocolDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<TestProgramm, Long> protocolDao =
                    DaoManager.createDao(connectionSource, TestProgramm.class);

            actionable.onAction(protocolDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
