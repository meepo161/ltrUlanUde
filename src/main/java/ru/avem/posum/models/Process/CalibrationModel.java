package ru.avem.posum.models.Process;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Calibration.CalibrationPoint;

import java.util.List;
import java.util.Optional;

public class CalibrationModel {
    private int CHANNELS = 4; // количество каналов АЦП
    private int SLOTS = 16; // количество слотов в крейте

    private double[][] calibratedData = new double[SLOTS][CHANNELS];
    private double[][] calibratedLowerBound = new double[SLOTS][CHANNELS];
    private double[][] calibratedUpperBound = new double[SLOTS][CHANNELS];
    private double[][] firstPointChannelValue = new double[SLOTS][CHANNELS];
    private double[][] firstPointLoadValue = new double[SLOTS][CHANNELS];
    private boolean[][] isCalibrationsExists = new boolean[SLOTS][CHANNELS];
    private double[][] lowerBound = new double[SLOTS][CHANNELS];
    private double[][] secondPointChannelValue = new double[SLOTS][CHANNELS];
    private double[][] secondPointLoadValue = new double[SLOTS][CHANNELS];
    private double[][] tickUnit = new double[SLOTS][CHANNELS];
    private double[][] upperBound = new double[SLOTS][CHANNELS];
    private String[][] valueName = new String[SLOTS][CHANNELS];

    public void loadCalibrations(List<ChannelModel> channels, List<Modules> modules) {
        for (ChannelModel channel : channels) {
            parseCalibration(channel, modules);
        }
    }

    private void parseCalibration(ChannelModel channel, List<Modules> modules) {
        String channelDescription = parseDescription(channel);

        for (int moduleIndex = 0; moduleIndex < modules.size(); moduleIndex++) {
            Modules module = modules.get(moduleIndex);

            if (!module.getModuleType().equals(Crate.LTR34)) {
                List<Pair<Integer, String>> channelsDescriptions = Modules.getChannelsDescriptions(module);

                for (Pair<Integer, String> description : channelsDescriptions) {
                    if (description.getValue().equals(channelDescription)) {
                        int channelIndex = description.getKey() - 1;
                        List<String> calibrationSettings = Modules.getCalibrations(module, channelIndex);
                        isCalibrationsExists[moduleIndex][channelIndex] = calibrationSettings.size() > 1;

                        if (isCalibrationsExists[moduleIndex][channelIndex]) {
                            parseValueName(calibrationSettings.get(channelIndex), moduleIndex, channelIndex);
                            parse(calibrationSettings, moduleIndex, channelIndex);
                            break;
                        }
                    }
                }
            }
        }


    }

    private String parseDescription(ChannelModel channel) {
        if (channel.getName().contains(" => ")) {
            return channel.getName().split(" => ")[1];
        } else {
            return channel.getName();
        }
    }

    private void parseValueName(String calibrationSettings, int moduleIndex, int channelIndex) {
        valueName[moduleIndex][channelIndex] = CalibrationPoint.parseValueName(calibrationSettings);
    }

    private void parse(List<String> calibrationSettings, int moduleIndex, int channelIndex) {
        for (int settingsIndex = 0; settingsIndex < calibrationSettings.size() - 1; settingsIndex++) {
            if (CalibrationPoint.parseValueName(calibrationSettings.get(settingsIndex)).isEmpty()) { // если градуировка нуля
                continue;
            }

            String firstCalibrationPoint = calibrationSettings.get(settingsIndex);
            String secondCalibrationPoint = calibrationSettings.get(settingsIndex + 1);
            firstPointChannelValue[moduleIndex][channelIndex] = CalibrationPoint.parseChannelValue(firstCalibrationPoint);
            firstPointLoadValue[moduleIndex][channelIndex] = CalibrationPoint.parseLoadValue(firstCalibrationPoint);
            secondPointChannelValue[moduleIndex][channelIndex] = CalibrationPoint.parseChannelValue(secondCalibrationPoint);
            secondPointLoadValue[moduleIndex][channelIndex] = CalibrationPoint.parseLoadValue(secondCalibrationPoint);

            defineBounds(moduleIndex, channelIndex);
        }

        defineCalibratedBounds(calibrationSettings, moduleIndex, channelIndex);
    }

