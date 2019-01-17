package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Objects;

@DatabaseTable(tableName = "event")
public class Event {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long idTest;
    @DatabaseField
    private long millis = System.currentTimeMillis();
    @DatabaseField
    private String date;
    @DatabaseField
    private String time;
    @DatabaseField
    private String description;

    public Event() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Event(long idTest, String description, long millis) {
        this.idTest = idTest;
        this.millis = millis;
        this.date = new SimpleDateFormat("dd.MM.yy").format(millis);
        this.time = new SimpleDateFormat("HH:mm:ss").format(millis);
        this.description = description;
    }

    public Event(long idTest, String description) {
        this(idTest, description, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return this.date + " " + this.time;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
        this.date = new SimpleDateFormat("dd.MM.yy").format(millis);
        this.time = new SimpleDateFormat("HH:mm:ss").format(millis);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
