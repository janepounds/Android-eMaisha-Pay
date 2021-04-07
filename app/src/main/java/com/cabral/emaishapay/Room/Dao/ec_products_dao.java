package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.ec_products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Dao
public interface ec_products_dao {

    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    boolean addProduct(ec_products products);


    //get products
    @Query("SELECT * FROM  products ORDER BY product_id DESC")
    List<HashMap<String, String>> getProducts();


    //delete product
    @Delete
    boolean deleteProduct(String product_id);

    //get product data
    @Query("SELECT * FROM products WHERE product_name LIKE :s OR product_code LIKE :s ORDER BY product_id DESC")
    List<HashMap<String, String>> getSearchProducts(String s);

    //get product name
    @Query("SELECT * FROM products WHERE product_id=:product_id")
    String getProductName(String product_id);

    //get product image
    @Query("SELECT * FROM products WHERE product_id=:product_id")
    String getProductImage(String product_id);

    //add product name
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    boolean addProductName(ec_products products);

    //get offline product names
    @Query("SELECT * FROM products ORDER BY product_name DESC ")
    List<HashMap<String, String>> getOfflineProductNames();

    //get unsynced products
    @Query("SELECT * FROM products WHERE sync_status=:sync_status")
    List<HashMap<String, String>> getUnsyncedProducts(String sync_status);

    //update sync status
    @Query("UPDATE products SET sync_status=:sync_status WHERE product_id=:product_id")
    boolean updateProductSyncStatus(String product_id,String sync_status);
}
