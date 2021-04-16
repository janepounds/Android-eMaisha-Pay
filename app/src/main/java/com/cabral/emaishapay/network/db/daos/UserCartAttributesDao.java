package com.cabral.emaishapay.network.db.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.UserCartAttributes;

import java.util.List;

@Dao
public interface UserCartAttributesDao {

    //insert cart attributes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartAttributes(UserCartAttributes userCartAttributes);

    //fetch cart attributes
    @Query("SELECT * FROM UserCartAttributes WHERE  cart_table_id =:cart_id")
    List<UserCartAttributes> getCartAttributes(int cart_id);


    //get cart attributes product
    @Query("SELECT * FROM UserCartAttributes  WHERE cart_table_id =:cart_id")
    List<UserCartAttributes>getCartAttributesProduct(int cart_id);


    //get cart attributes product 2
    @Query("SELECT * FROM UserCartAttributes  WHERE cart_table_id =:cart_id")
    List<UserCartAttributes>getCartAattributesProduct2(int cart_id);

}
