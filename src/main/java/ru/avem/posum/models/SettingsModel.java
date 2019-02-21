package ru.avem.posum.models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.db.LTR212ModuleRepository;
import ru.avem.posum.db.LTR24TablesRepository;
import ru.avem.posum.db.LTR34ModuleRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR212Module;
import ru.avem.posum.db.models.LTR24Table;
import ru.avem.posum.db.models.LTR34Module;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.CrateModel;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.hardware.LTR34;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsModel {
    private String crate;
    private int selectedCrate;
    private boolean isEditMode;
    private CrateModel crateModel;
    private TestProgram testProgram;
    private ObservableList<String> modulesNames;

    public void createModulesInstances(CrateModel crateModel) {
        this.crateModel = crateModel;
        crate = crateModel.getCrates()[0][selectedCrate]; // серийный номер крейта
        modulesNames = crateModel.getModulesNames();

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    ltr24.setCrate(crate);
                    ltr24.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR24> ltr24Pair = new Pair<>(i, ltr24);
                    crateModel.getLtr24ModulesList().add(ltr24Pair);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = new LTR34();
                    ltr34.setCrate(crate);
                    ltr34.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
                    crateModel.getLtr34ModulesList().add(ltr34Pair);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    ltr212.setCrate(crate);
                    ltr212.setSlot(parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
                    crateModel.getLtr212ModulesList().add(ltr212Pair);
                    break;
            }
        }
    }

    public void loadChannelsSettings(TestProgram testProgram, CrateModel crateModel, ObservableList<String> modulesNames, int selectedCrate) {
        this.testProgram = testProgram;
        this.selectedCrate = selectedCrate;
        this.crateModel = crateModel;
        this.modulesNames = modulesNames;

        crateModel.fillModulesNames(selectedCrate);
        saveModulesSettings(crateModel);
    }

    private void saveModulesSettings(CrateModel crateModel) {
        List<LTR24Table> ltr24Tables = fillLTR24ModulesList();
        List<LTR34Module> ltr34Modules = fillLTR34ModulesList();
        List<LTR212Module> ltr212Modules = fillLTR212ModulesList();

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    setLTR24ChannelsSettings(ltr24, ltr24Tables, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR24> ltr24Pair = new Pair<>(i, ltr24);
                    crateModel.getLtr24ModulesList().add(ltr24Pair);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = new LTR34();
                    setLTR34ChannelsSettings(ltr34, ltr34Modules, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
                    crateModel.getLtr34ModulesList().add(ltr34Pair);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    setLTR212ChannelsSettings(ltr212, ltr212Modules, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
                    crateModel.getLtr212ModulesList().add(ltr212Pair);
                    break;
            }
        }
    }

    private List<LTR24Table> fillLTR24ModulesList() {
        List<LTR24Table> ltr24Tables = new ArrayList<>();

        for (LTR24Table module : LTR24TablesRepository.getAllLTR24Tables()) {
            if (module.getTestProgramId() == testProgram.getTestProgramId()) {
                ltr24Tables.add(module);
            }
        }
        return ltr24Tables;
    }

    private List<LTR34Module> fillLTR34ModulesList() {
        List<LTR34Module> ltr34Modules = new ArrayList<>();

        for (LTR34Module module : LTR34ModuleRepository.getAllLTR34Modules()) {
            if (module.getTestProgrammId() == testProgram.getTestProgramId()) {
                ltr34Modules.add(module);
            }
        }
        return ltr34Modules;
    }

    private List<LTR212Module> fillLTR212ModulesList() {
        List<LTR212Module> ltr212Modules = new ArrayList<>();

        for (LTR212Module module : LTR212ModuleRepository.getAllLTR212Modules()) {
            if (module.getTestProgrammId() == testProgram.getTestProgramId()) {
                ltr212Modules.add(module);
            }
        }
        return ltr212Modules;
    }

    public int parseSlotNumber(String module) {
        return Integer.parseInt(module.split("Слот ")[1].split("\\)")[0]); // номер слота
    }

    private void setLTR24ChannelsSettings(LTR24 ltr24, List<LTR24Table> ltr24Tables, int slot) {
        boolean[] checkedChannels = ltr24.getCheckedChannels();
        int parsedSlot;
        int[] channelsTypes = ltr24.getChannelsTypes();
        int[] measuringRanges = ltr24.getMeasuringRanges();
        String[] channelsDescription = ltr24.getChannelsDescription();
        String[] calibrationSettings = ltr24.getCalibrationSettings();

        for (LTR24Table ltr24Table : ltr24Tables) {
            parsedSlot = Integer.parseInt(ltr24Table.getSlot());
            if (slot == parsedSlot) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr24Table.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr24Table.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr24Table.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr24Table.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                calibrationSettings[0] = ltr24Table.getCalibrationOfChannelN1();
                calibrationSettings[1] = ltr24Table.getCalibrationOfChannelN2();
                calibrationSettings[2] = ltr24Table.getCalibrationOfChannelN3();
                calibrationSettings[3] = ltr24Table.getCalibrationOfChannelN4();

                ltr24.setCrate(ltr24Table.getCrate());
                ltr24.setSlot(parsedSlot);
            }
        }
    }

    private void setLTR34ChannelsSettings(LTR34 ltr34, List<LTR34Module> ltr34Modules, int slot) {
        boolean[] checkedChannels = ltr34.getCheckedChannels();
        int[][] channelsParameters = ltr34.getChannelsParameters();

        for (LTR34Module ltr34Module : ltr34Modules) {
            if (slot == ltr34Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr34Module.getCheckedChannels().split(", ", 9)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsParameters[0][i] = Integer.parseInt(ltr34Module.getChannelsAmplitude().split(", ", 9)[i]);
                        channelsParameters[1][i] = Integer.parseInt(ltr34Module.getChannelsFrequency().split(", ", 9)[i]);
                        channelsParameters[2][i] = Integer.parseInt(ltr34Module.getChannelsPhase().split(", ", 9)[i]);
                    }
                }

                ltr34.setCrate(ltr34Module.getCrate());
                ltr34.setSlot(ltr34Module.getSlot());
            }
        }
    }

    private void setLTR212ChannelsSettings(LTR212 ltr212, List<LTR212Module> ltr212Modules, int slot) {
        boolean[] checkedChannels = ltr212.getCheckedChannels();
        int[] channelsTypes = ltr212.getChannelsTypes();
        int[] measuringRanges = ltr212.getMeasuringRanges();
        String[] channelsDescription = ltr212.getChannelsDescription();

        for (LTR212Module ltr212Module : ltr212Modules) {
            if (slot == ltr212Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr212Module.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr212Module.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr212Module.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr212Module.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                ltr212.setCrate(ltr212Module.getCrate());
                ltr212.setSlot(ltr212Module.getSlot());
            }
        }
    }

    public void saveGeneralSettings(HashMap<String, String> generalSettings, boolean isEditMode) {
        this.isEditMode = isEditMode;

        /* сохранение общих данных */
        String testProgramName = generalSettings.get("Test Program Name");
        String sampleName = generalSettings.get("Sample Name");
        String sampleSerialNumber = generalSettings.get("Sample Serial Number");
        String documentNumber = generalSettings.get("Document Number");
        String testProgramType = generalSettings.get("Test Program Type");
        String testProgramTime = generalSettings.get("Test Program Time");
        String testProgramDate = generalSettings.get("Test Program Date");
        String leadEngineer = generalSettings.get("Lead Engineer");
        String comments = generalSettings.get("Comments");
        String crate = generalSettings.get("Crate Serial Number");

        if (isEditMode) {
            testProgram.setTestProgramName(testProgramName);
            testProgram.setSampleName(sampleName);
            testProgram.setSampleSerialNumber(sampleSerialNumber);
            testProgram.setDocumentNumber(documentNumber);
            testProgram.setTestProgramType(testProgramType);
            testProgram.setTestProgramTime(testProgramTime);
            testProgram.setTestProgramDate(testProgramDate);
            testProgram.setLeadEngineer(leadEngineer);
            testProgram.setComments(comments);

            TestProgramRepository.updateTestProgram(testProgram);
        } else {
            testProgram = new TestProgram(
                    crate,
                    testProgramName,
                    sampleName,
                    sampleSerialNumber,
                    documentNumber,
                    testProgramType,
                    testProgramTime,
                    testProgramDate,
                    leadEngineer,
                    comments);

            TestProgramRepository.insertTestProgram(testProgram);
        }
    }

    public void saveHardwareSettings(boolean isEditMode) {
        this.isEditMode = isEditMode;
        int testProgramId = testProgram.getTestProgramId();
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (String modulesName : modulesNames) {
            switch (modulesName.split(" ")[0]) {
                case CrateModel.LTR24:
                    updateLTR24Table(testProgramId, ltr24Index++);
                    break;
                case CrateModel.LTR34:
                    updateLTR34Settings(testProgramId, ltr34Index++);
                    break;
                case CrateModel.LTR212:
                    updateLTR212Settings(testProgramId, ltr212Index++);
                    break;
            }
        }
    }

    private void updateLTR24Table(int testProgramId, int ltr24Index) {
        LTR24 ltr24 = crateModel.getLtr24ModulesList().get(ltr24Index).getValue();
        int channels = 4; // количество каналов
        String[][] moduleSettings = new String[7][channels];
        String[] calibrationSettings = ltr24.getCalibrationSettings();
        boolean[] checkedChannels = ltr24.getCheckedChannels();
        int[] channelsTypes = ltr24.getChannelsTypes();
        int[] measuringRanges = ltr24.getMeasuringRanges();
        String[] channelsDescription = ltr24.getChannelsDescription();

        moduleSettings[0][0] = ltr24.getCrate(); // серийный номер крейта
        moduleSettings[1][0] = String.valueOf(ltr24.getSlot()); // номер слота
        for (int i = 0; i < channels; i++) {
            if (checkedChannels[i]) {
                moduleSettings[2][i] = "1"; // канал отмечен
                moduleSettings[3][i] = String.valueOf(channelsTypes[i]);
                moduleSettings[4][i] = String.valueOf(measuringRanges[i]);
                moduleSettings[5][i] = String.valueOf(channelsDescription[i]);
                moduleSettings[6][i] = calibrationSettings[i];
            } else {
                moduleSettings[2][i] = "0"; // канал не отмечен
                moduleSettings[3][i] = "0";
                moduleSettings[4][i] = "0";
                moduleSettings[5][i] = "0";
                moduleSettings[6][i] = "0.0, 0.0, 0.0, 0.0, В";
            }
        }

        if (isEditMode) {
            for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
                if (ltr24Table.getTestProgramId() == testProgramId && Integer.parseInt(ltr24Table.getSlot()) == ltr24.getSlot()) {
                    ltr24Table.setCrate(moduleSettings[0][0]);
                    ltr24Table.setSlot(moduleSettings[1][0]);
                    ltr24Table.setCheckedChannels(LTR24Table.settingsToString(moduleSettings[2]));
                    ltr24Table.setChannelsTypes(LTR24Table.settingsToString(moduleSettings[3]));
                    ltr24Table.setMeasuringRanges(LTR24Table.settingsToString(moduleSettings[4]));
                    ltr24Table.setChannelsDescription(LTR24Table.settingsToString(moduleSettings[5]));
                    ltr24Table.setCalibrationOfChannelN1(ltr24.getCalibrationSettings()[0]);
                    ltr24Table.setCalibrationOfChannelN2(ltr24.getCalibrationSettings()[1]);
                    ltr24Table.setCalibrationOfChannelN3(ltr24.getCalibrationSettings()[2]);
                    ltr24Table.setCalibrationOfChannelN4(ltr24.getCalibrationSettings()[3]);
                    LTR24TablesRepository.updateLTR24Module(ltr24Table);
                }
            }
        } else {
            LTR24Table newLTR24Table = new LTR24Table(testProgramId, moduleSettings);
            LTR24TablesRepository.insertLTR24Table(newLTR24Table);
        }

    }

    private void updateLTR212Settings(int testProgramId, int ltr212Index) {
        LTR212 ltr212 = crateModel.getLtr212ModulesList().get(ltr212Index).getValue();
        boolean[] checkedChannels = ltr212.getCheckedChannels();

        if (isEditMode) {
            for (LTR212Module ltr212Module : LTR212ModuleRepository.getAllLTR212Modules()) {
                if (ltr212Module.getTestProgrammId() == testProgramId && ltr212Module.getSlot() == ltr212.getSlot()) {
                    updateLTR212Data(ltr212, checkedChannels, ltr212Module);
                }
            }
        } else {
            createLTR212Module(ltr212);
        }
    }

    private void updateLTR34Settings(int testProgramId, int ltr34Index) {
        LTR34 ltr34 = crateModel.getLtr34ModulesList().get(ltr34Index).getValue();
        boolean[] checkedChannels = ltr34.getCheckedChannels();

        if (isEditMode) {
            for (LTR34Module ltr34Module : LTR34ModuleRepository.getAllLTR34Modules()) {
                if (ltr34Module.getTestProgrammId() == testProgramId && ltr34Module.getSlot() == ltr34.getSlot()) {
                    updateLTR34Data(ltr34, checkedChannels, ltr34Module);
                }
            }
        } else {
            createLTR34Module(ltr34);
        }
    }

    private void updateLTR34Data(LTR34 ltr34, boolean[] checkedChannels, LTR34Module ltr34Module) {
        int[][] channelsParameters = ltr34.getChannelsParameters();
        String channels = "";
        String amplitudes = "";
        String frequencies = "";
        String phases = "";

        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channels += 1 + ", ";
                amplitudes += channelsParameters[0][i] + ", ";
                frequencies += channelsParameters[1][i] + ", ";
                phases += channelsParameters[2][i] + ", ";
            } else {
                channels += 0 + ", ";
                amplitudes += 0 + ", ";
                frequencies += 0 + ", ";
                phases += 0 + ", ";
            }
        }
        ltr34Module.setCheckedChannels(channels);
        ltr34Module.setChannelsAmplitudes(amplitudes);
        ltr34Module.setChannelsFrequencies(frequencies);
        ltr34Module.setChannelsPhases(phases);
        ltr34Module.setCrate(ltr34.getCrate());
        ltr34Module.setSlot(ltr34.getSlot());

        LTR34ModuleRepository.updateLTR34Module(ltr34Module);
    }

    private void createLTR34Module(LTR34 ltr34) {
        LTR34Module ltr34Module = new LTR34Module(
                testProgram.getId(),
                ltr34.getCheckedChannels(),
                ltr34.getChannelsParameters(),
                ltr34.getCrate(),
                ltr34.getSlot());

        LTR34ModuleRepository.insertLTR34Module(ltr34Module);
    }

    private void updateLTR212Data(LTR212 ltr212, boolean[] checkedChannels, LTR212Module ltr212Module) {
        String channels = "";
        String types = "";
        String ranges = "";
        String descriptions = "";

        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channels += 1 + ", ";
                types += ltr212.getChannelsTypes()[i] + ", ";
                ranges += ltr212.getMeasuringRanges()[i] + ", ";
                descriptions += ltr212.getChannelsDescription()[i] + ", ";
            } else {
                channels += 0 + ", ";
                types += 0 + ", ";
                ranges += 0 + ", ";
                descriptions += ", ";
            }
        }
        ltr212Module.setCheckedChannels(channels);
        ltr212Module.setChannelsTypes(types);
        ltr212Module.setMeasuringRanges(ranges);
        ltr212Module.setChannelsDescription(descriptions);
        ltr212Module.setCrate(ltr212.getCrate());
        ltr212Module.setSlot(ltr212.getSlot());

        LTR212ModuleRepository.updateLTR212Module(ltr212Module);
    }

    private void createLTR212Module(LTR212 ltr212) {
        LTR212Module ltr212Module = new LTR212Module(
                testProgram.getId(),
                ltr212.getCheckedChannels(),
                ltr212.getChannelsTypes(),
                ltr212.getMeasuringRanges(),
                ltr212.getChannelsDescription(),
                ltr212.getCrate(),
                ltr212.getSlot());

        LTR212ModuleRepository.insertLTR212Module(ltr212Module);
    }

    public String getCrate() {
        return crate;
    }
}
