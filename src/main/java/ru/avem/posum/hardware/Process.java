package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class Process {
    private final int SLOTS = 16;
    private String[] operations = new String[SLOTS];
    private String[] statuses = new String[SLOTS];
    private TextEncoder textEncoder = new TextEncoder();

    public Process() {
        initArrays();
    }

    private void initArrays() {
        for (int stringIndex = 0; stringIndex < statuses.length; stringIndex++) {
            operations[stringIndex] = "";
            statuses[stringIndex] = "";
        }
    }

    public void openConnection() {
        String crateSerialNumber = "2T364030";
        int[] modulesTypes = {2, 2, 2, 0, 1};
        int[] slots = {1, 19, 3, 8, 11};
        String ltr212bioPath = System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio";

        openConnection(crateSerialNumber, modulesTypes, slots, ltr212bioPath);
        checkStatuses();
    }

    private void checkStatuses() {
        for (int i = 0; i < SLOTS; i++) {
            if (!statuses[i].equals("Операция успешно выполнена")) {
                statuses[i] = textEncoder.cp2utf(statuses[i]);
            }
        }
    }

    public native void openConnection(String crateSerialNumber, int[] modulesTypes, int[] slots, String ltr212biosPath);

//    public native String checkConnection(int[] slots);

//    public native String initialize(int[] slots, int[][] channelsTypes, int[][] measuringRanges, int[][] moduleSettings, String firFilePath, String iirFilePath);

//    public native String start(double[] signal);

//    public native String stop();

//    public native String closeConnection(int[] slot);

    static {
        System.loadLibrary("ProcessLibrary");
    }
}
