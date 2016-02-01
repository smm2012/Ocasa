package com.android.ocasa.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by ignacio on 26/01/16.
 */
@DatabaseTable(tableName = "records")
public class Record {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Table table;

    @ForeignCollectionField(eager = true)
    private Collection<Field> fields;

    public Record() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public void setFields(Collection<Field> fields) {
        this.fields = fields;
    }
}
