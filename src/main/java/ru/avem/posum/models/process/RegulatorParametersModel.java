package ru.avem.posum.models.process;

public class RegulatorParametersModel {
    private int toProgramCounter = 2; // счетчик нажатия на кнопку "К программе"

    public boolean checkToProgramClicksCounter() {
        if (toProgramCounter % 2 == 0) {
            toProgramCounter = 1;
            return false;
        } else {
            toProgramCounter++;
            return true;
        }
    }

    public void resetProgramClickCounter() {
        toProgramCounter = 2;
    }
}
