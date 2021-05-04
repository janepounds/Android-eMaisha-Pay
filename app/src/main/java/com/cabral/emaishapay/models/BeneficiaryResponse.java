package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeneficiaryResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<Beneficiaries> beneficiariesList;

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

    public List<Beneficiaries> getBeneficiariesList() {
        return beneficiariesList;
    }

    public void setBeneficiariesList(List<Beneficiaries> beneficiariesList) {
        this.beneficiariesList = beneficiariesList;
    }



    public class Beneficiaries {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("transaction_type")
        @Expose
        private String transaction_type;
        @SerializedName("bank")
        @Expose
        private String bank;
        @SerializedName("bank_branch")
        @Expose
        private String bank_branch;
        @SerializedName("account_name")
        @Expose
        private String account_name;
        @SerializedName("account_number")
        @Expose
        private String account_number;
        @SerializedName("beneficiary_phone")
        @Expose
        private String beneficiary_phone;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("street_address_1")
        @Expose
        private String street_address_1;

        @SerializedName("street_address_2")
        @Expose
        private String street_address_2;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTransaction_type() {
            return transaction_type;
        }

        public void setTransaction_type(String transaction_type) {
            this.transaction_type = transaction_type;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBank_branch() {
            return bank_branch;
        }

        public void setBank_branch(String bank_branch) {
            this.bank_branch = bank_branch;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getBeneficiary_phone() {
            return beneficiary_phone;
        }

        public void setBeneficiary_phone(String beneficiary_phone) {
            this.beneficiary_phone = beneficiary_phone;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getStreet_address_1() {
            return street_address_1;
        }

        public void setStreet_address_1(String street_address_1) {
            this.street_address_1 = street_address_1;
        }

        public String getStreet_address_2() {
            return street_address_2;
        }

        public void setStreet_address_2(String street_address_2) {
            this.street_address_2 = street_address_2;
        }
    }
}
