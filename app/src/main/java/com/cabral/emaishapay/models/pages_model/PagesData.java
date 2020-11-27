package com.cabral.emaishapay.models.pages_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class PagesData {
    
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("pages_data")
    @Expose
    private List<com.cabral.emaishapay.models.pages_model.PagesDetails> pagesData = new ArrayList<>();
    
    
    public String getSuccess() {
        return success;
    }
    
    public void setSuccess(String success) {
        this.success = success;
    }
    
    public List<com.cabral.emaishapay.models.pages_model.PagesDetails> getPagesData() {
        return pagesData;
    }
    
    public void setPagesData(List<com.cabral.emaishapay.models.pages_model.PagesDetails> pagesData) {
        this.pagesData = pagesData;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
}
