package com.cabral.emaishapay.network.api_helpers

import com.cabral.emaishapay.network.db.entities.MerchantOrder
import com.google.gson.annotations.SerializedName


/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class MerchantOdersResponse(
        @SerializedName("success") val success: String = "0",
        @SerializedName("data") val data: List<MerchantOrder> = emptyList(),
        @SerializedName("message") val message: String,
        val nextPage: Int? = null
)