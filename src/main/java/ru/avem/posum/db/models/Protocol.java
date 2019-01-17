package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@DatabaseTable(tableName = "protocols")
public class Protocol {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String sampleName;
    @DatabaseField
    private String serialNumber;

    public Protocol() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Protocol(TestingSample selectedTestingSample){
        getTestingSampleInfo(selectedTestingSample);
    }

    public void getTestingSampleInfo(TestingSample testingSample) {
        sampleName = testingSample.getSampleName();
        serialNumber = testingSample.getSerialNumber();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Protocol protocol = (Protocol) o;
        return id == protocol.id &&
                Objects.equals(sampleName, protocol.sampleName) &&
                Objects.equals(serialNumber, protocol.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sampleName, serialNumber);
    }
}
