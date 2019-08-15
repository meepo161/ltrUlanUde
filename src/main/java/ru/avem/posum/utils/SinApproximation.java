/**
 * Класс аппроксимирует массив, принимаемый в конструкторе синусоидой по методу наименьших квадратов
 */

package ru.avem.posum.utils;

public class SinApproximation {
    private double frequency;
    private double samplesCount;
    private double ySum;
    private double sinXSum;
    private double cosXSum;
    private double sin2XSum;
    private double cos2XSum;
    private double sinXcosXSum;
    private double ySinXSum;
    private double yCosXSum;
    private double[][] matrixA = new double[3][3];
    private double[] b = new double[3];
    private double[] roots = new double[3];
    private double[] approximatedData;

    public SinApproximation(double[] rawData, double frequency) {
        double[] xValues = new double[rawData.length];
        samplesCount = rawData.length;
        this.frequency = frequency;

        for (int i = 0; i < rawData.length; i++) {
            xValues[i] = (double) i / rawData.length * frequency;
            ySum += rawData[i];
            sinXSum += Math.sin(2 * Math.PI * xValues[i]);
            cosXSum += Math.cos(2 * Math.PI * xValues[i]);
            sin2XSum += Math.pow(Math.sin(2 * Math.PI * xValues[i]), 2);
            cos2XSum += Math.pow(Math.cos(2 * Math.PI * xValues[i]), 2);
            sinXcosXSum += Math.sin(2 * Math.PI * xValues[i]) * Math.cos(2 * Math.PI * xValues[i]);
            ySinXSum += rawData[i] * Math.sin(2 * Math.PI * xValues[i]);
            yCosXSum += rawData[i] * Math.cos(2 * Math.PI * xValues[i]);
        }
    }

    // Составляет систему уравнений
    public void createEquationSystem() {
        matrixA[0][0] = sin2XSum;
        matrixA[0][1] = sinXcosXSum;
        matrixA[0][2] = sinXSum;

        matrixA[1][0] = sinXcosXSum;
        matrixA[1][1] = cos2XSum;
        matrixA[1][2] = cosXSum;

        matrixA[2][0] = sinXSum;
        matrixA[2][1] = cosXSum;
        matrixA[2][2] = samplesCount;

        b[0] = ySinXSum;
        b[1] = yCosXSum;
        b[2] = ySum;
    }

    // Рассчитывает корни уравнения
    public void calculateRoots() {
        roots = GaussianElimination.lsolve(matrixA, b);
    }

    /* Создает массив аппроксимированных даннх требуемого размера */
    public void createSin(int samplesPerSecond) {
        approximatedData = new double[samplesPerSecond];
        double coefficientA = roots[0];
        double coefficientB = roots[1];
        double coefficientC = roots[2];
        double xValue;

        for (int i = 0; i < samplesPerSecond; i++) {
            xValue = (double) i / samplesPerSecond * frequency;
            approximatedData[i] = coefficientA * Math.sin(2 * Math.PI * xValue) +
                    coefficientB * Math.cos(2 * Math.PI * xValue) + coefficientC;
        }
    }

    public double[] getApproximatedData() {
        return approximatedData;
    }
}