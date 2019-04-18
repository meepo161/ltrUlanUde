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
    private long testProgramId;

    @DatabaseField
    private long moduleId;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<List<String>> calibrationSettings;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<List<Double>> calibrationCoefficients;

    public Calibration() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Calibration(long testProgramId, long moduleId, ArrayList<List<String>> calibrationSettings, ArrayList<List<Double>> calibrationCoefficients) {
        this.testProgramId = testProgramId;
        this.moduleId = moduleId;
        this.calibrationSettings = calibrationSettings;
        this.calibrationCoefficients = calibrationCoefficients;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public ArrayList<List<Double>> getCalibrationCoefficients() {
        return calibrationCoefficients;
    }

    public void setCalibrationCoefficients(ArrayList<List<Double>> calibrationCoefficients) {
        this.calibrationCoefficients = calibrationCoefficients;
    }

    public ArrayList<List<String>> getCalibrationSettings() {
        return calibrationSettings;
    }

    public void setCalibrationSettings(ArrayList<List<String>> calibrationSettings) {
        this.calibrationSettings = calibrationSettings;
    }
}
