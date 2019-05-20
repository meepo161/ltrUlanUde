package ru.avem.posum.hardware;

public class Test {

    public static void main(String[] args) {
        boolean result;
        Process process = new Process();
        process.connect();
        result = process.isConnected();
        process.initialize();
        result = process.isInitialized();
        process.launch();
        result = process.isLaunched();

        while (result) {
            process.perform();
        }
    }
}
