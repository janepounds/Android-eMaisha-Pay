package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.ShopOrder;

import java.util.List;

@Dao
public interface ShopOrderDao {

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrder(ShopOrder orderList);

    //get order id
    @Query("SELECT order_id FROM ShopOrder WHERE order_id=:id")
    int getID(String id);

    //insert order
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrder(List<ShopOrder> order_list);

    //get order list
    @Query("SELECT * FROM ShopOrder ORDER BY order_id DESC")
    LiveData<List<ShopOrder>> getOrderList();

    //update order
    @Query("UPDATE ShopOrder SET order_status=:status WHERE order_id=:id ")
    void updateOrder(String id, String status);

    //search order
    @Query("SELECT * FROM ShopOrder WHERE customer_name MATCH :s  ORDER BY order_id DESC")
    LiveData<List<ShopOrder>> searchOrderList(String s);

}
