package ru.avem.posum.models;

import ru.avem.posum.utils.Complex;

public class ReceivedSignal {
    private double amplitude;
    private Complex[] complexArray;
    private int channel;
    private double[] data;
    private double maxValue;
    private double minValue;
    private double phase;
    private double zeroShift;

    public void calculateBaseParameters(double[] rawData, int channel) {
        setFields(rawData, channel);
        calculateMinAndMaxValues();
        calculateAmplitude();
        calculateZeroShift();
        calculatePhase();
    }

    private void calculateAmplitude() {
        amplitude = (maxValue - minValue) / 2;
    }

    private void calculatePhase() {
        createComplexArray();
    }

    private void createComplexArray() {
        Complex complexNumber = new Complex(zeroShift, 0);
        phase = complexNumber.phase();
    }

    private void calculateZeroShift() {
        zeroShift = (maxValue + minValue) / 2;
    }

    private void setFields(double[] rawData, int channel) {
        this.data = rawData;
        this.channel = channel;
    }

    private void calculateMinAndMaxValues() {
        maxValue = -999_999_999;
        minValue = 999_999_999;

        for (int i = channel; i < data.length; i += 4) {
            if (data[i] > maxValue) {
                maxValue = data[i];
            }

            if (data[i] < minValue) {
                minValue = data[i];
            }
        }
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getZeroShift() {
        return zeroShift;
    }

    public double getPhase() {
        return phase;
    }
}
