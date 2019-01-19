package ru.avem.posum.models;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.List;

public interface IChannelsModel {
    void loadCheckedChannels(int[] checkedChannels, List<CheckBox> channels);

    void enableChannelsTypes(int[] checkedChannels, List<ComboBox> channelsTypes);

    void enableChannelsMeasuringRanges(int[] checkedChannels, List<ComboBox> measuringRanges);

    void loadSelectedComboBoxes(int[] selectedComboBoxes, List<ComboBox> comboBoxes);

    void toggleCheckedChannels(List<CheckBox> channels, List<ComboBox> channelsTypes, List<ComboBox> measuringRanges);

    void toggleComboBoxes(CheckBox checkBox, ComboBox channelType, ComboBox measuringRange);

    void writeCheckedChannels(int[] checkedChannels, List<CheckBox> channels, int beginIndex);

    void writeSelectedComboBoxes(int[] selectedComboBox, List<ComboBox> comboBoxes, int beginIndex);
}
