package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.EcUserCart;
import com.cabral.emaishapay.network.db.entities.EcUserCartAttributes;
import com.cabral.emaishapay.network.db.entities.ShopUserCart;
import com.cabral.emaishapay.network.db.entities.ShopUserCartAttributes;

import java.util.List;

public class ShopUserCartWithAttributes {
    @Embedded
    public ShopUserCart userCart;
    @Relation(
            parentColumn = "cart_id",
            entityColumn = "cart_table_id"
    )
    List<ShopUserCartAttributes> userCartAttributesList;

}
