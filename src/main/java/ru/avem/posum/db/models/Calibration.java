package ru.avem.posum.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ru.avem.posum.models.CalibrationModel;

import java.util.ArrayList;

@DatabaseTable(tableName = "calibration")
public class Calibration {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long moduleId;

    @DatabaseField
    private int channel;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList loadValue;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList channelValue;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList valueName;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList coefficientA;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList coefficientB;

    public Calibration() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Calibration(long moduleId, CalibrationModel calibrationModel) {
        this.moduleId = moduleId;
        channel = calibrationModel.getChannel();
        loadValue = (ArrayList<Double>) calibrationModel.getLoadValue();
        channelValue = (ArrayList<Double>) calibrationModel.getChannelValue();
        valueName = (ArrayList<String>) calibrationModel.getValueName();
        coefficientA = (ArrayList<Double>) calibrationModel.getCoefficientA();
        coefficientB = (ArrayList<Double>) calibrationModel.getCoefficientB();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModuleId() {
        return moduleId;
    }

    public ArrayList getLoadValue() {
        return loadValue;
    }
}
