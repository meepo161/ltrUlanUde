package ru.avem.posum.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class Utils {
    private static final int DECIMAL_SCALE_LIMIT = 7; // максимальное количество знаков после запятой

    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static String convertFromExponentialFormat(double value, int decimalFormatScale) {
        int scale = (int) Math.log10(decimalFormatScale);
        String convertedValue = String.format("%.7f", value);
        return convertedValue.substring(0, convertedValue.length() - (DECIMAL_SCALE_LIMIT - scale));
    }

    public static String parseModuleType(String moduleName) {
        return moduleName.split(" ")[0];
    }

    public static int parseSlotNumber(String moduleName) {
        return Integer.parseInt(moduleName.split("Слот ")[1].split("\\)")[0]); // номер слота
    }

    public static double roundValue(double value, int rounder) {
        return (double) Math.round(value * rounder) / rounder;
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    public static void makeHeaderWrappable(TableColumn col) {
        Label label = new Label(col.getText());
        label.setStyle("-fx-padding: 8px;");
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        StackPane stack = new StackPane();
        stack.getChildren().add(label);
        stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
        label.prefWidthProperty().bind(stack.prefWidthProperty());
        col.setGraphic(stack);
    }

    public static int getDecimalScaleLimit() {
        return DECIMAL_SCALE_LIMIT;
    }

    public static int getRounder(int rarefactionCoefficient) { return (int) Math.pow(10, rarefactionCoefficient); }
}
