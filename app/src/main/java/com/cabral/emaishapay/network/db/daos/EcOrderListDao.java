package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.network.db.entities.ShopOrderList;

import org.json.JSONObject;

import java.util.List;

@Dao
public interface EcOrderListDao {

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrder(ShopOrderList orderList);

    //get order id
    @Query("SELECT invoice_id FROM ShopOrderList WHERE invoice_id=:id")
    int getID(String id);

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(ShopOrderList order_list);

    //get order list
    @Query("SELECT * FROM ShopOrderList ORDER BY order_id DESC")
    List<ShopOrderList> getOrderList();

    //update order
    @Query("UPDATE ShopOrderList SET order_status=:status WHERE order_id=:id ")
    void updateOrder(String id, String status);

    //search order
    @Query("SELECT * FROM ShopOrderList WHERE customer_name LIKE :s OR invoice_id LIKE :s ORDER BY order_id DESC")
    List<ShopOrderList> searchOrderList(String s);

}
