package ru.avem.posum.hardware;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class LTR24 extends ADC {
    private double frequency; // значение поля устанавливается из библиотеки dll, не удалять!

    public LTR24() {
        initializeModuleSettings();
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

    // Инициализирует модуль переданными параметрами
    @Override
    public void initializeModule() {
        status = initialize(getSlot(), getTypeOfChannels(), getMeasuringRanges(), getLTR24ModuleSettings());
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
        write(getSlot(), data, timeMarks, data.length);
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

    public native String initialize(int slot, int[] channelsTypes, int[] measuringRanges, int[] moduleSettings);

    public native String getFrequency(int slot);

    public native String start(int slot);

    public native String write(int slot, double[] data, double[] timeMarks, int dataLength);

    public native String stop(int slot);

    public native String closeConnection(int slot);

    static {
        System.loadLibrary("LTR24Library");
    }

    // Задает настройки модуля по умолчанию
    private void initializeModuleSettings() {
        getSettingsOfModule().put(Settings.FREQUENCY, 0); // частота дискретизации 117 кГц
    }

    // Возвращает настройки модуля
    private int[] getLTR24ModuleSettings() {
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
        return frequency;
    }
}
