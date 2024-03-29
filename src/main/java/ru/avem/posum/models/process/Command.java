package ru.avem.posum.models.process;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Модель запланированной команды
 */

public class Command {
    private long id; // id команды
    private final StringProperty description; // описание команды
    private StringProperty type; // вид команды

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

    // Возвращает длительность паузы
    public long getDelay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date mills = new Date();
        String pauseTime = description.get().split("На ")[1].split(" ")[0];
        try {
            mills = simpleDateFormat.parse(pauseTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mills.getTime();
    }

    // Возвращае время выполнения команды
    public long getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        boolean isPause = type.get().equals(CommandsTypes.PAUSE.getTypeName());
        boolean isStop = type.get().equals(CommandsTypes.STOP.getTypeName());
        String time = "";
        Date mills = new Date();

        if (isPause) {
            time = description.get().split("через ")[1].split("с начала запуска")[0];
        } else if (isStop) {
            time = description.get().split("Через ")[1].split("с начала запуска")[0];
        }

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
