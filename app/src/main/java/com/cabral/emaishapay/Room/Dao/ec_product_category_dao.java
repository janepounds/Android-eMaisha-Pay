package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.ec_product_category;

import java.util.HashMap;
import java.util.List;

@Dao
public interface ec_product_category_dao {

    //add product category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    boolean addProductCategory(ec_product_category product_category);


    //get offline product categories
    @Query("SELECT * FROM product_category ORDER BY category_name DESC ")
    List<HashMap<String, String>> getOfflineProductCategories();
}
