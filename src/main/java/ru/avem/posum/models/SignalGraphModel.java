package ru.avem.posum.models;

import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.RingBuffer;

import java.util.HashMap;
import java.util.List;

public class SignalGraphModel {
    private ADC adc;
    private double amplitude;
    private double averageCount;
    private double[] buffer;
    private boolean calibrationExists;
    private int channel;
    private double frequency;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private boolean isICPMode;
    private double lowerBound;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleType;
    private double phase;
    private ReceivedSignal receivedSignal = new ReceivedSignal();
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
        ltr24.setBuffer(new double[ltr24.getData().length]);
        ltr24.setRingBuffer(new RingBuffer(ltr24.getData().length * 100));
    }

    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        ltr212.setData(new double[2048]);
        ltr212.setBuffer(new double[ltr212.getData().length]);
        ltr212.setRingBuffer(new RingBuffer(ltr212.getData().length * 10));
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
            receivedSignal.defineCalibratedBounds(adc);
            setBounds();
            setValueName();
        }
    }

    private void setBounds() {
        this.lowerBound = receivedSignal.getLowerBound();
        this.upperBound = receivedSignal.getUpperBound();
        this.tickUnit = receivedSignal.getTickUnit();
    }

    private void setValueName() {
        this.valueName = receivedSignal.getValueName();
    }

    public void setDefaultValueName() {
        valueName = "В";
    }

    public void setICPMode(boolean isICPMode) {
        this.isICPMode = isICPMode;
    }

    public void getDefaultBounds() {
        addDefiningBoundsInstructions();
        runInstructions();
    }

    public void addDefiningBoundsInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::defineLTR24Bounds);
        instructions.put(CrateModel.LTR212, this::defineLTR212Bounds);
    }

    private void defineLTR24Bounds() {
        if (isICPMode) {
            defineICPModeRanges();
        } else {
            defineDifferentialModeRanges();
        }
    }

    private void defineICPModeRanges() {
        switch (ltr24.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(0, 1, 0.1);
                break;
            case 1:
                setBounds(0, 5, 0.5);
                break;
            default:
                setBounds(0, 5, 0.5);
        }
    }

    private void defineDifferentialModeRanges() {
        switch (ltr24.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(-2, 2, 0.4);
                break;
            case 1:
                setBounds(-10, 10, 2);
                break;
            default:
                setBounds(-10, 10, 2);
        }
    }

    private void setBounds(double lowerBound, double upperBound, double tickUnit) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tickUnit = tickUnit;
    }

    private void defineLTR212Bounds() {
        switch (ltr212.getMeasuringRanges()[channel]) {
            case 0:
                setBounds(-0.01, 0.01, 0.002);
                break;
            case 1:
                setBounds(-0.02, 0.02, 0.004);
                break;
            case 2:
                setBounds(-0.04, 0.04, 0.008);
                break;
            case 3:
                setBounds(-0.08, 0.08, 0.016);
                break;
            case 4:
                setBounds(0, 0.01, 0.001);
                break;
            case 5:
                setBounds(0, 0.02, 0.002);
                break;
            case 6:
                setBounds(0, 0.04, 0.004);
                break;
            case 7:
                setBounds(0, 0.08, 0.008);
                break;
            default:
                setBounds(-10, 10, 1);
        }
    }

    public void getData(int averageCount) {
        this.averageCount = averageCount;
        addReceivingDataInstructions();
        runInstructions();
        processData();
    }

    private void addReceivingDataInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::getLTR24Data);
        instructions.put(CrateModel.LTR212, this::getLTR212Data);
    }

    private void getLTR24Data() {
        double[] data = ltr24.getData();
        RingBuffer ringBuffer = ltr24.getRingBuffer();

        ltr24.receive(data);
        ringBuffer.put(data);
    }

    private void getLTR212Data() {
        double[] data = ltr212.getData();
        RingBuffer ringBuffer = ltr212.getRingBuffer();

        ltr212.receive(data);
        ringBuffer.put(data);
    }

    private void processData() {
        fillBuffer();
        calculate();
        getSignalParameters();
    }

    private void fillBuffer() {
        buffer = adc.getBuffer();
        adc.getRingBuffer().take(buffer, adc.getData().length);
    }

    private void calculate() {
        receivedSignal.setFields(adc, channel);
        receivedSignal.calculateParameters(buffer, averageCount, calibrationExists);
    }

    private void getSignalParameters() {
        amplitude = receivedSignal.getAmplitude();
        phase = receivedSignal.getPhase();
        zeroShift = receivedSignal.getZeroShift();
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

    public double getLowerBound() {
        return lowerBound;
    }

    public String getModuleType() {
        return moduleType;
    }

    public double getPhase() {
        return phase;
    }

    public ReceivedSignal getReceivedSignal() {
        return receivedSignal;
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

    public String getValueName() { return valueName; }

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
