package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class AccountCreation  implements Serializable {
            @SerializedName("dob")
            @Expose
            private String dob;
            @SerializedName("firstname")
            @Expose
            private String firstname;
            @SerializedName("lastname")
            @Expose
            private String lastname;
            @SerializedName("middlename")
            @Expose
            private String middlename;
            @SerializedName("gender")
            @Expose
            private String gender;
            @SerializedName("next_of_kin")
            @Expose
            private String next_of_kin;
            @SerializedName("next_of_kin_contact")
            @Expose
            private String next_of_kin_contact;
            @SerializedName("district")
            @Expose
            private String district;
            @SerializedName("sub_county")
            @Expose
            private String sub_county;
            @SerializedName("village")
            @Expose
            private String village;
            @SerializedName("landmark")
            @Expose
            private String landmark;
            @SerializedName("phone_number")
            @Expose
            private String phone_number;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("next_of_kin_relationship")
            @Expose
            private String next_of_kin_relationship;
            @SerializedName("idtype")
            @Expose
            private String idtype;
            @SerializedName("nin")
            @Expose
            private String nin;
            @SerializedName("national_id_valid_upto")
            @Expose
            private String national_id_valid_upto;
            @SerializedName("national_id_photo")
            @Expose
            private String national_id_photo;
            @SerializedName("customer_photo")
            @Expose
            private String customer_photo;
            @SerializedName("customer_photo_with_id")
            @Expose
            private String customer_photo_with_id;
            @SerializedName("card_number")
            @Expose
            private String card_number;
            @SerializedName("cvv")
            @Expose
            private String cvv;
            @SerializedName("expiry")
            @Expose
            private String expiry;
            @SerializedName("account_name")
            @Expose
            private String account_name;
            @SerializedName("pin")
            @Expose
            private String pin;

            public AccountCreation(){ }



    public AccountCreation(JSONObject accountObject) throws JSONException {
        setDob(accountObject.getString("dob") );
        setFirstname(accountObject.getString("firstname") );
        setLastname(accountObject.getString("lastname")  );
        setMiddlename(accountObject.getString("middlename")  );
        setGender(accountObject.getString("gender")  );
        setNext_of_kin(accountObject.getString("next_of_kin")  );
        setNext_of_kin_contact(accountObject.getString("next_of_kin_contact")  );
        setDistrict(accountObject.getString("district")  );
        setSub_county(accountObject.getString("sub_county")  );
        setVillage(accountObject.getString("village")  );
        setLandmark(accountObject.getString("landmark")  );
        setPhone_number(accountObject.getString("phone_number")  );
        setEmail(accountObject.getString("email")  );
        setNext_of_kin_relationship(accountObject.getString("next_of_kin_relationship")  );
        setNin(accountObject.getString("idtype")  );
        setNin(accountObject.getString("nin")  );
        setNational_id_valid_upto(accountObject.getString("national_id_valid_upto")  );
        setNational_id_photo(accountObject.getString("national_id_photo")  );
        setCustomer_photo(accountObject.getString("customer_photo")  );
        setCustomer_photo_with_id(accountObject.getString("customer_photo_with_id")  );
        setCard_number(accountObject.getString("card_number")  );
        setCvv(accountObject.getString("cvv") );
        setExpiry(accountObject.getString("expiry"));
        setAccount_name(accountObject.getString("account_name") );
        setPin(accountObject.getString("pin")  );


    }


    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNext_of_kin() {
        return next_of_kin;
    }

    public void setNext_of_kin(String next_of_kin) {
        this.next_of_kin = next_of_kin;
    }

    public String getNext_of_kin_contact() {
        return next_of_kin_contact;
    }

    public void setNext_of_kin_contact(String next_of_kin_contact) {
        this.next_of_kin_contact = next_of_kin_contact;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSub_county() {
        return sub_county;
    }

    public void setSub_county(String sub_county) {
        this.sub_county = sub_county;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNext_of_kin_relationship() {
        return next_of_kin_relationship;
    }

    public void setNext_of_kin_relationship(String next_of_kin_relationship) {
        this.next_of_kin_relationship = next_of_kin_relationship;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }
    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getNational_id_valid_upto() {
        return national_id_valid_upto;
    }

    public void setNational_id_valid_upto(String national_id_valid_upto) {
        this.national_id_valid_upto = national_id_valid_upto;
    }

    public String getNational_id_photo() {
        return national_id_photo;
    }

    public void setNational_id_photo(String national_id_photo) {
        this.national_id_photo = national_id_photo;
    }

    public String getCustomer_photo() {
        return customer_photo;
    }

    public void setCustomer_photo(String customer_photo) {
        this.customer_photo = customer_photo;
    }

    public String getCustomer_photo_with_id() {
        return customer_photo_with_id;
    }

    public void setCustomer_photo_with_id(String customer_photo_with_id) {
        this.customer_photo_with_id = customer_photo_with_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
