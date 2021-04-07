package com.cabral.emaishapay.network.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcProductWeight {

    @PrimaryKey(autoGenerate = true)
    private int weight_id;
    @ColumnInfo(name = "weight_unit")
    private String weight_unit;

    public EcProductWeight(int weight_id, String weight_unit) {
        this.weight_id = weight_id;
        this.weight_unit = weight_unit;
    }

    public int getWeight_id() {
        return weight_id;
    }

    public void setWeight_id(int weight_id) {
        this.weight_id = weight_id;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }
}
