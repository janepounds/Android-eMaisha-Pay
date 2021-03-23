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
    }
}
