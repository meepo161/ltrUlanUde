package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "experiments")
public class Experiment {
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

    public Experiment() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Experiment(String experimentName,
                      String sampleName,
                      String sampleSerialNumber,
                      String documentNumber,
                      String experimentType,
                      String experimentTime,
                      String experimentDate,
                      String leadEngineer,
                      String comments) {
        this.experimentName = experimentName;
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

    public String getExperimentName() {
        return experimentName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getSampleSerialNumber() {
        return sampleSerialNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public String getExperimentTime() {
        return experimentTime;
    }

    public String getExperimentDate() {
        return experimentDate;
    }

    public String getLeadEngineer() {
        return leadEngineer;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experiment experiment = (Experiment) o;
        return id == experiment.id &&
                Objects.equals(experimentName, experiment.experimentName) &&
                Objects.equals(sampleName, experiment.sampleName) &&
                Objects.equals(sampleSerialNumber, experiment.sampleSerialNumber) &&
                Objects.equals(documentNumber, experiment.documentNumber) &&
                Objects.equals(experimentType, experiment.experimentType) &&
                Objects.equals(experimentTime, experiment.experimentTime) &&
                Objects.equals(experimentDate, experiment.experimentDate) &&
                Objects.equals(leadEngineer, experiment.leadEngineer) &&
                Objects.equals(comments, experiment.comments);

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
