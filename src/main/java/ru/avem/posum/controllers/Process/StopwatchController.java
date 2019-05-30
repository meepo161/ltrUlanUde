package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.time.StopWatch;
import ru.avem.posum.utils.Utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StopwatchController {
    private int daysCount;
    private boolean firstStart = true;
    private ProcessController processController;
    private boolean stopped;
    private StopWatch stopWatch = new StopWatch();
    private Label stopwatchLabel;
    private TextField stopwatchTextField;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public StopwatchController(ProcessController processController, Label stopwatchLabel, TextField stopwatchTextField) {
        this.processController = processController;
        this.stopwatchLabel = stopwatchLabel;
        this.stopwatchTextField = stopwatchTextField;
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void startStopwatch() {
        resetStopwatch();
        stopWatch.start();
        showTime();
    }

    public void showTime() {
        new Thread(() -> {
            while (!stopped && !processController.getProcess().isStopped()) {
                String elapsedTime = timeFormat.format(stopWatch.getTime());
                String elapsedDays = getDays(elapsedTime);

                Platform.runLater(() -> stopwatchTextField.setText(elapsedDays + elapsedTime));
                Utils.sleep(1000);
            }
        }).start();
    }

    private String getDays(String currentTime) {
        if (!firstStart && currentTime.substring(6, 8).equals("00")) {
            ++daysCount;
        }

        firstStart = false;
        return String.format("%d ", daysCount);
    }

    public void stopStopwatch() {
        stopped = true;
        stopWatch.stop();
        resetStopwatch();
        Platform.runLater(() -> stopwatchTextField.setText("0 00:00:00"));
    }

    public void pauseStopwatch() {
        stopped = true;
        stopWatch.suspend();
    }

    public void resume() {
        stopped = false;
        stopWatch.resume();
        showTime();
    }

    private void resetStopwatch() {
        daysCount = 0;
        firstStart = true;
        stopWatch.reset();
    }

    public void toggleTimer(boolean isHide) {
        Platform.runLater(() -> {
            stopwatchLabel.setDisable(isHide);
            stopwatchTextField.setDisable(isHide);
        });
    }
}
