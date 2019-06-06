package ru.avem.posum.models.Process;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Event {
    private long id;
    private final StringProperty description;
    private final StringProperty status;
    private final StringProperty time;

    public Event(long id, String description, String status, String time) {
        this.id = id;
        this.time = new SimpleStringProperty(time);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
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

    public long getId() {
        return id;
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

    public void setStatus(String status) {
        this.status.set(status);
    }
}
