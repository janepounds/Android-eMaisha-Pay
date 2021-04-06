package com.cabral.emaishapay.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "regionDetails")
public class regionDetails {
    @PrimaryKey(autoGenerate = true)
    private int tableId;
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "regionType")
    private String regionType;
    @ColumnInfo(name = "region")
    private String region;
    @ColumnInfo(name = "belongs_to")
    private String belongs_to;

    public regionDetails(int tableId, int id, String regionType, String region, String belongs_to) {
        this.tableId = tableId;
        this.id = id;
        this.regionType = regionType;
        this.region = region;
        this.belongs_to = belongs_to;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBelongs_to() {
        return belongs_to;
    }

    public void setBelongs_to(String belongs_to) {
        this.belongs_to = belongs_to;
    }
}
