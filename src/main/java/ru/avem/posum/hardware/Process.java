package ru.avem.posum.hardware;

import ru.avem.posum.models.Settings.LTR34SettingsModel;
import ru.avem.posum.utils.TextEncoder;

import java.util.List;
import java.util.Random;

public class Process {
    private final int SLOTS = 16; // количество слотов в крейте

    private String bioPath = LTR212.getBioPath();
    private int[] channelsCount = new int[SLOTS];
    private String crateSerialNumber;
    private double[][] data = new double[SLOTS][SLOTS];
    private String[] firPath = new String[SLOTS];
    private String[] iirPath = new String[SLOTS];
    private int[][] measuringRanges = new int[SLOTS][SLOTS];
    private int[] modulesTypes = new int[SLOTS];
    private String[] operations = new String[SLOTS];
    private int[][] settingsOfModules = new int[SLOTS][SLOTS];
    private int[] slots = new int[SLOTS];
    private String[] statuses = new String[SLOTS];
    private TextEncoder textEncoder = new TextEncoder();
    private double[][] timeMarks = new double[SLOTS][SLOTS];
    private int[][] typesOfChannels = new int[SLOTS][SLOTS];

    public Process() {
        initStrings();
    }

    private void initStrings() {
        for (int stringIndex = 0; stringIndex < statuses.length; stringIndex++) {
            operations[stringIndex] = "";
            statuses[stringIndex] = "";
            firPath[stringIndex] = "";
            iirPath[stringIndex] = "";
        }

        crateSerialNumber = "";
    }

    public void connect() {
        openConnection(crateSerialNumber, modulesTypes, slots, bioPath);
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
        initialize(typesOfChannels, measuringRanges, settingsOfModules, firPath, iirPath, channelsCount);
        encodeStatuses();
    }

    public boolean isInitialized() {
        return checkStatuses();
    }

    public void launch() {
        start();
        encodeStatuses();
    }

    public boolean isLaunched() {
        return checkStatuses();
    }


    public void perform() {
        data[0] = new double[7680 * 4];
        data[1] = new double[7680 * 4];
        data[2] = new double[7680 * 4];
        data[3] = new double[7680 * 4];
        data[4] = new double[7680 * 4];
        data[5] = new double[7680 * 4];
        data[6] = new double[7680 * 4];
        data[7] = new double[117_188 * 4];
        data[8] = new double[117_188 * 4];
        data[9] = new double[117_188 * 4];
        data[10] = new double[117_188 * 4];
        data[11] = new double[31_250];

        timeMarks[0] = new double[data[0].length * 2];
        timeMarks[1] = new double[data[1].length * 2];
        timeMarks[2] = new double[data[2].length * 2];
        timeMarks[3] = new double[data[3].length * 2];
        timeMarks[4] = new double[data[4].length * 2];
        timeMarks[5] = new double[data[5].length * 2];
        timeMarks[6] = new double[data[6].length * 2];
        timeMarks[7] = new double[data[7].length * 2];
        timeMarks[8] = new double[data[8].length * 2];
        timeMarks[9] = new double[data[9].length * 2];
        timeMarks[10] = new double[data[10].length * 2];

        LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
        Random random = new Random();
        int randomSignalType = random.nextInt(5);
        ltr34SettingsModel.calculateSignal(randomSignalType);
        System.arraycopy(ltr34SettingsModel.getSignal(), 0, data[11], 0, ltr34SettingsModel.getSignal().length);

        perform(data, timeMarks);

        System.out.printf("\nLTR212 slot 1. Data[0]: %f. TimeMarks[0]: %f\n", data[0][0], timeMarks[0][0]);
        System.out.printf("LTR212 slot 2. Data[0]: %f. TimeMarks[0]: %f\n", data[1][0], timeMarks[1][0]);
        System.out.printf("LTR212 slot 3. Data[0]: %f. TimeMarks[0]: %f\n", data[2][0], timeMarks[2][0]);
        System.out.printf("LTR212 slot 4. Data[0]: %f. TimeMarks[0]: %f\n", data[3][0], timeMarks[3][0]);
        System.out.printf("LTR212 slot 5. Data[0]: %f. TimeMarks[0]: %f\n", data[4][0], timeMarks[4][0]);
        System.out.printf("LTR212 slot 6. Data[0]: %f. TimeMarks[0]: %f\n", data[5][0], timeMarks[5][0]);
        System.out.printf("LTR212 slot 7. Data[0]: %f. TimeMarks[0]: %f\n", data[6][0], timeMarks[6][0]);
        System.out.printf("LTR24 slot 8. Data[0]: %f. TimeMarks[0]: %f\n", data[7][0], timeMarks[7][0]);
        System.out.printf("LTR24 slot 9. Data[0]: %f. TimeMarks[0]: %f\n", data[8][0], timeMarks[8][0]);
        System.out.printf("LTR24 slot 10. Data[3]: %f. TimeMarks[0]: %f\n", data[9][3], timeMarks[9][0]);
        System.out.printf("LTR24 slot 11. Data[0]: %f. TimeMarks[0]: %f\n", data[10][0], timeMarks[10][0]);
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
                                  String[] firFilePath, String[] iirFilePath, int[] channelsCount);

    public native void start();

    public native void perform(double[][] data, double[][] timeMarks);

    public native void stop();

    public native void closeConnection();

    static {
        System.loadLibrary("ProcessLibrary");
    }

    public void setModulesTypes(List<String> modulesTypes) {
        int modulesCount = modulesTypes.size();
        this.modulesTypes = new int[modulesCount];

        for (int moduleIndex = 0; moduleIndex < modulesCount; moduleIndex++) {
            switch (modulesTypes.get(moduleIndex)) {
                case Crate.LTR24:
                    this.modulesTypes[moduleIndex] = 0;
                    break;
                case Crate.LTR34:
                    this.modulesTypes[moduleIndex] = 1;
                    break;
                case Crate.LTR212:
                    this.modulesTypes[moduleIndex] = 2;
                    break;
            }
        }
    }

    public void setSlots(List<Integer> slots) {
        int slotsCount = slots.size();

        for (int moduleIndex = 0; moduleIndex < slotsCount; moduleIndex++) {
            this.slots[moduleIndex] = slots.get(moduleIndex);
        }
    }

    public void setCrateSerialNumber(String serialNumber) {
        this.crateSerialNumber = serialNumber;
    }

    public void setTypesOfChannels(List<int[]> typesOfChannels) {
        int typesCount = typesOfChannels.size();

        for (int typeIndex = 0; typeIndex < typesCount; typeIndex++) {
            this.typesOfChannels[typeIndex] = typesOfChannels.get(typeIndex);
        }
    }

    public void setMeasuringRanges(List<int[]> measuringRanges) {
        int rangesCount = measuringRanges.size();

        for (int rangeIndex = 0; rangeIndex < rangesCount; rangeIndex++) {
            this.measuringRanges[rangeIndex] = measuringRanges.get(rangeIndex);
        }
    }

    public void setSettingsOfModules(List<int[]> settingsOfModules) {
        int settingsCount = settingsOfModules.size();

        for (int settingsIndex = 0; settingsIndex < settingsCount; settingsIndex++) {
            this.settingsOfModules[settingsIndex] = settingsOfModules.get(settingsIndex);
        }
    }

    public void setFirPath(String[] firPath) {
        this.firPath = firPath;
    }

    public void setIirPath(String[] iirPath) {
        this.iirPath = iirPath;
    }

    public void setChannelsCount(int[] channelsCount) {
        this.channelsCount = channelsCount;
    }
}
