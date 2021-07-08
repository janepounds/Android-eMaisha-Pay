package com.cabral.emaishapay.network.db.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cabral.emaishapay.network.db.entities.MerchantOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;

import java.io.Serializable;
import java.util.List;

public class ShopOrderWithProducts implements Serializable {
    @Embedded
    public MerchantOrder shopOrder;
    @Relation(
            parentColumn = "order_id",
            entityColumn = "product_order_id"
    )
    List<ShopOrderProducts> orderProducts;

    public MerchantOrder getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(MerchantOrder shopOrder) {
        this.shopOrder = shopOrder;
    }

    public List<ShopOrderProducts> getOrderProducts() {
        return this.orderProducts;
    }

    public void setOrderProducts(List<ShopOrderProducts> orderProducts) {
        this.orderProducts = orderProducts;
    }
}
