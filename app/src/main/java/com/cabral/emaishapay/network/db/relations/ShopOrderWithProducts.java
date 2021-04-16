package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.network.db.entities.ShopUserCart;
import com.cabral.emaishapay.network.db.entities.ShopUserCartAttributes;

import java.io.Serializable;
import java.util.List;

public class ShopOrderWithProducts implements Serializable {
    @Embedded
    public ShopOrder shopOrder;
    @Relation(
            parentColumn = "order_id",
            entityColumn = "product_order_id"
    )
    List<ShopOrderProducts> orderProducts;

    public ShopOrder getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(ShopOrder shopOrder) {
        this.shopOrder = shopOrder;
    }

    public List<ShopOrderProducts> getOrderProducts() {
        return this.orderProducts;
    }

    public void setOrderProducts(List<ShopOrderProducts> orderProducts) {
        this.orderProducts = orderProducts;
    }
}
