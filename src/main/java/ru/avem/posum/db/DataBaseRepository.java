package ru.avem.posum.db;

import ru.avem.posum.db.models.*;

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
            Experiment experimentN1 = new Experiment("Испытания внешнего подвеса вертолета Ми-8", "Подвес", "120021", "№342", "Тестовые испытания", "01:00:00", "18:01:2019", "Петрович", "Hello world!");
            Experiment experimentN2 = new Experiment("Испытания внешнего подвеса вертолета Ми-17", "Подвес", "540321", "№356", "Тестовые испытания", "01:00:00", "18:01:2019", "Иванович", "Bye world!");

            TestingSampleRepository.createTable(TestingSample.class);
            TestingSampleRepository.insertTestingSample(testingSampleN1);
            TestingSampleRepository.insertTestingSample(testingSampleN2);

            ProtocolRepository.createTable(Protocol.class);

            Protocol protocolN1 = new Protocol(experimentN1);
            Protocol protocolN2 = new Protocol(experimentN2);
            ProtocolRepository.insertProtocol(protocolN1);
            ProtocolRepository.insertProtocol(protocolN2);

            EventRepository.createTable(Event.class);

            Event event = new Event(0, "База создана");
            EventRepository.insertEvent(event);

        }
    }
}
