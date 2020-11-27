package com.cabral.emaishapay.models.address_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class RegionDetails {
    @SerializedName("id")
    @Expose
    private int id = 0;
    @SerializedName("regionType")
    @Expose
    private String regionType;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("belongs_to")
    @Expose
    private String belongs_to;

    @SerializedName("tableId")
    @Expose
    private int tableId;

    public int getId() {
        return id;
    }

    public String getRegionType() {
        return regionType;
    }

    public String getRegion() {
        return region;
    }

    public String getBelongs_to() {
        return belongs_to;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setBelongs_to(String belongs_to) {
        this.belongs_to = belongs_to;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public  RegionDetails(){}


    public JSONObject toJSON() {

        JSONObject object = new JSONObject();

        try {
            object.put("id", id);
            object.put("tableId",tableId);
            object.put("regionType",regionType);
            object.put("region",region);
            object.put("belongs_to",belongs_to);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public RegionDetails(JSONObject object) throws JSONException {
        setId(object.getInt("id"));
        setTableId(object.getInt("tableId"));
        setRegionType(object.getString("regionType"));
        setRegion(object.getString("region"));
        setBelongs_to(object.getString("belongs_to"));

    }
}
