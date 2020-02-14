package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Таблица для добавленных и связанных каналов в базе данных
 */

@DatabaseTable(tableName = "channels")
public class Channels {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgramId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String pCoefficient;
    @DatabaseField
    private String iCoefficient;
    @DatabaseField
    private String dCoefficient;
    @DatabaseField
    private String chosenParameterIndex;
    @DatabaseField
    private String chosenParameterValue;
    @DatabaseField
    private String amplitudeValue;
    @DatabaseField
    private String responseColor;

    public Channels() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Channels(long testProgramId, String name, String pCoefficient, String iCoefficient,
                    String dCoefficient, String chosenParameterIndex, String chosenParameterValue,
                    String responseColor, String amplitudeValue) {

        this.testProgramId = testProgramId;
        this.name = name;
        this.pCoefficient = pCoefficient;
        this.iCoefficient = iCoefficient;
        this.dCoefficient = dCoefficient;
        this.chosenParameterIndex = chosenParameterIndex;
        this.chosenParameterValue = chosenParameterValue;
        this.amplitudeValue = amplitudeValue;
        this.responseColor = responseColor;
    }

    public long getId() {
        return id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPCoefficient() {
        return pCoefficient;
    }

    public void setPCoefficient(String pCoefficient) {
        this.pCoefficient = pCoefficient;
    }

    public String getICoefficient() {
        return iCoefficient;
    }

    public void setICoefficient(String iCoefficient) {
        this.iCoefficient = iCoefficient;
    }

    public String getDCoefficient() {
        return dCoefficient;
    }

    public void setDCoefficient(String dCoefficient) {
        this.dCoefficient = dCoefficient;
    }

    public String getChosenParameterIndex() {
        return chosenParameterIndex;
    }

    public void setChosenParameterIndex(String chosenParameterIndex) {
        this.chosenParameterIndex = chosenParameterIndex;
    }

    public String getChosenParameterValue() {
        return chosenParameterValue;
    }

    public void setChosenParameterValue(String chosenParameterValue) {
        this.chosenParameterValue = chosenParameterValue;
    }

    public String getResponseColor() {
        return responseColor;
    }

    public void setResponseColor(String responseColor) {
        this.responseColor = responseColor;
    }

    public String getAmplitudeValue() {
        return amplitudeValue;
    }

    public void setAmplitudeValue(String amplitudeValue) {
        this.amplitudeValue = amplitudeValue;
    }

}
