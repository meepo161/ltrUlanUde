package ru.avem.posum.controllers.signal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.controlsfx.control.StatusBar;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.signal.SignalModel;
import ru.avem.posum.utils.StatusBarLine;
import ru.avem.posum.utils.Utils;

public class SignalController implements BaseController {
    @FXML
    private Label amplitudeLabel;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private CheckBox autoRangeCheckBox;
    @FXML
    private CheckBox averageCheckBox;
    @FXML
    private TextField averageTextField;
    @FXML
    private Button calibrateButton;
    @FXML
    private CheckBox calibrationCheckBox;
    @FXML
    private Label checkIcon;
    @FXML
    private ComboBox<String> decimalFormatComboBox;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private LineChart<Number, Number> graph;
    @FXML
    private ComboBox<String> graphTypesComboBox;
    @FXML
    private ComboBox<String> horizontalScalesComboBox;
    @FXML
    private Label loadsCounterLabel;
    @FXML
    private TextField loadsCounterTextField;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox<String> rarefactionCoefficientComboBox;
    @FXML
    private Label rarefactionCoefficientLabel;
    @FXML
    private Label rmsLabel;
    @FXML
    private TextField rmsTextField;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<String> verticalScalesComboBox;
    @FXML
    private Label warningIcon;
    @FXML
    private Label zeroShiftLabel;
    @FXML
    private TextField zeroShiftTextField;

    private double amplitude;
    private ControllerManager cm;
    private double frequency;
    private GraphController graphController = new GraphController(this);
    private double loadsCounter;
    private double rms;
    private SignalModel signalModel = new SignalModel();
    private StatusBarLine statusBarLine;
    private WindowsManager wm;
    private double zeroShift;

    public void initializeView() {
        statusBarLine = new StatusBarLine(checkIcon, false, progressIndicator,
                statusBar, warningIcon);
        setTitleLabel();
        graphController.initGraph();
        graphController.initComboBoxes();
        graphController.initCheckBoxes();

        initializeTextFields();
        checkCalibration();
        startShow();
    }

    private void setTitleLabel() {
        titleLabel.setText("Текущая нагрузка на " + (signalModel.getChannel() + 1) + " канале"
                + " (" + signalModel.getModuleType() + " слот " + signalModel.getSlot() + ")");
    }

    private void initializeTextFields() {
        resetCounters();
        setSignalParametersLabels();
    }

    private void resetCounters() {
        signalModel.setAmplitude(0);
        signalModel.setFrequency(0);
        signalModel.setLoadsCounter(0);
        signalModel.setRMS(0);
        signalModel.setDc(0);
    }

    public void setSignalParametersLabels() {
        Platform.runLater(() -> {
            amplitudeLabel.setText(String.format("Амлитуда, %s:", signalModel.getValueName()));
            loadsCounterLabel.setText("Нагружений:");
            rmsLabel.setText(String.format("RMS, %s:", signalModel.getValueName()));
            zeroShiftLabel.setText(String.format("Статика, %s:", signalModel.getValueName()));
        });
    }

    public void checkCalibration() {
        signalModel.defineModuleInstance(cm.getCrateModelInstance().getModulesList());
        signalModel.checkCalibration();

        if (signalModel.isCalibrationExists()) {
            graphController.setShowFinished(true);
            graphController.clearSeries();
            calibrationCheckBox.setSelected(true);
            graphController.setValueNameToGraph();
            graphController.setGraphBounds();
            setSignalParametersLabels();
        } else if (signalModel.getAdc().getCalibrationSettings().isEmpty()) {
            statusBarLine.setStatus("Градуировочные коэффициенты отсутсвуют", false);
        }
    }

    private void startShow() {
        cm.setStopped(false);
        receiveData();
        showData();
    }

