package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcProductCategory implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int category_id;
    @ColumnInfo(name = "category_name")
    private String category_name;

    public EcProductCategory(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    protected EcProductCategory(Parcel in) {
        category_id = in.readInt();
        category_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category_id);
        dest.writeString(category_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcProductCategory> CREATOR = new Creator<EcProductCategory>() {
        @Override
        public EcProductCategory createFromParcel(Parcel in) {
            return new EcProductCategory(in);
        }

        @Override
        public EcProductCategory[] newArray(int size) {
            return new EcProductCategory[size];
        }
    };

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
