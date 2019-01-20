package ru.avem.posum.controllers;

import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public class ProcessController implements BaseController {

    private WindowsManager wm;

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
        wm.setScene(WindowsManager.Scenes.MAIN_SCENE);
    }

    public void handleAddEventButton() {
    }

    public void handleExpandEventTableButton() {
    }

    public void handlePidButton() {
    }

    public void handlePlugButton() {
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {

    }
}
