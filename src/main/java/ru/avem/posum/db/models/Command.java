package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.poi.ss.usermodel.IndexedColors;
import ru.avem.posum.models.process.CommandsTypes;

/**
 * Таблица для хранения запланированных команд в базе данных
 */

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

    public String getCommand() {
        String command = "";

        if (type.equals(CommandsTypes.PAUSE.getTypeName())) {
            command = CommandsTypes.PAUSE.getTypeName();
        } else if (type.equals(CommandsTypes.STOP.getTypeName())) {
            command = CommandsTypes.STOP.getTypeName();
        }

        return command;
    }

    public Short getColorIndex() {
        return IndexedColors.WHITE.index;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
