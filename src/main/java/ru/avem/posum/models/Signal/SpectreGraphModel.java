package ru.avem.posum.models.Signal;

import javafx.scene.chart.XYChart;
import org.vitrivr.cineast.core.util.dsp.fft.FFT;
import org.vitrivr.cineast.core.util.dsp.fft.Spectrum;
import org.vitrivr.cineast.core.util.dsp.fft.windows.HanningWindow;
import ru.avem.posum.controllers.Signal.SpectreGraph;

public class SpectreGraphModel {
    private FFT fft = new FFT();
    private HanningWindow hanningWindow = new HanningWindow();

    public void doFFT(double[] data) {
        fft.forward(data, data.length, hanningWindow);
    }

    public XYChart.Series<Number, Number> createSpectrumSeries(Spectrum spectrum) {
        XYChart.Series<Number, Number> spectrumSeries = new XYChart.Series<>();

        for (int i = 0; i < spectrum.size(); i++) {
            double frequency = spectrum.getFrequency(i);
            double value = spectrum.getValue(i);
            XYChart.Data<Number, Number> point = new XYChart.Data<>(frequency, value);
            spectrumSeries.getData().add(point);
        }

        return spectrumSeries;
    }

    public FFT getFft() {
        return fft;
    }
}
