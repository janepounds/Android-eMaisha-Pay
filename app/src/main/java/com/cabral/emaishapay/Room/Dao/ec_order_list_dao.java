package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.Room.Entities.ec_order_list;

import org.json.JSONObject;

import java.util.List;

@Dao
public interface ec_order_list_dao {

    //insert order
    boolean addOrder(JSONObject obj);

    //get order id
    @Query("SELECT invoice_id FROM order_list WHERE invoice_id=:id")
    int getID(String id);

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(ec_order_list order_list);

    //get order list
    @Query("SELECT * FROM order_list ORDER BY order_id DESC")
    List<ec_order_list> getOrderList();

    //update order
    @Update()
    boolean updateOrder(String id, String status);

}
