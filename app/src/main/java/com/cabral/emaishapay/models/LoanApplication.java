package com.cabral.emaishapay.models;


import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoanApplication implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("amount")
    @Expose
    private float amount;
    @SerializedName("ninNumber")
    @Expose
    private String ninNumber;
    @SerializedName("loanType")
    @Expose
    private String loanType;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("interestRate")
    @Expose
    private float interestRate;
    @SerializedName("dateApproved")
    @Expose
    private String dateApproved;


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
    private float amountExpected;
    @SerializedName("totalFines")
    @Expose
    private float totalFines;
    @SerializedName("nationalIDFrontPic")
    @Expose
    private String nationalIDFrontPic;
    @SerializedName("nationalIDBackPic")
    @Expose
    private String nationalIDBackPic;
    @SerializedName("userPhotoPic")
    @Expose
    private String userPhotoPic;

    @SerializedName("farm_photo")
    @Expose
    private String farm_photo;
    @SerializedName("requestDate")
    @Expose
    private String requestDate;

    @SerializedName("dueAmount")
    @Expose
    private int dueAmount;

    //crop
    @SerializedName("crop")
    @Expose
    private String crop;
    @SerializedName("crop_area")
    @Expose
    private double crop_area;
    @SerializedName("crop_area_unit")
    @Expose
    private String crop_area_unit;
    @SerializedName("expected_yield")
    @Expose
    private double expected_yield;
    @SerializedName("expected_revenue")
    @Expose
    private int expected_revenue;
    @SerializedName("yeild_units")
    @Expose
    private String yeild_units;
    @SerializedName("from_insurance")
    @Expose
    private boolean from_insurance;
    @SerializedName("purpose_for_fetilizer")
    @Expose
    private boolean purpose_for_fetilizer;
    @SerializedName("purpose_for_seeds")
    @Expose
    private boolean purpose_for_seeds;
    @SerializedName("purpose_for_crop_protection")
    @Expose
    private boolean purpose_for_crop_protection;
    @SerializedName("purpose_for_equipments")
    @Expose
    private boolean purpose_for_equipments;
    @SerializedName("loan_gaurantor1")
    @Expose
    private com.cabral.emaishapay.models.Referee loan_gaurantor1;
    @SerializedName("loan_gaurantor2")
    @Expose
    private com.cabral.emaishapay.models.Referee loan_gaurantor2;

    @SerializedName("application_fee")
    @Expose
    private int application_fee;

    @SerializedName("possible_action")
    @Expose
    private String possible_action;

    public LoanApplication(){

    }

    public LoanApplication(JSONObject loanObject) throws JSONException {

       setId(loanObject.getString("id"));
        setAmount((float)loanObject.getDouble("amount"));
        setFarm_photo(loanObject.getString("farm_photo"));
        setNinNumber(loanObject.getString("ninNumber"));
       setLoanType(loanObject.getString("loanType"));
       setDuration(loanObject.getInt("duration"));
       setStatus(loanObject.getString("status"));
        setInterestRate((float)loanObject.getDouble("interestRate"));
       setDateApproved(loanObject.getString("dateApproved"));
        setRequestDate(loanObject.getString("requestDate"));
        setDueDate(loanObject.getString("dueDate"));
       setNationalIDFrontPic(loanObject.getString("nationalIDFrontPic"));
       setNationalIDBackPic(loanObject.getString("nationalIDBackPic"));
       setUserPhotoPic(loanObject.getString("userPhotoPic"));
       setAmountExpected((float)loanObject.getDouble("amountExpected"));
       setTotalFines((float)loanObject.getDouble("totalFines"));
        setDueAmount((int)loanObject.getDouble("dueAmount"));
        setAmountPaid((double)loanObject.getDouble("totalPayments"));
        setApplication_fee(Integer.parseInt(loanObject.getString("application_fee")));
        setPossible_action(loanObject.getString("possible_action"));


    }
    public String getId() {
        return id;
    }
    public double getDueAmount(){
        return this.dueAmount;

    }

    public void setFarm_photo(String farm_photo) {
        this.farm_photo = farm_photo;
    }

    public String getFarm_photo() {
        return farm_photo;
    }

    public String getCrop() {
        return crop;
    }

    public double getCrop_area() {
        return crop_area;
    }

    public String getCrop_area_unit() {
        return crop_area_unit;
    }

    public double getExpected_yield() {
        return expected_yield;
    }

    public int getExpected_revenue() {
        return expected_revenue;
    }

    public String getYeild_units() {
        return yeild_units;
    }

    public boolean isFrom_insurance() {
        return from_insurance;
    }

    public boolean isPurpose_for_fetilizer() {
        return purpose_for_fetilizer;
    }

    public boolean isPurpose_for_seeds() {
        return purpose_for_seeds;
    }

    public boolean isPurpose_for_crop_protection() {
        return purpose_for_crop_protection;
    }

    public boolean isPurpose_for_equipments() {
        return purpose_for_equipments;
    }

    public com.cabral.emaishapay.models.Referee getLoan_gaurantor1() {
        return loan_gaurantor1;
    }

    public com.cabral.emaishapay.models.Referee getLoan_gaurantor2() {
        return loan_gaurantor2;
    }

    public void setLoan_gaurantor1(com.cabral.emaishapay.models.Referee loan_gaurantor1) {
        this.loan_gaurantor1 = loan_gaurantor1;
    }

    public void setLoan_gaurantor2(com.cabral.emaishapay.models.Referee loan_gaurantor2) {
        this.loan_gaurantor2 = loan_gaurantor2;
    }

    public void setId(String id) {
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

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public void setCrop_area(double crop_area) {
        this.crop_area = crop_area;
    }

    public void setCrop_area_unit(String crop_area_unit) {
        this.crop_area_unit = crop_area_unit;
    }

    public void setExpected_yield(double expected_yield) {
        this.expected_yield = expected_yield;
    }

    public void setExpected_revenue(int expected_revenue) {
        this.expected_revenue = expected_revenue;
    }

    public void setYeild_units(String yeild_units) {
        this.yeild_units = yeild_units;
    }

    public void setFrom_insurance(boolean from_insurance) {
        this.from_insurance = from_insurance;
    }

    public void setPurpose_for_fetilizer(boolean purpose_for_fetilizer) {
        this.purpose_for_fetilizer = purpose_for_fetilizer;
    }

    public void setPurpose_for_seeds(boolean purpose_for_seeds) {
        this.purpose_for_seeds = purpose_for_seeds;
    }

    public void setPurpose_for_crop_protection(boolean purpose_for_crop_protection) {
        this.purpose_for_crop_protection = purpose_for_crop_protection;
    }

    public void setPurpose_for_equipments(boolean purpose_for_equipments) {
        this.purpose_for_equipments = purpose_for_equipments;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
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
    public void setDueAmount(int dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public float getAmountExpected() {
        return amountExpected;
    }

    public void setAmountExpected(float amountExpected) {
        this.amountExpected = amountExpected;
    }

    public float getTotalFines() {
        return totalFines;
    }

    public void setTotalFines(float totalFines) {
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

    public String generateStatus(){
        if (status.equals("Rejected") || status.equals("Approved")){
            return status;
        }
        if(nationalIDFrontPic ==null || nationalIDBackPic==null | userPhotoPic==null){
            return "Photos missing";
        }

        else {
            return status;
        }
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

    public boolean isApproved(){
        return generateStatus().equals("Approved");
    }
    public boolean isEditable(){
        return !(generateStatus().equals("Approved") || generateStatus().equals("Rejected"));
    }

    public String computeDueDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat().replace("mm","MM"));
        Log.d("DATE FORMAT", WalletSettingsSingleton.getInstance().getDateFormat().replace("MM","mm"));

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());
        if (getLoanType().toLowerCase().equals("weekly")){
            todayCalendar.add(Calendar.DAY_OF_YEAR, getDuration()*7);
        }else if (getLoanType().toLowerCase().equals("monthly")){
            todayCalendar.add(Calendar.DAY_OF_YEAR, getDuration()*30);
        }
        return dateFormat.format(todayCalendar.getTime());
    }
    public int computeDueAmount(){
        return  Math. round(this.amount*(1+this.interestRate/100));
    }
    public String getDurationLabel(){

        if(getLoanType().equals("Daily")){
            return getLoanType().replace("il","")+(getDuration()==1?"":"s");
        }
        return getLoanType().replace("ly","")+(getDuration()==1?"":"s");
    }

}



