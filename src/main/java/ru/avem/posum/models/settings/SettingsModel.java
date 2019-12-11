package ru.avem.posum.models.settings;

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

/**
 * Модель настроек программы испытаний
 */

public class SettingsModel implements BaseController {
    private ADC adc; // инстанс модуля АЦП
    private double[] amplitudes; // амплитуды каналов модуля
    private ArrayList<List<Double>> calibrationCoefficients; // градуировочные коэффициенты модуля
    private ArrayList<List<String>> calibrationSettings; // настройки градуировки модуля
    private int channelsCount; // количество каналов
    private String[] descriptions; // описания каналов модуля
    private int[] typesOfChannels; // режимы работы модуля
    private boolean[] checkedChannels; // задействованные каналы модуля
    private ControllerManager cm;
    private int channels; // количесвто каналов модуля
    private String crateSerialNumber; // серийный номер крейта
    private Crate crate; // инстанс крейта
    private DAC dac; // инстанс модуля ЦАП
    private double[] dc; // постоянные составляющие каналов модуля
    private String firPath; // путь к КИХ фильтру
    private double[] frequencies; // частоты каналов модуля
    private String iirPath; // путь к БИХ фильтру
    private boolean isEditMode; // флаг редактирования программы испытаний
    private HashMap<String, Actionable> instructions = new HashMap<>(); // список команд для выполнения
    private int[] measuringRanges; // диапазоны измерений каналов модуля
    private long moduleId; // id модуля
    private HashMap<Integer, Module> modules; // список модулей с номером слота
    private int moduleIndex; // индекс модуля
    private ObservableList<String> modulesNames; // список модулей
    private HashMap<String, String> moduleSettings; // список конфигураций модулей
    private String moduleType; // название модуля
    private int[] phases; // фазы каналов модуля
    private int slot; // номер слота модуля
    private TestProgram testProgram; // модель программы испытаний
    private long testProgramId; // id программы испытаний

    // Создает инстансы модулей
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

