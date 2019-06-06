package ru.avem.posum.db;

import ru.avem.posum.db.models.*;

import java.io.File;

public class DataBaseRepository {
    private static String DATABASE_NAME = "posum.db";
    protected static String DATABASE_URL = "jdbc:" + "sqlite:" + DATABASE_NAME;

    public static void init(boolean forceInit) {
        if (!new File(DATABASE_NAME).exists() || forceInit) {
            AccountRepository.createTable(Account.class);
            CalibrationsRepository.createTable(Calibration.class);
            CommandsRepository.createTable(Command.class);
            EventRepository.createTable(Event.class);
            ModulesRepository.createTable(Modules.class);
            TestProgramRepository.createTable(TestProgram.class);

            Account admin = new Account("admin", "AS45TR8");
            AccountRepository.insertAccount(admin);

            Account user = new Account("user", "user");
            AccountRepository.insertAccount(user);
        }
    }
}
