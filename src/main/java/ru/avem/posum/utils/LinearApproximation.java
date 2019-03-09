package ru.avem.posum.utils;

import javafx.scene.chart.XYChart;
import ru.avem.posum.models.CalibrationModel;

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

    public LinearApproximation(List<XYChart.Data<Double, Double>> rawData, CalibrationModel calibrationModel) {
        points = 2; // всегда аппроксимируются две точки

        for (int i = 0; i < rawData.size() - 1; i++) {
            double firstPointX = rawData.get(i).getXValue();
            double firstPointY = rawData.get(i).getYValue();
            double secondPointX = rawData.get(i + 1).getXValue();
            double secondPointY = rawData.get(i + 1).getYValue();

            x2Sum = Math.pow(firstPointX, 2) + Math.pow(secondPointX, 2);
            xSum = firstPointX + secondPointX;
            xYSum = firstPointX * firstPointY + secondPointX * secondPointY;
            ySum = firstPointY + secondPointY;

            createEquationSystem();
            calculateRoots();

            calibrationModel.getCoefficientA().add(roots[0]);
            calibrationModel.getCoefficientB().add(roots[1]);
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

    // Рассчитывает значение функции от yValue
    /*public void approximate(double yValue) {
        double coefficientA = roots[0];
        double coefficientB = roots[1];

        approximatedValue = (yValue - coefficientB) / coefficientA;
    }*/
}
