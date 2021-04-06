package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.default_address;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface defaultAddressDao {

    //insert default_address
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDefaultAddress(default_address default_address);

    //get default_address
    @Query("SELECT * FROM default_address WHERE customers_id=:customer_id ")
    List<default_address> getDefaultAddress(String customer_id);


}
