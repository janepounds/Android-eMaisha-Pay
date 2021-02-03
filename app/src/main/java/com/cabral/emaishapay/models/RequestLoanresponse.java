package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestLoanresponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private DataDetails data;

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

    public DataDetails getData() {
        return data;
    }

    public void setData(DataDetails data) {
        this.data = data;
    }


    public class DataDetails {
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("loanApplicationId")
        @Expose
        private int loanApplicationId;
        @SerializedName("loan_no")
        @Expose
        private String loan_no;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLoanApplicationId() {
            return loanApplicationId + "";
        }

        public void setLoanApplicationId(int loanApplicationId) {
            this.loanApplicationId = loanApplicationId;
        }

        public String getLoan_no() {
            return loan_no;
        }

        public void setLoan_no(String loan_no) {
            this.loan_no = loan_no;
        }
    }

}
