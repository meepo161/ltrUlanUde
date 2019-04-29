package ru.avem.posum.controllers.Signal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.models.Signal.SignalGraphModel;
import ru.avem.posum.models.Signal.SpectreGraphModel;

public class SpectreGraph {
    private int onShowSpectreButtonClicksCounter;
    private int previousScaleValue;
    private SignalGraph signalGraph;
    private SignalGraphModel signalGraphModel = new SignalGraphModel();
    private SpectreGraphModel spectreGraphModel = new SpectreGraphModel();

    public SpectreGraph(SignalGraph signalGraph) {
        this.signalGraph = signalGraph;
    }

    public void incrementOnShowSpectreButtonClicksCounter() {
        onShowSpectreButtonClicksCounter++;
    }

    public void changeGraphTitle() {
        if (onShowSpectreButtonClicksCounter % 2 == 0) {
            signalGraph.getGraph().setTitle("График сигнала");
        } else {
            signalGraph.getGraph().setTitle("Спектр сигнала");
        }
    }

    public void changeGraphHorizontalScales() {
        if (onShowSpectreButtonClicksCounter % 2 == 0) {
            signalGraph.setHorizontalScaleValues();
            signalGraph.getHorizontalScalesComboBox().getSelectionModel().select(previousScaleValue);
        } else {
            previousScaleValue = signalGraph.getHorizontalScalesComboBox().getSelectionModel().getSelectedIndex();
            ObservableList<String> scaleValues = FXCollections.observableArrayList();

            scaleValues.add("1 Гц/дел");
            scaleValues.add("2 Гц/дел");
            scaleValues.add("5 Гц/дел");
            scaleValues.add("10 Гц/дел");
            scaleValues.add("50 Гц/дел");
            scaleValues.add("100 Гц/дел");
            scaleValues.add("200 Гц/дел");

            signalGraph.getHorizontalScalesComboBox().setItems(scaleValues);
            signalGraph.getHorizontalScalesComboBox().getSelectionModel().select(0);
        }

        signalGraph.listenScalesComboBox(signalGraph.getHorizontalScalesComboBox());
    }

    public void toggleRarefactionCoefficient() {
        signalGraph.getRarefactionCoefficientLabel().setDisable(onShowSpectreButtonClicksCounter % 2 == 0);
        signalGraph.getRarefactionCoefficientComboBox().setDisable(onShowSpectreButtonClicksCounter % 2 == 0);
    }

    public void toggleFrequencyCalculation() {
        signalGraph.getFrequencyCalculationComboBox().setDisable(onShowSpectreButtonClicksCounter % 2 == 0);
        signalGraph.getFrequencyCalculationLabel().setDisable(onShowSpectreButtonClicksCounter % 2 == 0);
    }

    public void toggleCalibration() {
        signalGraph.getCalibrationCheckBox().setDisable(onShowSpectreButtonClicksCounter % 2 == 0);
    }

    public void changeButtonTitle() {
        if (onShowSpectreButtonClicksCounter % 2 == 0) {
            signalGraph.getShowSpectreButton().setText("Спектр сигнала");
        } else {
            signalGraph.getShowSpectreButton().setText("График сигнала");
        }
    }

    public void changeTitlesOfAxis() {
        if (onShowSpectreButtonClicksCounter % 2 == 0) {
            signalGraph.getGraph().getYAxis().setLabel("Напряжение, В");
            signalGraph.getGraph().getXAxis().setLabel("Время, с");
        } else {
            signalGraph.getGraph().getYAxis().setLabel("Амплитуда, В");
            signalGraph.getGraph().getXAxis().setLabel("Частота, Гц");
        }
    }

    public void setCalculateFFt() {
        signalGraph.setFFT(onShowSpectreButtonClicksCounter % 2 == 0);
    }

    public SpectreGraphModel getSpectreGraphModel() {
        return spectreGraphModel;
    }
}
