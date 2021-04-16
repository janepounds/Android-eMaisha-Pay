package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.network.db.entities.EcProduct;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcProductsDao {

    //insert product
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addProduct(List<EcProduct> products);


    //get products
    @Query("SELECT * FROM  EcProduct ORDER BY product_id DESC")
    LiveData<List<EcProduct>> getProducts();


    //delete product
    @Delete
    void deleteProduct(EcProduct product);

    //update product
    @Query("UPDATE EcProduct SET product_name=:product_name, product_code=:product_code,product_category=:product_category, product_description=:product_description,product_buy_price=:product_buy_price, product_sell_price=:product_sell_price,product_supplier=:product_supplier, product_image=:product_image" +
            "+product_stock=:product_stock,product_weight_unit=:product_weight_unit,product_weight=:product_weight,manufacturer=:manufacturer WHERE product_id=:product_id")
    void updateProductStock(String product_id,String product_name,String product_code, String product_category,String product_description,String product_buy_price,String product_sell_price,String product_supplier,String product_image, String product_stock,String product_weight_unit,String product_weight,String manufacturer);

    //get product data
    @Query("SELECT EcProduct.* FROM EcProduct JOIN EcProductFts ON (EcProduct.id = EcProductFts.rowid) WHERE EcProductFts MATCH :query")
    LiveData<List<EcProduct>> getSearchProducts(String query);

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
