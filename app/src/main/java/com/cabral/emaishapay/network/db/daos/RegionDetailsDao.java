package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.regionDetails;

import java.util.List;

@Dao
public interface RegionDetailsDao {
    //insert all regions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRegionDetails(List<regionDetails>regionDetails);

    //get maximum region id
    @Query("SELECT MAX(id)  FROM regionDetails")
    int getMaxRegionId();

    //get all region details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.regionType=:district")
    List<regionDetails> getRegionDetails(String  district);


    //get subcountry details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    List<regionDetails> getSubcountyDetails(String belongs_to, String subcounty);

    //get village details
    @Query("SELECT * FROM regionDetails WHERE regionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    List<regionDetails> getVillageDetails(String belongs_to, String subcounty);




}
