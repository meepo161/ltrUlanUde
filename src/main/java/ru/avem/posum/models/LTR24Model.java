package ru.avem.posum.models;

import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.utils.RingBuffer;
import ru.avem.posum.utils.TextEncoder;

public class LTR24Model {
    private int moduleSlot;
    private double[] data = new double[1024];
    public double[] outputArray = new double[data.length * 4];
    private double[] bufferData = new double[data.length];
    private RingBuffer ringBuffer = new RingBuffer(data.length * 100);
    private TextEncoder textEncoder = new TextEncoder();
    private String decodedError;
    private boolean isInitialized;
    private boolean isFilled;
    private boolean isStopped;


    public LTR24Model(int slot) {
        moduleSlot = slot;
    }

    public void initialize() {
//        String status = ltr24.initialize(moduleSlot, LTR24ChannelsModel.getSelectedChannelsTypes(), LTR24ChannelsModel.getSelectedMeasuringRanges());
//        isInitialized = checkError(status);
//        Platform.runLater(() -> {
//            iMainController.setMainStatusBarText(status == null ? "LTR24: Инициализация выполнена без ошибок" : decodedError);
//        });
    }

    public void receiveData() {
//        ltr24.fillArray(data);
        ringBuffer.put(data);
    }

    public void doFFT() {
        /* ringBuffer.take(outputArray,data.length * 4);
        Complex[] complexArray = new Complex[data.length * 4];

        for (int i = 0; i < complexArray.length; i++) {
            complexArray[i] = new Complex(outputArray[i], 0);
        }

        Complex[] outputArray = FFT.fft(complexArray);
        FFT.prepare(outputArray, 3662); */
    }

    public double[] takeChannelData(int channel) {
        double[] channelData = new double[data.length / 4];
        int j = 0;

        ringBuffer.take(bufferData, bufferData.length);

        for (int i = --channel; i < bufferData.length; i += 4) {
            channelData[j++] = bufferData[i];
        }

        return channelData;
    }

//    public void stop() {
//        isStopped = checkError(ltr24.stop());
//    }

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
