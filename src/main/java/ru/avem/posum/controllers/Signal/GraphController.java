package ru.avem.posum.controllers.Signal;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.avem.posum.models.Signal.GraphModel;
import ru.avem.posum.utils.Utils;

public class GraphController {
    private SignalController signalController;
    private LineChart<Number, Number> graph;
    private GraphModel graphModel = new GraphModel();
    private XYChart.Series<Number, Number> graphSeries = new XYChart.Series<>();
    private boolean isFFT;
    private boolean isShowFinished;

    public GraphController(SignalController signalController) {
        this.signalController = signalController;
    }

    public void initGraph() {
        graph = signalController.getGraphController();
        signalController.getGraphController().getData().clear();
        signalController.getGraphController().getData().add(graphSeries);
        clearSeries();
        toggleAutoRange(false);
    }

    private void toggleAutoRange(boolean isAutoRangeEnabled) {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(isAutoRangeEnabled);
        Utils.sleep(100);
    }

    public void clearSeries() {
        Platform.runLater(() -> graphSeries.getData().clear());
        Utils.sleep(50);
    }

    public void initComboBoxes() {
        setVerticalSignalScales();
        setHorizontalScales();
        setDefaultScales();
        listenScalesComboBox(signalController.getVerticalScalesComboBox());
        listenScalesComboBox(signalController.getHorizontalScalesComboBox());
        addRarefactionCoefficients();
        listenRarefactionComboBox();
        setGraphTypes();
        listenGraphTypesComboBox();
        setDecimalFormats();
    }

    private void setVerticalSignalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        scales.add("1 мВ/дел");
        scales.add("10 мВ/дел");
        scales.add("100 мВ/дел");
        scales.add("1 В/дел");
        scales.add("10 В/дел");
        scales.add("100 В/дел");

