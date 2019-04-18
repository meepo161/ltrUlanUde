package ru.avem.posum.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.avem.posum.db.models.Account;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AccountRepository extends DataBaseRepository {
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

    public static void insertAccount(Account account) {
        sendAction((accountDao) -> accountDao.create(account));
    }

    public static void updateAccount(Account account) {
        sendAction((accountDao) -> accountDao.update(account));
    }

    public static void deleteAccount(Account account) {
        sendAction((accountDao) -> accountDao.delete(account));
    }

    public static List<Account> getAllAccounts(){
        final List[] accounts = {null};
        sendAction((accountsDao -> accounts[0] = accountsDao.queryForAll()));
        return (List<Account>) accounts[0];
    }

    public static Account getAccount(long id) {
        final Account[] account = {null};
        sendAction((accountsDao -> account[0] = accountsDao.queryForId(id)));
        return account[0];
    }

    @FunctionalInterface
    private interface Actionable {
        void onAction(Dao<Account, Long> accountsDao) throws SQLException;
    }

    private static void sendAction(Actionable actionable) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            Dao<Account, Long> accountDao = DaoManager.createDao(connectionSource, Account.class);

            actionable.onAction(accountDao);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
