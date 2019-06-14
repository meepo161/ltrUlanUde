package ru.avem.posum.controllers.signal;

public enum GraphTypes {
    SIGNAL("Сигнал"), SPECTRUM("Спектр");

    private String typeName;

    GraphTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
