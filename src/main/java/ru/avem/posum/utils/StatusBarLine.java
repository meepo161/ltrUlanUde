package ru.avem.posum.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private Label checkIcon;
    private boolean isStatusBarHidden = true;
    private Thread statusBarThread;
    private String text;
    private Label warningIcon;

    public void setStatus(String text, StatusBar statusBar) {
        statusBar.setText(text);

        if (isStatusBarHidden) {
            startNewStatusBarThread(statusBar);
        } else {
            statusBarThread.interrupt();
            startNewStatusBarThread(statusBar);
        }
    }

    public void setStatus(String text, StatusBar statusBar, Label checkIcon, Label warningIcon) {
        this.checkIcon = checkIcon;
        this.text = text;
        this.warningIcon = warningIcon;

        initIcons();
        statusBar.setText(text);

        if (isStatusBarHidden) {
            startNewStatusBarThread(statusBar);
        } else {
            statusBarThread.interrupt();
            startNewStatusBarThread(statusBar);
        }
    }

    private void initIcons() {
        checkIcon.setTextFill(Color.web("#009700"));
        warningIcon.setTextFill(Color.web("#FAB600"));

        if (text.equals("Операция успешно выполнена")) {
            checkIcon.setStyle("-fx-opacity: 1;");
        } else {
            warningIcon.setStyle("-fx-opacity: 1;");
        }
    }

    private void startNewStatusBarThread(StatusBar statusBar) {
        statusBarThread = new Thread(() -> {
            isStatusBarHidden = false;
            try {
                sleep(5000);
                Platform.runLater(() -> {
                    checkIcon.setStyle("-fx-opacity: 0;");
                    warningIcon.setStyle("-fx-opacity: 0;");
                    statusBar.setText("");
                });
            } catch (InterruptedException ignored) {
            } finally {
                isStatusBarHidden = true;
            }
        });
        statusBarThread.start();
    }
}
