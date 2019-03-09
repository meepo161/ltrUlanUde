package ru.avem.posum.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CalibrationModel {
    private int channel;
    private List<Double> loadValue = new ArrayList<>();
    private List<Double> channelValue = new ArrayList<>();
    private List<String> valueName = new ArrayList<>();
    private List<Double> coefficientA = new ArrayList<>();
    private List<Double> coefficientB = new ArrayList<>();

    public byte[] convertToByteArray(List list) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(list);
        return bos.toByteArray();
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public List<Double> getLoadValue() {
        return loadValue;
    }

    public List<Double> getChannelValue() {
        return channelValue;
    }

    public List<String> getValueName() {
        return valueName;
    }

    public List<Double> getCoefficientA() {
        return coefficientA;
    }

    public List<Double> getCoefficientB() {
        return coefficientB;
    }
}
