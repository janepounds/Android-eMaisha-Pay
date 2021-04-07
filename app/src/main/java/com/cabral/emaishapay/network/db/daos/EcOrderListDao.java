package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.network.db.entities.EcOrderList;

import org.json.JSONObject;

import java.util.List;

@Dao
public interface EcOrderListDao {

    //insert order
    boolean addOrder(JSONObject obj);

    //get order id
    @Query("SELECT invoice_id FROM EcOrderList WHERE invoice_id=:id")
    int getID(String id);

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(EcOrderList order_list);

    //get order list
    @Query("SELECT * FROM EcOrderList ORDER BY order_id DESC")
    List<EcOrderList> getOrderList();

    //update order
    @Update()
    boolean updateOrder(String id, String status);

}
