package ru.avem.posum.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "accounts")
public class Account {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String userName;

    @DatabaseField
    private String userPassword;

    public Account() {
        // ORMLite and XML binder need a no-arg constructor
    }

    public Account(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return  id == account.id &&
                Objects.equals(userName, account.userName) &&
                Objects.equals(userPassword, account.userPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, userPassword);
    }
}
