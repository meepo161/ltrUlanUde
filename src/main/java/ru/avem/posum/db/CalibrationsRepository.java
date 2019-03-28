package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Calibration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CalibrationsRepository extends DataBaseRepository {
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

    public static void insertCalibration(Calibration calibration) {
        sendAction((calibrationDao) -> calibrationDao.create(calibration));
    }

    public static void updateCalibration(Calibration calibration) {
        sendAction((calibrationDao) -> calibrationDao.update(calibration));
    }

    public static void deleteCalibration(Calibration calibration) {
        sendAction((calibrationDao) -> calibrationDao.delete(calibration));
    }

    public static List<Calibration> getAllCalibrations(){
        final List[] calibrations = {null};
        sendAction((calibrationsDao -> calibrations[0] = calibrationsDao.queryForAll()));
        return (List<Calibration>) calibrations[0];
    }

    public static Calibration getCalibration(long id) {
        final Calibration[] calibration = {null};
        sendAction((calibrationsDao -> calibration[0] = calibrationsDao.queryForId(id)));
        return calibration[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Calibration, Long> calibrationsDao) throws SQLException;
    }

    private static void sendAction(CalibrationsRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Calibration, Long> calibrationDao = DaoManager.createDao(connectionSource, Calibration.class);

            actionable.onAction(calibrationDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
