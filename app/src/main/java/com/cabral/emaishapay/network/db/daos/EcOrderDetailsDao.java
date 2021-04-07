package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.HashMap;
import java.util.List;

@Dao
public interface EcOrderDetailsDao {

    //get order details list
    @Query("SELECT * FROM EcOrderDetails WHERE invoice_id=:order_id ORDER BY order_details_id DESC")
    List<HashMap<String, String>> getOrderDetailsList(String order_id);

    //get all sales item
    @Query("SELECT EcOrderDetails.*,EcOrderList.order_payment_method FROM EcOrderDetails INNER JOIN EcOrderList ON EcOrderDetails.invoice_id=EcOrderList.invoice_id WHERE EcOrderList.order_status='Approved' ORDER BY order_details_id DESC")
    List<HashMap<String, String>> getAllSalesItems();


    //total order price
    @Query("SELECT * FROM EcOrderDetails WHERE invoice_id=:invoice_id")
    double totalOrderPrice(String invoice_id);

    //delete order
    @Delete
    boolean deleteOrder(String invoice_id);

    //search order
    @Query("SELECT * FROM EcOrderList WHERE customer_name LIKE :s OR invoice_id LIKE :s ORDER BY order_id DESC")
    List<HashMap<String, String>> searchOrderList(String s);
}
