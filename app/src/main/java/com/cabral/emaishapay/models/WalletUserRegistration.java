package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletUserRegistration {
    @SerializedName("status")
    @Expose
 private String status;
    @SerializedName("message")
    @Expose
 private String message;
    @SerializedName("data")
    @Expose
 private ResponseData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public class ResponseData {
     @SerializedName("firstname")
     @Expose
     private String firstname;
     @SerializedName("lastname")
     @Expose
     private String lastname;
     @SerializedName("password")
     @Expose
     private String password;
     @SerializedName("phoneNumber")
     @Expose
     private String phoneNumber;
     @SerializedName("email")
     @Expose
     private String email;
     @SerializedName("addressStreet")
     @Expose
     private String addressStreet;
    @SerializedName("addressCityOrTown")
    @Expose
    private String addressCityOrTown;

    @SerializedName("id")
    @Expose
    private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddressStreet() {
            return addressStreet;
        }

        public void setAddressStreet(String addressStreet) {
            this.addressStreet = addressStreet;
        }

        public String getAddressCityOrTown() {
            return addressCityOrTown;
        }

        public void setAddressCityOrTown(String addressCityOrTown) {
            this.addressCityOrTown = addressCityOrTown;
        }
    }

}
