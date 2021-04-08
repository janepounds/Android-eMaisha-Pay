package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcProductWeight   implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int weight_id;
    @ColumnInfo(name = "weight_unit")
    private String weight_unit;

    public EcProductWeight(int weight_id, String weight_unit) {
        this.weight_id = weight_id;
        this.weight_unit = weight_unit;
    }

    protected EcProductWeight(Parcel in) {
        weight_id = in.readInt();
        weight_unit = in.readString();
    }

    public static final Creator<EcProductWeight> CREATOR = new Creator<EcProductWeight>() {
        @Override
        public EcProductWeight createFromParcel(Parcel in) {
            return new EcProductWeight(in);
        }

        @Override
        public EcProductWeight[] newArray(int size) {
            return new EcProductWeight[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(weight_id);
        dest.writeString(weight_unit);
    }
}
