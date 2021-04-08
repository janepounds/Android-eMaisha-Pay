package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcSupplier  implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int suppliers_id;
    @ColumnInfo(name = "suppliers_name")
    private String suppliers_name;
    @ColumnInfo(name = "suppliers_contact_person")
    private String suppliers_contact_person;
    @ColumnInfo(name = "suppliers_cellvv")
    private String suppliers_cell;
    @ColumnInfo(name = "suppliers_email")
    private String suppliers_email;
    @ColumnInfo(name = "suppliers_address")
    private String suppliers_address;
    @ColumnInfo(name = "suppliers_address_two")
    private String suppliers_address_two;
    @ColumnInfo(name = "suppliers_image")
    private String suppliers_image;

    public EcSupplier(int suppliers_id, String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address, String suppliers_address_two, String suppliers_image) {
        this.suppliers_id = suppliers_id;
        this.suppliers_name = suppliers_name;
        this.suppliers_contact_person = suppliers_contact_person;
        this.suppliers_cell = suppliers_cell;
        this.suppliers_email = suppliers_email;
        this.suppliers_address = suppliers_address;
        this.suppliers_address_two = suppliers_address_two;
        this.suppliers_image = suppliers_image;
    }

    protected EcSupplier(Parcel in) {
        suppliers_id = in.readInt();
        suppliers_name = in.readString();
        suppliers_contact_person = in.readString();
        suppliers_cell = in.readString();
        suppliers_email = in.readString();
        suppliers_address = in.readString();
        suppliers_address_two = in.readString();
        suppliers_image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(suppliers_id);
        dest.writeString(suppliers_name);
        dest.writeString(suppliers_contact_person);
        dest.writeString(suppliers_cell);
        dest.writeString(suppliers_email);
        dest.writeString(suppliers_address);
        dest.writeString(suppliers_address_two);
        dest.writeString(suppliers_image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcSupplier> CREATOR = new Creator<EcSupplier>() {
        @Override
        public EcSupplier createFromParcel(Parcel in) {
            return new EcSupplier(in);
        }

        @Override
        public EcSupplier[] newArray(int size) {
            return new EcSupplier[size];
        }
    };

    public int getSuppliers_id() {
        return suppliers_id;
    }

    public void setSuppliers_id(int suppliers_id) {
        this.suppliers_id = suppliers_id;
    }

    public String getSuppliers_name() {
        return suppliers_name;
    }

    public void setSuppliers_name(String suppliers_name) {
        this.suppliers_name = suppliers_name;
    }

    public String getSuppliers_contact_person() {
        return suppliers_contact_person;
    }

    public void setSuppliers_contact_person(String suppliers_contact_person) {
        this.suppliers_contact_person = suppliers_contact_person;
    }

    public String getSuppliers_cell() {
        return suppliers_cell;
    }

    public void setSuppliers_cell(String suppliers_cell) {
        this.suppliers_cell = suppliers_cell;
    }

    public String getSuppliers_email() {
        return suppliers_email;
    }

    public void setSuppliers_email(String suppliers_email) {
        this.suppliers_email = suppliers_email;
    }

    public String getSuppliers_address() {
        return suppliers_address;
    }

    public void setSuppliers_address(String suppliers_address) {
        this.suppliers_address = suppliers_address;
    }

    public String getSuppliers_address_two() {
        return suppliers_address_two;
    }

    public void setSuppliers_address_two(String suppliers_address_two) {
        this.suppliers_address_two = suppliers_address_two;
    }

    public String getSuppliers_image() {
        return suppliers_image;
    }

    public void setSuppliers_image(String suppliers_image) {
        this.suppliers_image = suppliers_image;
    }
}

