package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletAuthentication {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("access_token")
    @Expose
    private String access_token;
    @SerializedName("data")
    @Expose
    private UserData data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public class UserData {
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("addressStreet")
        @Expose
        private String addressStreet;
        @SerializedName("addressCityOrTown")
        @Expose
        private String addressCityOrTown;
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("firebaseToken")
        @Expose
        private String firebaseToken;
        @SerializedName("business_role")
        @Expose
        private String business_role;
        @SerializedName("isVerified")
        @Expose
        private String isVerified;
        @SerializedName("pictrure")
        @Expose
        private String pictrure;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("balance")
        @Expose
        private String balance;

        UserData(){ }

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getFirebaseToken() {
            return firebaseToken;
        }

        public void setFirebaseToken(String firebaseToken) {
            this.firebaseToken = firebaseToken;
        }

        public String getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(String isVerified) {
            this.isVerified = isVerified;
        }

        public String getPictrure() {
            return pictrure;
        }

        public void setPictrure(String pictrure) {
            this.pictrure = pictrure;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getAccountRole() {
            return  business_role;
        }

        public String setAccountRole(String business_role) {
           return  this.business_role=business_role;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getBusiness_role() {
            return business_role;
        }

        public void setBusiness_role(String business_role) {
            this.business_role = business_role;
        }
    }
}
