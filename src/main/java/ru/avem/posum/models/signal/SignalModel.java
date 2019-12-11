package ru.avem.posum.models.signal;

import javafx.scene.chart.XYChart;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.models.calibration.CalibrationPoint;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.models.signal.SignalParametersModel;
import java.util.HashMap;
import java.util.List;

public class SignalModel {
    private ADC adc; // инстанс модуля
    private double amplitude; // амплитуды каналов
    private double averageCount = 1;
    private double[] buffer;
    private boolean calibrationExists; // флаг наличия градуировки
    private boolean calibrationOfNullExists; // флаг наличия градуированного нуля
    private int channel; // номер канала
    private boolean connectionLost; // флаг потери соединения с модулем
    private double dc; // постоянная составляющая сигнала
    private double frequency; // частота сигнала
    private HashMap<String, Actionable> instructions = new HashMap<>(); // команды для выполнения
    private double loadsCounter; // количество нагружений
    private double lowerBound; // нижняя граница графика
    private LTR24 ltr24; // инстанс модуля LTR24
    private LTR212 ltr212; // инстанс модуля LTR212
    private LTR27 ltr27; // инстанс модуля LTR212
    private String moduleType; // название модуля
    private SignalParametersModel signalParametersModel = new SignalParametersModel(); // модель параметров сигала
    private int rarefactionCoefficient = 10; // коэффициент прореживания
    private double rms; // действующее значение
    private int slot; // номер слота
    private double tickUnit;
    private double upperBound; // верхняя граница графика
    private String valueName = "В"; // название величины вертикальной оси графика

    public void setFields(String moduleType, int slot, int channel) {
        this.moduleType = moduleType;
        this.slot = slot;
        this.channel = channel;
    }

    // Задает инстанс модуля АЦП
    public void defineModuleInstance(HashMap<Integer, Module> modules) {
        adc = (ADC) modules.get(slot);
        getADCInstance();
    }

    private void getADCInstance() {
        addInitModuleInstructions();
        runInstructions();
    }

