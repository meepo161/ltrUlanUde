package ru.avem.posum.models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SettingsModel implements BaseController {
    private String[] channelsDescription;
    private int[] channelsTypes;
    private boolean[] checkedChannels;
    private ControllerManager cm;
    private String crate;
    private CrateModel crateModel;
    private GeneralSettings generalSettings;
    private boolean isEditMode;
    private int[] measuringRanges;
    private ObservableList<String> modulesNames;
    private HashMap<String, StringBuffer> moduleSettings = new HashMap<>();
    private int slot;
    private int selectedCrate;
    private TestProgram testProgram;
    private long testProgramId;

    public void createModulesInstances(ObservableList<String> modulesNames) {
        this.crate = cm.getCrate();
        this.modulesNames = modulesNames;
        crateModel = cm.getCrateModelInstance();

        for (int moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            switch (modulesNames.get(moduleIndex).split(" ")[0]) {
                case CrateModel.LTR24:
                    LTR24 ltr24 = new LTR24();
                    setModuleSettings(ltr24, moduleIndex);
                    setDefaultADCSettings(ltr24, 0, 1);
                    saveModuleInstance(ltr24);
                    break;
                case CrateModel.LTR34:
                    LTR34 ltr34 = new LTR34();
                    setModuleSettings(ltr34, moduleIndex);
                    setDefaultDACSettings(ltr34);
                    saveModuleInstance(ltr34);
                    break;
                case CrateModel.LTR212:
                    LTR212 ltr212 = new LTR212();
                    setModuleSettings(ltr212, moduleIndex);
                    setDefaultADCSettings(ltr212, 2, 3);
                    saveModuleInstance(ltr212);
                    break;
            }
        }
    }


    private void setModuleSettings(Module module, int moduleIndex) {
        slot = parseSlotNumber(moduleIndex);
        module.setSlot(slot);
        module.setCrate(crate);
        module.setStatus("");
    }

    private int parseSlotNumber(int moduleIndex) {
        String moduleName = modulesNames.get(moduleIndex);
        return Utils.parseSlotNumber(moduleName);
    }

    private void setDefaultADCSettings(ADC adc, int channeslType, int measuringRange) {
        checkedChannels = adc.getCheckedChannels();
        channelsTypes = adc.getChannelsTypes();
        measuringRanges = adc.getMeasuringRanges();
        channelsDescription = adc.getChannelsDescription();

        for (int i = 0; i < adc.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            channelsTypes[i] = channeslType;
            measuringRanges[i] = measuringRange;
            channelsDescription[i] = "";
        }
    }

    private void saveModuleInstance(Module module) {
        crateModel.getModulesList().add(new Pair<>(slot, module));
    }

    private void setDefaultDACSettings(DAC dac) {
        checkedChannels = dac.getCheckedChannels();
        int[] amplitudes = dac.getAmplitudes();
        int[] frequencies = dac.getFrequencies();
        int[] phases = dac.getPhases();


        for (int i = 0; i < dac.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            amplitudes[i] = 0;
            frequencies[i] = 0;
            phases[i] = 0;
        }
    }

//    public void loadChannelsSettings(TestProgram testProgram, CrateModel crateModel, ObservableList<String> modulesNames, int selectedCrate) {
//        this.testProgram = testProgram;
//        this.selectedCrate = selectedCrate;
//        this.crateModel = crateModel;
//        this.modulesNames = modulesNames;
//
//        crateModel.getModulesNames(selectedCrate);
//        saveModulesSettings(crateModel);
//    }
//
//    private void saveModulesSettings(CrateModel crateModel) {
//        List<LTR24Table> ltr24Tables = fillLTR24ModulesList();
//        List<LTR34Table> ltr34Tables = fillLTR34ModulesList();
//        List<LTR212Table> ltr212Tables = fillLTR212ModulesList();
//
//        for (int i = 0; i < modulesNames.size(); i++) {
//            switch (modulesNames.get(i).split(" ")[0]) {
//                case CrateModel.LTR24:
//                    LTR24 ltr24 = new LTR24();
//                    setLTR24ChannelsSettings(ltr24, ltr24Tables, parseSlotNumber(modulesNames.get(i)));
//                    Pair<Integer, LTR24> ltr24Pair = new Pair<>(i, ltr24);
//                    crateModel.getLtr24ModulesList().add(ltr24Pair);
//                    break;
//                case CrateModel.LTR34:
//                    LTR34 ltr34 = new LTR34();
//                    setLTR34ChannelsSettings(ltr34, ltr34Tables, parseSlotNumber(modulesNames.get(i)));
//                    Pair<Integer, LTR34> ltr34Pair = new Pair<>(i, ltr34);
//                    crateModel.getLtr34ModulesList().add(ltr34Pair);
//                    break;
//                case CrateModel.LTR212:
//                    LTR212 ltr212 = new LTR212();
//                    setLTR212ChannelsSettings(ltr212, ltr212Tables, parseSlotNumber(modulesNames.get(i)));
//                    Pair<Integer, LTR212> ltr212Pair = new Pair<>(i, ltr212);
//                    crateModel.getLtr212ModulesList().add(ltr212Pair);
//                    break;
//            }
//        }
//    }
//
//    private List<LTR24Table> fillLTR24ModulesList() {
//        List<LTR24Table> ltr24Tables = new ArrayList<>();
//
//        for (LTR24Table module : LTR24TablesRepository.getAllLTR24Tables()) {
//            if (module.getTestProgramId() == testProgram.getId()) {
//                ltr24Tables.add(module);
//            }
//        }
//        return ltr24Tables;
//    }
//
//    private List<LTR34Table> fillLTR34ModulesList() {
//        List<LTR34Table> ltr34Tables = new ArrayList<>();
//
//        for (LTR34Table module : LTR34TablesRepository.getAllLTR34Tables()) {
//            if (module.getTestProgramId() == testProgram.getId()) {
//                ltr34Tables.add(module);
//            }
//        }
//        return ltr34Tables;
//    }
//
//    private List<LTR212Table> fillLTR212ModulesList() {
//        List<LTR212Table> ltr212Tables = new ArrayList<>();
//
//        for (LTR212Table module : LTR212TablesRepository.getAllLTR212Tables()) {
//            if (module.getTestProgramId() == testProgram.getId()) {
//                ltr212Tables.add(module);
//            }
//        }
//        return ltr212Tables;
//    }
//

    //    private void setLTR24ChannelsSettings(LTR24 ltr24, List<LTR24Table> ltr24Tables, int slot) {
//        boolean[] checkedChannels = ltr24.getCheckedChannels();
//        int parsedSlot;
//        int[] channelsTypes = ltr24.getChannelsTypes();
//        int[] measuringRanges = ltr24.getMeasuringRanges();
//        String[] channelsDescription = ltr24.getChannelsDescription();
//
//        for (LTR24Table ltr24Table : ltr24Tables) {
//            parsedSlot = Integer.parseInt(ltr24Table.getSlot());
//            if (slot == parsedSlot) {
//                for (int i = 0; i < checkedChannels.length; i++) {
//                    if (ltr24Table.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
//                        checkedChannels[i] = false;
//                    } else {
//                        checkedChannels[i] = true;
//                        channelsTypes[i] = Integer.parseInt(ltr24Table.getChannelsTypes().split(", ", 5)[i]);
//                        measuringRanges[i] = Integer.parseInt(ltr24Table.getMeasuringRanges().split(", ", 5)[i]);
//                        channelsDescription[i] = ltr24Table.getChannelsDescription().split(", ", 5)[i];
//                    }
//                }
//
//                ltr24.setCrate(ltr24Table.getCrate());
//                ltr24.setSlot(parsedSlot);
//            }
//        }
//    }
//
//    private void setLTR34ChannelsSettings(LTR34 ltr34, List<LTR34Table> ltr34Tables, int slot) {
//        boolean[] checkedChannels = ltr34.getCheckedChannels();
//        int[][] channelsParameters = ltr34.getChannelsParameters();
//
//        for (LTR34Table ltr34Table : ltr34Tables) {
//            if (slot == ltr34Table.getSlot()) {
//                for (int i = 0; i < checkedChannels.length; i++) {
//                    if (ltr34Table.getCheckedChannels().split(", ", 9)[i].equals("0")) { // 0 - канал не был отмечен
//                        checkedChannels[i] = false;
//                    } else {
//                        checkedChannels[i] = true;
//                        channelsParameters[0][i] = Integer.parseInt(ltr34Table.getChannelsAmplitude().split(", ", 9)[i]);
//                        channelsParameters[1][i] = Integer.parseInt(ltr34Table.getChannelsFrequency().split(", ", 9)[i]);
//                        channelsParameters[2][i] = Integer.parseInt(ltr34Table.getChannelsPhase().split(", ", 9)[i]);
//                    }
//                }
//
//                ltr34.setCrate(ltr34Table.getCrate());
//                ltr34.setSlot(ltr34Table.getSlot());
//            }
//        }
//    }
//
//    private void setLTR212ChannelsSettings(LTR212 ltr212, List<LTR212Table> ltr212Tables, int slot) {
//        boolean[] checkedChannels = ltr212.getCheckedChannels();
//        int parsedSlot;
//        int[] channelsTypes = ltr212.getChannelsTypes();
//        int[] measuringRanges = ltr212.getMeasuringRanges();
//        String[] channelsDescription = ltr212.getChannelsDescription();
//
//        for (LTR212Table ltr212Table : ltr212Tables) {
//            parsedSlot = Integer.parseInt(ltr212Table.getSlot());
//            if (slot == parsedSlot) {
//                for (int i = 0; i < checkedChannels.length; i++) {
//                    if (ltr212Table.getCheckedChannels().split(", ", 5)[i].equals("0")) { // 0 - канал не был отмечен
//                        checkedChannels[i] = false;
//                    } else {
//                        checkedChannels[i] = true;
//                        channelsTypes[i] = Integer.parseInt(ltr212Table.getChannelsTypes().split(", ", 5)[i]);
//                        measuringRanges[i] = Integer.parseInt(ltr212Table.getMeasuringRanges().split(", ", 5)[i]);
//                        channelsDescription[i] = ltr212Table.getChannelsDescription().split(", ", 5)[i];
//                    }
//                }
//
//                ltr212.setCrate(ltr212Table.getCrate());
//                ltr212.setSlot(parsedSlot);
//            }
//        }
//    }
//
    public void saveGeneralSettings(HashMap<String, String> generalSettings, boolean isEditMode) {
        setMode(isEditMode);

        if (isEditMode) {
            updateTestProgram(generalSettings);
        } else {
            createNewTestProgram(generalSettings);
        }
    }

    private void setMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    private void updateTestProgram(HashMap<String, String> generalSettings) {
        setTestProgramFields(generalSettings);
        TestProgramRepository.updateTestProgram(testProgram);
    }

    private void setTestProgramFields(HashMap<String, String> generalSettings) {
        testProgram.setCrate(generalSettings.get("Crate Serial Number"));
        testProgram.setTestProgramName(generalSettings.get("Test Program Name"));
        testProgram.setSampleName(generalSettings.get("Sample Name"));
        testProgram.setSampleSerialNumber(generalSettings.get("Sample Serial Number"));
        testProgram.setDocumentNumber(generalSettings.get("Document Number"));
        testProgram.setTestProgramType(generalSettings.get("Test Program Type"));
        testProgram.setTestProgramTime(generalSettings.get("Test Program Time"));
        testProgram.setTestProgramDate(generalSettings.get("Test Program Date"));
        testProgram.setLeadEngineer(generalSettings.get("Lead Engineer"));
        testProgram.setComments(generalSettings.get("Comments"));
        testProgram.setChanged(new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()));
    }

    private void createNewTestProgram(HashMap<String, String> generalSettings) {
        testProgram = new TestProgram(
                generalSettings.get("Crate Serial Number"),
                generalSettings.get("Test Program Name"),
                generalSettings.get("Sample Name"),
                generalSettings.get("Sample Serial Number"),
                generalSettings.get("Document Number"),
                generalSettings.get("Test Program Type"),
                generalSettings.get("Test Program Time"),
                generalSettings.get("Test Program Date"),
                generalSettings.get("Lead Engineer"),
                generalSettings.get("Comments"),
                new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()),
                new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()));

        TestProgramRepository.insertTestProgram(testProgram);
    }


    @Override
    public void setWindowManager(WindowsManager wm) {

    }

    //    public void saveHardwareSettings(boolean isEditMode) {
