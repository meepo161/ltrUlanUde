package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;

@DatabaseTable(tableName = "events")
public class Event {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgramId;
    @DatabaseField
    private String date;
    @DatabaseField
    private String time;
    @DatabaseField
    private String description;
    @DatabaseField
    private String status;

    public Event() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Event(long testProgramId, String description, String status, long millis) {
        this.testProgramId = testProgramId;
        this.setTime(millis);
        this.description = description;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public String getTime() {
        return this.time + " " + this.date;
    }

    public void setTime(long millis) {
        this.date = new SimpleDateFormat("dd.MM.yy").format(millis);
        this.time = new SimpleDateFormat("HH:mm:ss").format(millis);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
