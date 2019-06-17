package ru.avem.posum.models.signal;

import javafx.scene.chart.XYChart;
import org.vitrivr.cineast.core.util.dsp.fft.FFT;
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow;

import java.util.ArrayList;
import java.util.List;

public class GraphModel {
    private FFT fft = new FFT();
    private HanningWindow hanningWindow = new HanningWindow();
    private double lowerBound;
    private double scale;
    private double tickUnit;
    private double upperBound;
    private String valueName;

    public void calculateGraphBounds() {
        if (valueName.equals("В")) {
            lowerBound = -scale * 10;
            tickUnit = scale * 2;
            upperBound = scale * 10;
        } else if (valueName.equals("с") || valueName.equals("Гц")) {
            lowerBound = 0;
            tickUnit = scale;
            upperBound = scale * 10;
        }

    }

    public void parseGraphScale(String scale) {
        String[] separatedScale = scale.split(" ");
        double digits = Double.parseDouble(separatedScale[0]);
        String suffix = separatedScale[1].substring(0, 1);
        separatedScale = scale.split("/дел");

        if (suffix.equals("м")) {
            valueName = separatedScale[0].substring(separatedScale[0].length() - 1);
            this.scale = digits * 0.001;
        } else {
            valueName = scale.split(" ")[1].split("/дел")[0];
            this.scale = digits;
        }
    }

    public void doFFT(int channel, double[] data) {
        int channels = 4; // TODO: delete this shit
        double[] channelData = new double[data.length / 4];

        for (int i = channel, j = 0; i < data.length; i += channels) {
            channelData[j++] = data[i];
        }

        fft.forward(channelData, channelData.length, hanningWindow);
    }

    public List<XYChart.Data<Number, Number>> getMagnitude() {
        List<XYChart.Data<Number, Number>> intermediateList = new ArrayList<>();

        for (int i = 0; i < fft.getMagnitudeSpectrum().size(); i++) {
            double frequency = fft.getMagnitudeSpectrum().getFrequency(i);
            double magnitude = fft.getMagnitudeSpectrum().getValue(i);
            intermediateList.add(new XYChart.Data<>(frequency, magnitude));
        }

        return intermediateList;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public double getUpperBound() {
        return upperBound;
    }
}