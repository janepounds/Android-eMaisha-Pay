package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.cabral.emaishapay.network.db.entities.MerchantOrder;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;

import java.util.List;

@Dao
public interface ShopOrderDao {

    //insert order
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrder(MerchantOrder orderList);

    //get order id
    @Query("SELECT order_id FROM ShopOrder WHERE order_id=:id")
    int getID(String id);

    //get order list
    @Transaction
    @Query("SELECT ShopOrder.* FROM ShopOrder ORDER BY ShopOrder.order_id DESC")
    LiveData<List<ShopOrderWithProducts>> getOrderList();

    //update order
    @Query("UPDATE ShopOrder SET order_status=:status WHERE order_id=:id ")
    int updateOrder(String id, String status);

    //search order
    @Query("SELECT ShopOrder.* FROM ShopOrder JOIN ShopOrderFts ON (ShopOrder.id=ShopOrderFts.rowid) WHERE ShopOrderFts MATCH :s  ORDER BY order_id DESC")
    LiveData<List<MerchantOrder>> searchOrderList(String s);

    //get all sales item
    //get order list
    @Transaction
    @Query("SELECT ShopOrder.* FROM ShopOrder WHERE ShopOrder.order_status='Approved' ORDER BY ShopOrder.order_id DESC")
    LiveData<List<ShopOrderWithProducts>> getAllSalesItems();

    @Transaction
    @Query("SELECT ShopOrder.* FROM ShopOrder JOIN ShopOrderFts ON (ShopOrder.id=ShopOrderFts.rowid) WHERE ShopOrder.order_status='Approved' AND  ShopOrderFts MATCH :query ORDER BY ShopOrder.order_id DESC")
    LiveData<List<ShopOrderWithProducts>> searchOrderSales(String query);
}
