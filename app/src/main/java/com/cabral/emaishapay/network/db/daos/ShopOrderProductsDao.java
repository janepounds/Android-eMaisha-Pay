package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;

import java.util.List;

@Dao
public interface ShopOrderProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShopProduct(ShopOrderProducts shopOrderProduct);

    //get order details list
    @Query("SELECT * FROM ShopOrderProducts WHERE product_order_id=:order_id ORDER BY order_details_id DESC")
    LiveData<List<ShopOrderProducts>> getOrderDetailsList(String order_id);


    //total order price
    @Query("SELECT * FROM ShopOrderProducts WHERE product_order_id=:invoice_id")
    double totalOrderPrice(String invoice_id);

    //delete order
    @Delete
    void deleteOrder(ShopOrderProducts orderDetails);


    //get monthly total price
    @Query("SELECT * FROM ShopOrderProducts WHERE strftime('%m', product_order_date) =:current_month ")
    List<ShopOrderProducts> getMonthlyTotalPrice(String current_month);


    //get yearly total price
    @Query("SELECT * FROM ShopOrderProducts WHERE strftime('%Y', product_order_date) =:currentYear")
    List<ShopOrderProducts> getYearlyTotalPrice(String currentYear);

    //get yearly total price
    @Query("SELECT * FROM ShopOrderProducts WHERE strftime('%Y', product_order_date) =:currentDate ORDER BY order_Details_id DESC")
    List<ShopOrderProducts> getDailyTotalPrice(String currentDate);

    //get total price
    @Query("SELECT * FROM ShopOrderProducts")
    List<ShopOrderProducts> getTotalPrice();

}
