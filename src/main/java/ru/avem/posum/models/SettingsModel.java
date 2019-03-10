package ru.avem.posum.models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.db.ModulesRepository;
import ru.avem.posum.db.TestProgramRepository;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.db.models.TestProgram;
import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SettingsModel implements BaseController {
    private ADC adc;
    private int[] amplitudes;
    private String[] channelsDescription;
    private int[] channelsTypes;
    private boolean[] checkedChannels;
    private ControllerManager cm;
    private String crate;
    private CrateModel crateModel;
    private DAC dac;
    private int[] frequencies;
    private boolean isEditMode;
    private int[] measuringRanges;
    private List<Pair<Integer, Module>> modules;
    private ObservableList<String> modulesNames;
    private HashMap<String, StringBuffer> moduleSettings = new HashMap<>();
    private int[] phases;
    private int slot;
    private TestProgram testProgram;
    private long testProgramId;

    public void createModulesInstances(ObservableList<String> modulesNames) {
        this.crate = cm.getCrate();
        this.modulesNames = modulesNames;
        crateModel = cm.getCrateModelInstance();

        for (int moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            switch (modulesNames.get(moduleIndex).split(" ")[0]) {
                case CrateModel.LTR24:
                    adc = new LTR24();
                    setModuleSettings(adc, moduleIndex);
                    setDefaultADCSettings(0, 1);
                    saveModuleInstance(adc);
                    break;
                case CrateModel.LTR34:
                    dac = new LTR34();
                    setModuleSettings(dac, moduleIndex);
                    setDefaultDACSettings();
                    saveModuleInstance(dac);
                    break;
                case CrateModel.LTR212:
                    adc = new LTR212();
                    setModuleSettings(adc, moduleIndex);
                    setDefaultADCSettings(2, 3);
                    saveModuleInstance(adc);
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

    private void setDefaultADCSettings(int channelsType, int measuringRange) {
        setADCSettingsFields();

        for (int i = 0; i < adc.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            channelsTypes[i] = channelsType;
            measuringRanges[i] = measuringRange;
            channelsDescription[i] = ", ";
        }
    }

    private void setADCSettingsFields() {
        checkedChannels = adc.getCheckedChannels();
        channelsTypes = adc.getChannelsTypes();
        measuringRanges = adc.getMeasuringRanges();
        channelsDescription = adc.getChannelsDescription();
    }

    private void saveModuleInstance(Module module) {
        crateModel.getModulesList().add(new Pair<>(slot, module));
    }

    private void setDefaultDACSettings() {
        setDACSettingsFields();

        for (int i = 0; i < dac.getChannelsCount(); i++) {
            checkedChannels[i] = false;
            amplitudes[i] = 0;
            frequencies[i] = 0;
            phases[i] = 0;
        }
    }

    private void setDACSettingsFields() {
        checkedChannels = dac.getCheckedChannels();
        amplitudes = dac.getAmplitudes();
        frequencies = dac.getFrequencies();
        phases = dac.getPhases();
    }

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

    public void saveHardwareSettings(boolean isEditMode) {
        this.isEditMode = isEditMode;
        this.testProgramId = testProgram.getId();
        int ltr24Index = 0; // индексы сохраняют номер последнего взятого объекта
        int ltr212Index = 0;
        int ltr34Index = 0;

        for (String modulesName : modulesNames) {
            switch (modulesName.split(" ")[0]) {
                case CrateModel.LTR24:
                    getADCInstance(ltr24Index++);
                    saveADCSettings();
                    break;
                case CrateModel.LTR34:
                    getDACInstance(ltr34Index++);
                    saveDACSettings();
                    break;
                case CrateModel.LTR212:
                    getADCInstance(ltr212Index++);
                    saveADCSettings();
                    break;
            }
        }
    }

    private void getADCInstance(int index) {
        adc = (ADC) crateModel.getModulesList().get(index).getValue();
    }

    private void saveADCSettings() {
        if (isEditMode) {
            updateADCFields();
        } else {
            addNewModule();
        }

    }

    private void updateADCFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == adc.getSlot()) {
                module.setCheckedChannels(adc.getCheckedChannels());
                module.setChannelsTypes(adc.getChannelsTypes());
                module.setMeasuringRanges(adc.getMeasuringRanges());
                module.setChannelsDescription(adc.getChannelsDescription());

                updateModuleSettings(module);
            }
        }
    }

    private void updateModuleSettings(Modules module) {
        ModulesRepository.updateModules(module);
    }


    private void addNewModule() {
        Modules module = new Modules(moduleSettings);
        ModulesRepository.insertModules(module);
    }

    private void getDACInstance(int index) {
        dac = (DAC) crateModel.getModulesList().get(index).getValue();
    }

    private void saveDACSettings() {
        if (isEditMode) {
            updateDACFields();
        } else {
            addNewModule();
        }
    }

    private void updateDACFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == adc.getSlot()) {
                module.setCheckedChannels(dac.getCheckedChannels());
                module.setAmplitudes(dac.getAmplitudes());
                module.setFrequencies(dac.getFrequencies());
                module.setPhases(dac.getPhases());

                updateModuleSettings(module);
            }
        }
    }

    public void loadChannelsSettings(TestProgram testProgram, CrateModel crateModel, int selectedCrate) {
        setFields(testProgram, crateModel, selectedCrate);
        fillModulesList();
        loadSettings();
    }

    private void setFields(TestProgram testProgram, CrateModel crateModel, int selectedCrate) {
        this.testProgram = testProgram;
        this.crateModel = crateModel;
        this.modulesNames = crateModel.getModulesNames(selectedCrate);
    }

    private void fillModulesList() {
        modules = crateModel.getModulesList();
        testProgramId = testProgram.getId();

        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId) {
                modules.add(new Pair<>(module.getSlot(), new Module()));
            }
        }
    }

    private void loadSettings() {
        for (int i = 0; i < modulesNames.size(); i++) {
            slot = parseSlotNumber(i);

            switch (modulesNames.get(i).split(" ")[0]) {
                case CrateModel.LTR24:
                case CrateModel.LTR212:
                    adc = (ADC) modules.get(slot).getValue();
                    loadADCSettings();
                    break;
                case CrateModel.LTR34:
                    dac = (DAC) modules.get(slot).getValue();
                    loadDACSettings();
                    break;
            }
        }
    }

    private void loadADCSettings() {
        setADCSettingsFields();
        int CHANNELS = adc.getChannelsCount();

        for (Modules module : ModulesRepository.getAllModules()) {
            adc.setSlot(slot);
            adc.setModuleId(module.getId());

            if (slot == module.getSlot()) {
                String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", 5);
                String[] parsedChannelsTypes = module.getChannelsTypes().split(", ", 5);
                String[] parsedMeasuringRanges = module.getMeasuringRanges().split(", ", 5);
                String[] parsedChannelsDescriptions = module.getChannelsDescription().split(", ", 5);

                for (int i = 0; i < CHANNELS; i++) {
                    checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
                    channelsTypes[i] = Integer.parseInt(parsedChannelsTypes[i]);
                    measuringRanges[i] = Integer.parseInt(parsedMeasuringRanges[i]);
                    parsedChannelsDescriptions[i] = parsedMeasuringRanges[i];
                }
            }
        }
    }

    private void loadDACSettings() {
        setDACSettingsFields();
        int CHANNELS = dac.getChannelsCount();

        for (Modules module : ModulesRepository.getAllModules()) {
            if (slot == module.getSlot()) {
                dac.setSlot(slot);
                dac.setModuleId(module.getId());

                if (slot == module.getSlot()) {
                    String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", 9);
                    String[] parsedAmplitudes = module.getChannelsTypes().split(", ", 9);
                    String[] parsedFrequencies = module.getMeasuringRanges().split(", ", 9);
                    String[] parsedPhases = module.getChannelsDescription().split(", ", 9);

                    for (int i = 0; i < CHANNELS; i++) {
                        checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
                        amplitudes[i] = Integer.parseInt(parsedAmplitudes[i]);
                        frequencies[i] = Integer.parseInt(parsedFrequencies[i]);
                        phases[i] = Integer.parseInt(parsedPhases[i]);
                    }
                }
            }
        }
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}
