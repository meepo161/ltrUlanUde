package ru.avem.posum.models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.db.LTR212ModuleRepository;
import ru.avem.posum.db.LTR24ModuleRepository;
import ru.avem.posum.db.LTR34ModuleRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.LTR212Module;
import ru.avem.posum.db.models.LTR24Module;
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
        List<LTR24Module> ltr24Modules = fillLTR24ModulesList();
        List<LTR34Module> ltr34Modules = fillLTR34ModulesList();
        List<LTR212Module> ltr212Modules = fillLTR212ModulesList();

        for (int i = 0; i < modulesNames.size(); i++) {
            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    setLTR24ChannelsSettings(ltr24, ltr24Modules, parseSlotNumber(modulesNames.get(i)));
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

    private List<LTR24Module> fillLTR24ModulesList() {
        List<LTR24Module> ltr24Modules = new ArrayList<>();

        for (LTR24Module module : LTR24ModuleRepository.getAllLTR24Modules()) {
            if (module.getTestProgrammId() == testProgram.getTestProgramId()) {
                ltr24Modules.add(module);
            }
        }
        return ltr24Modules;
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

    private void setLTR24ChannelsSettings(LTR24 ltr24, List<LTR24Module> ltr24Modules, int slot) {
        boolean[] checkedChannels = ltr24.getCheckedChannels();
        int[] channelsTypes = ltr24.getChannelsTypes();
        int[] measuringRanges = ltr24.getMeasuringRanges();
        String[] channelsDescription = ltr24.getChannelsDescription();

        for (LTR24Module ltr24Module : ltr24Modules) {
            if (slot == ltr24Module.getSlot()) {
                for (int i = 0; i < checkedChannels.length; i++) {
                    if (ltr24Module.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
                        checkedChannels[i] = false;
                    } else {
                        checkedChannels[i] = true;
                        channelsTypes[i] = Integer.parseInt(ltr24Module.getChannelsTypes().split(", ", 5)[i]);
                        measuringRanges[i] = Integer.parseInt(ltr24Module.getMeasuringRanges().split(", ", 5)[i]);
                        channelsDescription[i] = ltr24Module.getChannelsDescription().split(", ", 5)[i];
                    }
                }

                ltr24.setCrate(ltr24Module.getCrate());
                ltr24.setSlot(ltr24Module.getSlot());
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
                        channelsParameters[0][i] = Integer.parseInt(ltr34Module.getChannelsFrequency().split(", ", 9)[i]);
                        channelsParameters[1][i] = Integer.parseInt(ltr34Module.getChannelsAmplitude().split(", ", 9)[i]);
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

    public void saveHardwareSettings() {
        /* сохранение настроек оборудования */
        int testProgramId = testProgram.getTestProgramId();
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (String modulesName : modulesNames) {
            switch (modulesName.split(" ")[0]) {
                case CrateModel.LTR24:
                    updateLTR24Settings(testProgramId, ltr24Index++);
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

    private void updateLTR24Settings(int testProgramId, int ltr24Index) {
        LTR24 ltr24 = crateModel.getLtr24ModulesList().get(ltr24Index).getValue();
        boolean[] checkedChannels = ltr24.getCheckedChannels();

        if (isEditMode) {
            for (LTR24Module ltr24Module : LTR24ModuleRepository.getAllLTR24Modules()) {
                if (ltr24Module.getTestProgrammId() == testProgramId && ltr24Module.getSlot() == ltr24.getSlot()) {
                    updateLTR24Data(ltr24, checkedChannels, ltr24Module);
                }
            }
        } else {
            createNewLTR24Module(ltr24);
        }
    }

    private void updateLTR24Data(LTR24 ltr24, boolean[] checkedChannels, LTR24Module ltr24Module) {
        String channels = "";
        String types = "";
        String ranges = "";
        String descriptions = "";

        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channels += 1 + ", ";
                types += ltr24.getChannelsTypes()[i] + ", ";
                ranges += ltr24.getMeasuringRanges()[i] + ", ";
                descriptions += ltr24.getChannelsDescription()[i] + ", ";
            } else {
                channels += 0 + ", ";
                types += 0 + ", ";
                ranges += 0 + ", ";
                descriptions += ", ";
            }
        }
        ltr24Module.setCheckedChannels(channels);
        ltr24Module.setChannelsTypes(types);
        ltr24Module.setMeasuringRanges(ranges);
        ltr24Module.setChannelsDescription(descriptions);
        ltr24Module.setCrate(ltr24.getCrate());
        ltr24Module.setSlot(ltr24.getSlot());

        LTR24ModuleRepository.updateLTR24Module(ltr24Module);
    }

    private void createNewLTR24Module(LTR24 ltr24) {
        LTR24Module ltr24Module = new LTR24Module(
                testProgram.getId(),
                ltr24.getCheckedChannels(),
                ltr24.getChannelsTypes(),
                ltr24.getMeasuringRanges(),
                ltr24.getChannelsDescription(),
                ltr24.getCrate(),
                ltr24.getSlot());

        LTR24ModuleRepository.insertLTR24Module(ltr24Module);
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
        String channels = "";
        String frequencies = "";
        String amplitudes = "";

        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i]) {
                channels += 1 + ", ";
                frequencies += ltr34.getChannelsParameters()[0][i] + ", ";
                amplitudes += ltr34.getChannelsParameters()[1][i] + ", ";
            } else {
                channels += 0 + ", ";
                frequencies += 0 + ", ";
                amplitudes += 0 + ", ";
            }
        }
        ltr34Module.setCheckedChannels(channels);
        ltr34Module.setChannelsFrequency(frequencies);
        ltr34Module.setChannelsAmplitude(amplitudes);
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
