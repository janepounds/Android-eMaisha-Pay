package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.network.db.entities.EcUserCart;

import java.util.List;

@Dao
public interface EcUserCartDao {

    //get last  cart id
    @Query("SELECT MAX(cart_id)  FROM EcUserCart")
    int getLastCartID();

    //insert cart items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartItem(EcUserCart userCart);


    //fetch cart items
    @Query("SELECT * FROM EcUserCart")
    List<EcUserCart>getCartItems();


    //get cart product
    @Query("SELECT * FROM EcUserCart WHERE  products_id=:product_id")
    EcUserCart getCartProduct(int product_id);


    //get cart product 2
    @Query("SELECT * FROM EcUserCart WHERE products_id =:product_id OR products_name =:product_name")
    EcUserCart getCartProduct2(String product_id, String product_name);


    //get cart items id
    @Query("SELECT products_id  FROM EcUserCart")
    List<Integer>getCartItemsIDs();

    //check cart
    @Query("SELECT * FROM EcUserCart WHERE products_id=:product_id")
    List<EcUserCart>checkCartHasProductAndMeasure(int product_id);


    //update cart
    @Update
    void updateCart(EcUserCart cart);

    //update cart item
    @Update
    void updateCartItem(EcUserCart cart);

    //delete cart item
    @Delete
    void deleteCartItem(int cart_id);

    //clear cart
    @Delete
    void clearCart();


    //check cart product
    @Query("SELECT * FROM EcUserCart WHERE products_id=:product_id OR products_name =:product_name")
    List<EcUserCart> checkCartProduct(int product_id, String product_name);

    //update cart items2
    @Update
    void updateCartItem2(EcUserCart cart);




}
