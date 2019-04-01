package ru.avem.posum.models;

import ru.avem.posum.hardware.*;
import ru.avem.posum.utils.RingBuffer;

import java.util.HashMap;
import java.util.List;

public class SignalModel {
    private ADC adc;
    private double amplitude;
    private double averageCount;
    private double[] buffer;
    private boolean calibrationExists;
    private int channel;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private boolean isICPMode;
    private double loadsCounter;
    private double lowerBound;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleType;
    private ReceivedSignal receivedSignal = new ReceivedSignal();
    private double rms;
    private int slot;
    private double tickUnit;
    private double upperBound;
    private String valueName = "В";
    private double zeroShift;
    private int dataArrayCounter;

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
        ltr212 = (LTR212) adc;
        ltr212.setData(new double[30720]);
        ltr212.setTimeMarks(new double[61440]);
        ltr212.setDataBuffer(new double[ltr212.getData().length]);
        ltr212.setDataRingBuffer(new RingBuffer(ltr212.getData().length * 100));
        ltr212.setTimeMarksBuffer(new double[ltr212.getTimeMarks().length]);
        ltr212.setTimeMarksRingBuffer(new RingBuffer(ltr212.getTimeMarks().length * 100));
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
        dataRingBuffer.put(data);
        System.out.println(String.format("Putted: %d arrays", dataArrayCounter));
        timeMarksRingBuffer.put(timeMarks);
        printTimeMarks();
    }

    public void processData() {
//        defineBufferLength();
        fillBuffer();
        calculate();
        getSignalParameters();
    }

    private void printTimeMarks() {
        double[] timeMarks = adc.getTimeMarks();
        int bufferedTimeMarkCounter = 0;
        int timeMarkCounter = 0;
        double bufferedTimeMark = 0;
        double timeMark = 0;


        for (double tMark : timeMarks) {
            if (tMark != 0) {
                if (bufferedTimeMark == 0) {
                    bufferedTimeMark = tMark;
                }

                if (bufferedTimeMark == tMark) {
                    bufferedTimeMarkCounter++;
                } else if (tMark == bufferedTimeMark + 1) {
                    timeMarkCounter++;
                    timeMark = tMark;
                }

            }
        }
        System.out.println(String.format("Time mark: %.1f had found %d times. Time mark: %.1f had found %d times.",
                bufferedTimeMark, bufferedTimeMarkCounter, timeMark, timeMarkCounter));
    }

    private void defineBufferLength() {
        RingBuffer timeMarks = adc.getTimeMarksRingBuffer();
        adc.setTimeMarksBuffer(new double[timeMarks.capacity]);
        double[] timeMarksBuffer = adc.getTimeMarksBuffer();
        timeMarks.take(timeMarksBuffer, timeMarksBuffer.length);
        int samplesPerSecond;
        int samplesCounter = 0;
        double bufferedTimeMark = 0;

        for (double timeMark : timeMarksBuffer) {
            if (timeMark != 0) {
                if (bufferedTimeMark == 0) {
                    bufferedTimeMark = timeMark;
                }

                if (bufferedTimeMark == timeMark) {
                    samplesCounter++;
                } else {
                    samplesPerSecond = samplesCounter;
                    System.out.println(String.format("First time mark: %.1f. Second time mark: %.1f. Samples per second: %d",
                            bufferedTimeMark, timeMark, samplesPerSecond));
                    break;
                }
            }
        }

    }

    private void fillBuffer() {
        buffer = adc.getDataBuffer();
        adc.getDataRingBuffer().take(buffer, buffer.length);
//        System.out.println("Buffer[last] = " + buffer[buffer.length - 1]);
    }

    private void calculate() {
        receivedSignal.setFields(adc, channel);
        receivedSignal.calculateParameters(buffer, averageCount, calibrationExists);
    }

    private void getSignalParameters() {
        amplitude = receivedSignal.getAmplitude();
        rms = receivedSignal.getPhase();
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
}
