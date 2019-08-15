package ru.avem.posum.utils;

/**
* Класс для расчета скользящего среднего
*/

public class MovingAverage {
    private double alpha;
    private Double oldValue;
    private int averageWindowSize;

    public MovingAverage(int averageWindowSize) {
        this.alpha = (double) 2 / (averageWindowSize + 1);
        this.averageWindowSize = averageWindowSize;
    }

    public double exponentialMovingAverage(double value) {
        if (oldValue == null) {
            oldValue = value;
            return value;
        }
        double newValue = oldValue + alpha * (value - oldValue);
        oldValue = newValue;
        return newValue;
    }
    public double[] exponentialMovingAverage(double[] value,int channel) {
        double[] out = new double[value.length];
        for (int i = channel; i <value.length ; i+=4) {
            out[i]=exponentialMovingAverage(value[i]);
        }
        return out;
    }
    public double[] simpleMovingAverage(double[] value) {
        double[] newValue;
        if (value.length > averageWindowSize) {
            newValue = new double[value.length - averageWindowSize];
            for (int i = 0; i < averageWindowSize; i++) {
                newValue[0] += value[i];
            }
            newValue[0] = newValue[0] / averageWindowSize;
            for (int i = averageWindowSize + 1; i < value.length; i++) {
                int k = i - (averageWindowSize + 1);
                newValue[i - averageWindowSize] = newValue[k] + (value[i - 1] - value[k]) / averageWindowSize;
            }
        } else {
            newValue = new double[1];
            for (int i = 0; i < value.length; i++) {
                newValue[0] += value[i];
            }
            newValue[0] = newValue[0] / value.length;
        }
        return newValue;
    }
}