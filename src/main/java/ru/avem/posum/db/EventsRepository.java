package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Event;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class EventsRepository extends DataBaseRepository {
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

    public static void insertEvent(Event event) {
        sendAction((eventDao) -> eventDao.create(event));
    }

    public static void updateEvent(Event event) {
        sendAction((eventDao) -> eventDao.update(event));
    }

    public static void deleteEvent(Event event) {
        sendAction((eventDao) -> eventDao.delete(event));
    }

    public static List<Event> getAllEvents() {
        final List[] event = {null};
        sendAction((eventDao) -> event[0] = eventDao.queryForAll());
        return (List<Event>) event[0];
    }

    public static List<Event> getEventsByTestId(long idTest) {
        final List[] event = {null};
        Map<String, Object> map = new HashMap<>();
        map.put("idTest", idTest);
        sendAction((eventDao) -> event[0] = eventDao.queryForFieldValues(map));
        return (List<Event>) event[0];
    }

    public static Event getEvent(long id) {
        final Event[] event = {null};
        sendAction((eventDao) -> event[0] = eventDao.queryForId(id));
        return event[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Event, Long> eventDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Event, Long> eventDao =
                    DaoManager.createDao(connectionSource, Event.class);

            actionable.onAction(eventDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
