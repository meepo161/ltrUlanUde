package ru.avem.posum.models.Process;

public class ProgramModel {
    private int toProgramCounter = 2;

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
