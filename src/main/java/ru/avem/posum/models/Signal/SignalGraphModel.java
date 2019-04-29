package ru.avem.posum.models.Signal;

public class SignalGraphModel {
    private double lowerBound;
    private double scale;
    private double tickUnit;
    private double upperBound;
    private String valueName;

    public void calculateGraphBounds() {
        if (valueName.equals("В")) {
            lowerBound = -scale * 10;
            tickUnit = scale * 2;
            upperBound = scale * 10;
        } else if (valueName.equals("с") || valueName.equals("Гц")) {
            lowerBound = 0;
            tickUnit = scale;
            upperBound = scale * 10;
        }

    }

    public void parseGraphScale(String scale) {
        String[] separatedScale = scale.split(" ");
        double digits = Double.parseDouble(separatedScale[0]);
        String suffix = separatedScale[1].substring(0, 1);
        separatedScale = scale.split("/дел");

        if (suffix.equals("м")) {
            valueName = separatedScale[0].substring(separatedScale[0].length() - 1);
            this.scale = digits * 0.001;
        } else {
            valueName = scale.split(" ")[1].split("/дел")[0];
            this.scale = digits;
        }
        System.out.println("Value name: " + valueName);
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public double getUpperBound() {
        return upperBound;
    }
}
