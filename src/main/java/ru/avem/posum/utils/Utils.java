package ru.avem.posum.utils;

public class Utils {
    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    public static int parseSlotNumber(String moduleName) {
        return Integer.parseInt(moduleName.split("Слот ")[1].split("\\)")[0]); // номер слота
    }
}
