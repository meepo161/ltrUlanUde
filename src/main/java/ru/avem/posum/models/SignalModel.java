package ru.avem.posum.models;

import javafx.scene.chart.XYChart;
import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.RingBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignalModel {
    private ADC adc;
    private double amplitude;
    private double averageCount;
    private double[] buffer;
    private boolean calibrationExists;
    private int channel;
    private int dataArrayCounter;
    private volatile boolean dataReceived;
    private double frequency;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();
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
        ltr24.setData(new double[39064]);
        ltr24.setDataBuffer(new double[ltr24.getData().length]);
        ltr24.setDataRingBuffer(new RingBuffer(ltr24.getData().length * 100));
    }

    private void initLTR212Module() {
        final int SAMPLES = 30720;
        ltr212 = (LTR212) adc;
        ltr212.setData(new double[SAMPLES]);
        ltr212.setTimeMarks(new double[SAMPLES * 2]);
        ltr212.setDataBuffer(new double[SAMPLES * 4]);
        ltr212.setDataRingBuffer(new RingBuffer(SAMPLES * 10));
        ltr212.setTimeMarksBuffer(new double[SAMPLES * 2]);
        ltr212.setTimeMarksRingBuffer(new RingBuffer(SAMPLES * 2 * 10));
    }

    private void runInstructions() {
        instructions.get(moduleType).onAction();
    }

    public void checkCalibration() {
        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);

        if (!calibrationCoefficients.isEmpty()) {
            setCalibrationExists(true);
        }
    }

    public void parseCalibration() {
        if (calibrationExists) {
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
        this.valueName = signalParametersModel.getValueName();
    }

    public void setDefaultValueName() {
        valueName = "В";
    }

    public void setICPMode(boolean isICPMode) {
        this.isICPMode = isICPMode;
    }

    public void getData(int averageCount) {
        this.averageCount = averageCount;
        dataReceived = false;
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
        RingBuffer ringBuffer = ltr24.getDataRingBuffer();

        ltr24.receive(data);
        ringBuffer.put(data);
    }

    private void getLTR212Data() {
        double[] data = ltr212.getData();
        double[] timeMarks = ltr212.getTimeMarks();
        RingBuffer dataRingBuffer = ltr212.getDataRingBuffer();
        RingBuffer timeMarksRingBuffer = ltr212.getTimeMarksRingBuffer();

        ltr212.receive(data, timeMarks);
        dataRingBuffer.reset();
        dataRingBuffer.put(data);
        timeMarksRingBuffer.reset();
        timeMarksRingBuffer.put(timeMarks);
        dataReceived = true;
    }

    public void processData() {
        calculate();
        getSignalParameters();
    }

    public void fillBuffer() {
        buffer = adc.getDataBuffer();
        adc.getDataRingBuffer().take(buffer, buffer.length);

        for (int i = 0; i < buffer.length; i += 4) {
            System.out.printf("Value: %f, index: %d\n", buffer[i], i);
        }
    }

    private void calculate() {
        signalParametersModel.setFields(adc, channel);
        signalParametersModel.calculateParameters(adc.getData(), averageCount, calibrationExists);
    }

    private void getSignalParameters() {
        amplitude = signalParametersModel.getAmplitude();
        frequency = signalParametersModel.getFrequency();
//        rms = signalParametersModel.getPhase();
        zeroShift = signalParametersModel.getZeroShift();
    }

    public XYChart.Data getPoint(int valueIndex) {
        if (calibrationExists) {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = signalParametersModel.applyCalibration(adc, buffer[valueIndex]);
            XYChart.Data<Number, Number> calibratedPoint = new XYChart.Data<>(xValue, yValue);
            return calibratedPoint;
        } else {
            double xValue = (double) (valueIndex - adc.getChannelsCount()) / buffer.length;
            double yValue = buffer[valueIndex];
            XYChart.Data<Number, Number> point = new XYChart.Data<>(xValue, yValue);
            return point;
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

    public double getFrequency() {
        return frequency;
    }

    public List<XYChart.Data<Number, Number>> getIntermediateList() {
        return intermediateList;
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

    public void setCalibrationExists(boolean calibrationExists) {
        this.calibrationExists = calibrationExists;
    }
}
