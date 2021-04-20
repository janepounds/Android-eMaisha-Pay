package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.DefaultAddress;

import java.util.List;

@Dao
public interface DefaultAddressDao {

    //insert default_address
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDefaultAddress(DefaultAddress default_address);

    //get default_address
    @Query("SELECT * FROM DefaultAddress WHERE customers_id=:customer_id ")
    LiveData<List<DefaultAddress>> getDefaultAddress(String customer_id);


}
