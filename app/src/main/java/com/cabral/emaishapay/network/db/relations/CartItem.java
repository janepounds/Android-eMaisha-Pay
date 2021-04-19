package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.UserCartAttributes;
import com.cabral.emaishapay.network.db.entities.UserCart;

import java.util.List;

public class CartItem {
    @Embedded
    public UserCart userCart;
    @Relation(
            parentColumn = "cart_id",
            entityColumn = "cart_table_id"
    )
    List<UserCartAttributes> userCartAttributesList;

    public CartItem(UserCart userCart, List<UserCartAttributes> userCartAttributesList) {
        this.userCart = userCart;
        this.userCartAttributesList = userCartAttributesList;
    }

    public UserCart getUserCart() {
        return userCart;
    }

    public List<UserCartAttributes> getUserCartAttributesList() {
        return userCartAttributesList;
    }
}
