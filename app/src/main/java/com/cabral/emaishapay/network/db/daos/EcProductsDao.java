package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcProduct;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcProductsDao {

    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addProduct(EcProduct products);


    //get products
    @Query("SELECT * FROM  EcProduct ORDER BY product_id DESC")
    List<EcProduct> getProducts();


    //delete product
    @Delete
    void deleteProduct(EcProduct product);

    //get product data
    @Query("SELECT * FROM EcProduct WHERE product_name LIKE :s OR product_code LIKE :s ORDER BY product_id DESC")
    List<EcProduct> getSearchProducts(String s);

    //get product name
    @Query("SELECT * FROM EcProduct WHERE product_id=:product_id")
    List<EcProduct> getProductName(String product_id);

    //get product image
    @Query("SELECT * FROM EcProduct WHERE product_id=:product_id")
    List<EcProduct> getProductImage(String product_id);

    //add product name
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addProductName(EcProduct products);

    //get offline product names
    @Query("SELECT * FROM EcProduct ORDER BY product_name DESC ")
    List<EcProduct> getOfflineProductNames();

    //get unsynced products
    @Query("SELECT * FROM EcProduct WHERE sync_status=:sync_status")
    List<EcProduct> getUnsyncedProducts(String sync_status);

    //update sync status
    @Query("UPDATE EcProduct SET sync_status=:sync_status WHERE product_id=:product_id")
    void updateProductSyncStatus(String product_id,String sync_status);
}