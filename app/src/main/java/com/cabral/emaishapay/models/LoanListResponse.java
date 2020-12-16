package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanListResponse {
    @SerializedName("loans")
    @Expose
    private List<LoanApplication> loans;
    @SerializedName("Interest")
    @Expose
    private double Interest;

    @SerializedName("application_fee")
    @Expose
    private int application_fee;

    @SerializedName("possible_action")
    @Expose
    private String possible_action;

    public List<LoanApplication> getLoans() {
        return loans;
    }

    public void setLoans(List<LoanApplication> loans) {
        this.loans = loans;
    }

    public double getInterest() {
        return Interest;
    }

    public void setInterest(double interest) {
        Interest = interest;
    }

    public int getApplication_fee() {
        return application_fee;
    }

    public void setApplication_fee(int application_fee) {
        this.application_fee = application_fee;
    }

    public String getPossible_action() {
        return possible_action;
    }

    public void setPossible_action(String possible_action) {
        this.possible_action = possible_action;
    }

    public class Loans{
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("amount")
        @Expose
        private float amount;

        @SerializedName("ninNumber")
        @Expose
        private String ninNumber;

        @SerializedName("duration")
        @Expose
        private String duration;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("interestRate")
        @Expose
        private double interestRate;

        @SerializedName("dateApproved")
        @Expose
        private String dateApproved;

        @SerializedName("walletReferenceNumber")
        @Expose
        private String walletReferenceNumber;

        @SerializedName("repaymentPlan")
        @Expose
        private String repaymentPlan;

        @SerializedName("amountPaid")
        @Expose
        private double amountPaid;

        @SerializedName("isDue")
        @Expose
        private String isDue;

        @SerializedName("dueDate")
        @Expose
        private String dueDate;

        @SerializedName("amountExpected")
        @Expose
        private double amountExpected;

        @SerializedName("totalFines")
        @Expose
        private double totalFines;

        @SerializedName("nationalIDFrontPic")
        @Expose
        private String nationalIDFrontPic;

        @SerializedName("nationalIDBackPic")
        @Expose
        private String nationalIDBackPic;

        @SerializedName("userPhotoPic")
        @Expose
        private String userPhotoPic;

        @SerializedName("requestDate")
        @Expose
        private String requestDate;

        @SerializedName("applicationDate")
        @Expose
        private String applicationDate;

        @SerializedName("dueAmount")
        @Expose
        private double dueAmount;

        @SerializedName("totalPayments")
        @Expose
        private double totalPayments;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getNinNumber() {
            return ninNumber;
        }

        public void setNinNumber(String ninNumber) {
            this.ninNumber = ninNumber;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(double interestRate) {
            this.interestRate = interestRate;
        }

        public String getDateApproved() {
            return dateApproved;
        }

        public void setDateApproved(String dateApproved) {
            this.dateApproved = dateApproved;
        }

        public String getWalletReferenceNumber() {
            return walletReferenceNumber;
        }

        public void setWalletReferenceNumber(String walletReferenceNumber) {
            this.walletReferenceNumber = walletReferenceNumber;
        }

        public String getRepaymentPlan() {
            return repaymentPlan;
        }

        public void setRepaymentPlan(String repaymentPlan) {
            this.repaymentPlan = repaymentPlan;
        }

        public double getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(double amountPaid) {
            this.amountPaid = amountPaid;
        }

        public String getIsDue() {
            return isDue;
        }

        public void setIsDue(String isDue) {
            this.isDue = isDue;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public double getAmountExpected() {
            return amountExpected;
        }

        public void setAmountExpected(double amountExpected) {
            this.amountExpected = amountExpected;
        }

        public double getTotalFines() {
            return totalFines;
        }

        public void setTotalFines(double totalFines) {
            this.totalFines = totalFines;
        }

        public String getNationalIDFrontPic() {
            return nationalIDFrontPic;
        }

        public void setNationalIDFrontPic(String nationalIDFrontPic) {
            this.nationalIDFrontPic = nationalIDFrontPic;
        }

        public String getNationalIDBackPic() {
            return nationalIDBackPic;
        }

        public void setNationalIDBackPic(String nationalIDBackPic) {
            this.nationalIDBackPic = nationalIDBackPic;
        }

        public String getUserPhotoPic() {
            return userPhotoPic;
        }

        public void setUserPhotoPic(String userPhotoPic) {
            this.userPhotoPic = userPhotoPic;
        }

        public String getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(String requestDate) {
            this.requestDate = requestDate;
        }

        public String getApplicationDate() {
            return applicationDate;
        }

        public void setApplicationDate(String applicationDate) {
            this.applicationDate = applicationDate;
        }

        public double getDueAmount() {
            return dueAmount;
        }

        public void setDueAmount(double dueAmount) {
            this.dueAmount = dueAmount;
        }

        public double getTotalPayments() {
            return totalPayments;
        }

        public void setTotalPayments(double totalPayments) {
            this.totalPayments = totalPayments;
        }



    }
}
