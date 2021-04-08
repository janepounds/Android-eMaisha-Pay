package com.cabral.emaishapay.network.db.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.network.db.entities.EcUserCart;
import com.cabral.emaishapay.network.db.entities.EcUserCartAttributes;

import java.util.List;

@Dao
public interface EcUserCartAttributesDao {

    //insert cart attributes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartAttributes(EcUserCartAttributes userCartAttributes);

    //fetch cart attributes
    @Query("SELECT * FROM EcUserCartAttributes WHERE  cart_table_id =:cart_id")
    List<EcUserCartAttributes> getCartAttributes(int cart_id);


    //get cart attributes product
    @Query("SELECT * FROM EcUserCartAttributes  WHERE cart_table_id =:cart_id")
    List<EcUserCartAttributes>getCartAttributesProduct(int cart_id);


    //get cart attributes product 2
    @Query("SELECT * FROM EcUserCartAttributes  WHERE cart_table_id =:cart_id")
    List<EcUserCartAttributes>getCartAattributesProduct2(int cart_id);

}
