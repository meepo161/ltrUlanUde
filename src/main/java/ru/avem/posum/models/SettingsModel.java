package ru.avem.posum.models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.db.LTR212TablesRepository;
import ru.avem.posum.db.LTR24TablesRepository;
import ru.avem.posum.db.LTR34TablesRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR212Table;
import ru.avem.posum.db.models.LTR24Table;
import ru.avem.posum.db.models.LTR34Table;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.*;

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
        List<LTR34Table> ltr34Tables = fillLTR34ModulesList();
        List<LTR212Table> ltr212Tables = fillLTR212ModulesList();

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
                    setLTR34ChannelsSettings(ltr34, ltr34Tables, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
                    crateModel.getLtr34ModulesList().add(ltr34Pair);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    setLTR212ChannelsSettings(ltr212, ltr212Tables, parseSlotNumber(modulesNames.get(i)));
                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
                    crateModel.getLtr212ModulesList().add(ltr212Pair);
                    break;
            }
        }
    }

    private List<LTR24Table> fillLTR24ModulesList() {
        List<LTR24Table> ltr24Tables = new ArrayList<>();

        for (LTR24Table module : LTR24TablesRepository.getAllLTR24Tables()) {
            if (module.getTestProgramId() == testProgram.getId()) {
                ltr24Tables.add(module);
            }
        }
        return ltr24Tables;
    }

    private List<LTR34Table> fillLTR34ModulesList() {
        List<LTR34Table> ltr34Tables = new ArrayList<>();

        for (LTR34Table module : LTR34TablesRepository.getAllLTR34Tables()) {
            if (module.getTestProgramId() == testProgram.getId()) {
                ltr34Tables.add(module);
            }
        }
        return ltr34Tables;
    }

    private List<LTR212Table> fillLTR212ModulesList() {
        List<LTR212Table> ltr212Tables = new ArrayList<>();

        for (LTR212Table module : LTR212TablesRepository.getAllLTR212Tables()) {
            if (module.getTestProgramId() == testProgram.getId()) {
                ltr212Tables.add(module);
            }
        }
        return ltr212Tables;
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

    private void setLTR34ChannelsSettings(LTR34 ltr34, List<LTR34Table> ltr34Tables, int slot) {
        boolean[] checkedChannels = ltr34.getCheckedChannels();
        int[][] channelsParameters = ltr34.getChannelsParameters();

        for (LTR34Table ltr34Table : ltr34Tables) {
            if (slot == ltr34Table.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr34Table.getCheckedChannels().split(", ", 9)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsParameters[0][i] = Integer.parseInt(ltr34Table.getChannelsAmplitude().split(", ", 9)[i]);
                        channelsParameters[1][i] = Integer.parseInt(ltr34Table.getChannelsFrequency().split(", ", 9)[i]);
                        channelsParameters[2][i] = Integer.parseInt(ltr34Table.getChannelsPhase().split(", ", 9)[i]);
                    }
                }

                ltr34.setCrate(ltr34Table.getCrate());
                ltr34.setSlot(ltr34Table.getSlot());
            }
        }
    }

    private void setLTR212ChannelsSettings(LTR212 ltr212, List<LTR212Table> ltr212Tables, int slot) {
        boolean[] checkedChannels = ltr212.getCheckedChannels();
        int parsedSlot;
        int[] channelsTypes = ltr212.getChannelsTypes();
        int[] measuringRanges = ltr212.getMeasuringRanges();
        String[] channelsDescription = ltr212.getChannelsDescription();
        String[] calibrationSettings = ltr212.getCalibrationSettings();

        for (LTR212Table ltr212Table : ltr212Tables) {
            parsedSlot = Integer.parseInt(ltr212Table.getSlot());
            if (slot == parsedSlot) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr212Table.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr212Table.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr212Table.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr212Table.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                calibrationSettings[0] = ltr212Table.getCalibrationOfChannelN1();
                calibrationSettings[1] = ltr212Table.getCalibrationOfChannelN2();
                calibrationSettings[2] = ltr212Table.getCalibrationOfChannelN3();
                calibrationSettings[3] = ltr212Table.getCalibrationOfChannelN4();

                ltr212.setCrate(ltr212Table.getCrate());
                ltr212.setSlot(parsedSlot);
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
        long testProgramId = testProgram.getId();
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (String modulesName : modulesNames) {
            switch (modulesName.split(" ")[0]) {
                case CrateModel.LTR24:
                    updateLTR24Table(crateModel.getLtr24ModulesList().get(ltr24Index++).getValue(), testProgramId);
                    break;
                case CrateModel.LTR34:
                    updateLTR34Settings(crateModel.getLtr34ModulesList().get(ltr34Index).getValue(), testProgramId);
                    break;
                case CrateModel.LTR212:
                    updateLTR212Table(crateModel.getLtr212ModulesList().get(ltr212Index++).getValue(), testProgramId);
                    break;
            }
        }
    }

    private void updateLTR24Table(ADC adc, long testProgramId) {
        String[][] moduleSettings = setSettings(adc);

        if (isEditMode) {
            for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
                if (ltr24Table.getTestProgramId() == testProgramId && Integer.parseInt(ltr24Table.getSlot()) == adc.getSlot()) {
                    ltr24Table.setCrate(moduleSettings[0][0]);
                    ltr24Table.setSlot(moduleSettings[1][0]);
                    ltr24Table.setCheckedChannels(LTR24Table.settingsToString(moduleSettings[2]));
                    ltr24Table.setChannelsTypes(LTR24Table.settingsToString(moduleSettings[3]));
                    ltr24Table.setMeasuringRanges(LTR24Table.settingsToString(moduleSettings[4]));
                    ltr24Table.setChannelsDescription(LTR24Table.settingsToString(moduleSettings[5]));
                    ltr24Table.setCalibrationOfChannelN1(adc.getCalibrationSettings()[0]);
                    ltr24Table.setCalibrationOfChannelN2(adc.getCalibrationSettings()[1]);
                    ltr24Table.setCalibrationOfChannelN3(adc.getCalibrationSettings()[2]);
                    ltr24Table.setCalibrationOfChannelN4(adc.getCalibrationSettings()[3]);
                    LTR24TablesRepository.updateLTR24Table(ltr24Table);
                }
            }
        } else {
            LTR24Table newLTR24Table = new LTR24Table(testProgramId, moduleSettings);
            LTR24TablesRepository.insertLTR24Table(newLTR24Table);
        }

    }

    private String[][] setSettings(ADC adc) {
        int channels = 4; // количество каналов
        String[][] moduleSettings = new String[7][channels];
        String[] calibrationSettings = adc.getCalibrationSettings();
        boolean[] checkedChannels = adc.getCheckedChannels();
        int[] channelsTypes = adc.getChannelsTypes();
        int[] measuringRanges = adc.getMeasuringRanges();
        String[] channelsDescription = adc.getChannelsDescription();

        moduleSettings[0][0] = adc.getCrate(); // серийный номер крейта
        moduleSettings[1][0] = String.valueOf(adc.getSlot()); // номер слота
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
                moduleSettings[6][i] = "notSetted, 0.0, 0.0, 0.0, 0.0, В";
            }
        }
        return moduleSettings;
    }

    private void updateLTR34Settings(LTR34 ltr34, long testProgramId) {
        boolean[] checkedChannels = ltr34.getCheckedChannels();

        if (isEditMode) {
            for (LTR34Table ltr34Table : LTR34TablesRepository.getAllLTR34Tables()) {
                if (ltr34Table.getTestProgramId() == testProgramId && ltr34Table.getSlot() == ltr34.getSlot()) {
                    updateLTR34Data(ltr34, checkedChannels, ltr34Table);
                }
            }
        } else {
            createLTR34Module(ltr34);
        }
    }

    private void updateLTR212Table(ADC adc, long testProgramId) {
        String[][] moduleSettings = setSettings(adc);

        if (isEditMode) {
            for (LTR212Table ltr212Table : LTR212TablesRepository.getAllLTR212Tables()) {
                if (ltr212Table.getTestProgramId() == testProgramId && Integer.parseInt(ltr212Table.getSlot()) == adc.getSlot()) {
                    ltr212Table.setCrate(moduleSettings[0][0]);
                    ltr212Table.setSlot(moduleSettings[1][0]);
                    ltr212Table.setCheckedChannels(LTR212Table.settingsToString(moduleSettings[2]));
                    ltr212Table.setChannelsTypes(LTR212Table.settingsToString(moduleSettings[3]));
                    ltr212Table.setMeasuringRanges(LTR212Table.settingsToString(moduleSettings[4]));
                    ltr212Table.setChannelsDescription(LTR212Table.settingsToString(moduleSettings[5]));
                    ltr212Table.setCalibrationOfChannelN1(adc.getCalibrationSettings()[0]);
                    ltr212Table.setCalibrationOfChannelN2(adc.getCalibrationSettings()[1]);
                    ltr212Table.setCalibrationOfChannelN3(adc.getCalibrationSettings()[2]);
                    ltr212Table.setCalibrationOfChannelN4(adc.getCalibrationSettings()[3]);
                    LTR212TablesRepository.updateLTR212Table(ltr212Table);
                }
            }
        } else {
            LTR212Table newLTR212Table = new LTR212Table(testProgramId, moduleSettings);
            LTR212TablesRepository.insertLTR212Table(newLTR212Table);
        }
    }

    private void updateLTR34Data(LTR34 ltr34, boolean[] checkedChannels, LTR34Table ltr34Table) {
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
        ltr34Table.setCheckedChannels(channels);
        ltr34Table.setChannelsAmplitudes(amplitudes);
        ltr34Table.setChannelsFrequencies(frequencies);
        ltr34Table.setChannelsPhases(phases);
        ltr34Table.setCrate(ltr34.getCrate());
        ltr34Table.setSlot(ltr34.getSlot());

        LTR34TablesRepository.updateLTR34Table(ltr34Table);
    }

    private void createLTR34Module(LTR34 ltr34) {
        LTR34Table ltr34Table = new LTR34Table(
                testProgram.getId(),
                ltr34.getCheckedChannels(),
                ltr34.getChannelsParameters(),
                ltr34.getCrate(),
                ltr34.getSlot());

        LTR34TablesRepository.insertLTR34Module(ltr34Table);
    }

    public String getCrate() {
        return crate;
    }
}
