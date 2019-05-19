package ru.avem.posum.hardware;

import ru.avem.posum.utils.TextEncoder;

public class Process {
    private final int SLOTS = 16; // количество слотов в крейте
    private double[][] data = new double[SLOTS][];
    private String firPath;
    private String iirPath;
    private int ltr34ChannelsCount;
    private int[][] measuringRanges = new int[SLOTS][];
    private String[] operations = new String[SLOTS];
    private int[][] settingsOfModules = new int[SLOTS][];
    private String[] statuses = new String[SLOTS];
    private TextEncoder textEncoder = new TextEncoder();
    private double[][] timeMarks = new double[SLOTS][];
    private int[][] typesOfChannels = new int[SLOTS][];

    public Process() {
        initArrays();
    }

    private void initArrays() {
        for (int stringIndex = 0; stringIndex < statuses.length; stringIndex++) {
            operations[stringIndex] = "";
            statuses[stringIndex] = "";
        }
    }

    public void connect() {
        String crateSerialNumber = "2T364030";
        int[] modulesTypes = {2, 2, 2, 0, 1};
        int[] slots = {1, 19, 3, 8, 11};
        String ltr212bioPath = System.getProperty("user.dir").replace("\\", "/") + "/ltr212.bio";

        openConnection(crateSerialNumber, modulesTypes, slots, ltr212bioPath);
        encodeStatuses();
    }

    private void encodeStatuses() {
        for (int i = 0; i < SLOTS; i++) {
            if (!statuses[i].isEmpty() && !statuses[i].equals("Операция успешно выполнена")) {
                statuses[i] = textEncoder.cp2utf(statuses[i]);
            }
        }
    }

    public boolean isConnected() {
        checkConnection();
        encodeStatuses();

        return checkStatuses();
    }

    private boolean checkStatuses() {
        for (int i = 0; i < SLOTS; i++) {
            if (!statuses[i].isEmpty() && !statuses[i].equals("Операция успешно выполнена")) {
                return false;
            }
        }

        return true;
    }

    public void initialize() {
        initialize(typesOfChannels, measuringRanges, settingsOfModules, firPath, iirPath, ltr34ChannelsCount);
        encodeStatuses();
    }

    public boolean isInitialized() {
        return checkStatuses();
    }

    public void launch() {
        start(data, timeMarks);
    }


    public void finish() {
        stop();
    }

    public void disconnect() {
        closeConnection();
        encodeStatuses();
    }

    public native void openConnection(String crateSerialNumber, int[] modulesTypes, int[] slots, String ltr212biosPath);

    public native void checkConnection();

    public native void initialize(int[][] channelsTypes, int[][] measuringRanges, int[][] moduleSettings,
                                  String firFilePath, String iirFilePath, int ltr34channelsCount);

    public native void start(double[][] data, double[][] timeMarks);

    public native void perform(double[][] data, double[][] timeMarks);

    public native void stop();

    public native void closeConnection();

    static {
        System.loadLibrary("ProcessLibrary");
    }
}
