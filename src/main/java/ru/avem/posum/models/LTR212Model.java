package ru.avem.posum.models;

import javafx.application.Platform;
import ru.avem.posum.controllers.IMainController;
import ru.avem.posum.hardware.LTR212;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

public class LTR212Model {
    private int moduleSlot;
    private LTR212 ltr212 = new LTR212();
    private double[] data = new double[1024];
    private double[] bufferData = new double[data.length];
    private RingBuffer ringBuffer = new RingBuffer(data.length * 100);
    private TextEncoder textEncoder = new TextEncoder();
    private String decodedError;
    private final IMainController iMainController;
    private boolean isInitialized;
    private boolean isFilled;
    private boolean isStopped;


    public LTR212Model(IMainController iMainController, int slot) {
        this.iMainController = iMainController;
        moduleSlot = slot;
    }

    public void initialize() {
        String status = ltr212.initialize(moduleSlot, LTR212ChannelsModel.getSelectedBridgeTypes(), LTR212ChannelsModel.getSelectedMeasuringRanges());
        isInitialized = checkError(status);
        Platform.runLater(() -> {
            iMainController.setMainStatusBarText(status == null ? "LTR212: Инициализация выполнена без ошибок" : decodedError);
        });
    }

    public void receiveData() {
        ltr212.fillArray(data);
        ringBuffer.put(data);
    }

    public void stop() {
        isStopped = checkError(ltr212.stop());
    }

    private boolean checkError(String status) {
        if (status != null) {
            decodedError = decodeError(status);
            return false;
        }
        return true;
    }

    private String decodeError(String error) {
        return "LTR212 (слот " + moduleSlot + "): " + textEncoder.cp2utf(error);
    }

    public RingBuffer getRingBuffer() {
        return ringBuffer;
    }

    public double[] getBufferData() {
        return bufferData;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public boolean isStopped() {
        return isStopped;
    }
}
