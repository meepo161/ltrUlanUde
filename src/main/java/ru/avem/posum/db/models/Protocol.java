package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@DatabaseTable(tableName = "protocols")
public class Protocol{
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String experimentName;
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

    public Protocol() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Protocol(Experiment experiment){
        getExperimentInfo(experiment);
    }

    private void getExperimentInfo(Experiment experiment) {
        experimentName = experiment.getExperimentName();
        sampleName = experiment.getSampleName();
        sampleSerialNumber = experiment.getSampleSerialNumber();
        documentNumber = experiment.getDocumentNumber();
        experimentType = experiment.getExperimentType();
        experimentTime = experiment.getExperimentTime();
        experimentDate = experiment.getExperimentDate();
        leadEngineer = experiment.getLeadEngineer();
        comments = experiment.getComments();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
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
        return "Protocol{" +
                "id=" + id +
                ", experimentName='" + experimentName + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", sampleSerialNumber='" + sampleSerialNumber + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", experimentType='" + experimentType + '\'' +
                ", experimentTime='" + experimentTime + '\'' +
                ", experimentDate='" + experimentDate + '\'' +
                ", leadEngineer='" + leadEngineer + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Protocol protocol = (Protocol) o;
        return id == protocol.id &&
                Objects.equals(experimentName, protocol.experimentName) &&
                Objects.equals(sampleName, protocol.sampleName) &&
                Objects.equals(sampleSerialNumber, protocol.sampleSerialNumber) &&
                Objects.equals(documentNumber, protocol.documentNumber) &&
                Objects.equals(experimentType, protocol.experimentType) &&
                Objects.equals(experimentTime, protocol.experimentTime) &&
                Objects.equals(experimentDate, protocol.experimentDate) &&
                Objects.equals(leadEngineer, protocol.leadEngineer) &&
                Objects.equals(comments, protocol.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                experimentName,
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
