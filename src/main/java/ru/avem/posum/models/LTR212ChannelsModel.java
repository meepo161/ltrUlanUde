///**
// * Здесь 28 - количество каналов LTR212
// */
//
//package ru.avem.posum.models;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.ComboBox;
//
//import java.util.List;
//
//public class LTR212ChannelsModel extends ChannelsModel {
//    private static int[] checkedChannels = new int[28];
//    private static int[] selectedBridgeTypes = new int[28];
//    private static int[] selectedMeasuringRanges = new int[28];
//
//    protected void addListOfBridgeTypes(List<ComboBox> bridgeTypeComboBoxes) {
//        ObservableList<String> strings = FXCollections.observableArrayList();
//        strings.add("Сбалансированный мост (200 Ом)");
//        strings.add("Сбалансированный мост (350 Ом)");
//        strings.add("Сбалансированный мост (внешний резистор)");
//        strings.add("Разбалансированный мост (200 Ом)");
//        strings.add("Разбалансированный мост (350 Ом)");
//        strings.add("Разбалансированный мост (внешний резистор)");
//
//        for (ComboBox comboBox : bridgeTypeComboBoxes) {
//            comboBox.getItems().addAll(strings);
//        }
//    }
//
//    protected void addListOfMeasuringRanges(List<ComboBox> measuringRangeComboBoxes) {
//        ObservableList<String> strings = FXCollections.observableArrayList();
//        strings.add("-10 мВ/+10 мВ");
//        strings.add("-20 мВ/+20 мВ");
//        strings.add("-40 мВ/+40 мВ");
//        strings.add("-80 мВ/+80 мВ");
//        strings.add("0 мВ/+10 мВ");
//        strings.add("0 мВ/+20 мВ");
//        strings.add("0 мВ/+40 мВ");
//        strings.add("0 мВ/+80 мВ");
//
//        for (ComboBox comboBox : measuringRangeComboBoxes) {
//            comboBox.getItems().addAll(strings);
//        }
//    }
//
//    protected void setParameters() {
//        for (int i = 0; i < checkedChannels.length; i++) {
//            selectedBridgeTypes[i] = 1;
//            selectedMeasuringRanges[i] = 3;
//
//        }
//    }
//
//    public static int[] getCheckedChannels() {
//        return checkedChannels;
//    }
//
//    public static int[] getSelectedBridgeTypes() {
//        return selectedBridgeTypes;
//    }
//
//    public static int[] getSelectedMeasuringRanges() {
//        return selectedMeasuringRanges;
//    }
//}