    private void receiveData() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.calculateData();
                signalModel.getData();
                checkConnection();
            }
        }).start();
    }

    private void showData() {
        checkConnection();
        printData();
        printGraph();
    }

    private void checkConnection() {
        if (signalModel.isConnectionLost()) {
            signalModel.setFrequency(0);
            statusBarLine.setStatus(signalModel.getAdc().getStatus(), false);
        }
    }

    private void printData() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                showCalculatedValues();
                Utils.sleep(1000);
            }
        }).start();
    }

    private void showCalculatedValues() {
        setParametersFields();
        showParameters();
    }

    private void setParametersFields() {
        int decimalFormatScale = getDecimalFormatScale();
        if (signalModel.isCalibrationExists()) {
            amplitude = Utils.roundValue(signalModel.getCalibratedAmplitude(), decimalFormatScale);
            frequency = Utils.roundValue(signalModel.getFrequency(), decimalFormatScale);
            loadsCounter = Utils.roundValue(signalModel.getLoadsCounter(), decimalFormatScale);
            rms = Utils.roundValue(signalModel.getCalibratedRms(), decimalFormatScale);
            zeroShift = Utils.roundValue(signalModel.getCalibratedZeroShift(), decimalFormatScale);
        } else {
            amplitude = Utils.roundValue(signalModel.getAmplitude(), decimalFormatScale);
            frequency = Utils.roundValue(signalModel.getFrequency(), decimalFormatScale);
            loadsCounter = Utils.roundValue((int) signalModel.getLoadsCounter(), decimalFormatScale);
            rms = Utils.roundValue(signalModel.getRms(), decimalFormatScale);
            zeroShift = Utils.roundValue(signalModel.getDc(), decimalFormatScale);
        }
    }

    private void showParameters() {
        Platform.runLater(() -> {
            amplitudeTextField.setText(Utils.convertFromExponentialFormat(amplitude, getDecimalFormatScale()));
            frequencyTextField.setText(Utils.convertFromExponentialFormat(frequency, getDecimalFormatScale()));
            loadsCounterTextField.setText(Utils.convertFromExponentialFormat(loadsCounter, getDecimalFormatScale()));
            rmsTextField.setText(Utils.convertFromExponentialFormat(rms, getDecimalFormatScale()));
            zeroShiftTextField.setText(Utils.convertFromExponentialFormat(zeroShift, getDecimalFormatScale()));
        });
    }

    private void printGraph() {
        new Thread(() -> {
            while (!cm.isClosed() && !cm.isStopped()) {
                signalModel.fillBuffer();
                graphController.clearSeries();
                if (graphController.isFFT()) {
                    graphController.showSpectre();
                } else {
                    graphController.showGraph();
                }
                Utils.sleep(1000);
            }
        }).start();
    }

    @FXML
    private void handleCalibrate() {
        cm.loadDefaultCalibrationSettings(signalModel);
        cm.showChannelValue();
        wm.setScene(WindowsManager.Scenes.CALIBRATION_SCENE);
    }

    @FXML
    private void handleBackButton() {
        toggleProgressIndicatorState(false);
        statusBarLine.setStatusOfProgress("Загрузка настроек модуля");
        new Thread(() -> {
            stopReceivingOfData();
            resetShowingSettings();
            changeScene();
            statusBarLine.toggleProgressIndicator(true);
            statusBarLine.clear();
        }).start();
    }

    private void stopReceivingOfData() {
        cm.setStopped(true);
        Utils.sleep(1000);
        signalModel.getAdc().stop();
    }

    private void toggleProgressIndicatorState(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void resetShowingSettings() {
        Platform.runLater(() -> {
            graphController.disableAutoRange();
            graphController.disableAverage();
            graphController.disableCalibration();
            signalModel.setLoadsCounter(0);
        });
    }

    private void changeScene() {
        String moduleType = signalModel.getModuleType();
        int slot = signalModel.getSlot();
        Platform.runLater(() -> wm.setModuleScene(moduleType, slot - 1));
        cm.loadItemsForModulesTableView();
    }

    public int getDecimalFormatScale() {
        return (int) Math.pow(10, decimalFormatComboBox.getSelectionModel().getSelectedIndex() + 1);
    }

    public CheckBox getCalibrationCheckBox() {
        return calibrationCheckBox;
    }

    public LineChart<Number, Number> getGraph() {
        return graph;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public ComboBox<String> getHorizontalScalesComboBox() {
        return horizontalScalesComboBox;
    }

    public ComboBox<String> getRarefactionCoefficientComboBox() {
        return rarefactionCoefficientComboBox;
    }

    public SignalModel getSignalModel() {
        return signalModel;
    }

    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }

    public CheckBox getAutoRangeCheckBox() {
        return autoRangeCheckBox;
    }

    public CheckBox getAverageCheckBox() {
        return averageCheckBox;
    }

    public TextField getAverageTextField() {
        return averageTextField;
    }

    public Label getCheckIcon() {
        return checkIcon;
    }

    public ComboBox<String> getDecimalFormatComboBox() {
        return decimalFormatComboBox;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public ComboBox<String> getVerticalScalesComboBox() {
        return verticalScalesComboBox;
    }

    public Label getWarningIcon() {
        return warningIcon;
    }

    public ControllerManager getCm() {
        return cm;
    }

    public WindowsManager getWm() {
        return wm;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public ComboBox<String> getGraphTypesComboBox() {
        return graphTypesComboBox;
    }

    public Label getRarefactionCoefficientLabel() {
        return rarefactionCoefficientLabel;
    }

    public Button getCalibrateButton() {
        return calibrateButton;
    }

    public StatusBarLine getStatusBarLine() {
        return statusBarLine;
    }
}
