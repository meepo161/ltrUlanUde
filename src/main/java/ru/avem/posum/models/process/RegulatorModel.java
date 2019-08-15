package ru.avem.posum.models.process;

/**
 * Модель ПИД - регулятора
 */

public class RegulatorModel {
    private double bufferedError; // сохраненое значение ошибки регулирования
    private double bufferedIValue; // сохраненное значение интегральной составляющей
    private int calculationCount = 1;
    private double dCoefficient; // Д - коэффициент
    private double dValue; // дифференциальная составляющая
    private double iCoefficient; // И - коэффициент
    private double iValue; // интегральная составляющая
    private double neededAmplitude; // заданное значение амплитуды
    private double neededDc; // заданное значение постоянной составляющей
    private double neededFrequency; // заданное значение частоты
    private double neededRms; // заданное действующее значение
    private double pCoefficient; // П - коэффициент
    private double pValue; // пропорциональная составляющая
    private double responseAmplitude; // измеренное значение амплитуды
    private double responseDc; // измеренное значение постоянной составляющей
    private double responseFrequency; // измеренное значение частоты

    // вовзращает скорректированное значение амплитуды
    public double getAmplitude() {
        return calculateRegulator(neededAmplitude, responseAmplitude);
    }

    // рассчитывает корректировку
    private double calculateRegulator(double neededParameter, double response) {
        double error = neededParameter - response;
        pValue = pCoefficient * error;
        iValue = iCoefficient * error;

        if (calculationCount % 2 != 0) {
            bufferedError = error;
            bufferedIValue = iValue;
        } else {
            iValue = bufferedIValue + iCoefficient * error;
            dValue = dCoefficient * (error - bufferedError);
            calculationCount = 1;
        }

        return pValue + iValue + dValue;
    }

    // вовзращает скорректриованное значение постоянной составляющей
    public double getDc() {
        return calculateRegulator(neededDc, responseDc);
    }

    // вовзращает скорректированное значение частоты
    public double getFrequency() {
        return calculateRegulator(neededFrequency, responseFrequency);
    }

    // вовзращает скорректированное действующее значение
    public double getRms() {
        return neededRms;
    }

    public void setNeededAmplitude(double neededAmplitude) {
        this.neededAmplitude = neededAmplitude;
    }

    public void setNeededDc(double neededDc) {
        this.neededDc = neededDc;
    }

    public void setNeededFrequency(double neededFrequency) {
        this.neededFrequency = neededFrequency;
    }

    public void setNeededRms(double neededRms) {
        this.neededRms = neededRms;
    }

    public void setResponseAmplitude(double responseAmplitude) {
        this.responseAmplitude = responseAmplitude;
    }

    public void setResponseDc(double responseDc) {
        this.responseDc = responseDc;
    }

    public void setResponseFrequency(double responseFrequency) {
        this.responseFrequency = responseFrequency;
    }

    public void setPCoefficient(double pValue) {
        this.pCoefficient = pValue;
    }

    public void setICoefficient(double iValue) {
        this.iCoefficient = iValue;
    }

    public void setCoefficient(double dValue) {
        this.dCoefficient = dValue;
    }
}
