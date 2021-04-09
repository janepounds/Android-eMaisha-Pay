package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.RegionDetails;

import java.util.List;

@Dao
public interface RegionDetailsDao {
    //insert all regions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRegionDetails(List<RegionDetails>regionDetails);

    //get maximum region id
    @Query("SELECT MAX(id)  FROM RegionDetails")
    int getMaxRegionId();

    //get all region details
    @Query("SELECT * FROM RegionDetails WHERE RegionDetails.regionType=:district")
    List<RegionDetails> getRegionDetails(String  district);


    //get subcountry details
    @Query("SELECT * FROM RegionDetails WHERE RegionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    List<RegionDetails> getSubcountyDetails(String belongs_to, String subcounty);

    //get village details
    @Query("SELECT * FROM RegionDetails WHERE RegionDetails.belongs_to=:belongs_to AND regionType=:subcounty")
    List<RegionDetails> getVillageDetails(String belongs_to, String subcounty);




}