    private void defineBounds(int moduleIndex, int channelIndex) {
        if (firstPointChannelValue[moduleIndex][channelIndex] > secondPointChannelValue[moduleIndex][channelIndex]) {
            double bufferForChannelValue = firstPointChannelValue[moduleIndex][channelIndex];
            double bufferForLoadValue = firstPointLoadValue[moduleIndex][channelIndex];
            firstPointChannelValue[moduleIndex][channelIndex] = secondPointChannelValue[moduleIndex][channelIndex];
            secondPointChannelValue[moduleIndex][channelIndex] = bufferForChannelValue;
            firstPointLoadValue[moduleIndex][channelIndex] = secondPointLoadValue[moduleIndex][channelIndex];
            secondPointLoadValue[moduleIndex][channelIndex] = bufferForLoadValue;
        }

        lowerBound[moduleIndex][channelIndex] = firstPointChannelValue[moduleIndex][channelIndex];
        upperBound[moduleIndex][channelIndex] = secondPointChannelValue[moduleIndex][channelIndex];
    }

    public void defineCalibratedBounds(List<String> calibrationSettings, int moduleIndex, int channelIndex) {
        if (!calibrationSettings.isEmpty()) {
            double minLoadValue = Double.MAX_VALUE;
            double maxLoadValue = Double.MIN_VALUE;
            int GRAPH_SCALE = 5;

            for (String calibrationSetting : calibrationSettings) {
                String calibratedValueName = CalibrationPoint.parseValueName(calibrationSetting);
                if (calibratedValueName.isEmpty()) {
                    continue;
                }

                double loadValue = CalibrationPoint.parseLoadValue(calibrationSetting);
                if (minLoadValue > loadValue) {
                    minLoadValue = loadValue;
                }
                if (maxLoadValue < loadValue) {
                    maxLoadValue = loadValue;
                }
            }

            calibratedLowerBound[moduleIndex][channelIndex] = minLoadValue;
            calibratedUpperBound[moduleIndex][channelIndex] = maxLoadValue;
            tickUnit[moduleIndex][channelIndex] = maxLoadValue / GRAPH_SCALE;
        }
    }

    public void calibrate(double[][] data) {
        for (int moduleIndex = 0; moduleIndex < data.length; moduleIndex++) {
            calibratedData[moduleIndex] = new double[data[moduleIndex].length];
            for (int channelIndex = 0; channelIndex < CHANNELS; channelIndex++) {
                for (int i = channelIndex; i < data[moduleIndex].length; i += CHANNELS) {
                    if (isCalibrationsExists[moduleIndex][channelIndex]) {
                        calibratedData[moduleIndex][i] = calibrate(data[moduleIndex][i], moduleIndex, channelIndex);
                    } else {
                        calibratedData[moduleIndex][i] = data[moduleIndex][i];
                    }
                }
            }
        }
    }

    private double calibrate(double value, int moduleIndex, int channelIndex) {
        double calibratedValue;
        double accuracyCoefficient = 1.2;

        if (value > lowerBound[moduleIndex][channelIndex] * accuracyCoefficient && value <= upperBound[moduleIndex][channelIndex] * accuracyCoefficient) {
            double k = (secondPointLoadValue[moduleIndex][channelIndex] - firstPointLoadValue[moduleIndex][channelIndex])
                    / (secondPointChannelValue[moduleIndex][channelIndex] - firstPointChannelValue[moduleIndex][channelIndex]);
            double b = firstPointLoadValue[moduleIndex][channelIndex] - k * firstPointChannelValue[moduleIndex][channelIndex];
            calibratedValue = k * value + b;
        } else if (value <= lowerBound[moduleIndex][channelIndex] / accuracyCoefficient) {
            calibratedValue = lowerBound[moduleIndex][channelIndex];
        } else {
            calibratedValue = upperBound[moduleIndex][channelIndex];
        }

        return calibratedValue;
    }

    public double[][] getCalibratedData() {


        return calibratedData;
    }
}
