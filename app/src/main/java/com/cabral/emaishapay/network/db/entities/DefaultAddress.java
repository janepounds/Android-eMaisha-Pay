package com.cabral.emaishapay.network.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DefaultAddress {
    @PrimaryKey(autoGenerate = true)
    private int default_id;
    @ColumnInfo(name = "customers_id")
    private String customers_id;
    @ColumnInfo(name = "entry_firstname")
    private String entry_firstname;
    @ColumnInfo(name = "entry_lastname")
    private String entry_lastname;
    @ColumnInfo(name = "entry_street_address")
    private String entry_street_address;
    @ColumnInfo(name = "entry_postcode")
    private String entry_postcode;
    @ColumnInfo(name = "entry_city")
    private String entry_city;
    @ColumnInfo(name = "entry_country_id")
    private String entry_country_id;
    @ColumnInfo(name = "entry_latitude")
    private String entry_latitude;
    @ColumnInfo(name = "entry_longitude")
    private String entry_longitude;
    @ColumnInfo(name = "entry_contact")
    private String entry_contact;
    @ColumnInfo(name = "is_default")
    private String is_default;

    public DefaultAddress(int default_id, String customers_id, String entry_firstname, String entry_lastname, String entry_street_address, String entry_postcode, String entry_city, String entry_country_id, String entry_latitude, String entry_longitude, String entry_contact, String is_default) {
        this.default_id = default_id;
        this.customers_id = customers_id;
        this.entry_firstname = entry_firstname;
        this.entry_lastname = entry_lastname;
        this.entry_street_address = entry_street_address;
        this.entry_postcode = entry_postcode;
        this.entry_city = entry_city;
        this.entry_country_id = entry_country_id;
        this.entry_latitude = entry_latitude;
        this.entry_longitude = entry_longitude;
        this.entry_contact = entry_contact;
        this.is_default = is_default;
    }

    public int getDefault_id() {
        return default_id;
    }

    public void setDefault_id(int default_id) {
        this.default_id = default_id;
    }

    public String getCustomers_id() {
        return customers_id;
    }

    public void setCustomers_id(String customers_id) {
        this.customers_id = customers_id;
    }

    public String getEntry_firstname() {
        return entry_firstname;
    }

    public void setEntry_firstname(String entry_firstname) {
        this.entry_firstname = entry_firstname;
    }

    public String getEntry_lastname() {
        return entry_lastname;
    }

    public void setEntry_lastname(String entry_lastname) {
        this.entry_lastname = entry_lastname;
    }

    public String getEntry_street_address() {
        return entry_street_address;
    }

    public void setEntry_street_address(String entry_street_address) {
        this.entry_street_address = entry_street_address;
    }

    public String getEntry_postcode() {
        return entry_postcode;
    }

    public void setEntry_postcode(String entry_postcode) {
        this.entry_postcode = entry_postcode;
    }

    public String getEntry_city() {
        return entry_city;
    }

    public void setEntry_city(String entry_city) {
        this.entry_city = entry_city;
    }

    public String getEntry_country_id() {
        return entry_country_id;
    }

    public void setEntry_country_id(String entry_country_id) {
        this.entry_country_id = entry_country_id;
    }

    public String getEntry_latitude() {
        return entry_latitude;
    }

    public void setEntry_latitude(String entry_latitude) {
        this.entry_latitude = entry_latitude;
    }

    public String getEntry_longitude() {
        return entry_longitude;
    }

    public void setEntry_longitude(String entry_longitude) {
        this.entry_longitude = entry_longitude;
    }

    public String getEntry_contact() {
        return entry_contact;
    }

    public void setEntry_contact(String entry_contact) {
        this.entry_contact = entry_contact;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
