package ru.avem.posum.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Events {

    private final StringProperty time;
    private final StringProperty description;
    private final StringProperty status;
    private List<StringProperty> properties = new ArrayList<>();

    public Events(String time, String description, String status) {
        this.time = new SimpleStringProperty(time);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
        properties.addAll(Arrays.asList(this.time, this.description, this.status));
    }

    public Events(String time, String description, EventsTypes status) {
        this(time, description, status.toString());
    }

    public Events(String time, String description) {
        this(time, description, EventsTypes.LOG);
    }

    public Events() {
        this(null, null, EventsTypes.LOG);
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

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void clearProperties() {
        properties.forEach(stringProperty -> stringProperty.set(""));
    }

}
