package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Channels;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.avem.posum.db.DataBaseRepository.DATABASE_URL;

public class ChannelsRepository {
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

    // Добавляет добавленные и связанные каналы в базу данных
    public static void insertChannel(Channels channel) {
        sendAction((channelDao) -> channelDao.create(channel));
    }

    // Обновляет добавленные и связанные каналы в базе данных
    public static void updateChannel(Channels channel) {
        sendAction((channelDao) -> channelDao.update(channel));
    }

    // Удаляет добавленные и связанные каналы из базы данных
    public static void deleteChannel(Channels channel) {
        sendAction((channelDao) -> channelDao.delete(channel));
    }

    // Возвращает список всех добавленных и связанных каналов
    public static List<Channels> getAllChannels() {
        final List[] channel = {null};
        sendAction((channelDao) -> channel[0] = channelDao.queryForAll());
        return (List<Channels>) channel[0];
    }

    // Возвращает добавленных и связанных каналов с указанным id
    public static List<Channels> getChannelsByTestId(long idTest) {
        final List[] channel = {null};
        Map<String, Object> map = new HashMap<>();
        map.put("idTest", idTest);
        sendAction((channelDao) -> channel[0] = channelDao.queryForFieldValues(map));
        return (List<Channels>) channel[0];
    }

    public static Channels getChannel(long id) {
        final Channels[] channel = {null};
        sendAction((channelDao) -> channel[0] = channelDao.queryForId(id));
        return channel[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Channels, Long> channelDao) throws SQLException;
    }

    private static void sendAction(ChannelsRepository.Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Channels, Long> channelDao =
                    DaoManager.createDao(connectionSource, Channels.class);

            actionable.onAction(channelDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
