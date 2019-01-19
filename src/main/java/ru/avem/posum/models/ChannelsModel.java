package ru.avem.posum.models;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.List;

public class ChannelsModel implements ru.avem.posum.models.IChannelsModel {
    @Override
    public void loadCheckedChannels(int[] checkedChannels, List<CheckBox> channels) {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i] == 1) {
                channels.get(i).setSelected(true);
            }
        }
    }

    @Override
    public void enableChannelsTypes(int[] checkedChannels, List<ComboBox> channelsTypes) {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i] == 1) {
                channelsTypes.get(i).setDisable(false);
            }
        }
    }

    @Override
    public void enableChannelsMeasuringRanges(int[] checkedChannels, List<ComboBox> measuringRanges) {
        for (int i = 0; i < checkedChannels.length; i++) {
            if (checkedChannels[i] == 1) {
                measuringRanges.get(i).setDisable(false);
            }
        }
    }

    @Override
    public void loadSelectedComboBoxes(int[] selectedComboBoxes, List<ComboBox> comboBoxes) {
        for (int i = 0; i < comboBoxes.size(); i++) {
            comboBoxes.get(i).getSelectionModel().select(selectedComboBoxes[i]);
        }
    }

    @Override
    public void toggleCheckedChannels(List<CheckBox> channels, List<ComboBox> channelsTypes, List<ComboBox> measuringRanges) {
        int i = 0;

        for (CheckBox channel : channels) {
            toggleComboBoxes(channel, channelsTypes.get(i), measuringRanges.get(i));
            i++;
        }
    }

    @Override
    public void toggleComboBoxes(CheckBox checkBox, ComboBox channelType, ComboBox measuringRange) {
        checkBox.selectedProperty().addListener(observable -> {
            if (checkBox.isSelected()) {
                channelType.setDisable(false);
                measuringRange.setDisable(false);
            } else {
                channelType.setDisable(true);
                measuringRange.setDisable(true);
            }
        });
    }

    /**
     * 1 - CheckBox отмечен, 0 - CheckBox не отмечен
     */
    @Override
    public void writeCheckedChannels(int[] checkedChannels, List<CheckBox> channels, int beginIndex) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).isSelected()) {
                checkedChannels[i + beginIndex] = 1;
            } else {
                checkedChannels[i + beginIndex] = 0;
            }
        }
    }

    @Override
    public void writeSelectedComboBoxes(int[] selectedComboBox, List<ComboBox> comboBoxes, int beginIndex) {
        for (int i = 0; i < comboBoxes.size(); i++) {
            selectedComboBox[i + beginIndex] = comboBoxes.get(i).getSelectionModel().getSelectedIndex();
        }
    }
}