package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Dao
public interface ec_order_details_dao {

    //get order details list
    @Query("SELECT * FROM order_details WHERE invoice_id=:order_id ORDER BY order_details_id DESC")
    List<HashMap<String, String>> getOrderDetailsList(String order_id);

    //get all sales item
    @Query("SELECT order_details.*,order_list.order_payment_method FROM order_details INNER JOIN order_list ON order_details.invoice_id=order_list.invoice_id WHERE order_list.order_status='Approved' ORDER BY order_details_id DESC")
    List<HashMap<String, String>> getAllSalesItems();


    //total order price
    @Query("SELECT * FROM order_details WHERE invoice_id=:invoice_id")
    double totalOrderPrice(String invoice_id);

    //delete order
    @Delete
    boolean deleteOrder(String invoice_id);

    //search order
    @Query("SELECT * FROM order_list WHERE customer_name LIKE :s OR invoice_id LIKE :s ORDER BY order_id DESC")
    List<HashMap<String, String>> searchOrderList(String s);
}
