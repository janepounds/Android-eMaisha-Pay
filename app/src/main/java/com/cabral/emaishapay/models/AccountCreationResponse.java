package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountCreationResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AccountData data;

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

    public AccountData getData() {
        return data;
    }

    public void setData(AccountData data) {
        this.data = data;
    }

    public class AccountData{
        @SerializedName("is_emaisha_card_and_registered_by")
        @Expose
        private String is_emaisha_card_and_registered_by;
        @SerializedName("identifier")
        @Expose
        private String identifier;
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
        @SerializedName("updated_at")
        @Expose
        private String updated_at;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("id")
        @Expose
        private String id;


        public String getIs_emaisha_card_and_registered_by() {
            return is_emaisha_card_and_registered_by;
        }

        public void setIs_emaisha_card_and_registered_by(String is_emaisha_card_and_registered_by) {
            this.is_emaisha_card_and_registered_by = is_emaisha_card_and_registered_by;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
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

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}





