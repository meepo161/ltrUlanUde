package ru.avem.posum.hardware;

public class Test {
    public static void main(String[] args) {
        LTR27 ltr27 = new LTR27();
        ltr27.crateSerialNumber = "2T364030";
        ltr27.setSlot(15);
        ltr27.settingsOfModule.put(ADC.Settings.FREQUENCY, 2);

        ltr27.openConnection();
        System.out.println(ltr27.status);

        ltr27.initializeModule();
        System.out.println(ltr27.status);

        ltr27.defineFrequency();
        System.out.println("Frequency = " + ltr27.getFrequency());

        ltr27.start();
        System.out.println(ltr27.status);

        for (int i = 0; i < 100; i++) {
            double[] data = new double[(int) ltr27.getFrequency()];
            double[] timeMarks = new double[data.length];
            ltr27.write(data, timeMarks);
        }

        ltr27.stop();
        System.out.println(ltr27.status);

        ltr27.closeConnection();
        System.out.println(ltr27.status);
    }
}
