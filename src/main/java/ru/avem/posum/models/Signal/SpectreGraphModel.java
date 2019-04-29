package ru.avem.posum.models.Signal;

import javafx.scene.chart.XYChart;
import org.vitrivr.cineast.core.util.dsp.fft.FFT;
import org.vitrivr.cineast.core.util.dsp.fft.Spectrum;
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow;

public class SpectreGraphModel {
    private FFT fft = new FFT();
    private HanningWindow hanningWindow = new HanningWindow();

    public void doFFT(double[] data) {
        fft.forward(data, data.length, hanningWindow);
    }

    public FFT getFft() {
        return fft;
    }
}
