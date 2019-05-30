package ru.avem.posum.hardware;

import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private final int SLOTS = 16; // количество слотов в крейте

    private String bioPath = LTR212.getBioPath();
    private int[] channelsCount = new int[SLOTS];
    private String crateSerialNumber;
    private double[][] data = new double[SLOTS][SLOTS];
    private String[] firPaths = new String[SLOTS];
    private String[] iirPaths = new String[SLOTS];
    private int[][] measuringRanges = new int[SLOTS][SLOTS];
    private int[] modulesTypes = new int[SLOTS];
    private String[] operations = new String[SLOTS];
    private int[][] settingsOfModules = new int[SLOTS][SLOTS];
    private int[] slots = new int[SLOTS];
    private String[] statuses = new String[SLOTS];
    private boolean stopped = true;
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
            firPaths[stringIndex] = "";
            iirPaths[stringIndex] = "";
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
        initialize(typesOfChannels, measuringRanges, settingsOfModules, firPaths, iirPaths, channelsCount);
        encodeStatuses();
    }

    public boolean isInitialized() {
        return checkStatuses();
    }

    public void run() {
        start();
        encodeStatuses();
    }

    public boolean isRan() {
        return checkStatuses();
    }

    public void perform() {
        // Эмуляция управления
//        LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
//        Random random = new Random();
//        int randomSignalType = random.nextInt(5);
//        ltr34SettingsModel.calculateSignal(randomSignalType);
//        System.arraycopy(ltr34SettingsModel.getSignal(), 0, data[11], 0, ltr34SettingsModel.getSignal().length);

        perform(data, timeMarks);
    }

    public void initData(List<Modules> modules) {
        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            data[moduleIndex] = new double[modules.get(moduleIndex).getDataLength()];
            timeMarks[moduleIndex] = new double[modules.get(moduleIndex).getDataLength() * 2];
        }
    }

    public void finish() {
        stop();
        disconnect();
    }

    public void disconnect() {
        closeConnection();
        encodeStatuses();
    }

    public boolean isFinished() {
       return checkStatuses();
    }

    public native void openConnection(String crateSerialNumber, int[] modulesTypes, int[] slots, String ltr212biosPath);

    public native void checkConnection();

    public native void initialize(int[][] typeOfChannels, int[][] measuringRanges, int[][] settingsOfModules,
                                  String[] firFilePath, String[] iirFilePath, int[] channelsCount);

    public native void start();

    public native void perform(double[][] data, double[][] timeMarks);

    public native void stop();

    public native void closeConnection();

    static {
        System.loadLibrary("ProcessLibrary");
    }

    public boolean isStopped() {
        return stopped;
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

    public void setFirPaths(List<String> firPaths) {
        int pathCount = firPaths.size();

        for (int pathIndex = 0; pathIndex < pathCount; pathIndex++) {
            this.firPaths[pathIndex] = firPaths.get(pathIndex);
        }
    }

    public void setIirPaths(List<String> iirPaths) {
        int pathCount = iirPaths.size();

        for (int pathIndex = 0; pathIndex < pathCount; pathIndex++) {
            this.iirPaths[pathIndex] = iirPaths.get(pathIndex);
        }
    }

    public void setChannelsCounts(List<Integer> channelsCounts) {
        int size = channelsCounts.size();

        for (int i = 0; i < size; i++) {
            this.channelsCount[i] = channelsCounts.get(i);
        }
    }

    public List<Pair<String, String>> getBadStatus() {
        List<Pair<String, String>> outputList = new ArrayList<>();

        for (int moduleIndex = 0; moduleIndex < SLOTS; moduleIndex++) {
            String status = statuses[moduleIndex];
            if (!status.isEmpty() && !status.equals("Операция успешно выполнена")) {
                outputList.add(new Pair<>(operations[moduleIndex], statuses[moduleIndex]));
            }
        }

        return outputList;
    }

    public double[][] getData() {
        return data;
    }

    public double[] getData(int slot) {
        double[] output = new double[data[slot].length];
        System.arraycopy(data[slot], 0, output, 0, output.length);
        return output;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
