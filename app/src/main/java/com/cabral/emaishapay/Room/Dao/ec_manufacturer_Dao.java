package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.ec_manufacturers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Dao
public interface ec_manufacturer_Dao {

    //insert manufacturer
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    boolean addManufacturers(ec_manufacturers manufacturers);

    //get offline manufacturers
    @Query("SELECT * FROM manufacturers ORDER BY manufacturer_name DESC ")
   List<HashMap<String, String>> getOfflineManufacturers();
}
