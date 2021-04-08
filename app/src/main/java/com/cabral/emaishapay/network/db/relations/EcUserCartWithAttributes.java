package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.EcUserCart;
import com.cabral.emaishapay.network.db.entities.EcUserCartAttributes;

import java.util.List;

public class EcUserCartWithAttributes {
    @Embedded
    public EcUserCart userCart;
    @Relation(
            parentColumn = "cart_id",
            entityColumn = "cart_table_id"
    )
    List<EcUserCartAttributes> userCartAttributesList;

}
