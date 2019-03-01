package ru.avem.posum.utils;

import javafx.scene.chart.XYChart;

import java.util.List;

public class LinearApproximation {
    private int points;
    private double x2Sum;
    private double xSum;
    private double xYSum;
    private double ySum;
    private double[][] matrixA = new double[2][2];
    private double[] b = new double[2];
    private double[] roots = new double[2];
    private double approximatedValue;

    public LinearApproximation(List<XYChart.Data<Double, Double>> rawData) {
        points = rawData.size();

        for (int i = 0; i < rawData.size(); i++) {
            double xValue = rawData.get(i).getXValue();
            double yValue = rawData.get(i).getYValue();

            x2Sum += Math.pow(xValue, 2);
            xSum += xValue;
            xYSum += xValue * yValue;
            ySum += yValue;
        }
    }

    public void createEquationSystem() {
        matrixA[0][0] = x2Sum;
        matrixA[0][1] = xSum;

        matrixA[1][0] = xSum;
        matrixA[1][1] = points;

        b[0] = xYSum;
        b[1] = ySum;
    }

    /* Находит корни системы уравнений 2х2 */
    public void calculateRoots() {
        roots = GaussianElimination.lsolve(matrixA, b);
    }

    public void approximate(double yValue) {
        double coefficientA = roots[0];
        double coefficientB = roots[1];

        approximatedValue = (yValue - coefficientB) / coefficientA;
    }

    public double getApproximatedValue() {
        return approximatedValue;
    }
}
