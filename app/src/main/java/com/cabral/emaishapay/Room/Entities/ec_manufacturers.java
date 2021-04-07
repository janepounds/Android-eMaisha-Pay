package com.cabral.emaishapay.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "manufacturers")
public class ec_manufacturers {
    @PrimaryKey(autoGenerate = true)
    private int manufacturer_id;
    @ColumnInfo(name = "manufacturer_name")
    private String manufacturer_name;

    public ec_manufacturers(int manufacturer_id, String manufacturer_name) {
        this.manufacturer_id = manufacturer_id;
        this.manufacturer_name = manufacturer_name;
    }

    public int getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(int manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }
}
