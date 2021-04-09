package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "regionDetails")
public class RegionDetails implements Parcelable {
    @PrimaryKey
    @NonNull
    private int id;

    private String regionType;

    private String region;

    private String belongs_to;

    public RegionDetails(int id, String regionType, String region, String belongs_to) {
        this.id = id;
        this.regionType = regionType;
        this.region = region;
        this.belongs_to = belongs_to;
    }

    protected RegionDetails(Parcel in) {
        id = in.readInt();
        regionType = in.readString();
        region = in.readString();
        belongs_to = in.readString();
    }

    public static final Creator<RegionDetails> CREATOR = new Creator<RegionDetails>() {
        @Override
        public RegionDetails createFromParcel(Parcel in) {
            return new RegionDetails(in);
        }

        @Override
        public RegionDetails[] newArray(int size) {
            return new RegionDetails[size];
        }
    };

    public RegionDetails() {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(regionType);
        dest.writeString(region);
        dest.writeString(belongs_to);
    }
}
