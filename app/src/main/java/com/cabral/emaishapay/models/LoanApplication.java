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

    @SerializedName("payment_amount_on_schedule")
    public double payment_amount_on_schedule=0;


    @SerializedName("amountPaid")
    @Expose
    private double amountPaid=0;
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
    @SerializedName("farming_details")
    @Expose
    private String farming_details;

    //crop
    @SerializedName("crop_data")
    @Expose
    private Crop crop_data;

    @SerializedName("poultry_data")
    @Expose
    private Poultry poultry_data;

    @SerializedName("piggery_data")
    @Expose
    private Piggery piggery_data;

    @SerializedName("loan_gaurantor1")
    @Expose
    private com.cabral.emaishapay.models.Referee loan_gaurantor1;
    @SerializedName("loan_gaurantor2")
    @Expose
    private com.cabral.emaishapay.models.Referee loan_gaurantor2;

    @SerializedName("customer_no")
    @Expose
    private String phone;

    public String getCheck_selected() {
        return farming_details;
    }

    public void setCheck_selected(String check_selected) {
        this.farming_details = check_selected;
    }

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
        setPayment_amount_on_schedule((double)loanObject.getDouble("payment_amount_on_schedule"));
        setPhone(loanObject.getString("merchant_no"));
        setCrop_data((Crop)loanObject.get("crop_data"));
        setPoultry_data((Poultry) loanObject.get("poultry_data"));
        setPiggery_data((Piggery) loanObject.get("piggery_data"));




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

    public double getPayment_amount_on_schedule() {
        return payment_amount_on_schedule;
    }

    public void setPayment_amount_on_schedule(double payment_amount_on_schedule) {
        this.payment_amount_on_schedule = payment_amount_on_schedule;
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
    public String getDurationWithUnits(){

        if(getLoanType().equals("Daily")){
            return this.getDuration()+" days";
        }else if(getLoanType().equalsIgnoreCase("Weekly")){

            return this.getDuration()+" weeks";
        }
        else if(getLoanType().equalsIgnoreCase("After 3 months")){
            return (3*this.getDuration())+" months";
        }
        else if(getLoanType().equalsIgnoreCase("After 6 months")){
            return (6*this.getDuration())+" months";
        }

        return this.getDuration()+" months";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Crop getCrop_data() {
        return crop_data;
    }

    public void setCrop_data(Crop crop_data) {
        this.crop_data = crop_data;
    }

    public Poultry getPoultry_data() {
        return poultry_data;
    }

    public void setPoultry_data(Poultry poultry_data) {
        this.poultry_data = poultry_data;
    }

    public Piggery getPiggery_data() {
        return piggery_data;
    }

    public void setPiggery_data(Piggery piggery_data) {
        this.piggery_data = piggery_data;
    }

    public static class Crop {
        @SerializedName("crop")
        @Expose
        private String crop;
        @SerializedName("crop_area")
        @Expose
        private double crop_area = 0;
        @SerializedName("crop_area_unit")
        @Expose
        private String crop_area_unit;
        @SerializedName("expected_yield")
        @Expose
        private double expected_yield=0;
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

    }

    public static class Poultry{
        @SerializedName("type_of_birds")
        @Expose
        private String type_of_birds;

        @SerializedName("date_of_hatch")
        @Expose
        private String date_of_hatch;

        @SerializedName("date_purchased")
        @Expose
        private String date_purchased;

        @SerializedName("no_of_birds_purchased")
        @Expose
        private int no_of_birds_purchased;
        @SerializedName("cost_per_chick")
        @Expose
        private double cost_per_chick=0;
        @SerializedName("source")
        @Expose
        private String source;
        @SerializedName("expected_disposal")
        @Expose
        private String expected_disposal;
        @SerializedName("housing_system")
        @Expose
        private String housing_system;
        @SerializedName("source_of_feeds")
        @Expose
        private String source_of_feeds;
        @SerializedName("experience")
        @Expose
        private String experience;
        @SerializedName("farm_vet_personnel")
        @Expose
        private String farm_vet_personnel;
        @SerializedName("records_kept_vaccination")
        @Expose
        private boolean records_kept_vaccination;
        @SerializedName("records_kept_production")
        @Expose
        private boolean records_kept_production;
        @SerializedName("records_kept_mortality_records")
        @Expose
        private boolean records_kept_mortality_records;
        @SerializedName("records_kept_feed_consumption")
        @Expose
        private boolean records_kept_feed_consumption;
        @SerializedName("records_kept_disease_incidences")
        @Expose
        private boolean records_kept_disease_incidences;
        @SerializedName("loan_purpose_feeds")
        @Expose
        private boolean loan_purpose_feeds;
        @SerializedName("loan_purpose_medication")
        @Expose
        private boolean loan_purpose_medication;
        @SerializedName("loan_purpose_purchase_chicks")
        @Expose
        private boolean loan_purpose_purchase_chicks;
        @SerializedName("loan_purpose_shed_construction")
        @Expose
        private boolean loan_purpose_shed_construction;
        @SerializedName("loan_purpose_equipment_purchase")
        @Expose
        private boolean loan_purpose_equipment_purchase;

        public String getType_of_birds() {
            return type_of_birds;
        }

        public void setType_of_birds(String type_of_birds) {
            this.type_of_birds = type_of_birds;
        }

        public String getDate_of_hatch() {
            return date_of_hatch;
        }

        public void setDate_of_hatch(String date_of_hatch) {
            this.date_of_hatch = date_of_hatch;
        }

        public String getDate_purchased() {
            return date_purchased;
        }

        public void setDate_purchased(String date_purchased) {
            this.date_purchased = date_purchased;
        }

        public int getNo_of_birds_purchased() {
            return no_of_birds_purchased;
        }

        public void setNo_of_birds_purchased(int no_of_birds_purchased) {
            this.no_of_birds_purchased = no_of_birds_purchased;
        }

        public double getCost_per_chick() {
            return cost_per_chick;
        }

        public void setCost_per_chick(double cost_per_chick) {
            this.cost_per_chick = cost_per_chick;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getExpected_disposal() {
            return expected_disposal;
        }

        public void setExpected_disposal(String expected_disposal) {
            this.expected_disposal = expected_disposal;
        }

        public String getHousing_system() {
            return housing_system;
        }

        public void setHousing_system(String housing_system) {
            this.housing_system = housing_system;
        }

        public String getSource_of_feeds() {
            return source_of_feeds;
        }

        public void setSource_of_feeds(String source_of_feeds) {
            this.source_of_feeds = source_of_feeds;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getFarm_vet_personnel() {
            return farm_vet_personnel;
        }

        public void setFarm_vet_personnel(String farm_vet_personnel) {
            this.farm_vet_personnel = farm_vet_personnel;
        }

        public boolean isRecords_kept_vaccination() {
            return records_kept_vaccination;
        }

        public void setRecords_kept_vaccination(boolean records_kept_vaccination) {
            this.records_kept_vaccination = records_kept_vaccination;
        }

        public boolean isRecords_kept_production() {
            return records_kept_production;
        }

        public void setRecords_kept_production(boolean records_kept_production) {
            this.records_kept_production = records_kept_production;
        }

        public boolean isRecords_kept_mortality_records() {
            return records_kept_mortality_records;
        }

        public void setRecords_kept_mortality_records(boolean records_kept_mortality_records) {
            this.records_kept_mortality_records = records_kept_mortality_records;
        }

        public boolean isRecords_kept_feed_consumption() {
            return records_kept_feed_consumption;
        }

        public void setRecords_kept_feed_consumption(boolean records_kept_feed_consumption) {
            this.records_kept_feed_consumption = records_kept_feed_consumption;
        }

        public boolean isRecords_kept_disease_incidences() {
            return records_kept_disease_incidences;
        }

        public void setRecords_kept_disease_incidences(boolean records_kept_disease_incidences) {
            this.records_kept_disease_incidences = records_kept_disease_incidences;
        }



        public boolean isLoan_purpose_feeds() {
            return loan_purpose_feeds;
        }

        public void setLoan_purpose_feeds(boolean loan_purpose_feeds) {
            this.loan_purpose_feeds = loan_purpose_feeds;
        }

        public boolean isLoan_purpose_medication() {
            return loan_purpose_medication;
        }

        public void setLoan_purpose_medication(boolean loan_purpose_medication) {
            this.loan_purpose_medication = loan_purpose_medication;
        }

        public boolean isLoan_purpose_purchase_chicks() {
            return loan_purpose_purchase_chicks;
        }

        public void setLoan_purpose_purchase_chicks(boolean loan_purpose_purchase_chicks) {
            this.loan_purpose_purchase_chicks = loan_purpose_purchase_chicks;
        }

        public boolean isLoan_purpose_shed_construction() {
            return loan_purpose_shed_construction;
        }

        public void setLoan_purpose_shed_construction(boolean loan_purpose_shed_construction) {
            this.loan_purpose_shed_construction = loan_purpose_shed_construction;
        }

        public boolean isLoan_purpose_equipment_purchase() {
            return loan_purpose_equipment_purchase;
        }

        public void setLoan_purpose_equipment_purchase(boolean loan_purpose_equipment_purchase) {
            this.loan_purpose_equipment_purchase = loan_purpose_equipment_purchase;
        }
    }

    public static class Piggery {
     @SerializedName("total_Animals")
     @Expose
     private int total_Animals;

     @SerializedName("no_of_females")
     @Expose
     private int no_of_females;

     @SerializedName("no_of_males")
     @Expose
     private int no_of_males;

     @SerializedName("annual_revenue")
     @Expose
     private double annual_revenue=0;
     @SerializedName("experience")
     @Expose
     private String experience;
     @SerializedName("source_of_feeds")
     @Expose
     private String source_of_feeds;
     @SerializedName("farm_vet_personnel")
     @Expose
     private String farm_vet_personnel;
     @SerializedName("business_model_selling_piglets")
     @Expose
     private boolean business_model_selling_piglets;
     @SerializedName("business_model_meat_production")
     @Expose
     private boolean business_model_meat_production;
     @SerializedName("business_model_selling_breeding_stock")
     @Expose
     private boolean business_model_selling_breeding_stock;
     @SerializedName("records_kept_feeds")
     @Expose
     private boolean records_kept_feeds;
     @SerializedName("records_kept_income_expenses")
     @Expose
     private boolean records_kept_income_expenses;
     @SerializedName("records_kept_medical")
     @Expose
     private boolean records_kept_medical;
     @SerializedName("records_kept_breeding")
     @Expose
     private boolean records_kept_breeding;

     @SerializedName("loan_purpose_feeds")
     @Expose
     private boolean loan_purpose_feeds;
     @SerializedName("loan_purpose_medication")
     @Expose
     private boolean loan_purpose_medication;
     @SerializedName("loan_purpose_equipment_purchase")
     @Expose
     private boolean loan_purpose_equipment_purchase;
     @SerializedName("loan_purpose_breeding_stock_purchase")
     @Expose
     private boolean loan_purpose_breeding_stock_purchase;
     public int getTotal_Animals() {
            return total_Animals;
        }

        public void setTotal_Animals(int total_Animals) {
            this.total_Animals = total_Animals;
        }

        public int getNo_of_females() {
            return no_of_females;
        }

        public void setNo_of_females(int no_of_females) {
            this.no_of_females = no_of_females;
        }

        public int getNo_of_males() {
            return no_of_males;
        }

        public void setNo_of_males(int no_of_males) {
            this.no_of_males = no_of_males;
        }

        public double getAnnual_revenue() {
            return annual_revenue;
        }

        public void setAnnual_revenue(double annual_revenue) {
            this.annual_revenue = annual_revenue;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getSource_of_feeds() {
            return source_of_feeds;
        }

        public void setSource_of_feeds(String source_of_feeds) {
            this.source_of_feeds = source_of_feeds;
        }

        public String isFarm_vet_personnel() {
            return farm_vet_personnel;
        }

        public void setFarm_vet_personnel(String farm_vet_personnel) {
            this.farm_vet_personnel = farm_vet_personnel;
        }

        public boolean isBusiness_model_selling_piglets() {
            return business_model_selling_piglets;
        }

        public void setBusiness_model_selling_piglets(boolean business_model_selling_piglets) {
            this.business_model_selling_piglets = business_model_selling_piglets;
        }

        public boolean isBusiness_model_meat_production() {
            return business_model_meat_production;
        }

        public void setBusiness_model_meat_production(boolean business_model_meat_production) {
            this.business_model_meat_production = business_model_meat_production;
        }

        public boolean isBusiness_model_selling_breeding_stock() {
            return business_model_selling_breeding_stock;
        }

        public void setBusiness_model_selling_breeding_stock(boolean business_model_selling_breeding_stock) {
            this.business_model_selling_breeding_stock = business_model_selling_breeding_stock;
        }

        public boolean isRecords_kept_feeds() {
            return records_kept_feeds;
        }

        public void setRecords_kept_feeds(boolean records_kept_feeds) {
            this.records_kept_feeds = records_kept_feeds;
        }

        public boolean isRecords_kept_income_expenses() {
            return records_kept_income_expenses;
        }

        public void setRecords_kept_income_expenses(boolean records_kept_income_expenses) {
            this.records_kept_income_expenses = records_kept_income_expenses;
        }

        public boolean isRecords_kept_medical() {
            return records_kept_medical;
        }

        public void setRecords_kept_medical(boolean records_kept_medical) {
            this.records_kept_medical = records_kept_medical;
        }

        public boolean isRecords_kept_breeding() {
            return records_kept_breeding;
        }

        public void setRecords_kept_breeding(boolean records_kept_breeding) {
            this.records_kept_breeding = records_kept_breeding;
        }





        public boolean isLoan_purpose_feeds() {
            return loan_purpose_feeds;
        }

        public void setLoan_purpose_feeds(boolean loan_purpose_feeds) {
            this.loan_purpose_feeds = loan_purpose_feeds;
        }

        public boolean isLoan_purpose_medication() {
            return loan_purpose_medication;
        }

        public void setLoan_purpose_medication(boolean loan_purpose_medication) {
            this.loan_purpose_medication = loan_purpose_medication;
        }

        public boolean isLoan_purpose_equipment_purchase() {
            return loan_purpose_equipment_purchase;
        }

        public void setLoan_purpose_equipment_purchase(boolean loan_purpose_equipment_purchase) {
            this.loan_purpose_equipment_purchase = loan_purpose_equipment_purchase;
        }

        public boolean isLoan_purpose_breeding_stock_purchase() {
            return loan_purpose_breeding_stock_purchase;
        }

        public void setLoan_purpose_breeding_stock_purchase(boolean loan_purpose_breeding_stock_purchase) {
            this.loan_purpose_breeding_stock_purchase = loan_purpose_breeding_stock_purchase;
        }
    }


    }




