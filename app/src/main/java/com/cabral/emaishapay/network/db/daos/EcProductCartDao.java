package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcProductCart;

import java.util.List;

@Dao
public interface EcProductCartDao {

    //insert cart product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCartProduct(EcProductCart productCart);

    //get total price
    @Query("SELECT * FROM EcProductCart")
    List<EcProductCart> getTotalPrice();

    //add to  cart
    @Query("SELECT * FROM EcProductCart WHERE product_id=:product_id")
    LiveData<List<EcProductCart>> selectCartProduct(String product_id);

    //delete product from cart
    @Query("DELETE FROM EcProductCart WHERE cart_id = :id")
    void deleteProductFromCart(String id);

    //get cart item count
    @Query("SELECT * FROM EcProductCart")
    int getCartItemCount();


    //update product quantity
    @Query("UPDATE EcProductCart SET product_qty=:qty WHERE product_id=:id")
    void updateProductQty(String id, String qty);
}
