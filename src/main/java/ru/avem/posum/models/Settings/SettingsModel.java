package ru.avem.posum.models.Settings;

import javafx.collections.ObservableList;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.CalibrationsRepository;
import ru.avem.posum.db.ModulesRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.Calibration;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SettingsModel implements BaseController {
    private ADC adc;
    private int[] amplitudes;
    private ArrayList<List<Double>> calibrationCoefficients;
    private ArrayList<List<String>> calibrationSettings;
    private String[] descriptions;
    private int[] typesOfChannels;
    private boolean[] checkedChannels;
    private ControllerManager cm;
    private int channels;
    private String crateSerialNumber;
    private Crate crate;
    private DAC dac;
    private int[] frequencies;
    private boolean isEditMode;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private int[] measuringRanges;
    private long moduleId;
    private HashMap<Integer, Module> modules;
    private int moduleIndex;
    private ObservableList<String> modulesNames;
    private HashMap<String, String> moduleSettings;
    private String moduleType;
    private int[] phases;
    private int slot;
    private TestProgram testProgram;
    private long testProgramId;

    public void createModulesInstances(ObservableList<String> modulesNames) {
        this.modulesNames = modulesNames;
        this.crateSerialNumber = cm.getCrateSerialNumber();
        this.crate = cm.getCrateModelInstance();

        addInitModuleInstructions();

        for (moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            moduleType = modulesNames.get(moduleIndex).split(" ")[0];
            runInstructions();
        }
    }

    private void addInitModuleInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::initLTR24Instance);
        instructions.put(Crate.LTR27, this::initLTR27Instance);
        instructions.put(Crate.LTR34, this::initLTR34Instance);
        instructions.put(Crate.LTR212, this::initLTR212Instance);
    }

    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    private void initLTR24Instance() {
        adc = new LTR24();
        setModuleSettings(adc);
        setDefaultADCSettings(0, 1);
        saveModuleInstance(adc);
    }

    private void initLTR27Instance() {

    }

    private void initLTR34Instance() {
        dac = new LTR34();
        setModuleSettings(dac);
        setDefaultDACSettings();
        saveModuleInstance(dac);
    }

    private void initLTR212Instance() {
        adc = new LTR212();
        setModuleSettings(adc);
        setDefaultADCSettings(0, 3);
        saveModuleInstance(adc);
    }

    private void setModuleSettings(Module module) {
        slot = parseSlotNumber(moduleIndex);
        module.setSlot(slot);
        module.setCrateSerialNumber(crateSerialNumber);
        module.setStatus("");
    }

    public int parseSlotNumber(int moduleIndex) {
        String moduleName = modulesNames.get(moduleIndex);
        return Utils.parseSlotNumber(moduleName);
    }

    private void setDefaultADCSettings(int channelsType, int measuringRange) {
        setADCSettingsFields();

        for (int i = 0; i < adc.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            typesOfChannels[i] = channelsType;
            measuringRanges[i] = measuringRange;
            descriptions[i] = ", ";
            calibrationSettings.add(new ArrayList<>());
            calibrationCoefficients.add(new ArrayList<>());
        }
    }

    private void setADCSettingsFields() {
        checkedChannels = adc.getCheckedChannels();
        typesOfChannels = adc.getTypeOfChannels();
        measuringRanges = adc.getMeasuringRanges();
        descriptions = adc.getDescriptions();
        calibrationSettings = adc.getCalibrationSettings();
        calibrationCoefficients = adc.getCalibrationCoefficients();
        amplitudes = null;
        frequencies = null;
        phases = null;
    }

    private void saveModuleInstance(Module module) {
        crate.getModulesList().put(slot, module);
    }

    private void setDefaultDACSettings() {
        setDACSettingsFields();

        for (int i = 0; i < dac.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            amplitudes[i] = 0;
            descriptions[i] = "";
            frequencies[i] = 0;
            phases[i] = 0;
        }
    }

    private void setDACSettingsFields() {
        checkedChannels = dac.getCheckedChannels();
        typesOfChannels = null;
        measuringRanges = null;
        descriptions = dac.getDescriptions();
        amplitudes = dac.getAmplitudes();
        frequencies = dac.getFrequencies();
        phases = dac.getPhases();
    }

    public void saveGeneralSettings(HashMap<String, String> generalSettings, boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (isEditMode) {
            updateTestProgram(generalSettings);
        } else {
            createNewTestProgram(generalSettings);
        }
    }

    private void updateTestProgram(HashMap<String, String> generalSettings) {
        setTestProgramFields(generalSettings);
        TestProgramRepository.updateTestProgram(testProgram);
    }

    private void setTestProgramFields(HashMap<String, String> generalSettings) {
        testProgram.setCrateSerialNumber(generalSettings.get("Crate Serial Number"));
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

    public void saveHardwareSettings(boolean isEditMode) {
        this.isEditMode = isEditMode;
        this.testProgramId = testProgram.getId();
        saveModulesSettings();
    }

    private void saveModulesSettings() {
        addSaveModuleInstructions();
        for (moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            moduleType = modulesNames.get(moduleIndex).split(" ")[0];
            slot = parseSlotNumber(moduleIndex);
            runInstructions();
        }
    }

    private void addSaveModuleInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::saveLTR24Settings);
        instructions.put(Crate.LTR27, this::saveLTR27Settings);
        instructions.put(Crate.LTR34, this::saveLTR34Settings);
        instructions.put(Crate.LTR212, this::saveLTR212Settings);
    }

    private void saveLTR24Settings() {
        getADCInstance();
        saveADCSettings(Crate.LTR24);
    }

    private void saveLTR27Settings() {
        System.out.println("LTR27 settings saved");
    }

    private void saveLTR34Settings() {
        getDACInstance();
        saveDACSettings(Crate.LTR34);
    }

    private void saveLTR212Settings() {
        getADCInstance();
        saveADCSettings(Crate.LTR212);
    }

    private void getADCInstance() {
        adc = (ADC) crate.getModulesList().get(slot);
    }

    private void saveADCSettings(String moduleName) {
        if (isEditMode) {
            updateADCFields();
            updateCalibration();
        } else {
            setADCSettingsFields();
            setADCModuleSettings(moduleName);
            addNewModule();
            addNewCalibrationSettings();
        }
    }

    private void updateADCFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == adc.getSlot()) {
                module.setCheckedChannels(adc.getCheckedChannels());
                module.setChannelsTypes(adc.getTypeOfChannels());
                module.setMeasuringRanges(adc.getMeasuringRanges());
                module.setChannelsDescription(adc.getDescriptions());
                module.setSettings(String.valueOf(adc.moduleSettingsToString()));

                updateModuleSettings(module);
            }
        }
    }

    private void updateModuleSettings(Modules module) {
        ModulesRepository.updateModules(module);
    }

    private void updateCalibration() {
        List<Calibration> allCalibrations = CalibrationsRepository.getAllCalibrations();

        for (Calibration calibration : allCalibrations) {
            if (calibration.getModuleId() == adc.getModuleId()) {
                calibration.setCalibrationSettings(adc.getCalibrationSettings());
                calibration.setCalibrationCoefficients(adc.getCalibrationCoefficients());
                CalibrationsRepository.updateCalibration(calibration);
            }
        }
    }

    private void addNewModule() {
        Modules module = new Modules(moduleSettings);
        ModulesRepository.insertModule(module);
        moduleId = module.getId();
    }

    private void addNewCalibrationSettings() {
        Calibration calibration = new Calibration(testProgramId, moduleId, adc.getCalibrationSettings(),
                adc.getCalibrationCoefficients());
        CalibrationsRepository.insertCalibration(calibration);
    }

    private void setADCModuleSettings(String moduleName) {
        moduleSettings = new HashMap<>();
        StringBuilder checkedChannelsLine = new StringBuilder();
        StringBuilder channelsTypesLine = new StringBuilder();
        StringBuilder measuringRangesLine = new StringBuilder();
        StringBuilder channelsDescriptionsLine = new StringBuilder();

        for (int i = 0; i < adc.getChannelsCount(); i++) {
            checkedChannelsLine.append(checkedChannels[i]).append(", ");
            channelsTypesLine.append(typesOfChannels[i]).append(", ");
            measuringRangesLine.append(measuringRanges[i]).append(", ");
            channelsDescriptionsLine.append(descriptions[i]);
        }

        moduleSettings.put("Test program id", String.valueOf(testProgramId));
        moduleSettings.put("Module type", moduleName);
        moduleSettings.put("Slot", String.valueOf(slot));
        moduleSettings.put("Checked channels", String.valueOf(checkedChannelsLine));
        moduleSettings.put("Channels types", String.valueOf(channelsTypesLine));
        moduleSettings.put("Measuring ranges", String.valueOf(measuringRangesLine));
        moduleSettings.put("Channels description", String.valueOf(channelsDescriptionsLine));
        moduleSettings.put("Module HardwareSettings", String.valueOf(adc.moduleSettingsToString()));
    }

    private void getDACInstance() {
        dac = (DAC) crate.getModulesList().get(slot);
    }

    private void saveDACSettings(String moduleName) {
        if (isEditMode) {
            updateDACFields();
        } else {
            setDACSettingsFields();
            setDACSettings(moduleName);
            addNewModule();
        }
    }

    private void setDACSettings(String moduleName) {
        moduleSettings = new HashMap<>();
        StringBuilder checkedChannelsLine = new StringBuilder();
        StringBuilder channelsDescriptionsLine = new StringBuilder();
        StringBuilder amplitudesLine = new StringBuilder();
        StringBuilder frequenciesLine = new StringBuilder();
        StringBuilder phasesLine = new StringBuilder();

        for (int i = 0; i < dac.getChannelsCount(); i++) {
            checkedChannelsLine.append(checkedChannels[i]).append(", ");
            amplitudesLine.append(amplitudes[i]).append(", ");
            channelsDescriptionsLine.append(descriptions[i]).append(", ");
            frequenciesLine.append(frequencies[i]).append(", ");
            phasesLine.append(phases[i]).append(", ");
        }

        moduleSettings.put("Test program id", String.valueOf(testProgramId));
        moduleSettings.put("Module type", moduleName);
        moduleSettings.put("Slot", String.valueOf(slot));
        moduleSettings.put("Checked channels", checkedChannelsLine.toString());
        moduleSettings.put("Channels description", channelsDescriptionsLine.toString());
        moduleSettings.put("Amplitudes", amplitudesLine.toString());
        moduleSettings.put("Frequencies", frequenciesLine.toString());
        moduleSettings.put("Phases", phasesLine.toString());
    }

    private void updateDACFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == dac.getSlot()) {
                module.setCheckedChannels(dac.getCheckedChannels());
                module.setAmplitudes(dac.getAmplitudes());
                module.setChannelsDescription(dac.getDescriptions());
                module.setFrequencies(dac.getFrequencies());
                module.setPhases(dac.getPhases());

                updateModuleSettings(module);
            }
        }
    }

    public void loadChannelsSettings(TestProgram testProgram, Crate crate, int selectedCrate) {
        setFields(testProgram, crate, selectedCrate);
        fillModulesList();
        loadSettings();
    }

    private void setFields(TestProgram testProgram, Crate crate, int selectedCrate) {
        this.testProgram = testProgram;
        this.crate = crate;
        this.crateSerialNumber = testProgram.getCrateSerialNumber();
        this.modulesNames = crate.getModulesNames(selectedCrate);
    }

    private void fillModulesList() {
        modules = crate.getModulesList();
        testProgramId = testProgram.getId();

        findModules();
    }

    private void findModules() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId) {
                modulesNames.add(module.getModuleType() + " (Слот " + module.getSlot() + ")");
            }
        }
    }

    private void loadSettings() {
        addLoadModulesInstructions();
        for (moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            moduleType = modulesNames.get(moduleIndex).split(" ")[0];
            slot = parseSlotNumber(moduleIndex);
            runInstructions();
        }
    }

    private void addLoadModulesInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::loadADCSettings);
        instructions.put(Crate.LTR27, this::loadLTR27Settings);
        instructions.put(Crate.LTR34, this::loadDACSettings);
        instructions.put(Crate.LTR212, this::loadADCSettings);
    }

    private void loadLTR27Settings() {
        System.out.println("LTR27 settings loaded");
    }

    private void loadADCSettings() {
        adc = (ADC) modules.get(slot);
        channels = adc.getChannelsCount();
        setADCSettingsFields();

        for (Modules module : ModulesRepository.getAllModules()) {
            parseADCSettings(module);
        }
    }

    private void parseADCSettings(Modules module) {
        if (slot == module.getSlot() & testProgramId == module.getTestProgramId()) {
            parseChannelsSettings(module);
            parseModuleSettings(module);
            loadCalibrationSettings(module);
        }
    }

    private void parseChannelsSettings(Modules module) {
        String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", 5);
        String[] parsedChannelsTypes = module.getChannelsTypes().split(", ", 5);
        String[] parsedMeasuringRanges = module.getMeasuringRanges().split(", ", 5);
        String[] parsedChannelsDescriptions = module.getChannelsDescription().split(", ", 5);

        adc.setSlot(slot);
        adc.setModuleId(module.getId());

        for (int i = 0; i < channels; i++) {
            checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
            typesOfChannels[i] = Integer.parseInt(parsedChannelsTypes[i]);
            measuringRanges[i] = Integer.parseInt(parsedMeasuringRanges[i]);
            descriptions[i] = parsedChannelsDescriptions[i];
        }
    }

    private void parseModuleSettings(Modules module) {
        String settings = module.getSettings();
        adc.parseModuleSettings(settings);
    }

    private void loadCalibrationSettings(Modules module) {
        long moduleId = module.getId();
        List<Calibration> allCalibrations = CalibrationsRepository.getAllCalibrations();

        for (Calibration calibration : allCalibrations) {
            if (calibration.getModuleId() == moduleId) {
                adc.setCalibrationSettings(calibration.getCalibrationSettings());
                adc.setCalibrationCoefficients(calibration.getCalibrationCoefficients());
            }
        }
    }

    private void loadDACSettings() {
        dac = (DAC) modules.get(slot);
        channels = dac.getChannelsCount();
        setDACSettingsFields();

        for (Modules module : ModulesRepository.getAllModules()) {
            parseDACSettings(module);
        }
    }

    private void parseDACSettings(Modules module) {
        if (slot == module.getSlot() & testProgramId == module.getTestProgramId()) {
            String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", 9);
            String[] parsedChannelsDescription = module.getChannelsDescription().split(", ", 9);
            String[] parsedAmplitudes = module.getAmplitudes().split(", ", 9);
            String[] parsedFrequencies = module.getFrequencies().split(", ", 9);
            String[] parsedPhases = module.getPhases().split(", ", 9);

            dac.setSlot(slot);
            dac.setModuleId(module.getId());

            for (int i = 0; i < channels; i++) {
                checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
                descriptions[i] = parsedChannelsDescription[i];
                amplitudes[i] = Integer.parseInt(parsedAmplitudes[i]);
                frequencies[i] = Integer.parseInt(parsedFrequencies[i]);
                phases[i] = Integer.parseInt(parsedPhases[i]);
            }
        }
    }

    public ObservableList<String> getModulesNames() {
        return modulesNames;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}