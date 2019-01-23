package ru.avem.posum.models;

import javafx.application.Platform;
import ru.avem.posum.hardware.LTR34;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

public class LTR34Model {
    private int moduleSlot;
    private LTR34 ltr34 = new LTR34();
    private double[] data = new double[31250];
    private double[] outputArray = new double[31250];
    private RingBuffer ringBuffer = new RingBuffer(data.length * 10);
    private TextEncoder textEncoder = new TextEncoder();
    private String decodedError;
    private int riseTime = 10;
    private boolean isInitialized;
    private boolean isStarted;
    private boolean isStopped;

    public LTR34Model(int slot) {
        moduleSlot = slot;
    }

    public void initialize() {
        String status = ltr34.initialize(moduleSlot);
        isInitialized = checkError(status);
        Platform.runLater(() -> {
//            iMainController.setMainStatusBarText(status == null ? "LTR34: Инициализация выполнена без ошибок" : decodedError);
        });
    }

    public void generateSignal(int sin8HzAmplitude, int sin20HzAmplitude) {
        String status = ltr34.start();
        isStarted = checkError(status);
//        iMainController.setMainStatusBarText(status == null ? "LTR34: Запуск выполнен без ошибок" : decodedError);

//        new Thread(() -> {
//            while (!MainController.experimentFinished) {
//                try {
//                    doSmoothStart(sin8HzAmplitude,sin20HzAmplitude);
//                    Thread.sleep(950);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).initModule();
    }

    public void doSmoothStart(int sin8HzAmplitude, int sin20HzAmplitude) {
        if (riseTime > 1) {
            calculateSignal((double)sin8HzAmplitude / riseTime, (double)sin20HzAmplitude / riseTime);
            ringBuffer.put(data);
            ltr34.dataSend(data);
            riseTime -= 1;
        }
        else {
            calculateSignal((double)sin8HzAmplitude, (double)sin20HzAmplitude);
            ltr34.dataSend(data);
            ringBuffer.put(data);
        }
    }

    public boolean doSmoothStop(int sin8HzAmplitude, int sin20HzAmplitude) {
        if (riseTime != 10) {
            calculateSignal((double)sin8HzAmplitude / riseTime, (double)sin20HzAmplitude / riseTime);
            ringBuffer.put(data);
            ltr34.dataSend(data);
            riseTime++;
        }
        else {
            double[] stopData = new double[31250];
            ltr34.dataSend(stopData);
            ringBuffer.put(data);

            String status = ltr34.stop();
            isStopped = checkError(status);
            Platform.runLater(() -> {
//                iMainController.setMainStatusBarText(status == null ? "LTR34: Остановка выполнена без ошибок" : decodedError);
            });

            return true;
        }
        return false;
    }

    private boolean checkError(String status) {
        if (status != null) {
            decodedError = decodeError(status);
            return false;
        }
        return true;
    }

    private String decodeError(String error) {
        return "LTR34: " + textEncoder.cp2utf(error);
    }

    public void calculateSignal(double sin8HzAmplitude, double sin20HzAmplitude) {
        for (int i = 0; i < data.length; i++) {
            data[i] = sin8HzAmplitude * Math.sin(2 * 3.14 * 8 * (i + 1) / 31250) +
                    sin20HzAmplitude * Math.sin(2 * 3.14 * 20 * (i + 1) / 31250);
        }
    }

    public void stopGenerateSignal() {
        new Thread(() -> {
            while (!isStopped) {
                try {
//                    isStopped = doSmoothStop(MainController.sin8HzAmplitude, MainController.sin20HzAmplitude);
                    Thread.sleep(950);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public double[] takeData() {
        ringBuffer.take(outputArray, data.length);
        return outputArray;
    }

    public LTR34 getLtr34() {
        return ltr34;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }
}
