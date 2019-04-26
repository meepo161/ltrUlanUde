package ru.avem.posum.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private Label checkIcon;
    private boolean isMainView;
    private boolean isStatusBarHidden = true;
    private boolean isStatusOk;
    private StatusBar statusBar;
    private Thread statusBarThread;
    private String text;
    private Label warningIcon;

    public void setStatus(String text, StatusBar statusBar) {
        this.statusBar = statusBar;
        toggleIconsState("-fx-opacity: 0;");
        statusBar.setStyle("-fx-padding: 0 0 0 3.2;");
        Platform.runLater(() -> statusBar.setText(text));
        handleStatusBar();
    }

    private void toggleIconsState(String state) {
        if (checkIcon != null && warningIcon != null) {
            checkIcon.setStyle(state);
            warningIcon.setStyle(state);
        }
    }

    private void handleStatusBar() {
        if (isStatusBarHidden) {
            startNewStatusBarThread();
        } else {
            statusBarThread.interrupt();
            startNewStatusBarThread();
        }
    }

    private void startNewStatusBarThread() {
        statusBarThread = new Thread(() -> {
            isStatusBarHidden = false;
            try {
                sleep(5000);
                clearStatusBar(statusBar);
            } catch (InterruptedException ignored) {
            } finally {
                isStatusBarHidden = true;
            }
        });
        statusBarThread.start();
    }

    public void setStatus(String text, StatusBar statusBar, Label checkIcon, Label warningIcon) {
        this.checkIcon = checkIcon;
        this.statusBar = statusBar;
        this.text = text;
        this.warningIcon = warningIcon;
        initIcons();
        Platform.runLater(() -> statusBar.setText(text));
        handleStatusBar();
    }

    private void initIcons() {
        checkIcon.setTextFill(Color.web("#009700"));
        warningIcon.setTextFill(Color.web("#D30303"));

        if (text.equals("Операция успешно выполнена") || isStatusOk) {
            checkIcon.setStyle("-fx-opacity: 1;");
            String padding = isMainView ? "-fx-padding: 0 0 0 4;" : "-fx-padding: 0 0 0 1.1;";
            statusBar.setStyle(padding);
            isStatusOk = false;
        } else {
            warningIcon.setStyle("-fx-opacity: 1;");
            String padding = isMainView ? "-fx-padding: 0 0 0 -1.1;" : "-fx-padding: 0 0 0 -1.9;";
            statusBar.setStyle(padding);
            isMainView = false;
        }
    }

    public void clearStatusBar(StatusBar statusBar) {
        toggleIconsState("-fx-opacity: 0;");
        Platform.runLater(() -> statusBar.setText(""));
    }

    public void setStatusOk(boolean statusOk) {
        isStatusOk = statusOk;
    }

    public void setMainView(boolean mainView) {
        isMainView = mainView;
    }
}
