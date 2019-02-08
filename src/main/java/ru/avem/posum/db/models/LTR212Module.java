package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;
import java.util.Optional;

@DatabaseTable(tableName = "ltr212Modules")
public class LTR212Module {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private long testProgrammId;
    @DatabaseField
    private String checkedChannels = "";
    @DatabaseField
    private String channelsTypes = "";
    @DatabaseField
    private String measuringRanges = "";
    @DatabaseField
    private String channelsDescription = "";
    @DatabaseField
    private String crate = "";
    @DatabaseField
    private int slot;

    public LTR212Module() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public LTR212Module(long testProgrammId, boolean[] checkedChannels, int[] channelsTypes, int[] measuringRanges, String[] channelsDescription, String crate, int slot) {
        this.testProgrammId = testProgrammId;

        for (boolean checked : checkedChannels) {
            if (checked) {
                this.checkedChannels += "1, ";
            } else {
                this.checkedChannels += "0, ";
            }
        }

        for (int type : channelsTypes) {
            this.channelsTypes += type + ", ";
        }

        for (int range : measuringRanges) {
            this.measuringRanges += range + ", ";
        }

        for (String description : channelsDescription) {
            Optional<String> string = Optional.ofNullable(description);

            if (string.isPresent()) {
                this.channelsDescription += description + ", ";
            } else {
                this.channelsDescription += ", ";
            }
        }

        this.crate = crate;
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LTR212Module ltr212Module = (LTR212Module) o;
        return  id == ltr212Module.id &&
                Objects.equals(checkedChannels, ltr212Module.checkedChannels) &&
                Objects.equals(channelsTypes, ltr212Module.channelsTypes) &&
                Objects.equals(measuringRanges, ltr212Module.measuringRanges) &&
                Objects.equals(channelsDescription, ltr212Module.channelsDescription) &&
                Objects.equals(crate, ltr212Module.crate) &&
                Objects.equals(slot, ltr212Module.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkedChannels, channelsTypes, measuringRanges, channelsDescription, crate, slot);
    }

    public long getTestProgrammId() {
        return testProgrammId;
    }

    public void setTestProgrammId(int testProgrammId) {
        this.testProgrammId = testProgrammId;
    }
}
