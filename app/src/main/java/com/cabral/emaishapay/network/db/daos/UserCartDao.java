package com.cabral.emaishapay.network.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cabral.emaishapay.network.db.entities.UserCart;

import java.util.List;

@Dao
public interface UserCartDao {

    //insert cart product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCartProduct(UserCart userCartProduct);

    //get last  cart id
    @Query("SELECT MAX(cart_id)  FROM UserCart")
    int getLastCartID();

    //insert cart items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCartItem(UserCart userCart);


    //fetch cart items
    @Query("SELECT * FROM UserCart")
    List<UserCart>getCartItems();


    //get cart product
    @Query("SELECT * FROM UserCart WHERE  products_id=:product_id")
    UserCart getCartProduct(int product_id);


    //get cart product 2
    @Query("SELECT * FROM UserCart WHERE products_id =:product_id OR products_name =:product_name")
    UserCart getCartProduct(String product_id, String product_name);


    //get cart items id
    @Query("SELECT products_id  FROM UserCart")
    List<String>getCartItemsIDs();

    //check cart
    @Query("SELECT * FROM UserCart WHERE products_id=:product_id")
    List<UserCart>checkCartHasProductAndMeasure(int product_id);


    //update cart
    @Update
    void updateCart(UserCart cart);

    //update cart item
    @Update
    void updateCartItem(UserCart cart);

    //delete cart item
    @Delete
    void deleteCartItem(UserCart cart);

    //clear cart
    @Query("DELETE FROM UserCart")
    void clearCart();


    //check cart product
    @Query("SELECT * FROM UserCart WHERE products_id=:product_id OR products_name =:product_name")
    List<UserCart> checkCartProduct(int product_id, String product_name);

    //update cart items2
    @Update
    void updateCartItem2(UserCart cart);

    //update product quantity
    @Query("UPDATE UserCart SET product_quantity=:qty WHERE products_id=:id")
    void updateProductQty(String id, String qty);

    //get cart item count
    @Query("SELECT COUNT(*) FROM UserCart")
    int getCartItemCount();

    @Query("SELECT * FROM UserCart WHERE products_id=:product_id")
    LiveData<List<UserCart>> selectCartProduct(String product_id);

}
