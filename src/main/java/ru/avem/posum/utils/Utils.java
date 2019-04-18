package ru.avem.posum.utils;

public class Utils {
    private static final int DECIMAL_SCALE_LIMIT = 7; // максимальное количество знаков после запятой

    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static String convertFromExponentialFormat(double value, int decimalFormatScale) {
        int scale = (int) Math.log10(decimalFormatScale);
        String convertedValue = String.format("%.7f", value);
        return convertedValue.substring(0, convertedValue.length() - (DECIMAL_SCALE_LIMIT - scale));
    }

    public static String parseModuleType(String moduleName) {
        return moduleName.split(" ")[0];
    }

    public static int parseSlotNumber(String moduleName) {
        return Integer.parseInt(moduleName.split("Слот ")[1].split("\\)")[0]); // номер слота
    }

    public static double roundValue(double value, int rounder) {
        return (double) Math.round(value * rounder) / rounder;
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    public static int getDecimalScaleLimit() {
        return DECIMAL_SCALE_LIMIT;
    }
}
