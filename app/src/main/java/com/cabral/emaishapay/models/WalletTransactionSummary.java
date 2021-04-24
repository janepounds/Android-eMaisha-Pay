package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletTransactionSummary {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ResponseData> data;

    public class ResponseData {
        @SerializedName("lastDebit")
        @Expose
        private LastDebit lastDebit;
        @SerializedName("lastCredit")
        @Expose
        private LastCredit lastCredit;

    }

    public class LastDebit{
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("ourReference")
        @Expose
        private String ourReference;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("thirdParty")
        @Expose
        private String thirdParty;
        @SerializedName("in_tran")
        @Expose
        private String in_tran;
        @SerializedName("trans_amount")
        @Expose
        private double trans_amount;
        @SerializedName("ft_discount")
        @Expose
        private String ft_discount;
        @SerializedName("trans_charge")
        @Expose
        private String trans_charge;
        @SerializedName("tran_type")
        @Expose
        private String tran_type;
        @SerializedName("trans_currency")
        @Expose
        private String trans_currency;
        @SerializedName("trans_code")
        @Expose
        private String trans_code;
        @SerializedName("trans_message")
        @Expose
        private String trans_message;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("baseSystemId")
        @Expose
        private String baseSystemId;
        @SerializedName("referenceNumber")
        @Expose
        private String referenceNumber;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("receiver")
        @Expose
        private String receiver;
        @SerializedName("sender")
        @Expose
        private String sender;
        @SerializedName("receiptNumber")
        @Expose
        private String receiptNumber;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOurReference() {
            return ourReference;
        }

        public void setOurReference(String ourReference) {
            this.ourReference = ourReference;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThirdParty() {
            return thirdParty;
        }

        public void setThirdParty(String thirdParty) {
            this.thirdParty = thirdParty;
        }

        public String getIn_tran() {
            return in_tran;
        }

        public void setIn_tran(String in_tran) {
            this.in_tran = in_tran;
        }

        public double getTrans_amount() {
            return trans_amount;
        }

        public void setTrans_amount(double trans_amount) {
            this.trans_amount = trans_amount;
        }

        public String getFt_discount() {
            return ft_discount;
        }

        public void setFt_discount(String ft_discount) {
            this.ft_discount = ft_discount;
        }

        public String getTrans_charge() {
            return trans_charge;
        }

        public void setTrans_charge(String trans_charge) {
            this.trans_charge = trans_charge;
        }

        public String getTran_type() {
            return tran_type;
        }

        public void setTran_type(String tran_type) {
            this.tran_type = tran_type;
        }

        public String getTrans_currency() {
            return trans_currency;
        }

        public void setTrans_currency(String trans_currency) {
            this.trans_currency = trans_currency;
        }

        public String getTrans_code() {
            return trans_code;
        }

        public void setTrans_code(String trans_code) {
            this.trans_code = trans_code;
        }

        public String getTrans_message() {
            return trans_message;
        }

        public void setTrans_message(String trans_message) {
            this.trans_message = trans_message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getBaseSystemId() {
            return baseSystemId;
        }

        public void setBaseSystemId(String baseSystemId) {
            this.baseSystemId = baseSystemId;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(String receiptNumber) {
            this.receiptNumber = receiptNumber;
        }
    }
    public class LastCredit

    {
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("ourReference")
        @Expose
        private String ourReference;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("thirdParty")
        @Expose
        private String thirdParty;
        @SerializedName("in_tran")
        @Expose
        private String in_tran;
        @SerializedName("trans_amount")
        @Expose
        private double trans_amount;
        @SerializedName("ft_discount")
        @Expose
        private String ft_discount;
        @SerializedName("trans_charge")
        @Expose
        private String trans_charge;
        @SerializedName("tran_type")
        @Expose
        private String tran_type;
        @SerializedName("trans_currency")
        @Expose
        private String trans_currency;
        @SerializedName("trans_code")
        @Expose
        private String trans_code;
        @SerializedName("trans_message")
        @Expose
        private String trans_message;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("baseSystemId")
        @Expose
        private String baseSystemId;
        @SerializedName("referenceNumber")
        @Expose
        private String referenceNumber;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("receiver")
        @Expose
        private String receiver;
        @SerializedName("sender")
        @Expose
        private String sender;
        @SerializedName("receiptNumber")
        @Expose
        private String receiptNumber;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOurReference() {
            return ourReference;
        }

        public void setOurReference(String ourReference) {
            this.ourReference = ourReference;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThirdParty() {
            return thirdParty;
        }

        public void setThirdParty(String thirdParty) {
            this.thirdParty = thirdParty;
        }

        public String getIn_tran() {
            return in_tran;
        }

        public void setIn_tran(String in_tran) {
            this.in_tran = in_tran;
        }

        public double getTrans_amount() {
            return trans_amount;
        }

        public void setTrans_amount(double trans_amount) {
            this.trans_amount = trans_amount;
        }

        public String getFt_discount() {
            return ft_discount;
        }

        public void setFt_discount(String ft_discount) {
            this.ft_discount = ft_discount;
        }

        public String getTrans_charge() {
            return trans_charge;
        }

        public void setTrans_charge(String trans_charge) {
            this.trans_charge = trans_charge;
        }

        public String getTran_type() {
            return tran_type;
        }

        public void setTran_type(String tran_type) {
            this.tran_type = tran_type;
        }

        public String getTrans_currency() {
            return trans_currency;
        }

        public void setTrans_currency(String trans_currency) {
            this.trans_currency = trans_currency;
        }

        public String getTrans_code() {
            return trans_code;
        }

        public void setTrans_code(String trans_code) {
            this.trans_code = trans_code;
        }

        public String getTrans_message() {
            return trans_message;
        }

        public void setTrans_message(String trans_message) {
            this.trans_message = trans_message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getBaseSystemId() {
            return baseSystemId;
        }

        public void setBaseSystemId(String baseSystemId) {
            this.baseSystemId = baseSystemId;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(String receiptNumber) {
            this.receiptNumber = receiptNumber;
        }
    }

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

    public List<ResponseData> getData() {
        return data;
    }

    public void setData(List<ResponseData> data) {
        this.data = data;
    }
}






