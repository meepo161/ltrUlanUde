package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "commands")
public class Command {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgramId;
    @DatabaseField
    private String type;
    @DatabaseField
    private String description;

    public Command() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Command(long testProgramId, String description, String type) {
        this.testProgramId = testProgramId;
        this.description = description;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
