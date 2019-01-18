package ru.avem.posum.controllers;

import ru.avem.posum.Main;

public class ProcessController {
    private Main main;
    public void setMainApp(Main main) {
        this.main = main;
    }

    public void handleInitButton() {
    }
    public void handleRunButton() {
    }
    public void handleSmoothStopButton() {
    }
    public void handleStopButton() {
    }
    public void handleToProgrammButton() {
    }
    public void handleSavePointButton() {
    }
    public void handleSaveWaveformButton() {
    }
    public void handleSaveProtocolButton() {
    }
    public void handleBackButton() {
       Main.getPrimaryStage().setScene(main.getMainScene());
    }
    public void handleAddEventButton() {
    }
    public void handleExpandEventTableButton() {
    }
    public void handlePidButton() {
    }

    public void handlePlugButton() {
    }

}
