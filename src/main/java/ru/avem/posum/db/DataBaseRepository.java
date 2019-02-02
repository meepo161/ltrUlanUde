package ru.avem.posum.db;

import ru.avem.posum.db.models.*;

import java.io.File;

public class DataBaseRepository {
    private static String DATABASE_NAME = "posum.db";
    protected static String DATABASE_URL = "jdbc:" + "sqlite:" + DATABASE_NAME;

    public static void init(boolean forceInit) {
        if (!new File(DATABASE_NAME).exists() || forceInit) {
            AccountRepository.createTable(Account.class);

            Account admin = new Account("admin", "102030");
            AccountRepository.insertAccount(admin);

            Account user = new Account("user", "user");
            AccountRepository.insertAccount(user);

            TestingSampleRepository.createTable(TestingSample.class);

            ProtocolRepository.createTable(Protocol.class);

            EventRepository.createTable(Event.class);

            Event event = new Event(0, "База создана");
            EventRepository.insertEvent(event);

        }
    }
}
