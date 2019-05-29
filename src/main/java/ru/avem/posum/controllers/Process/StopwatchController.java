package ru.avem.posum.controllers.Process;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.time.StopWatch;
import ru.avem.posum.utils.Utils;

import java.util.Date;

public class StopwatchController {
    private ProcessController processController;
    private StopWatch stopWatch = new StopWatch();
    private Label stopwatchLabel;
    private TextField stopwatchTextField;
    private Date time;

    public StopwatchController(ProcessController processController, Label stopwatchLabel, TextField stopwatchTextField) {
        this.processController = processController;
        this.stopwatchLabel = stopwatchLabel;
        this.stopwatchTextField = stopwatchTextField;
    }

    public void start() {
        stopWatch.start();
    }

    public void showTime() {
        new Thread(() -> {
            while (!processController.getProcess().isStopped()) {
                time = new Date(stopWatch.getTime());
                time.setHours(0);
                System.out.println(time.toString());
//                System.out.println(String.format("%d:%d:%d\n", time.getHours(), time.getMinutes(), time.getSeconds()));
                Utils.sleep(500);
            }
        }).start();
    }

    public void stop() {
        stopWatch.stop();
    }

    public void pause() {
        stopWatch.suspend();
    }

    public void resume() {
        stopWatch.resume();
    }

    public void resetTimer() {
        stopWatch.reset();
    }

    public void toggleTimer(boolean isHide) {
        Platform.runLater(() -> {
            stopwatchLabel.setDisable(isHide);
            stopwatchTextField.setDisable(isHide);
        });
    }
}
