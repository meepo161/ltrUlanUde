package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;
import java.util.Objects;

@DatabaseTable(tableName = "crate")
public class Crate {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String serialNumber;
    @DatabaseField
    private String LTR24Modules = "";
    @DatabaseField
    private String LTR34Modules = "";
    @DatabaseField
    private String LTR212Modules = "";

    public Crate() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Crate(String serialNumber, List<String> LTR24Modules, List<String> LTR34Modules, List<String> LTR212Modules) {
        this.serialNumber = serialNumber;

        for (String module : LTR24Modules) {
            this.LTR24Modules += module + ", ";
        }

        for (String module : LTR34Modules) {
            this.LTR34Modules += module + ", ";
        }

        for (String module : LTR212Modules) {
            this.LTR212Modules += module + ", ";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crate crate = (Crate) o;
        return  id == crate.id &&
                Objects.equals(serialNumber, crate.serialNumber) &&
                Objects.equals(LTR24Modules, crate.LTR24Modules) &&
                Objects.equals(LTR34Modules, crate.LTR34Modules) &&
                Objects.equals(LTR212Modules, crate.LTR212Modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serialNumber, LTR24Modules, LTR34Modules, LTR212Modules);
    }
}
