package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.regionDetails;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface regionDetailsDao {
    //insert all regions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRegionDetails(List<regionDetails>regionDetails);

    //get maximum region id
    @Query("SELECT MAX(id)  FROM regionDetails")
    int getMaxRegionId();

    //get all region details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.regionType=:district")
    ArrayList<regionDetails> getRegionDetails(String  district);


    //get subcountry details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    ArrayList<regionDetails> getSubcountyDetails(String belongs_to, String subcounty);

    //get village details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    ArrayList<regionDetails> getVillageDetails(String belongs_to, String subcounty);




}
