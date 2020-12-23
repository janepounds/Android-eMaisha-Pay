package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BankBranch implements Serializable {

    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("BranchCode")
    @Expose
    private String BranchCode;
    @SerializedName("BranchName")
    @Expose
    private String BranchName;

    public BankBranch(JSONObject bankObject) throws JSONException {

        setId(bankObject.getString("Id"));
        setBranchCode(bankObject.getString("BranchCode"));
        setBranchName(bankObject.getString("BranchName"));
    }

    public BankBranch(String Id, String Code, String Name ){
        setId(Id);
        setBranchCode(Code);
        setBranchName(Name);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getBranchCode() {
        return BranchCode;
    }

    public void setBranchCode(String branchCode) {
        BranchCode = branchCode;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }
}
