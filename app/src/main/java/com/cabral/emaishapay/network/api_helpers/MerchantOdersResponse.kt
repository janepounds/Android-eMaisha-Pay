package com.cabral.emaishapay.network.api_helpers

import com.cabral.emaishapay.models.order_model.OrderDetails
import com.cabral.emaishapay.network.db.entities.ShopOrder
import com.google.gson.annotations.SerializedName


/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class MerchantOdersResponse(
        @SerializedName("success") val success: String = "0",
        @SerializedName("data") val data: List<ShopOrder> = emptyList(),
        @SerializedName("message") val message: String,
        val nextPage: Int? = null
)