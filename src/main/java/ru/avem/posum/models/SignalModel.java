package ru.avem.posum.models;

import javafx.scene.chart.XYChart;
import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.RingBuffer;

import java.util.HashMap;
import java.util.List;

public class SignalModel {
    private ADC adc;
    private double amplitude;
    private double averageCount = 1;
    private double[] buffer;
    private boolean calibrationExists;
    private int channel;
    private int dataRarefactionCoefficient;
    private double frequency;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private boolean isICPMode;
    private double loadsCounter;
    private double lowerBound;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleType;
    private SignalParametersModel signalParametersModel = new SignalParametersModel();
    private double rms;
    private int slot;
    private double tickUnit;
    private double upperBound;
    private String valueName = "В";
    private double zeroShift;

    public void setFields(String moduleType, int slot, int channel) {
        this.moduleType = moduleType;
        this.slot = slot;
        this.channel = channel;
    }

    public void defineModuleInstance(HashMap<Integer, Module> modules) {
        adc = (ADC) modules.get(slot);
        getADCInstance();
    }

    private void getADCInstance() {
        addInitModuleInstructions();
        runInstructions();
    }

    private void addInitModuleInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::initLTR24Module);
        instructions.put(CrateModel.LTR212, this::initLTR212Module);
    }

    private void initLTR24Module() {
        ltr24 = (LTR24) adc;
        int SAMPLES = (int) ltr24.getFrequency() * ltr24.getChannelsCount();
        ltr24.setData(new double[SAMPLES]);
        ltr24.setRingBufferForCalculation(new RingBuffer(SAMPLES));
        ltr24.setRingBufferForShow(new RingBuffer(SAMPLES));
        ltr24.setTimeMarks(new double[SAMPLES * 2]);
        ltr24.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2));
    }

    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        int adcMode = ltr212.getModuleSettings().get(ADC.Settings.ADC_MODE.getSettingName());
        int SAMPLES = adcMode == 0 ? 30720 : 150;
        ltr212.setData(new double[SAMPLES]);
        ltr212.setRingBufferForCalculation(new RingBuffer(SAMPLES));
        ltr212.setRingBufferForShow(new RingBuffer(SAMPLES));
        ltr212.setTimeMarks(new double[SAMPLES * 2]);
        ltr212.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2));
    }

    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    public void checkCalibration() {
        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);

        if (!calibrationCoefficients.isEmpty()) {
            setCalibrationExists(true);
            signalParametersModel.defineCalibratedBounds(adc);
            setBounds();
            setValueName();
        }
    }

    private void setBounds() {
        this.lowerBound = signalParametersModel.getLowerBound();
        this.upperBound = signalParametersModel.getUpperBound();
        this.tickUnit = signalParametersModel.getTickUnit();
    }

    private void setValueName() {
        this.valueName = signalParametersModel.getCalibrationValueName();
    }

    public void setDefaultValueName() {
        valueName = "В";
    }

    public void setICPMode(boolean isICPMode) {
        this.isICPMode = isICPMode;
    }

    public void getData() {
        addReceivingDataInstructions();
        runInstructions();
    }

    private void addReceivingDataInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::getLTR24Data);
        instructions.put(CrateModel.LTR212, this::getLTR212Data);
    }

    private void getLTR24Data() {
        double[] data = ltr24.getData();
        double[] timeMarks = ltr24.getTimeMarks();
        RingBuffer ringBufferForCalculation = ltr24.getRingBufferForCalculation();
        RingBuffer ringBufferForShow = ltr24.getRingBufferForShow();

        ltr24.write(data, timeMarks);
        ringBufferForCalculation.reset();
        ringBufferForCalculation.put(data);
        ringBufferForShow.reset();
        ringBufferForShow.put(data);
    }

    private void getLTR212Data() {
        double[] data = ltr212.getData();
        double[] timeMarks = ltr212.getTimeMarks();
        RingBuffer ringBufferForCalculation = ltr212.getRingBufferForCalculation();
        RingBuffer ringBufferForShow = ltr212.getRingBufferForShow();

        ltr212.write(data, timeMarks);
        ringBufferForCalculation.reset();
        ringBufferForCalculation.put(data);
        ringBufferForShow.reset();
        ringBufferForShow.put(data);
    }

    public void calculateData() {
        calculate();
        getSignalParameters();
    }

    private void calculate() {
        double[] buffer = new double[adc.getData().length];
        adc.getRingBufferForCalculation().take(buffer, buffer.length);
        signalParametersModel.setFields(adc, channel);
        signalParametersModel.calculateParameters(buffer, averageCount, calibrationExists);
    }

    private void getSignalParameters() {
        amplitude = signalParametersModel.getAmplitude();
        frequency = signalParametersModel.getSignalFrequency();
        loadsCounter = signalParametersModel.getLoadsCounter();
        rms = signalParametersModel.getRms();
        zeroShift = signalParametersModel.getZeroShift();
    }

    public void fillBuffer() {
        buffer = new double[adc.getData().length];
        adc.getRingBufferForShow().take(buffer, buffer.length);
    }

    public XYChart.Data getPoint(int valueIndex) {
        if (calibrationExists) {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = signalParametersModel.applyCalibration(adc, buffer[valueIndex]);
            return new XYChart.Data<Number, Number>(xValue, yValue);
        } else {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = buffer[valueIndex];
            return new XYChart.Data<Number, Number>(xValue, yValue);
        }
    }

    /* Определяет коэффициент для прореживания отображаемых точек на графике сигнала */
    public void defineDataRarefactionCoefficient() {
        if (frequency < 25) {
            dataRarefactionCoefficient = 10;
        } else if (frequency < 50) {
            dataRarefactionCoefficient = 2;
        } else {
            dataRarefactionCoefficient = 1;
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

    public int getChannel() {
        return channel;
    }

    public int getDataRarefactionCoefficient() {
        return dataRarefactionCoefficient;
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

    public double getZeroShift() {
        return zeroShift;
    }

    public boolean isCalibrationExists() {
        return calibrationExists;
    }

    public void setAverageCount(double averageCount) {
        this.averageCount = averageCount;
    }

    public void setCalibrationExists(boolean calibrationExists) {
        this.calibrationExists = calibrationExists;
    }

    public void setLoadsCounter(double loadsCounter) {
        this.loadsCounter = loadsCounter;
        signalParametersModel.setLoadsCounter(loadsCounter);
    }
}
