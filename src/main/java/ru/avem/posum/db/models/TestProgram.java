package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@DatabaseTable(tableName = "testPrograms")
public class TestProgram {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private int index;
    @DatabaseField
    private String crate;
    @DatabaseField
    private String testProgramName;
    @DatabaseField
    private String sampleName;
    @DatabaseField
    private String sampleSerialNumber;
    @DatabaseField
    private String documentNumber;
    @DatabaseField
    private String testProgramType;
    @DatabaseField
    private String testProgramTime;
    @DatabaseField
    private String testProgramDate;
    @DatabaseField
    private String leadEngineer;
    @DatabaseField
    private String comments;
    @DatabaseField
    private String created;
    @DatabaseField
    private String changed;

    public TestProgram() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public TestProgram(String crate,
                       String testProgramName,
                       String sampleName,
                       String sampleSerialNumber,
                       String documentNumber,
                       String testProgramType,
                       String testProgramTime,
                       String testProgramDate,
                       String leadEngineer,
                       String comments,
                       String created,
                       String changed) {
        index++;
        this.crate = crate;
        this.testProgramName = testProgramName;
        this.sampleName = sampleName;
        this.sampleSerialNumber = sampleSerialNumber;
        this.documentNumber = documentNumber;
        this.testProgramType = testProgramType;
        this.testProgramTime = testProgramTime;
        this.testProgramDate = testProgramDate;
        this.leadEngineer = leadEngineer;
        this.comments = comments;
        this.created = created;
        this.changed = changed;
    }

    public long getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public String getTestProgramName() {
        return testProgramName;
    }

    public void setTestProgramName(String testProgramName) {
        this.testProgramName = testProgramName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleSerialNumber() {
        return sampleSerialNumber;
    }

    public void setSampleSerialNumber(String sampleSerialNumber) {
        this.sampleSerialNumber = sampleSerialNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getTestProgramType() {
        return testProgramType;
    }

    public void setTestProgramType(String testProgramType) {
        this.testProgramType = testProgramType;
    }

    public String getTestProgramTime() {
        return testProgramTime;
    }

    public void setTestProgramTime(String testProgramTime) {
        this.testProgramTime = testProgramTime;
    }

    public String getTestProgramDate() {
        return testProgramDate;
    }

    public void setTestProgramDate(String testProgramDate) {
        this.testProgramDate = testProgramDate;
    }

    public String getLeadEngineer() {
        return leadEngineer;
    }

    public void setLeadEngineer(String leadEngineer) {
        this.leadEngineer = leadEngineer;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }
}
