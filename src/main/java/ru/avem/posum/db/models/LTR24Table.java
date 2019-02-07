package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;
import java.util.Optional;

@DatabaseTable(tableName = "ltr24")
public class LTR24Table {
    @DatabaseField(generatedId = true)
    private long id;
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
    private String slot = "";

    public LTR24Table() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public LTR24Table(boolean[] checkedChannels, int[] channelsTypes, int[] measuringRanges, String[] channelsDescription, String crate, int slot) {
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
        this.slot = String.valueOf(slot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LTR24Table ltr24Table = (LTR24Table) o;
        return  id == ltr24Table.id &&
                Objects.equals(checkedChannels, ltr24Table.checkedChannels) &&
                Objects.equals(channelsTypes, ltr24Table.channelsTypes) &&
                Objects.equals(measuringRanges, ltr24Table.measuringRanges) &&
                Objects.equals(channelsDescription, ltr24Table.channelsDescription) &&
                Objects.equals(crate, ltr24Table.crate) &&
                Objects.equals(slot, ltr24Table.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkedChannels, channelsTypes, measuringRanges, channelsDescription, crate, slot);
    }
}
