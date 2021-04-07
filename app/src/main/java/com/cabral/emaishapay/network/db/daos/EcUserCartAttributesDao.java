package com.cabral.emaishapay.network.db.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;

import java.util.List;

@Dao
public interface EcUserCartAttributesDao {

    //insert cart attributes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartAttributes(CartProduct cartProduct);

    //fetch cart attributes
    @Query("SELECT * FROM EcUserCartAttributes WHERE  cart_table_id =:cart_id")
    List<CartProductAttributes> getCartAttributes(int cart_id);


    //get cart attributes product
    @Query("SELECT * FROM EcUserCartAttributes  WHERE cart_table_id =:cart_id")
    List<CartProductAttributes>getCartAttributesProduct(int cart_id);


    //get cart attributes product 2
    @Query("SELECT * FROM EcUserCartAttributes  WHERE cart_table_id =:cart_id")
    List<CartProductAttributes>getCartAattributesProduct2(int cart_id);

}
