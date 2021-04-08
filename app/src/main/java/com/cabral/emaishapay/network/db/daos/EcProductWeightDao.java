package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcProductWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Dao
public interface EcProductWeightDao {

    @Insert
    void addWeight(EcProductWeight weight);
    //get product weight
    @Query("SELECT * FROM EcProductWeight")
    List<EcProductWeight> getWeightUnit();
}