//        this.isEditMode = isEditMode;
//        this.testProgramId = testProgram.getId();
//        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
//        int ltr212Index = 0;
//        int ltr34Index = 0;
//
//        for (String modulesName : modulesNames) {
//            switch (modulesName.split(" ")[0]) {
//                case CrateModel.LTR24:
//                    getLTR24Instance(ltr24Index++);
//                    saveLTR24Settings();
//                    break;
//                case CrateModel.LTR34:
//                    getLTR34Instance(ltr34Index++);
//                    saveLTR34Settings();
//                    break;
//                case CrateModel.LTR212:
//                    getLTR212Instance(ltr212Index++);
//                    saveLTR212Settings();
//                    break;
//            }
//        }
//    }
//
//    private void getLTR24Instance(int index) {
//        adc = crateModel.getLtr24ModulesList().get(index).getValue();
//    }
//
//    private void saveLTR24Settings() {
//        setSettings();
//
//        if (isEditMode) {
//            updateLTR24TableFields();
//        } else {
//            createNewLTR24Table();
//        }
//
//    }
//
//    private void setSettings() {
//        getModuleSettings();
//        setModuleInfo();
//        setChannelsSettings();
//    }
//
//    private void getModuleSettings() {
//        checkedChannels = adc.getCheckedChannels();
//        channelsTypes = adc.getChannelsTypes();
//        measuringRanges = adc.getMeasuringRanges();
//        channelsDescription = adc.getChannelsDescription();
//    }
//
//    private void setModuleInfo() {
//        moduleSettings[0][0] = adc.getCrate(); // серийный номер крейта
//        moduleSettings[1][0] = String.valueOf(adc.getSlot()); // номер слота
//    }
//
//    private void setChannelsSettings() {
//        for (int channel = 0; channel < CHANNELS; channel++) {
//            if (checkedChannels[channel]) {
//                setLoadedChannelSettings(channel);
//            } else {
//                setDefaultChannelsSettings(channel);
//            }
//        }
//    }
//
//    private void setDefaultChannelsSettings(int channel) {
//        moduleSettings[2][channel] = "0"; // канал не отмечен
//        moduleSettings[3][channel] = "0";
//        moduleSettings[4][channel] = "0";
//        moduleSettings[5][channel] = "0";
//    }
//
//    private void setLoadedChannelSettings(int channel) {
//        moduleSettings[2][channel] = "1"; // канал отмечен
//        moduleSettings[3][channel] = String.valueOf(channelsTypes[channel]);
//        moduleSettings[4][channel] = String.valueOf(measuringRanges[channel]);
//        moduleSettings[5][channel] = String.valueOf(channelsDescription[channel]);
//    }
//
//    private void updateLTR24TableFields() {
//        for (LTR24Table ltr24Table : LTR24TablesRepository.getAllLTR24Tables()) {
//            if (ltr24Table.getTestProgramId() == testProgramId && Integer.parseInt(ltr24Table.getSlot()) == adc.getSlot()) {
//                ltr24Table.setCrate(moduleSettings[0][0]);
//                ltr24Table.setSlot(moduleSettings[1][0]);
//                ltr24Table.setCheckedChannels(LTR24Table.settingsToString(moduleSettings[2]));
//                ltr24Table.setChannelsTypes(LTR24Table.settingsToString(moduleSettings[3]));
//                ltr24Table.setMeasuringRanges(LTR24Table.settingsToString(moduleSettings[4]));
//                ltr24Table.setChannelsDescription(LTR24Table.settingsToString(moduleSettings[5]));
//
//                updateLTR24Settings(ltr24Table);
//            }
//        }
//    }
//
//    private void updateLTR24Settings(LTR24Table ltr24Table) {
//        long id = ltr24Table.getId();
//
//        LTR24TablesRepository.updateLTR24Table(ltr24Table);
//        deleteOldCalibrationSettings(id);
//        saveNewCalibrationSettings(id);
//    }
//
//    private void deleteOldCalibrationSettings(long moduleId) {
//        for (Calibration calibration : CalibrationRepository.getAllCalibrations()) {
//            if (calibration.getModuleId() == moduleId) {
//                CalibrationRepository.deleteCalibration(calibration);
//            }
//        }
//    }
//
//    private void saveNewCalibrationSettings(long moduleId) {
//        for (CalibrationModel calibrationModel : adc.getCalibrationModel()) {
//            Calibration calibration = new Calibration(moduleId, calibrationModel);
//            CalibrationRepository.insertCalibration(calibration);
//        }
//    }
//
//    private void createNewLTR24Table() {
//        LTR24Table newLTR24Table = new LTR24Table(testProgramId, moduleSettings);
//        LTR24TablesRepository.insertLTR24Table(newLTR24Table);
//        saveNewCalibrationSettings(newLTR24Table.getId());
//    }
//
//    private void getLTR34Instance(int index) {
//        ltr34 = crateModel.getLtr34ModulesList().get(index).getValue();
//    }
//
//    private void saveLTR34Settings() {
//        if (isEditMode) {
//            updateLTR34TableFields();
//        } else {
//            createNewLTR34Table();
//        }
//    }
//
//    private void updateLTR34TableFields() {
//        boolean[] checkedChannels = ltr34.getCheckedChannels();
//
//        for (LTR34Table ltr34Table : LTR34TablesRepository.getAllLTR34Tables()) {
//            boolean isTableRowFound = (ltr34Table.getTestProgramId() == testProgramId) && (ltr34Table.getSlot() == ltr34.getSlot());
//
//            if (isTableRowFound) {
//                updateLTR34Settings(ltr34, checkedChannels, ltr34Table);
//            }
//        }
//    }
//
//    private void updateLTR34Settings(LTR34 ltr34, boolean[] checkedChannels, LTR34Table ltr34Table) {
//        int[][] channelsParameters = ltr34.getChannelsParameters();
//        StringBuilder channels = new StringBuilder();
//        StringBuilder amplitudes = new StringBuilder();
//        StringBuilder frequencies = new StringBuilder();
//        StringBuilder phases = new StringBuilder();
//
//        for (int i = 0; i < checkedChannels.length; i++) {
//            if (checkedChannels[i]) {
//                channels.append(1 + ", ");
//                amplitudes.append(channelsParameters[0][i]).append(", ");
//                frequencies.append(channelsParameters[1][i]).append(", ");
//                phases.append(channelsParameters[2][i]).append(", ");
//            } else {
//                channels.append(0 + ", ");
//                amplitudes.append(0 + ", ");
//                frequencies.append(0 + ", ");
//                phases.append(0 + ", ");
//            }
//        }
//        ltr34Table.setCheckedChannels(channels.toString());
//        ltr34Table.setChannelsAmplitudes(amplitudes.toString());
//        ltr34Table.setChannelsFrequencies(frequencies.toString());
//        ltr34Table.setChannelsPhases(phases.toString());
//        ltr34Table.setCrate(ltr34.getCrate());
//        ltr34Table.setSlot(ltr34.getSlot());
//
//        LTR34TablesRepository.updateLTR34Table(ltr34Table);
//    }
//
//    private void createNewLTR34Table() {
//        LTR34Table ltr34Table = new LTR34Table(
//                testProgram.getId(),
//                ltr34.getCheckedChannels(),
//                ltr34.getChannelsParameters(),
//                ltr34.getCrate(),
//                ltr34.getSlot());
//
//        LTR34TablesRepository.insertLTR34Module(ltr34Table);
//    }
//
//    private void getLTR212Instance(int index) {
//        adc = crateModel.getLtr212ModulesList().get(index).getValue();
//    }
//
//    private void saveLTR212Settings() {
//        setSettings();
//
//        if (isEditMode) {
//            updateLTR212TableFields();
//        } else {
//            createNewLTR212Table();
//        }
//    }
//
//    private void updateLTR212TableFields() {
//        for (LTR212Table ltr212Table : LTR212TablesRepository.getAllLTR212Tables()) {
//            if (ltr212Table.getTestProgramId() == testProgramId && Integer.parseInt(ltr212Table.getSlot()) == adc.getSlot()) {
//                ltr212Table.setCrate(moduleSettings[0][0]);
//                ltr212Table.setSlot(moduleSettings[1][0]);
//                ltr212Table.setCheckedChannels(LTR212Table.settingsToString(moduleSettings[2]));
//                ltr212Table.setChannelsTypes(LTR212Table.settingsToString(moduleSettings[3]));
//                ltr212Table.setMeasuringRanges(LTR212Table.settingsToString(moduleSettings[4]));
//                ltr212Table.setChannelsDescription(LTR212Table.settingsToString(moduleSettings[5]));
//
//                updateLTR212Settings(ltr212Table);
//            }
//        }
//    }
//
//    private void updateLTR212Settings(LTR212Table ltr212Table) {
//        long id = ltr212Table.getId();
//
//        LTR212TablesRepository.updateLTR212Table(ltr212Table);
//        deleteOldCalibrationSettings(id);
//        saveNewCalibrationSettings(id);
//    }
//
//    private void createNewLTR212Table() {
//        LTR212Table newLTR212Table = new LTR212Table(testProgramId, moduleSettings);
//        LTR212TablesRepository.insertLTR212Table(newLTR212Table);
//        saveNewCalibrationSettings(newLTR212Table.getId());
//    }
//
//    public String getCrate() {
//        return crate;
//    }
    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
