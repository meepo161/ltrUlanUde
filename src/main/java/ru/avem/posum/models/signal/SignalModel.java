package ru.avem.posum.models.signal;

import javafx.scene.chart.XYChart;
import ru.avem.posum.hardware.*;
import ru.avem.posum.models.Actionable;
import ru.avem.posum.models.calibration.CalibrationPoint;
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
    private boolean connectionLost;
    private double dc;
    private double frequency;
    private HashMap<String, Actionable> instructions = new HashMap<>();
    private double loadsCounter;
    private double lowerBound;
    private LTR24 ltr24;
    private LTR212 ltr212;
    private String moduleType;
    private SignalParametersModel signalParametersModel = new SignalParametersModel();
    private int rarefactionCoefficient = 10;
    private double rms;
    private int slot;
    private double tickUnit;
    private double upperBound;
    private String valueName = "В";

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
        instructions.put(Crate.LTR24, this::initLTR24Module);
        instructions.put(Crate.LTR212, this::initLTR212Module);
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
        int adcMode = ltr212.getSettingsOfModule().get(ADC.Settings.ADC_MODE);
        int SAMPLES = adcMode == 0 ? 30720 : 152;
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
        checkSettingOfNul();

        List<Double> calibrationCoefficients = adc.getCalibrationCoefficients().get(channel);

        if (!calibrationCoefficients.isEmpty()) {
            setCalibrationExists(true);
            signalParametersModel.defineCalibratedBounds(adc);
            setBounds();
            setValueName();
        }
    }

    private void checkSettingOfNul() {
        List<String> calibrations = adc.getCalibrationSettings().get(channel);

        for (String calibration : calibrations) {
            int channelFromCalibration = Integer.parseInt(calibration.substring(9, 10));
            if (channel == channelFromCalibration) {
                signalParametersModel.setShift(0);
                if (CalibrationPoint.parseValueName(calibration).isEmpty()) {
                    signalParametersModel.setShift(CalibrationPoint.parseChannelValue(calibration));
                    break;
                }
            }
        }
    }

    private void setBounds() {
        this.lowerBound = signalParametersModel.getLowerBound();
        this.upperBound = signalParametersModel.getUpperBound();
        this.tickUnit = signalParametersModel.getTickUnit();
    }

    private void setValueName() {
        this.valueName = signalParametersModel.getCalibratedValueName();
    }

    public void setDefaultValueName() {
        valueName = "В";
    }

    public void getData() {
        addReceivingDataInstructions();
        runInstructions();
    }

    private void addReceivingDataInstructions() {
        instructions.clear();
        instructions.put(Crate.LTR24, this::receive);
        instructions.put(Crate.LTR212, this::receive);
    }

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
        amplitude = signalParametersModel.getPeakValue();
        frequency = signalParametersModel.getSignalFrequency();
        loadsCounter = signalParametersModel.getLoadsCounter();
        rms = signalParametersModel.getRms();
        dc = signalParametersModel.getDc();
    }

    public void fillBuffer() {
        buffer = new double[adc.getData().length];
        adc.getRingBufferForShow().take(buffer, buffer.length);
    }

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