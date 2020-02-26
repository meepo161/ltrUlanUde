package ru.avem.posum.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Utils {
    private static final int DECIMAL_SCALE_LIMIT = 7; // максимальное количество знаков после запятой

    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    // Переводит число из экспоненциальной формы представления в форму с нужным количеством знаков после запятой
    public static String convertFromExponentialFormat(double value, int decimalFormatScale) {
        int scale = (int) Math.log10(decimalFormatScale);
        String convertedValue = String.format("%.7f", value);
        return convertedValue.substring(0, convertedValue.length() - (DECIMAL_SCALE_LIMIT - scale));
    }

    // Возвращает название модуля
    public static String parseModuleType(String moduleName) {
        return moduleName.split(" ")[0];
    }

    // Возвращает номер слота
    public static int parseSlotNumber(String moduleName) {
        return Integer.parseInt(moduleName.split("Слот ")[1].split("\\)")[0]); // номер слота
    }

    // Округляет число до нужного знака после запятой
    public static double roundValue(double value, int rounder) {
        return (double) Math.round(value * rounder) / rounder;
    }

    // Приостанавливает выполнение потока
    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    // Делает заголовок колонки в таблице переносимым на несколько строк
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

    public void saveNodeAsPngTo(Node node, File file) {
        WritableImage image = node.snapshot(new SnapshotParameters(), null);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
