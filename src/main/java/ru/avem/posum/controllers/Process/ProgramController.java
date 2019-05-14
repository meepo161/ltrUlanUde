package ru.avem.posum.controllers.Process;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.Process.ProgramModel;
import ru.avem.posum.models.Process.ProcessSampleModel;

public class ProgramController implements BaseController {
    private Slider amplitudeSlider;
    private TextField amplitudeTextField;
    private TextField calibratedAmplitudeTextField;
    private TextField calibratedFrequencyTextField;
    private Slider dcSlider;
    private TextField dcTextField;
    private Slider frequencySlider;
    private TextField frequencyTextField;
    private AnchorPane mainPanel;
    private ProcessSampleModel processSampleModel;
    private Slider phaseSlider;
    private TextField phaseTextField;
    private ProgramModel programModel = new ProgramModel();
    private ToolBar toolbarSettings;
    private VBox topPanel;
    private WindowsManager wm;

    public ProgramController(Slider amplitudeSlider, TextField amplitudeTextField,
                             TextField calibratedAmplitudeTextField, TextField calibratedFrequencyTextField,
                             Slider dcSlider, TextField dcTextField, Slider frequencySlider,
                             TextField frequencyTextField, AnchorPane mainPanel, ProcessSampleModel processSampleModel,
                             Slider phaseSlider, TextField phaseTextField, ToolBar toolbarSettings, VBox topPanel) {

        this.amplitudeSlider = amplitudeSlider;
        this.amplitudeTextField = amplitudeTextField;
        this.calibratedAmplitudeTextField = calibratedAmplitudeTextField;
        this.calibratedFrequencyTextField = calibratedFrequencyTextField;
        this.dcSlider = dcSlider;
        this.dcTextField = dcTextField;
        this.frequencySlider = frequencySlider;
        this.frequencyTextField = frequencyTextField;
        this.mainPanel = mainPanel;
        this.processSampleModel = processSampleModel;
        this.phaseSlider = phaseSlider;
        this.phaseTextField = phaseTextField;
        this.toolbarSettings = toolbarSettings;
        this.topPanel = topPanel;

        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        toolbarSettings.setVisible(false);
        initSliders();
    }

    public void toggleSettingsPanel() {
        int TOOLBAR_HEIGHT = 110;
        boolean hide = programModel.checkToProgramClicksCounter();
        double neededHeight = hide ? mainPanel.getMaxHeight() : mainPanel.getMaxHeight() + TOOLBAR_HEIGHT;

        toolbarSettings.setVisible(!hide);
        topPanel.setPrefHeight(neededHeight);
        topPanel.maxHeight(neededHeight);
        topPanel.minHeight(neededHeight);
        processSampleModel.fitTable();
    }

    private void initSliders() {
        init(amplitudeSlider, amplitudeTextField);
        init(dcSlider, dcTextField);
        init(frequencySlider, frequencyTextField);
        init(phaseSlider, phaseTextField);
    }

    private void init(Slider slider, TextField textField) {
        slider.valueProperty().addListener((ChangeListener) (arg0, arg1, arg2) -> textField.textProperty().setValue(
                String.valueOf((int) slider.getValue())));

        setDigitFilter(textField);
        listen(textField, slider);
    }


    private void setDigitFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.replaceAll("[^-\\d(\\.|,)]", ""));
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void listen(TextField textField, Slider slider) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = textField.getText();

            if (!value.equals("-") && !value.isEmpty()) {
                slider.setValue((int) Double.parseDouble(textField.getText()));
            }
        });
    }

    public void loadDacAndAdcChannels() {

    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
