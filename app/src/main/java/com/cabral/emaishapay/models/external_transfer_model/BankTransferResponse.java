package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankTransferResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData data;

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

    public com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData getData() {
        return data;
    }

    public void setData(com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData data) {
        this.data = data;
    }

    public class InfoData{
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("account_number")
        @Expose
        private String account_number;

        @SerializedName("bank_code")
        @Expose
        private String bank_code;

        @SerializedName("full_name")
        @Expose
        private String full_name;

        @SerializedName("created_at")
        @Expose
        private String created_at;

        @SerializedName("currency")
        @Expose
        private String currency;

        @SerializedName("amount")
        @Expose
        private String amount;

        @SerializedName("fee")
        @Expose
        private String fee;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("reference")
        @Expose
        private String reference;

        @SerializedName("narration")
        @Expose
        private String narration;

        @SerializedName("requires_approval")
        @Expose
        private int requires_approval;

        @SerializedName("is_approved")
        @Expose
        private int is_approved;

        @SerializedName("bank_name")
        @Expose
        private String bank_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getBank_code() {
            return bank_code;
        }

        public void setBank_code(String bank_code) {
            this.bank_code = bank_code;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        public int getRequires_approval() {
            return requires_approval;
        }

        public void setRequires_approval(int requires_approval) {
            this.requires_approval = requires_approval;
        }

        public int getIs_approved() {
            return is_approved;
        }

        public void setIs_approved(int is_approved) {
            this.is_approved = is_approved;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }
    }


}
