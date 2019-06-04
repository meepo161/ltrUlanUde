package ru.avem.posum.controllers.Process;

public enum RegulatorParameters {
    AMPLITUDE("Амплитуда"), DC("Статика"), FREQUENCY("Частота");

    private String typeName;

    RegulatorParameters(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
