package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@DatabaseTable(tableName = "testProgramms")
public class TestProgramm {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private int testProgrammId;
    @DatabaseField
    private String crate;
    @DatabaseField
    private String testProgrammName;
    @DatabaseField
    private String sampleName;
    @DatabaseField
    private String sampleSerialNumber;
    @DatabaseField
    private String documentNumber;
    @DatabaseField
    private String experimentType;
    @DatabaseField
    private String experimentTime;
    @DatabaseField
    private String experimentDate;
    @DatabaseField
    private String leadEngineer;
    @DatabaseField
    private String comments;

    public TestProgramm() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public TestProgramm(String crate,
                        String testProgrammName,
                        String sampleName,
                        String sampleSerialNumber,
                        String documentNumber,
                        String experimentType,
                        String experimentTime,
                        String experimentDate,
                        String leadEngineer,
                        String comments) {
        testProgrammId++;
        this.crate = crate;
        this.testProgrammName = testProgrammName;
        this.sampleName = sampleName;
        this.sampleSerialNumber = sampleSerialNumber;
        this.documentNumber = documentNumber;
        this.experimentType = experimentType;
        this.experimentTime = experimentTime;
        this.experimentDate = experimentDate;
        this.leadEngineer = leadEngineer;
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTestProgrammId() {
        return testProgrammId;
    }

    public void setTestProgrammId(int testProgrammId) {
        this.testProgrammId = testProgrammId;
    }

    public String getCrate() {
        return crate;
    }

    public void setCrate(String crate) {
        this.crate = crate;
    }

    public String getTestProgrammName() {
        return testProgrammName;
    }

    public void setTestProgrammName(String testProgrammName) {
        this.testProgrammName = testProgrammName;
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

    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getExperimentTime() {
        return experimentTime;
    }

    public void setExperimentTime(String experimentTime) {
        this.experimentTime = experimentTime;
    }

    public String getExperimentDate() {
        return experimentDate;
    }

    public void setExperimentDate(String experimentDate) {
        this.experimentDate = experimentDate;
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
        return "TestProgramm{" +
                "testProgrammId=" + id +
                ", testProgrammName='" + testProgrammName + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", sampleSerialNumber='" + sampleSerialNumber + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", testProgrammType='" + experimentType + '\'' +
                ", testProgrammTime='" + experimentTime + '\'' +
                ", testProgrammDate='" + experimentDate + '\'' +
                ", leadEngineer='" + leadEngineer + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestProgramm testProgramm = (TestProgramm) o;
        return id == testProgramm.id &&
                Objects.equals(testProgrammName, testProgramm.testProgrammName) &&
                Objects.equals(sampleName, testProgramm.sampleName) &&
                Objects.equals(sampleSerialNumber, testProgramm.sampleSerialNumber) &&
                Objects.equals(documentNumber, testProgramm.documentNumber) &&
                Objects.equals(experimentType, testProgramm.experimentType) &&
                Objects.equals(experimentTime, testProgramm.experimentTime) &&
                Objects.equals(experimentDate, testProgramm.experimentDate) &&
                Objects.equals(leadEngineer, testProgramm.leadEngineer) &&
                Objects.equals(comments, testProgramm.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,

                testProgrammName,
                sampleName,
                sampleSerialNumber,
                documentNumber,
                experimentType,
                experimentTime,
                experimentDate,
                leadEngineer,
                comments);
    }
}
