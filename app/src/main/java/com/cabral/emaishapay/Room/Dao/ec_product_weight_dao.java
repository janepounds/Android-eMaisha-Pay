package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.HashMap;

@Dao
public interface ec_product_weight_dao {

    //get product weight
    @Query("SELECT * FROM product_weight")
    ArrayList<HashMap<String, String>> getWeightUnit();
}
