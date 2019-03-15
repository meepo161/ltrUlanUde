package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;

@DatabaseTable(tableName = "Modules")
public class Module {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long testProgramId;

    @DatabaseField
    private String moduleType;

    @DatabaseField
    private String slot;

    @DatabaseField
    private String checkedChannels;

    @DatabaseField
    private String channelsTypes;

    @DatabaseField
    private String measuringRanges;

    @DatabaseField
    private String channelsDescription;

    public Module() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Module(HashMap<String, StringBuffer> moduleSettings) {
        testProgramId = Long.parseLong(moduleSettings.get("Test Program Id").toString());
        moduleType = moduleSettings.get("Module Type").toString();
        slot = moduleSettings.get("Slot").toString();
        checkedChannels = moduleSettings.get("Checked Channels").toString();
        channelsTypes = moduleSettings.get("Channels Types").toString();
        measuringRanges = moduleSettings.get("Measuring Ranges").toString();
        channelsDescription = moduleSettings.get("Channels Description").toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestProgramId() {
        return testProgramId;
    }

    public void setTestProgramId(long testProgramId) {
        this.testProgramId = testProgramId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getCheckedChannels() {
        return checkedChannels;
    }

    public void setCheckedChannels(String checkedChannels) {
        this.checkedChannels = checkedChannels;
    }

    public String getChannelsTypes() {
        return channelsTypes;
    }

    public void setChannelsTypes(String channelsTypes) {
        this.channelsTypes = channelsTypes;
    }

    public String getMeasuringRanges() {
        return measuringRanges;
    }

    public void setMeasuringRanges(String measuringRanges) {
        this.measuringRanges = measuringRanges;
    }

    public String getChannelsDescription() {
        return channelsDescription;
    }

    public void setChannelsDescription(String channelsDescription) {
        this.channelsDescription = channelsDescription;
    }
}
