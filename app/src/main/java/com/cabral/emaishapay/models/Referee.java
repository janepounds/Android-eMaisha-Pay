package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Referee implements Serializable {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("last_name")
    @Expose
    private String last_name;
    @SerializedName("relationship")
    @Expose
    private String relationship;
    @SerializedName("contact")
    @Expose
    private String contact;
    public Referee(JSONObject loanObject) throws JSONException {

        setId(loanObject.getString("id"));
        setFirst_name(loanObject.getString("first_name"));
        setLast_name(loanObject.getString("last_name"));
        setRelationship(loanObject.getString("relationship"));
        setContact(loanObject.getString("contact"));
    }

    public Referee(String first_name, String last_name, String relationship, String contact ){
        setFirst_name(first_name);
        setLast_name(last_name);
        setRelationship(relationship);
        setContact(contact);
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getContact() {
        return contact;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}