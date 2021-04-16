package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.UserCartProduct;

import java.util.List;

@Dao
public interface UserCartProductDao {

    //insert cart product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCartProduct(UserCartProduct userCartProduct);

    //get total price
    @Query("SELECT * FROM UserCartProduct")
    List<UserCartProduct> getTotalPrice();

    //add to  cart
    @Query("SELECT * FROM UserCartProduct WHERE product_id=:product_id")
    LiveData<List<UserCartProduct>> selectCartProduct(String product_id);

    //delete product from cart
    @Query("DELETE FROM UserCartProduct WHERE cart_id = :id")
    void deleteProductFromCart(String id);

    //get cart item count
    @Query("SELECT * FROM UserCartProduct")
    int getCartItemCount();


    //update product quantity
    @Query("UPDATE UserCartProduct SET product_qty=:qty WHERE product_id=:id")
    void updateProductQty(String id, String qty);
}
