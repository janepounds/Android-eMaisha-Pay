package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcManufacturer implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int manufacturer_id;
    @ColumnInfo(name = "manufacturers_id")
    private String manufacturers_id;
    @ColumnInfo(name = "manufacturer_name")
    private String manufacturer_name;

    public EcManufacturer( String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    protected EcManufacturer(Parcel in) {
        manufacturers_id = in.readString();
        manufacturer_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(manufacturers_id);
        dest.writeString(manufacturer_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcManufacturer> CREATOR = new Creator<EcManufacturer>() {
        @Override
        public EcManufacturer createFromParcel(Parcel in) {
            return new EcManufacturer(in);
        }

        @Override
        public EcManufacturer[] newArray(int size) {
            return new EcManufacturer[size];
        }
    };

    public int getManufacturer_id() {
        return manufacturer_id;
    }

    public String getManufacturers_id() {
        return manufacturers_id;
    }

    public void setManufacturers_id(String manufacturers_id) {
        this.manufacturers_id = manufacturers_id;
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
