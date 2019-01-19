/**
 * Здесь 16 - количество каналов LTR24
 */

package ru.avem.posum.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.List;

public class LTR24ChannelsModel extends ChannelsModel {
    private static int[] checkedChannels = new int[16];
    private static int[] selectedChannelsTypes = new int[16];
    private static int[] selectedMeasuringRanges = new int[16];

    protected void addListOfChannelsTypes(List<ComboBox> channelsTypesComboBoxes) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("Дифференциальный вход без отсечки постоянной составляющей");
        strings.add("Дифференциальный вход с отсечкой постоянной составляющей");
        strings.add("Режим ICP-вход");

        setComboBox(channelsTypesComboBoxes, strings);
    }

    private void setComboBox(List<ComboBox> measuringRangeComboBoxes, ObservableList<String> strings) {
        for (ComboBox comboBox : measuringRangeComboBoxes) {
            comboBox.getItems().addAll(strings);
        }
    }

    protected void addListOfDifferentialMeasuringRanges(ComboBox measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("-2 В/+2 В");
        strings.add("-10 В/+10 В");

        measuringRange.getItems().setAll(strings);
    }

    protected void addListOfICPMeasuringRanges(ComboBox measuringRange) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("~1 В");
        strings.add("~5 В");

        measuringRange.getItems().setAll(strings);
    }


    /**
     * Для каналов измерения виброускорения выбраны:
     * 0 - Режим ICP-вход
     * 1 - ~5 В
     *
     * Для каналов измерения перемещения выбраны:
     * 0 - Дифференциальный вход без отсечки постоянной составляющей
     * 1 - -10 В/+10 В
     */
    protected void setParameters() {
        for (int i = 0; i < checkedChannels.length; i++) {
            selectedChannelsTypes[i] = 0;
            selectedMeasuringRanges[i] = 1;
        }
    }

    protected void checkChannelType(List<CheckBox> channels, List<ComboBox> channelsTypes, List<ComboBox> measuringRanges) {
        for (int i = 0; i < channels.size(); i++) {
            toggleICPChannels(channelsTypes.get(i), measuringRanges.get(i));
        }
    }

    private void toggleICPChannels(ComboBox channelType, ComboBox measuringRange) {
        channelType.valueProperty().addListener(observable -> {
            if (channelType.getSelectionModel().isSelected(2)) {
                addListOfICPMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            } else {
                addListOfDifferentialMeasuringRanges(measuringRange);
                measuringRange.getSelectionModel().select(1);
            }
        });
    }

    public static int[] getCheckedChannels() {
        return checkedChannels;
    }

    public static int[] getSelectedChannelsTypes() {
        return selectedChannelsTypes;
    }

    public static int[] getSelectedMeasuringRanges() {
        return selectedMeasuringRanges;
    }
}
