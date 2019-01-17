package ru.avem.posum.db;

import ru.avem.posum.db.models.Account;
import ru.avem.posum.db.models.Protocol;
import ru.avem.posum.db.models.TestingSample;
import ru.avem.posum.db.models.Event;

import java.io.File;

public class DataBaseRepository {
    public static String DATABASE_NAME = "posum.db";
    protected static String DATABASE_URL = "jdbc:" + "sqlite:" + DATABASE_NAME;

    public static void init(boolean forceInit) {
        if (!new File(DATABASE_NAME).exists() || forceInit) {
            AccountRepository.createTable(Account.class);

            Account admin = new Account("admin", "102030");
            AccountRepository.insertAccount(admin);

            Account user = new Account("user", "user");
            AccountRepository.insertAccount(user);

            TestingSample testingSampleN1 = new TestingSample("Внешний подвес вертолета Ми-8", "11011");
            TestingSample testingSampleN2 = new TestingSample("Внешний подвес вертолета Ми-17", "13015");

            TestingSampleRepository.createTable(TestingSample.class);
            TestingSampleRepository.insertTestingSample(testingSampleN1);
            TestingSampleRepository.insertTestingSample(testingSampleN2);

            ProtocolRepository.createTable(Protocol.class);

            Protocol protocol = new Protocol(testingSampleN1);

            EventRepository.createTable(Event.class);

            Event event = new Event(0, "База создана");
            EventRepository.insertEvent(event);

        }
    }
}
