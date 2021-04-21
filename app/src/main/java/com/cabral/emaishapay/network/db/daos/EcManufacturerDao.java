package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcManufacturer;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcManufacturerDao {

    //insert manufacturer
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addManufacturers(EcManufacturer manufacturers);

    //get offline manufacturers
    @Query("SELECT * FROM EcManufacturer ORDER BY manufacturer_name DESC ")
   List<EcManufacturer> getOfflineManufacturers();
}
