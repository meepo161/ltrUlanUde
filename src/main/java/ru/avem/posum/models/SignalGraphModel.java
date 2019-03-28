package ru.avem.posum.models;

import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.RingBuffer;

import java.util.HashMap;
import java.util.List;

public class SignalGraphModel {
    private ADC adc;
    private double amplitude;
    private double averageCount;
    private double[] bufferedData;
    private double[] bufferedTimeMarks;
    private boolean calibrationExists;
    private int channel;
    private double frequency;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private boolean isICPMode;
    public volatile boolean isPeriodDefined;
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
        ltr24.setReceivedData(new RingBuffer(ltr24.getData().length * 100));
    }

    private void initLTR212Module() {
        ltr212 = (LTR212) adc;
        ltr212.setData(new double[2048]);
        ltr212.setTimeMarks(new double[ltr212.getData().length * 2]);
        ltr212.setReceivedData(new RingBuffer(ltr212.getData().length * 100));
        ltr212.setReceivedTimeMarks(new RingBuffer(ltr212.getTimeMarks().length * 100));
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
    }

    private void addReceivingDataInstructions() {
        instructions.clear();
        instructions.put(CrateModel.LTR24, this::getLTR24Data);
        instructions.put(CrateModel.LTR212, this::getLTR212Data);
    }

    private void getLTR24Data() {
        double[] data = ltr24.getData();
        RingBuffer ringBuffer = ltr24.getReceivedData();

        ltr24.receive(data);
        ringBuffer.put(data);
    }

    private void getLTR212Data() {
        double[] data = ltr212.getData();
        RingBuffer receivedData = ltr212.getReceivedData();
        RingBuffer receivedTimeMarks = ltr212.getReceivedTimeMarks();

        ltr212.receive(data);
        receivedData.put(data);
        receivedTimeMarks.put(ltr212.getTimeMarks());
    }

    public void definePeriod() {
        bufferedTimeMarks = new double[adc.getTimeMarks().length * 5];
        adc.getReceivedTimeMarks().take(bufferedTimeMarks, bufferedTimeMarks.length);
        int index = 0;
        int step = adc.getTimeMarks().length;
        while (index < bufferedTimeMarks.length) {
            if (bufferedTimeMarks[index] != 0) {
                definePeriodSamplesCount(bufferedTimeMarks[index]);
            }
            if (index == 0) {
                index += step - 1;
            } else {
                index += step;
            }
        }
    }

    private void definePeriodSamplesCount(double timeMark) {
        System.out.println("Time mark: " + timeMark);
        if (adc.getArraysCounter() == 0) {
            adc.setBufferedTimeMark(timeMark);
        }

        if (timeMark <= adc.getBufferedTimeMark()) {
            adc.setArraysCounter(adc.getArraysCounter() + 1);
            isPeriodDefined = false;
            System.out.println("isPeriodDefined = false;");
        } else {
            adc.setArraysPerSecond(adc.getArraysCounter());
            adc.setArraysCounter(0);
            isPeriodDefined = true;

            System.out.println("isPeriodDefined = true;");
//            System.out.println("Defined: " + adc.getArraysPerSecond());
        }
    }

    public void processData() {
        calculate();
        getSignalParameters();
    }

    private void calculate() {
        bufferedData = new double[adc.getData().length * adc.getArraysPerSecond()];
        adc.getReceivedData().take(bufferedData, bufferedData.length);
        receivedSignal.setFields(adc, channel);
        receivedSignal.calculateParameters(bufferedData, averageCount, calibrationExists);
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

    public double[] getBufferedData() {
        return bufferedData;
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

    public boolean isPeriodDefined() {
        return isPeriodDefined;
    }
}
