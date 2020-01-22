package ru.avem.posum.hardware;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LTR27 extends ADC {
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!
    public static final int MAX_FREQUENCY = 1000; // максимальная частота дискретизации модуля
    public static final int MAX_SUBMODULES = 8; // максимальное количество мезонинов в модуле
    private final int DESCRIPTIONS = 3; // количество строк описания мезонина
    private String[][] submodulesDescription = new String[MAX_SUBMODULES][DESCRIPTIONS]; // информация о субмодулях

    public LTR27() {
        initDescription();
        initializeModuleSettings();
    }

    private void initDescription() {
        for (int i = 0; i < MAX_SUBMODULES; i++) {
            for (int j = 0; j < DESCRIPTIONS; j++) {
                submodulesDescription[i][j] = "";
            }
        }
    }

    public String[][] getInfo() {
        openConnection();
        checkConnection();
        if (!status.equals("Потеряно соединение с крейтом")) {
            initializeModule();
            closeConnection();
        }
        return submodulesDescription;
    }

    // Открывает соединение с модулем
    @Override
    public void openConnection() {
        status = openConnection(crateSerialNumber, getSlot());
        setConnectionOpen(checkStatus());
    }

    // Проверяет соединение с модулем
    @Override
    public void checkConnection() {
        Crate crate = new Crate();

        if (crate.getCratesNames().isPresent()) {
            ObservableList<String> cratesNames = crate.getCratesNames().get();

            if (!cratesNames.isEmpty()) {
                status = checkConnection(getSlot());
            } else {
                status = "Потеряно соединение с крейтом";
                openConnection();
            }
        }
    }

    // Инициализирует модуль переданными параметрами, записывает информацию о субмодулях
    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getLTR27ModuleSettings(), submodulesDescription);

        for (int i = 0; i < MAX_SUBMODULES; i++) {
            submodulesDescription[i][2] = encode(submodulesDescription[i][2]); // расшифровка русских символов
        }
    }

    // Определяет частоту дискретизации модуля
    @Override
    public void defineFrequency() {
        getFrequency(getSlot());
    }

    // Запускает измерения
    @Override
    public void start() {
        status = start(getSlot());
    }

    // Записывает в массив измеренные значения
    @Override
    public void write(double[] data, double[] timeMarks) {
        write(getSlot(), data, timeMarks);
    }

    // Завершает измерения
    @Override
    public void stop() {
        status = stop(getSlot());
    }

    // Разрывает соединение с модулем
    @Override
    public void closeConnection() {
        if (isConnectionOpen()) {
            status = closeConnection(getSlot());
            setConnectionOpen(checkStatus());
        }
    }

    public native String openConnection(String crate, int slot);

    private native String checkConnection(int slot);

    public native String initialize(int slot, int[] moduleSettings, String[][] submodulesDescription);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR27Library");
    }

    // Задает настройки модуля по умолчанию
    private void initializeModuleSettings() {
        getSettingsOfModule().put(Settings.FREQUENCY, 9); // частота дискретизации 100 Гц
        channelsCount = MAX_SUBMODULES;
        setCheckedChannels(new boolean[MAX_SUBMODULES]);
        setTypeOfChannels(new int[MAX_SUBMODULES]);
        setMeasuringRanges(new int[MAX_SUBMODULES]);

        String[] descriptions = new String[MAX_SUBMODULES];
        Arrays.fill(descriptions, ", ");
        setDescriptions(descriptions);
    }

    // Возвращает настройки модуля
    private int[] getLTR27ModuleSettings() {
        List<Integer> settingsList = new ArrayList<>();
        settingsList.add(getSettingsOfModule().get(Settings.FREQUENCY));

        int[] settings = new int[settingsList.size()];

        for (int i = 0; i < settingsList.size(); i++) {
            settings[i] = settingsList.get(i);
        }

        return settings;
    }

    // Возвращает настройки модуля
    @Override
    public StringBuilder moduleSettingsToString() {
        StringBuilder settings = new StringBuilder();
        settings.append(settingsOfModule.get(Settings.FREQUENCY)).append(", ");
        return settings;
    }

    // Считывает настройки модуля
    @Override
    public void parseModuleSettings(String settings) {
        String[] separatedSettings = settings.split(", ");
        settingsOfModule.put(Settings.FREQUENCY, Integer.valueOf(separatedSettings[0]));
    }

    // Возвращает частоту дискретизации модуля
    @Override
    public double getFrequency() {
        defineFrequency();
        return frequency;
    }
}