        signalController.getVerticalScalesComboBox().setItems(scales);
        signalController.getVerticalScalesComboBox().getSelectionModel().select(3);
    }

    private void setHorizontalScales() {
        ObservableList<String> scales = FXCollections.observableArrayList();

        if (isFFT) {
            scales.add("1 Гц/дел");
            scales.add("2 Гц/дел");
            scales.add("5 Гц/дел");
            scales.add("10 Гц/дел");
            scales.add("20 Гц/дел");
            scales.add("50 Гц/дел");
            scales.add("100 Гц/дел");
        } else {
            scales.add("1 мс/дел");
            scales.add("10 мс/дел");
            scales.add("100 мс/дел");
        }

        signalController.getHorizontalScalesComboBox().setItems(scales);
        signalController.getHorizontalScalesComboBox().getSelectionModel().select(2);
    }

    private void setDefaultScales() {
        graphModel.parseGraphScale(signalController.getVerticalScalesComboBox().getSelectionModel().getSelectedItem());
        graphModel.calculateGraphBounds();
        setScale((NumberAxis) graph.getYAxis());

        graphModel.parseGraphScale(signalController.getHorizontalScalesComboBox().getSelectionModel().getSelectedItem());
        graphModel.calculateGraphBounds();
        setScale((NumberAxis) graph.getXAxis());
    }

    private void setScale(NumberAxis axis) {
        axis.setLowerBound(graphModel.getLowerBound());
        axis.setTickUnit(graphModel.getTickUnit());
        axis.setUpperBound(graphModel.getUpperBound());
    }

    private void listenScalesComboBox(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                graphModel.parseGraphScale(comboBox.getSelectionModel().getSelectedItem());
                graphModel.calculateGraphBounds();

                if (comboBox == signalController.getVerticalScalesComboBox()) {
                    setScale((NumberAxis) graph.getYAxis());
                } else {
                    setScale((NumberAxis) graph.getXAxis());
                }
            }
        });
    }

    private void addRarefactionCoefficients() {
        ObservableList<String> coefficients = FXCollections.observableArrayList();

        coefficients.add("Все");
        coefficients.add("В 2 меньше");
        coefficients.add("В 5 меньше");
        coefficients.add("В 10 меньше");
        coefficients.add("В 15 меньше");
        coefficients.add("В 20 меньше");
        coefficients.add("В 25 меньше");

        signalController.getRarefactionCoefficientComboBox().setItems(coefficients);
        signalController.getRarefactionCoefficientComboBox().getSelectionModel().select(3);
    }

    private void listenRarefactionComboBox() {
        signalController.getRarefactionCoefficientComboBox().valueProperty().addListener(observable -> {
            switch (signalController.getRarefactionCoefficientComboBox().getSelectionModel().getSelectedIndex()) {
                case 0:
                    signalController.getSignalModel().setRarefactionCoefficient(1);
                    break;
                case 1:
                    signalController.getSignalModel().setRarefactionCoefficient(2);
                    break;
                case 2:
                    signalController.getSignalModel().setRarefactionCoefficient(5);
                    break;
                case 3:
                    signalController.getSignalModel().setRarefactionCoefficient(10);
                    break;
                case 4:
                    signalController.getSignalModel().setRarefactionCoefficient(15);
                    break;
                case 5:
                    signalController.getSignalModel().setRarefactionCoefficient(20);
                    break;
                case 6:
                    signalController.getSignalModel().setRarefactionCoefficient(25);
                    break;
            }

            int selectedScale = signalController.getHorizontalScalesComboBox().getSelectionModel().getSelectedIndex();
            int lastScale = signalController.getHorizontalScalesComboBox().getItems().size() - 1;
            if (selectedScale == lastScale) {
                signalController.getHorizontalScalesComboBox().getSelectionModel().select(lastScale - 1);
                signalController.getHorizontalScalesComboBox().getSelectionModel().select(lastScale);
            } else {
                signalController.getHorizontalScalesComboBox().getSelectionModel().select(lastScale);
                signalController.getHorizontalScalesComboBox().getSelectionModel().select(selectedScale);
            }
        });
    }

    private void setGraphTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add(GraphTypes.SIGNAL.getTypeName());
        types.add(GraphTypes.SPECTRUM.getTypeName());

        signalController.getGraphTypesComboBox().getItems().setAll(types);
        signalController.getGraphTypesComboBox().getSelectionModel().select(0);
    }

    private void listenGraphTypesComboBox() {
        signalController.getGraphTypesComboBox().valueProperty().addListener(observable -> {
            String selectedType = signalController.getGraphTypesComboBox().getSelectionModel().getSelectedItem();
            isFFT = selectedType.equals(GraphTypes.SPECTRUM.getTypeName());
            toggleGraphLabels();
            setHorizontalScales();
            toggleUiElementsState(isFFT);
            toggleRarefactionCoefficient();
            restartOfShow();
        });
    }

    private void toggleGraphLabels() {
        if (isFFT) {
            graph.setTitle("Спектр сигнала");
            graph.getYAxis().setLabel("Амплитуда, В");
            graph.getXAxis().setLabel("Частота, Гц");
        } else {
            graph.setTitle("График сигнала");
            graph.getYAxis().setLabel("Напряжение, В");
            graph.getXAxis().setLabel("Время, с");
        }
    }

    private void toggleUiElementsState(boolean isDisable) {
        signalController.getRarefactionCoefficientLabel().setDisable(isDisable);
        signalController.getRarefactionCoefficientComboBox().setDisable(isDisable);
        signalController.getCalibrationCheckBox().setDisable(isDisable);
        signalController.getCalibrateButton().setDisable(isDisable);
    }

    private void toggleRarefactionCoefficient() {
        if (isFFT) {
            signalController.getRarefactionCoefficientComboBox().getSelectionModel().select(0);
        } else {
            signalController.getRarefactionCoefficientComboBox().getSelectionModel().select(3);
        }
    }

    private void setDecimalFormats() {
        if (signalController.getDecimalFormatComboBox().getItems().isEmpty()) {
            ObservableList<String> strings = FXCollections.observableArrayList();
            for (int i = 1; i <= Utils.getDecimalScaleLimit(); i++) {
                strings.add(String.format("%d", i));
            }
            signalController.getDecimalFormatComboBox().getItems().addAll(strings);
            signalController.getDecimalFormatComboBox().getSelectionModel().select(1);
        }
    }

    public void initCheckBoxes() {
        listenAverageCheckBox();
        initAverage();
        listenAutoRangeCheckBox();
        listenCalibrationCheckBox();
    }

    private void listenAverageCheckBox() {
        signalController.getAverageCheckBox().selectedProperty().addListener(observable -> {
            if (signalController.getAverageCheckBox().isSelected()) {
                signalController.getAverageTextField().setDisable(false);
            } else {
                signalController.getAverageTextField().setDisable(true);
                signalController.getAverageTextField().setText("");
                signalController.getSignalModel().setAverageCount(1);
            }
        });
    }

    private void initAverage() {
        setDigitFilterToAverageTextField();
        changeAverageUiElementsState();
    }

    private void setDigitFilterToAverageTextField() {
        TextField averageTextField = signalController.getAverageTextField();

        averageTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            averageTextField.setText(newValue.replaceAll("[^1-9][\\d]{2,3}", ""));
            if (!newValue.matches("^[1-9]|\\d{2,3}|$")) {
                averageTextField.setText(oldValue);
            }

            if (!averageTextField.getText().isEmpty()) {
                signalController.getSignalModel().setAverageCount(Integer.parseInt(averageTextField.getText()));
            }
        });
    }

    private void changeAverageUiElementsState() {
        signalController.getAverageCheckBox().selectedProperty().addListener(observable -> {
            if (signalController.getAverageCheckBox().isSelected()) {
                signalController.getAverageTextField().setDisable(false);
            } else {
                signalController.getAverageTextField().setDisable(true);
            }
        });
    }

    private void listenAutoRangeCheckBox() {
        signalController.getAutoRangeCheckBox().selectedProperty().addListener(observable -> {
            if (signalController.getAutoRangeCheckBox().isSelected()) {
                toggleAutoRange(true);
            } else {
                toggleAutoRange(false);
                resetGraphBounds();
            }
        });
    }

    public void disableAutoRange() {
        toggleAutoRange(false);
        signalController.getAutoRangeCheckBox().setSelected(false);
    }

    public void disableAverage() {
        signalController.getAverageTextField().setText("");
        signalController.getAverageCheckBox().setSelected(false);
    }

    public void disableCalibration() {
        signalController.getCalibrationCheckBox().setSelected(false);
        signalController.getSignalModel().setCalibrationExists(false);
    }

    private void resetGraphBounds() {
        if (signalController.getSignalModel().isCalibrationExists()) {
            setCalibratedGraphBounds();
        } else {
            setNonCalibratedGraphBounds();
        }
    }

    private void setCalibratedGraphBounds() {
        setGraphBounds();
        clearSeries();
    }

    public void setGraphBounds() {
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setLowerBound(signalController.getSignalModel().getLowerBound());
        yAxis.setUpperBound(signalController.getSignalModel().getUpperBound());
        yAxis.setTickUnit(signalController.getSignalModel().getTickUnit());
    }

    private void setNonCalibratedGraphBounds() {
        int selectedRange = signalController.getVerticalScalesComboBox().getSelectionModel().getSelectedIndex();
        if (selectedRange != 0) {
            signalController.getVerticalScalesComboBox().getSelectionModel().select(0);
        } else {
            signalController.getVerticalScalesComboBox().getSelectionModel().select(1);
        }
        signalController.getVerticalScalesComboBox().getSelectionModel().select(selectedRange);
    }

    private void listenCalibrationCheckBox() {
        signalController.getCalibrationCheckBox().selectedProperty().addListener(observable -> {
            if (signalController.getCalibrationCheckBox().isSelected()) {
                signalController.checkCalibration();
            } else {
                signalController.getSignalModel().setCalibrationExists(false);
                signalController.getSignalModel().setDefaultValueName();
                signalController.getVerticalScalesComboBox().setDisable(false);
                setValueNameToGraph();
                signalController.setSignalParametersLabels();
                resetGraphBounds();
            }
        });
    }

    public void setValueNameToGraph() {
        Platform.runLater(() -> graph.getYAxis().setLabel(signalController.getSignalModel().getValueName()));
    }

    public void showGraph() {
        String selectedGraphScale = signalController.getHorizontalScalesComboBox().getSelectionModel().getSelectedItem();
        graphModel.parseGraphScale(selectedGraphScale);
        graphModel.calculateGraphBounds();

        int channel = signalController.getSignalModel().getChannel();
        int channels = signalController.getSignalModel().getAdc().getChannelsCount();
        double[] data = signalController.getSignalModel().getBuffer();
        int rarefactionCoefficient = signalController.getSignalModel().getRarefactionCoefficient();

        for (int index = channel; index < data.length && !signalController.getCm().isStopped(); index += channels * rarefactionCoefficient) {
            if (isShowFinished) {
                Platform.runLater(() -> graphSeries.getData().clear());
                break;
            }

            XYChart.Data<Number, Number> point = signalController.getSignalModel().getPoint(index);
            Runnable addPoint = () -> {
                if (!graphSeries.getData().contains(point))
                    graphSeries.getData().add(point);
            };

            if ((double) point.getXValue() < graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }

            if (index + (channels * rarefactionCoefficient) >= data.length) {
                XYChart.Data<Number, Number> lastPoint = new XYChart.Data<>(1, data[index]);
                Platform.runLater(() -> graphSeries.getData().add(lastPoint));
                Utils.sleep(1);
            } else if ((double) point.getXValue() >= graphModel.getUpperBound()) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
                break;
            }
        }
    }

    public void showSpectre() {
        double[] data = signalController.getSignalModel().getBuffer();
        NumberAxis xAxis = (NumberAxis) graph.getXAxis();

        graphModel.doFFT(signalController.getSignalModel().getChannel(), data);
        graphSeries.getData().add(new XYChart.Data<>(0, 0));

        for (int i = 0; i < (xAxis.getUpperBound() * 2); i++) {
            if (isShowFinished) {
                Platform.runLater(() -> graphSeries.getData().clear());
                break;
            }

            XYChart.Data<Number, Number> point = graphModel.getMagnitude().get(i);
            XYChart.Data<Number, Number> previousPoint = new XYChart.Data<>((double) point.getXValue() / 1.001, 0);
            XYChart.Data<Number, Number> nextPoint = new XYChart.Data<>((double) point.getXValue() * 1.001, 0);

            Runnable addPoint = () -> {
                graphSeries.getData().add(previousPoint);
                graphSeries.getData().add(point);
                graphSeries.getData().add(nextPoint);
            };

            if ((double) point.getXValue() < xAxis.getUpperBound() * 2) {
                Platform.runLater(addPoint);
                Utils.sleep(1);
            }
        }
    }

    private void restartOfShow() {
        isShowFinished = true;
        Utils.sleep(100);
        isShowFinished = false;
    }

    public boolean isFFT() {
        return isFFT;
    }
}
