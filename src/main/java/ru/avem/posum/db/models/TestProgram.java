package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@DatabaseTable(tableName = "testPrograms")
public class TestProgram {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private int testProgramId;
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
                       String comments) {
        testProgramId++;
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
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(int testProgramId) {
        this.testProgramId = testProgramId;
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

    @Override
    public String toString() {
        return "TestProgram{" +
                "testProgramId=" + id +
                ", testProgramName='" + testProgramName + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", sampleSerialNumber='" + sampleSerialNumber + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", testProgramType='" + testProgramType + '\'' +
                ", testProgramTime='" + testProgramTime + '\'' +
                ", testProgramDate='" + testProgramDate + '\'' +
                ", leadEngineer='" + leadEngineer + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestProgram testProgram = (TestProgram) o;
        return id == testProgram.id &&
                Objects.equals(testProgramName, testProgram.testProgramName) &&
                Objects.equals(sampleName, testProgram.sampleName) &&
                Objects.equals(sampleSerialNumber, testProgram.sampleSerialNumber) &&
                Objects.equals(documentNumber, testProgram.documentNumber) &&
                Objects.equals(testProgramType, testProgram.testProgramType) &&
                Objects.equals(testProgramTime, testProgram.testProgramTime) &&
                Objects.equals(testProgramDate, testProgram.testProgramDate) &&
                Objects.equals(leadEngineer, testProgram.leadEngineer) &&
                Objects.equals(comments, testProgram.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,

                testProgramName,
                sampleName,
                sampleSerialNumber,
                documentNumber,
                testProgramType,
                testProgramTime,
                testProgramDate,
                leadEngineer,
                comments);
    }
}
