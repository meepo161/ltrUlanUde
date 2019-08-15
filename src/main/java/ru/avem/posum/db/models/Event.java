package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.poi.ss.usermodel.IndexedColors;
import ru.avem.posum.models.process.EventsTypes;

import java.text.SimpleDateFormat;

/**
 * Таблица для хранения журнала событий в базе данных
 */

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

    public Short getColorIndex() {
        if (status.equals(EventsTypes.LOG.toString())) {
            return IndexedColors.WHITE.index;
        } else if (status.equals(EventsTypes.ERROR.toString())) {
            return IndexedColors.RED.index;
        } else if (status.equals(EventsTypes.WARNING.toString())) {
            return IndexedColors.YELLOW.index;
        } else {
            return IndexedColors.GREEN.index;
        }
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public String getTime() {
        return this.time + " " + this.date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(long millis) {
        this.date = new SimpleDateFormat("dd.MM.yy").format(millis);
        this.time = new SimpleDateFormat("HH:mm:ss").format(millis);
    }
}
