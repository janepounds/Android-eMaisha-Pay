package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.cabral.emaishapay.Room.Entities.ec_product_cart;

@Dao
public interface ec_product_cart_dao {

    //get total price
    @Query("SELECT * FROM product_cart")
    double getTotalPrice();

    //add to  cart
    @Query("SELECT * FROM product_cart WHERE product_id=:product_id")
    int addToCart(String product_id, String weight, String weight_unit, String price, int qty);

    //delete product from cart
    @Query("DELETE FROM product_cart WHERE cart_id = :id")
    boolean deleteProductFromCart(String id);

    //get cart item count
    @Query("SELECT * FROM product_cart")
     int getCartItemCount();


    //update product quantity
    @Query("UPDATE product_cart SET product_qty=:qty WHERE product_id=:id")
    void updateProductQty(String id, String qty);
}
