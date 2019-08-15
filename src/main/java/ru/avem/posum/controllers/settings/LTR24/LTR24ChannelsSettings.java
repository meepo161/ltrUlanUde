package ru.avem.posum.controllers.settings.LTR24;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.avem.posum.hardware.ADC;
import ru.avem.posum.hardware.LTR24;
import ru.avem.posum.models.settings.LTR24SettingsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LTR24ChannelsSettings extends LTR24Settings {
    private CheckBox applyForAllChannels;
    private Button backButton;
    private List<ComboBox<String>> typesOfChannelComboBoxes = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<TextField> descriptions = new ArrayList<>();
    private Button initializeButton;
    private LTR24SettingsModel ltr24SettingsModel;
    private List<ComboBox<String>> measuringRangesComboBoxes = new ArrayList<>();
    private List<Button> valueOnChannelButtons = new ArrayList<>();

    LTR24ChannelsSettings(LTR24Settings ltr24Settings) {
        this.applyForAllChannels = ltr24Settings.getApplyForAllChannels();
        this.backButton = ltr24Settings.getBackButton();
        this.initializeButton = ltr24Settings.getInitializeButton();
        this.ltr24SettingsModel = ltr24Settings.getLtr24SettingsModel();
        fillListOfCheckBoxes(ltr24Settings);
        fillListOfDescriptionsTextFields(ltr24Settings);
        fillListOfChannelsTypesComboBoxes(ltr24Settings);
        fillListOfMeasuringRangesComboBoxes(ltr24Settings);
        fillListOfValueOnChannelButtons(ltr24Settings);
        addTypesOfChannels();
        listenCheckBoxes();
        listenComboBoxes(typesOfChannelComboBoxes);
        listenComboBoxes(measuringRangesComboBoxes);
        listenICPItemSelection();
        listenTestModes();
    }

    // Заполняет список пунктов
    private void fillListOfCheckBoxes(LTR24Settings ltr24Settings) {
        checkBoxes.addAll(Arrays.asList(
                ltr24Settings.getCheckChannelN1(),
                ltr24Settings.getCheckChannelN2(),
                ltr24Settings.getCheckChannelN3(),
                ltr24Settings.getCheckChannelN4()
        ));
    }

    // Заполняет список полей с описанием программы испытаний
    private void fillListOfDescriptionsTextFields(LTR24Settings ltr24Settings) {
        descriptions.addAll(Arrays.asList(
                ltr24Settings.getDescriptionOfChannelN1(),
                ltr24Settings.getDescriptionOfChannelN2(),
                ltr24Settings.getDescriptionOfChannelN3(),
                ltr24Settings.getDescriptionOfChannelN4()
        ));
    }

    // Заполняет список меню выбора режимов работы каналов
    private void fillListOfChannelsTypesComboBoxes(LTR24Settings ltr24Settings) {
        typesOfChannelComboBoxes.addAll(Arrays.asList(
                ltr24Settings.getTypeOfChannelN1(),
                ltr24Settings.getTypeOfChannelN2(),
                ltr24Settings.getTypeOfChannelN3(),
                ltr24Settings.getTypeOfChannelN4()
        ));
    }

    // Заполняет список меню выбора диапазонов измерения каналов
    private void fillListOfMeasuringRangesComboBoxes(LTR24Settings ltr24Settings) {
        measuringRangesComboBoxes.addAll(Arrays.asList(
                ltr24Settings.getMeasuringRangeOfChannelN1(),
                ltr24Settings.getMeasuringRangeOfChannelN2(),
                ltr24Settings.getMeasuringRangeOfChannelN3(),
                ltr24Settings.getMeasuringRangeOfChannelN4()
        ));
    }

    // Заполняет список кнопок "Нагрузка на канале"
    private void fillListOfValueOnChannelButtons(LTR24Settings ltr24Settings) {
        valueOnChannelButtons.addAll(Arrays.asList(
                ltr24Settings.getValueOnChannelN1(),
                ltr24Settings.getValueOnChannelN2(),
                ltr24Settings.getValueOnChannelN3(),
                ltr24Settings.getValueOnChannelN4()
        ));
    }

    // Добавляет режимы работы каналов
    private void addTypesOfChannels() {
        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("Дифференциальный вход без отсечки постоянной составляющей");
        types.add("Дифференциальный вход с отсечкой постоянной составляющей");
        types.add("Режим ICP-вход");
        types.add("Режим измерения собственного нуля");
        types.add("Режим ICP - тест");

        for (ComboBox<String> comboBox : typesOfChannelComboBoxes) {
            comboBox.getItems().addAll(types);
            comboBox.getSelectionModel().select(1);
        }
    }

    // Переключает состояние GUI
    private void listenCheckBoxes() {
        for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.size(); checkBoxIndex++) {
            toggleUiElementsState(checkBoxIndex);
        }
    }

    // Переключает состояние GUI
    private void toggleUiElementsState(int checkBoxIndex) {
        checkBoxes.get(checkBoxIndex).selectedProperty().addListener(observable -> {
            selectCheckBoxes(checkBoxes.get(checkBoxIndex).isSelected());
            changeUiElementsState(checkBoxIndex, !checkBoxes.get(checkBoxIndex).isSelected());
            resetChannelSettings(checkBoxIndex);
        });
    }

    // Переключает состояние GUI
    private void selectCheckBoxes(boolean isCheckBoxSelected) {
        if (applyForAllChannels.isSelected()) {
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setSelected(isCheckBoxSelected);
            }
        }
    }

    // Переключает состояние GUI
    private void changeUiElementsState(int channelIndex, boolean isDisable) {
        typesOfChannelComboBoxes.get(channelIndex).setDisable(isDisable);
        measuringRangesComboBoxes.get(channelIndex).setDisable(isDisable);
        descriptions.get(channelIndex).setDisable(isDisable);

        int disabledCheckBoxesCounter = 0;
        for (CheckBox channelCheckBox : checkBoxes) {
            if (!channelCheckBox.isSelected()) {
                disabledCheckBoxesCounter++;
            }
        }

        initializeButton.setDisable(disabledCheckBoxesCounter == checkBoxes.size());
    }

    // Сбрасывает конфигурацию каналов
    private void resetChannelSettings(int channelIndex) {
        if (!checkBoxes.get(channelIndex).isSelected()) {
            descriptions.get(channelIndex).setText("");
            typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(1);
            measuringRangesComboBoxes.get(channelIndex).getSelectionModel().select(1);
        }
    }

    // Задает выбранный параметр для всех каналов
    private void listenComboBoxes(List<ComboBox<String>> comboBoxes) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.valueProperty().addListener(observable -> {
                if (applyForAllChannels.isSelected()) {
                    selectItem(comboBoxes, comboBox.getSelectionModel().getSelectedIndex());
                }
            });
        }
    }

    private void selectItem(List<ComboBox<String>> comboBoxes, int itemIndex) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.getSelectionModel().select(itemIndex);
        }
    }

    private void listenICPItemSelection() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            toggleMeasuringRanges(typesOfChannelComboBoxes.get(channelIndex), measuringRangesComboBoxes.get(channelIndex));
        }
    }

    // Изменяет меню выбора диапазона измерений каналов
    private void toggleMeasuringRanges(ComboBox<String> typesOfChannel, ComboBox<String> measuringRanges) {
        typesOfChannel.valueProperty().addListener(observable -> {
            ObservableList<String> ranges = FXCollections.observableArrayList();

            if (typesOfChannel.getSelectionModel().isSelected(2)) { // выбран ICP режим
                ranges.add("~1 В");
                ranges.add("~5 В");
            } else { // выбран дифференциальный режим
                ranges.add("-2 В/+2 В");
                ranges.add("-10 В/+10 В");
            }

            measuringRanges.getItems().setAll(ranges);
            measuringRanges.getSelectionModel().select(1);
        });
    }

    private void listenTestModes() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            toggleTestModes(typesOfChannelComboBoxes.get(channelIndex));
        }
    }

    // Оставляет возможным выбор правильной настройки каналов для тестового режима работы
    private void toggleTestModes(ComboBox<String> typesOfChannel) {
        typesOfChannel.valueProperty().addListener(observable -> {
            int selectedMode = typesOfChannel.getSelectionModel().getSelectedIndex();

            if (typesOfChannel.getSelectionModel().isSelected(3) || typesOfChannel.getSelectionModel().isSelected(4)) { // выбран тестовый режим
                for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
                    checkBoxes.get(channelIndex).setSelected(true);
                    typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(selectedMode);
                }
            }
        });
    }

    // Загружает настройки модуля
    void setSettings() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            boolean isCheckBoxSelected = ltr24SettingsModel.getCheckedChannels()[channelIndex];
            int typeOfChannel = ltr24SettingsModel.getTypesOfChannels()[channelIndex];
            int measuringRange = ltr24SettingsModel.getMeasuringRanges()[channelIndex];
            String description = ltr24SettingsModel.getDescriptions()[channelIndex].replace(", ", "");

            checkBoxes.get(channelIndex).setSelected(isCheckBoxSelected);

            if (typeOfChannel == 0) { // TODO: change this shit
                typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(typeOfChannel + 1);
                typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(typeOfChannel);
            } else {
                typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(typeOfChannel - 1);
                typesOfChannelComboBoxes.get(channelIndex).getSelectionModel().select(typeOfChannel);
            }

            measuringRangesComboBoxes.get(channelIndex).getSelectionModel().select(measuringRange);
            descriptions.get(channelIndex).setText(description);
        }

        toggleInitializebutton();
    }

    // Сохраняет настройки модуля
    void saveSettings() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                ltr24SettingsModel.getCheckedChannels()[i] = true; // true - канал выбран
                ltr24SettingsModel.getDescriptions()[i] = descriptions.get(i).getText() + ", ";
                ltr24SettingsModel.getTypesOfChannels()[i] = typesOfChannelComboBoxes.get(i).getSelectionModel().getSelectedIndex();
                ltr24SettingsModel.getMeasuringRanges()[i] = measuringRangesComboBoxes.get(i).getSelectionModel().getSelectedIndex();
            } else {
                ltr24SettingsModel.getCheckedChannels()[i] = false; // false - канал не выбран
                ltr24SettingsModel.getDescriptions()[i] = ", ";
                ltr24SettingsModel.getTypesOfChannels()[i] = 0;
                ltr24SettingsModel.getMeasuringRanges()[i] = 1;
            }
        }
    }

    // Изменяет состояние GUI
    void disableUiElementsState() {
        for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.size(); checkBoxIndex++) {
            checkBoxes.get(checkBoxIndex).setDisable(true);
            descriptions.get(checkBoxIndex).setDisable(true);
            typesOfChannelComboBoxes.get(checkBoxIndex).setDisable(true);
            measuringRangesComboBoxes.get(checkBoxIndex).setDisable(true);
            valueOnChannelButtons.get(checkBoxIndex).setDisable(true);
        }
        applyForAllChannels.setDisable(true);
        backButton.setDisable(true);
        initializeButton.setDisable(true);
    }

    // Включает элеметы GUI
    void enableUiElements() {
        for (int channelIndex = 0; channelIndex < checkBoxes.size(); channelIndex++) {
            CheckBox checkBox = checkBoxes.get(channelIndex);
            checkBox.setDisable(false);
            descriptions.get(channelIndex).setDisable(!checkBox.isSelected());
            typesOfChannelComboBoxes.get(channelIndex).setDisable(!checkBox.isSelected());
            measuringRangesComboBoxes.get(channelIndex).setDisable(!checkBox.isSelected());
            valueOnChannelButtons.get(channelIndex).setDisable(true);
            initializeButton.setDisable(!checkBox.isSelected());
        }
        applyForAllChannels.setDisable(false);
        backButton.setDisable(false);
    }

    // Выключает кнопки "Нагрузка на канале"
    void disableValueOnChannelButtonsState() {
        for (Button valueOnChannelButton : valueOnChannelButtons) {
            valueOnChannelButton.setDisable(true);
        }
    }

    // Включает кнопки "Нагрузка на канале"
    void enableValueOnChannelButtonsState() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            valueOnChannelButtons.get(i).setDisable(!checkBoxes.get(i).isSelected());
        }
    }

    // Сохраняет пределы измерения модуля
    void saveMeasuringRangeOfChannel(int channel) {
        LTR24 ltr24 = ltr24SettingsModel.getLTR24Instance();
        int measuringRange = ltr24.getMeasuringRanges()[channel];
        int typeOfChannel = ltr24.getTypeOfChannels()[channel];

        switch (measuringRange) {
            case 0:
                if (typeOfChannel == 2) {
                    ADC.MeasuringRangeOfChannel.LOWER_BOUND.setBoundValue(-1);
                    ADC.MeasuringRangeOfChannel.UPPER_BOUND.setBoundValue(1);
                } else {
                    ADC.MeasuringRangeOfChannel.LOWER_BOUND.setBoundValue(-2);
                    ADC.MeasuringRangeOfChannel.UPPER_BOUND.setBoundValue(2);
                }
                break;
            case 1:
                if (typeOfChannel == 2) {
                    ADC.MeasuringRangeOfChannel.LOWER_BOUND.setBoundValue(-5);
                    ADC.MeasuringRangeOfChannel.UPPER_BOUND.setBoundValue(5);
                } else {
                    ADC.MeasuringRangeOfChannel.LOWER_BOUND.setBoundValue(-10);
                    ADC.MeasuringRangeOfChannel.UPPER_BOUND.setBoundValue(10);
                }
                break;
        }
    }

    // Меняет состояние кнопки "Инициализация"
    private void toggleInitializebutton() {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                initializeButton.setDisable(false);
                break;
            }
        }
    }
}
