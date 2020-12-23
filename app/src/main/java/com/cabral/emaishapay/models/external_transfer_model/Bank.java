package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Bank implements Serializable {

    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("Code")
    @Expose
    private String Code;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("IsMobileVerified")
    @Expose
    private Boolean IsMobileVerified;
    
    public Bank(JSONObject bankObject) throws JSONException {

        setId(bankObject.getString("Id"));
        setCode(bankObject.getString("Code"));
        setName(bankObject.getString("Name"));
        setMobileVerified(bankObject.getBoolean("IsMobileVerified"));
    }

    public Bank(String Id, String Code, String Name, Boolean IsMobileVerified ){
        setId(Id);
        setCode(Code);
        setName(Name);
        setMobileVerified(IsMobileVerified);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Boolean getMobileVerified() {
        return IsMobileVerified;
    }

    public void setMobileVerified(Boolean mobileVerified) {
        IsMobileVerified = mobileVerified;
    }
}
