package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "testingSamples")
public class TestingSample {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String sampleName;

    @DatabaseField
    private String serialNumber;

    public TestingSample() {
        // ORMLite needs a no-arg constructor
    }

    public TestingSample(String sampleName, String serialNumber) {
        this.sampleName = sampleName;
        this.serialNumber = serialNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestingSample testingSample = (TestingSample) o;
        return id == testingSample.id &&
                Objects.equals(sampleName, testingSample.sampleName) &&
                Objects.equals(serialNumber, testingSample.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sampleName, serialNumber);
    }
}


