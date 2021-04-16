package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.UserCart;
import com.cabral.emaishapay.network.db.entities.UserCartAttributes;

import java.util.List;

public class UserCartWithAttributes {
    @Embedded
    public UserCart userCart;
    @Relation(
            parentColumn = "cart_id",
            entityColumn = "cart_table_id"
    )
    List<UserCartAttributes> userCartAttributesList;

}