    // Инициализирует инстанс модуля АЦП
    private void addInitModuleInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::initLTR24Module);
        instructions.put(Crate.LTR212, this::initLTR212Module);
        instructions.put(Crate.LTR27, this::initLTR27Module);
    }

    // Инициализирует инстанс модуля LTR24
    private void initLTR24Module() {
        ltr24 = (LTR24) adc;
        int SAMPLES = (int) ltr24.getFrequency() * ltr24.getChannelsCount();
        ltr24.setData(new double[SAMPLES]);
        ltr24.setRingBufferForCalculation(new RingBuffer(SAMPLES));
        ltr24.setRingBufferForShow(new RingBuffer(SAMPLES));
        ltr24.setTimeMarks(new double[SAMPLES * 2]);
        ltr24.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2));
    }

    // Инициализирует инстанс модуля LTR212
    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        int adcMode = ltr212.getSettingsOfModule().get(ADC.Settings.ADC_MODE);
        int SAMPLES = adcMode == 0 ? 30720 : 152;
        ltr212.setData(new double[SAMPLES]);
        ltr212.setRingBufferForCalculation(new RingBuffer(SAMPLES));
        ltr212.setRingBufferForShow(new RingBuffer(SAMPLES));
        ltr212.setTimeMarks(new double[SAMPLES * 2]);
        ltr212.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2));
    }

    private void initLTR27Module() {
        ltr27 = (LTR27) adc;
        int SAMPLES = (int) ltr27.getFrequency() * ltr27.getChannelsCount();
        ltr27.setData(new double[SAMPLES]);
        ltr27.setRingBufferForCalculation(new RingBuffer(SAMPLES));
        ltr27.setRingBufferForShow(new RingBuffer(SAMPLES));
        ltr27.setTimeMarks(new double[SAMPLES * 2]);
        ltr27.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2));
    }

    // Выполняет инструкции
    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    // Проверяет наличие градуировки
    public void checkCalibration() {
        checkSettingOfNul();
        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);

        if (!calibrationCoefficients.isEmpty()) {
            setCalibrationExists(true);
            signalParametersModel.defineCalibratedBounds(adc);
            setBounds();
            setValueName();
        }
    }

    // Проверяет наличие градуировки нуля
    private void checkSettingOfNul() {
        List<String> calibrations = adc.getCalibrationSettings().get(channel);

        for (String calibration : calibrations) {
            int channelFromCalibration = Integer.parseInt(calibration.substring(9, 10));
            if (channel == channelFromCalibration) {
                signalParametersModel.setShift(0);
                if (CalibrationPoint.parseValueName(calibration).isEmpty()) {
                    signalParametersModel.setShift(CalibrationPoint.parseChannelValue(calibration));
                    calibrationOfNullExists = true;
                    break;
                }
            }
        }
    }

    // Задает параметры для отображения графика
    private void setBounds() {
        this.lowerBound = signalParametersModel.getLowerBound();
        this.upperBound = signalParametersModel.getUpperBound();
        this.tickUnit = signalParametersModel.getTickUnit();
    }

    // Задает название величины вертикальной оси графика
    private void setValueName() {
        this.valueName = signalParametersModel.getCalibratedValueName();
    }

    // Задает название величины вертикальной оси графика по умолчанию
    public void setDefaultValueName() {
        valueName = "В";
    }

    // Записывает данные, полученные от модуля
    public void getData() {
        addReceivingDataInstructions();
        runInstructions();
    }

    // Добавляет команды для записи данных, полученных от модуля
    private void addReceivingDataInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::receive);
        instructions.put(Crate.LTR212, this::receive);
        instructions.put(Crate.LTR27, this::receive);
    }

    // Записывает данные, полученные от модуля
    private void receive() {
        double[] data = adc.getData();
        double[] timeMarks = adc.getTimeMarks();
        RingBuffer ringBufferForCalculation = adc.getRingBufferForCalculation();
        RingBuffer ringBufferForShow = adc.getRingBufferForShow();

        adc.checkConnection();
        connectionLost = !adc.checkStatus();

        if (!connectionLost) {
            adc.write(data, timeMarks);
            ringBufferForCalculation.reset();
            ringBufferForCalculation.put(data);
            ringBufferForShow.reset();
            ringBufferForShow.put(data);
        } else {
            adc.setRingBufferForCalculation(new RingBuffer(ringBufferForCalculation.capacity));
            adc.setRingBufferForShow(new RingBuffer(ringBufferForShow.capacity));
            for (int i = 0; i < adc.getData().length; i++) {
                adc.getData()[i] = 0;
            }
        }
    }

    // Рассчитывает параметры сигнала
    public void calculateData() {
        calculate();
        getSignalParameters();
    }

    // Рассчитывает параметры сигнала
    private void calculate() {
        double[] buffer = new double[adc.getData().length];
        adc.getRingBufferForCalculation().take(buffer, buffer.length);
        signalParametersModel.setFields(adc, channel);
        signalParametersModel.calculateParameters(buffer, averageCount, calibrationExists);
    }

    // Сохраняет параметры сигнала
    private void getSignalParameters() {
        amplitude = signalParametersModel.getPeakValue();
        frequency = signalParametersModel.getSignalFrequency();
        loadsCounter = signalParametersModel.getLoadsCounter();
        rms = signalParametersModel.getRms();
        dc = signalParametersModel.getDc();
    }

    // Сохраняет данные, полученные от модуля в буфер
    public void fillBuffer() {
        buffer = new double[adc.getData().length];
        adc.getRingBufferForShow().take(buffer, buffer.length);
    }

    // Возвращает точку для графика
    public XYChart.Data<Number, Number> getPoint(int valueIndex) {
        if (calibrationExists) {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = signalParametersModel.applyCalibration(adc, buffer[valueIndex]);
            return new XYChart.Data<>(xValue, yValue);
        } else {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = buffer[valueIndex];
            return new XYChart.Data<>(xValue, yValue);
        }
    }

    public ADC getAdc() {
        return adc;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double[] getBuffer() {
        return buffer;
    }

    public double getCalibratedAmplitude() {
        return signalParametersModel.getCalibratedAmplitude();
    }

    public double getCalibratedRms() {
        return signalParametersModel.getCalibratedRms();
    }

    public double getCalibratedZeroShift() {
        return signalParametersModel.getCalibratedDC();
    }

    public int getChannel() {
        return channel;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getLoadsCounter() {
        return loadsCounter;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public String getModuleType() {
        return moduleType;
    }

    public int getRarefactionCoefficient() {
        return rarefactionCoefficient;
    }

    public double getRms() {
        return rms;
    }

    public int getSlot() {
        return slot;
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public String getValueName() {
        return valueName;
    }

    public double getDc() {
        return dc;
    }

    public boolean isCalibrationExists() {
        return calibrationExists;
    }

    public boolean isCalibrationOfNullExists() {
        return calibrationOfNullExists;
    }

    public boolean isConnectionLost() {
        return connectionLost;
    }

    public void setAccurateFrequencyCalculation(boolean isAccurateCalculation) {
        signalParametersModel.setAccurateFrequencyCalculation(isAccurateCalculation);
    }

    public void setAverageCount(double averageCount) {
        this.averageCount = averageCount;
    }

    public void setCalibrationExists(boolean calibrationExists) {
        this.calibrationExists = calibrationExists;
    }

    public void setAmplitude(int amplitude) {
        signalParametersModel.setPeakValue(amplitude);
    }

    public void setCalibratedNull(double value) {
        signalParametersModel.setShift(value);
    }

    public void setCalibrationOfNullExists(boolean calibrationOfNullExists) {
        this.calibrationOfNullExists = calibrationOfNullExists;
    }

    public void setFrequency(int frequency) {
        signalParametersModel.setFrequency(frequency);
    }

    public void setLoadsCounter(int loadsCounter) {
        this.loadsCounter = loadsCounter;
        signalParametersModel.setLoadsCounter(loadsCounter);
    }

    public void setRarefactionCoefficient(int rarefactionCoefficient) {
        this.rarefactionCoefficient = rarefactionCoefficient;
    }

    public void setRMS(int rms) {
        signalParametersModel.setRMS(rms);
    }

    public void setDc(int dc) {
        signalParametersModel.setDc(dc);
    }
}
