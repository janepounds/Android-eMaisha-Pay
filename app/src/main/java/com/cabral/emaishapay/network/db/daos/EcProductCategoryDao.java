package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcProductCategory;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcProductCategoryDao {

    //add product category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    boolean addProductCategory(EcProductCategory product_category);


    //get offline product categories
    @Query("SELECT * FROM EcProductCategory ORDER BY category_name DESC ")
    List<HashMap<String, String>> getOfflineProductCategories();
}
