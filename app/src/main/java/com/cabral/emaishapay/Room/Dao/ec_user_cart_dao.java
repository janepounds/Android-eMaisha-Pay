package com.cabral.emaishapay.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;

import java.util.List;

@Dao
public interface ec_user_cart_dao {

    //get last  cart id
    @Query("SELECT MAX(cart_id)  FROM user_cart")
    int getLastCartID();

    //insert cart items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartItem(CartProduct cartProduct);


    //fetch cart items
    @Query("SELECT * FROM user_cart")
    List<CartProduct>getCartItems();


    //get cart product
    @Query("SELECT * FROM user_cart WHERE  products_id=:product_id")
    CartProduct getCartProduct(int product_id);


    //get cart product 2
    @Query("SELECT * FROM user_cart WHERE products_id =:product_id OR products_name =:product_name")
    CartProduct getCartProduct2(String product_id, String product_name);


    //get cart items id
    @Query("SELECT products_id  FROM user_cart")
    List<Integer>getCartItemsIDs();

    //check cart
    @Query("SELECT * FROM user_cart WHERE products_id=:product_id")
    List<CartProduct>checkCartHasProductAndMeasure(int product_id);


    //update cart
    @Update
    void updateCart(CartProduct cart);

    //update cart item
    @Update
    void updateCartItem(CartProduct cart);

    //delete cart item
    @Delete
    void deleteCartItem(int cart_id);

    //clear cart
    @Delete
    void clearCart();


    //check cart product
    @Query("SELECT * FROM user_cart WHERE products_id=:product_id OR products_name =:product_name")
    Boolean checkCartProduct(int product_id, String product_name);

    //update cart items2
    @Update
    void updateCartItem2(CartProduct cart);




}
