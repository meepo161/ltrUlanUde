package ru.avem.posum.models.Process;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Command {
    private long id;
    private final StringProperty description;
    private StringProperty type;

    public Command(long id, String type, String description) {
        this.id = id;
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = description.get().split("Через ")[1].split("с начала запуска")[0];
        Date mills = new Date();
        try {
            mills = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return mills.getTime();
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