    // Добавляет команды инициализации модулей
    private void addInitModuleInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::initLTR24Instance);
        instructions.put(Crate.LTR27, this::initLTR27Instance);
        instructions.put(Crate.LTR34, this::initLTR34Instance);
        instructions.put(Crate.LTR212, this::initLTR212Instance);
    }

    // Выполняет команды
    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    // Инициализирует инстанс модуля LTR24
    private void initLTR24Instance() {
        adc = new LTR24();
        setModuleSettings(adc);
        setDefaultADCSettings(0, 1);
        adc.setData(new double[117188]); // частота дискретизации по умолчанию
        saveModuleInstance(adc);
    }

    // Инициализирует инстанс модуля LTR27
    private void initLTR27Instance() {
        adc = new LTR27();
        setModuleSettings(adc);
        setDefaultADCSettings(0, 1); //TODO УДАЛИТЬ
        adc.setData(new double[1000]); // частота дискретизации по умолчанию
        saveModuleInstance(adc);
    }

    // Инициализирует инстанс модуля LTR34
    private void initLTR34Instance() {
        dac = new LTR34();
        setModuleSettings(dac);
        setDefaultDACSettings();
        dac.setData(new double[31250]); // частота дискретизации по умолчанию
        saveModuleInstance(dac);
    }

    // Инициализирует инстанс модуля LTR212
    private void initLTR212Instance() {
        adc = new LTR212();
        setModuleSettings(adc);
        setDefaultADCSettings(0, 3);
        adc.setData(new double[7680]); // частта дискретизации по умолчанию
        saveModuleInstance(adc);
    }

    // Задает настройки модуля
    private void setModuleSettings(Module module) {
        slot = parseSlotNumber(moduleIndex);
        module.setSlot(slot);
        module.setCrateSerialNumber(crateSerialNumber);
        module.setStatus("");
    }

    // Считывает номер слота
    public int parseSlotNumber(int moduleIndex) {
        String moduleName = modulesNames.get(moduleIndex);
        return Utils.parseSlotNumber(moduleName);
    }

    // Задает настройки модуля АЦП по умолчанию
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

    // Задает поля инстанса модуля АЦП
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
        channelsCount = adc.getChannelsCount();
        firPath = adc.getFirPath();
        iirPath = adc.getIirPath();
    }

    // Сохраняет инстанс модуля
    private void saveModuleInstance(Module module) {
        crate.getModulesList().put(slot, module);
    }

    // Задает настройки модуля ЦАП по умолчанию
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

    // Задает значения полей инстанса ЦАП
    private void setDACSettingsFields() {
        channelsCount = dac.getChannelsCount();
        checkedChannels = dac.getCheckedChannels();
        typesOfChannels = null;
        measuringRanges = null;
        descriptions = dac.getDescriptions();
        amplitudes = dac.getAmplitudes();
        dc = dac.getDc();
        frequencies = dac.getFrequencies();
        phases = dac.getPhases();
    }

    // Сохраняет общую инфомацию о программе испытаний
    public void saveGeneralSettings(HashMap<String, String> generalSettings, boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (isEditMode) {
            updateTestProgram(generalSettings);
        } else {
            createNewTestProgram(generalSettings);
        }
    }

    // Обновляет программу испытаний
    private void updateTestProgram(HashMap<String, String> generalSettings) {
        setTestProgramFields(generalSettings);
        TestProgramRepository.updateTestProgram(testProgram);
    }

    // Задает поля модели программы испытаний
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

    // Создает новую программу испытаний
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

    // Сохраняет настройки крейта
    public void saveHardwareSettings(boolean isEditMode) {
        this.isEditMode = isEditMode;
        this.testProgramId = testProgram.getId();
        saveModulesSettings();
    }

    // Сохраняет настройки модулей
    private void saveModulesSettings() {
        addSaveModuleInstructions();
        for (moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            moduleType = modulesNames.get(moduleIndex).split(" ")[0];
            slot = parseSlotNumber(moduleIndex);
            runInstructions();
        }
    }

    // Добавляет команды для сохранения настроек модулей
    private void addSaveModuleInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::saveLTR24Settings);
        instructions.put(Crate.LTR27, this::saveLTR27Settings);
        instructions.put(Crate.LTR34, this::saveLTR34Settings);
        instructions.put(Crate.LTR212, this::saveLTR212Settings);
    }

    // Сохраняет настройки модуля LTR24
    private void saveLTR24Settings() {
        getADCInstance();
        saveADCSettings(Crate.LTR24);
    }

    // Сохраняет настройки модуля LTR27
    private void saveLTR27Settings() {
        getADCInstance();
        saveADCSettings(Crate.LTR27);
    }

    // Сохраняет настройки модуля LTR34
    private void saveLTR34Settings() {
        getDACInstance();
        saveDACSettings(Crate.LTR34);
    }

    // Сохраняет настройки модуля LTR212
    private void saveLTR212Settings() {
        getADCInstance();
        saveADCSettings(Crate.LTR212);
    }

    // Возвращает инстанс модуля АЦП
    private void getADCInstance() {
        adc = (ADC) crate.getModulesList().get(slot);
    }

    // Сохраняет настройки модуля АЦП
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

    // Обновляет поля инстанса модуля АЦП
    private void updateADCFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == adc.getSlot()) {
                module.setChannelsCount(adc.getChannelsCount());
                module.setCheckedChannels(adc.getCheckedChannels());
                module.setTypesOfChannels(adc.getTypeOfChannels());
                module.setMeasuringRanges(adc.getMeasuringRanges());
                module.setChannelsDescription(adc.getDescriptions());
                module.setSettings(String.valueOf(adc.moduleSettingsToString()));
                module.setFirPath(adc.getFirPath());
                module.setIirPath(adc.getIirPath());
                module.setDataLength(String.valueOf(adc.getData().length));

                updateModuleSettings(module);
            }
        }
    }

    // Обновляет настройки модуля
    private void updateModuleSettings(Modules module) {
        ModulesRepository.updateModules(module);
    }

    // Обновляет настройки калибровки модуля
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

    // Записывает настройки модуля в базу данных
    private void addNewModule() {
        Modules module = new Modules(moduleSettings);
        ModulesRepository.insertModule(module);
        moduleId = module.getId();
    }

    // Записывает настройки градуировки в базу данных
    private void addNewCalibrationSettings() {
        Calibration calibration = new Calibration(testProgramId, moduleId, adc.getCalibrationSettings(),
                adc.getCalibrationCoefficients());
        CalibrationsRepository.insertCalibration(calibration);
    }

    // Задает настройки модуля АЦП
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
        moduleSettings.put("Channels count", String.valueOf(adc.getChannelsCount()));
        moduleSettings.put("Checked channels", String.valueOf(checkedChannelsLine));
        moduleSettings.put("Channels types", String.valueOf(channelsTypesLine));
        moduleSettings.put("Measuring ranges", String.valueOf(measuringRangesLine));
        moduleSettings.put("Channels description", String.valueOf(channelsDescriptionsLine));
        moduleSettings.put("Module HardwareSettings", String.valueOf(adc.moduleSettingsToString()));
        moduleSettings.put("FIR path", adc.getFirPath());
        moduleSettings.put("IIR path", adc.getIirPath());
        moduleSettings.put("Data length", String.valueOf(adc.getData().length));
    }

    // Возвращает инстанс ЦАП
    private void getDACInstance() {
        dac = (DAC) crate.getModulesList().get(slot);
    }

    // Сохраняет настройки ЦАП
    private void saveDACSettings(String moduleName) {
        if (isEditMode) {
            updateDACFields();
        } else {
            setDACSettingsFields();
            setDACSettings(moduleName);
            addNewModule();
        }
    }

    // Обновляет значения полей инстанса модуля ЦАП
    private void updateDACFields() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId && module.getSlot() == dac.getSlot()) {
                module.setChannelsCount(dac.getChannelsCount());
                module.setCheckedChannels(dac.getCheckedChannels());
                module.setAmplitudes(dac.getAmplitudes());
                module.setDc(dac.getDc());
                module.setChannelsDescription(dac.getDescriptions());
                module.setFrequencies(dac.getFrequencies());
                module.setPhases(dac.getPhases());
                module.setDataLength(String.valueOf(dac.getData().length));

                updateModuleSettings(module);
            }
        }
    }

    // Задает настройки модуля ЦАП
    private void setDACSettings(String moduleName) {
        moduleSettings = new HashMap<>();
        StringBuilder checkedChannelsLine = new StringBuilder();
        StringBuilder channelsDescriptionsLine = new StringBuilder();
        StringBuilder amplitudesLine = new StringBuilder();
        StringBuilder dcLine = new StringBuilder();
        StringBuilder frequenciesLine = new StringBuilder();
        StringBuilder phasesLine = new StringBuilder();

        for (int i = 0; i < dac.getChannelsCount(); i++) {
            checkedChannelsLine.append(checkedChannels[i]).append(", ");
            amplitudesLine.append(amplitudes[i]).append(", ");
            dcLine.append(dc[i]).append(", ");
            channelsDescriptionsLine.append(descriptions[i]).append(", ");
            frequenciesLine.append(frequencies[i]).append(", ");
            phasesLine.append(phases[i]).append(", ");
        }

        moduleSettings.put("Test program id", String.valueOf(testProgramId));
        moduleSettings.put("Module type", moduleName);
        moduleSettings.put("Slot", String.valueOf(slot));
        moduleSettings.put("Channels count", String.valueOf(channelsCount));
        moduleSettings.put("Checked channels", checkedChannelsLine.toString());
        moduleSettings.put("Channels description", channelsDescriptionsLine.toString());
        moduleSettings.put("Amplitudes", amplitudesLine.toString());
        moduleSettings.put("Dc", dcLine.toString());
        moduleSettings.put("Frequencies", frequenciesLine.toString());
        moduleSettings.put("Phases", phasesLine.toString());
        moduleSettings.put("Data length", String.valueOf(dac.getData().length));
    }

    // Загружает настройки каналов
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

    // Заполняет список модулей
    private void fillModulesList() {
        modules = crate.getModulesList();
        testProgramId = testProgram.getId();
        findModules();
    }

    // Заполнаяет список модулей
    private void findModules() {
        for (Modules module : ModulesRepository.getAllModules()) {
            if (module.getTestProgramId() == testProgramId) {
                modulesNames.add(module.getModuleType() + " (Слот " + module.getSlot() + ")");
            }
        }
    }

    // Загружает настройки
    private void loadSettings() {
        addLoadModulesInstructions();
        for (moduleIndex = 0; moduleIndex < modulesNames.size(); moduleIndex++) {
            moduleType = modulesNames.get(moduleIndex).split(" ")[0];
            slot = parseSlotNumber(moduleIndex);
            runInstructions();
        }
    }

    // Добавляет команды загрузки настроек
    private void addLoadModulesInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::loadADCSettings);
        instructions.put(Crate.LTR27, this::loadADCSettings);
        instructions.put(Crate.LTR34, this::loadDACSettings);
        instructions.put(Crate.LTR212, this::loadADCSettings);
    }

    // Загружает настройки АЦП
    private void loadADCSettings() {
        adc = (ADC) modules.get(slot);

        for (Modules module : ModulesRepository.getAllModules()) {
            parseADCSettings(module);
        }
    }

    // Считывает настройки АЦП
    private void parseADCSettings(Modules module) {
        if (slot == module.getSlot() & testProgramId == module.getTestProgramId()) {
            channels = adc.getChannelsCount();
            setADCSettingsFields();
            parseChannelsSettings(module);
            parseModuleSettings(module);
            loadCalibrationSettings(module);
        }
    }

    // Считывает настройки каналов
    private void parseChannelsSettings(Modules module) {
        int channelsCount = module.getChannelsCount();

        String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", channelsCount + 1);
        String[] parsedChannelsTypes = module.getTypesOfChannels().split(", ", channelsCount + 1);
        String[] parsedMeasuringRanges = module.getMeasuringRanges().split(", ", channelsCount + 1);
        String[] parsedChannelsDescriptions = module.getChannelsDescriptions().split(", ", channelsCount + 1);

        adc.setChannelsCount(channelsCount);
        adc.setSlot(slot);
        adc.setModuleId(module.getId());
        adc.setFirPath(module.getFirPath());
        adc.setIirPath(module.getIirPath());
        adc.setData(new double[module.getDataLength()]);

        for (int i = 0; i < channelsCount; i++) {
            checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
            typesOfChannels[i] = Integer.parseInt(parsedChannelsTypes[i]);
            measuringRanges[i] = Integer.parseInt(parsedMeasuringRanges[i]);
            descriptions[i] = parsedChannelsDescriptions[i];
        }
    }

    // Считывает настройки модуля
    private void parseModuleSettings(Modules module) {
        String settings = module.getSettings();
        adc.parseModuleSettings(settings);
    }

    // Загружает настройки градуировок
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

    // Загружает настройки ЦАП
    private void loadDACSettings() {
        dac = (DAC) modules.get(slot);
        channels = dac.getChannelsCount();
        setDACSettingsFields();

        for (Modules module : ModulesRepository.getAllModules()) {
            parseDACSettings(module);
        }
    }

    // Считывает настройки ЦАП
    private void parseDACSettings(Modules module) {
        if (slot == module.getSlot() & testProgramId == module.getTestProgramId()) {
            String[] parsedCheckedChannels = module.getCheckedChannels().split(", ", 9);
            String[] parsedChannelsDescription = module.getChannelsDescriptions().split(", ", 9);
            String[] parsedAmplitudes = module.getAmplitudes().split(", ", 9);
            String[] parsedDc = module.getDc().split(", ", 9);
            String[] parsedFrequencies = module.getFrequencies().split(", ", 9);
            String[] parsedPhases = module.getPhases().split(", ", 9);

            dac.setChannelsCount(module.getChannelsCount());
            dac.setSlot(slot);
            dac.setModuleId(module.getId());
            dac.setData(new double[module.getDataLength()]);

            for (int i = 0; i < dac.getChannelsCount(); i++) {
                checkedChannels[i] = Boolean.parseBoolean(parsedCheckedChannels[i]);
                descriptions[i] = parsedChannelsDescription[i];
                amplitudes[i] = Double.parseDouble(parsedAmplitudes[i]);
                dc[i] = Double.parseDouble(parsedDc[i]);
                frequencies[i] = Double.parseDouble(parsedFrequencies[i]);
                phases[i] = Integer.parseInt(parsedPhases[i]);
            }
        }
    }

    // Возвращает список модулей
    public ObservableList<String> getModulesNames() {
        return modulesNames;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }
}