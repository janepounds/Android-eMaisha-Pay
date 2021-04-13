package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.ShopOrderDetails;
import com.cabral.emaishapay.network.db.entities.ShopOrderList;
import com.cabral.emaishapay.utils.Resource;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcOrderDetailsDao {

    //get order details list
    @Query("SELECT * FROM ShopOrderDetails WHERE invoice_id=:order_id ORDER BY order_details_id DESC")
    LiveData<List<ShopOrderDetails>> getOrderDetailsList(String order_id);

    //get all sales item
//    @Query("SELECT ShopOrderDetails.*,ShopOrderList.order_payment_method FROM ShopOrderDetails INNER JOIN ShopOrderList ON ShopOrderDetails.invoice_id=ShopOrderList.invoice_id WHERE ShopOrderList.order_status='Approved' ORDER BY order_details_id DESC")
//    List<ShopOrderDetails> getAllSalesItems();


    //total order price
    @Query("SELECT * FROM ShopOrderDetails WHERE invoice_id=:invoice_id")
    double totalOrderPrice(String invoice_id);

    //delete order
    @Delete
    void deleteOrder(ShopOrderDetails orderDetails);


    //get monthly total price
    @Query("SELECT * FROM ShopOrderDetails WHERE strftime('%m', product_order_date) =:current_month ")
    List<ShopOrderDetails> getMonthlyTotalPrice(String current_month);


    //get yearly total price
    @Query("SELECT * FROM ShopOrderDetails WHERE strftime('%Y', product_order_date) =:currentYear")
    List<ShopOrderDetails> getYearlyTotalPrice(String currentYear);

    //get yearly total price
    @Query("SELECT * FROM ShopOrderDetails WHERE strftime('%Y', product_order_date) =:currentDate ORDER BY order_Details_id DESC")
    List<ShopOrderDetails> getDailyTotalPrice(String currentDate);

    //get total price
    @Query("SELECT * FROM ShopOrderDetails")
    List<ShopOrderDetails> getTotalPrice();

}
