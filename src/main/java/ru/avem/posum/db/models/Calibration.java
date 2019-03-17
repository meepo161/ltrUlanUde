package ru.avem.posum.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "calibration")
public class Calibration {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long moduleId;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<List<String>> calibrationSettings;

    public Calibration() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Calibration(long moduleId, ArrayList<List<String>> calibrationSettings) {
        this.moduleId = moduleId;
        this.calibrationSettings = calibrationSettings;
    }

    public long getModuleId() {
        return moduleId;
    }

    public ArrayList<List<String>> getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }
}
