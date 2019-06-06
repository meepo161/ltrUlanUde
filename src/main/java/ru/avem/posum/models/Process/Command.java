package ru.avem.posum.models.Process;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Command {
    private final StringProperty description;
    private StringProperty type;

    public Command(String type, String description) {
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
