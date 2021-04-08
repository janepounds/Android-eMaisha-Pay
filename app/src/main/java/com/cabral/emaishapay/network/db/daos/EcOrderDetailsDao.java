package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.ShopOrderDetails;
import com.cabral.emaishapay.network.db.entities.ShopOrderList;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcOrderDetailsDao {

    //get order details list
    @Query("SELECT * FROM ShopOrderDetails WHERE invoice_id=:order_id ORDER BY order_details_id DESC")
    List<ShopOrderDetails> getOrderDetailsList(String order_id);

    //get all sales item
//    @Query("SELECT ShopOrderDetails.*,ShopOrderList.order_payment_method FROM ShopOrderDetails INNER JOIN ShopOrderList ON ShopOrderDetails.invoice_id=ShopOrderList.invoice_id WHERE ShopOrderList.order_status='Approved' ORDER BY order_details_id DESC")
//    List<ShopOrderDetails> getAllSalesItems();


    //total order price
    @Query("SELECT * FROM ShopOrderDetails WHERE invoice_id=:invoice_id")
    double totalOrderPrice(String invoice_id);

    //delete order
    @Delete
    void deleteOrder(ShopOrderDetails orderDetails);


}
