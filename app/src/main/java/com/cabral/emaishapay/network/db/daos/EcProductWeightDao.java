package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.HashMap;

@Dao
public interface EcProductWeightDao {

    //get product weight
    @Query("SELECT * FROM EcProductWeight")
    ArrayList<HashMap<String, String>> getWeightUnit();
}
