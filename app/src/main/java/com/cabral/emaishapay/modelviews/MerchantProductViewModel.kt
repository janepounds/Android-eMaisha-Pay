/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cabral.emaishapay.modelviews

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cabral.emaishapay.AppExecutors
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient
import com.cabral.emaishapay.network.db.entities.EcManufacturer
import com.cabral.emaishapay.network.db.entities.EcProduct
import com.cabral.emaishapay.network.db.entities.EcProductCategory
import com.cabral.emaishapay.network.pagingdata.MerchantRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MerchantProductViewModel(private val repository: MerchantRepository) : ViewModel() {
    private var currentQueryValue: String? = null
    private val manufacturers = MutableLiveData<List<EcManufacturer>>()
    private var currentSearchResult: Flow<PagingData<EcProduct>>? = null
    val TAG : String = "MerchantProductViewModel"

    init {
        requestOnlineManufacturers()
    }

    fun searchProducts(queryString: String): Flow<PagingData<EcProduct>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<EcProduct>> = repository.getMerchantProductsResultStream(queryString)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun deleteProduct(product: EcProduct, wallet_id : String) {
        val call1 = BuyInputsAPIClient
                .getInstance()
                .deleteMerchantProduct(product.product_id, wallet_id)
        call1.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    AppExecutors.getInstance().diskIO().execute { repository.deleteProductStock(product) }
                    //Log.d("Categories", String.valueOf(categories));
                } else {
                    Log.d("Failed", "Manufacturers Fetch failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                t.printStackTrace()
                Log.d("Failed", "Manufacturers Fetch failed")
            }
        })
    }


    private fun requestOnlineManufacturers() {
        val access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN
        val call1 = BuyInputsAPIClient
                .getInstance()
                .getManufacturers(access_token)
        call1.enqueue(object : Callback<ManufacturersResponse> {
            override fun onResponse(call: Call<ManufacturersResponse>, response: Response<ManufacturersResponse>) {
                if (response.isSuccessful && response.body()!=null ) {
                    Log.d( "Response", "onResponse: " + response.body()!!.toString())
                    manufacturers.setValue(response.body()!!.manufacturers)
                    //Log.d("Categories", String.valueOf(categories));
                } else {
                    Log.d("Failed", "Manufacturers Fetch failed")
                }
            }

            override fun onFailure(call: Call<ManufacturersResponse>, t: Throwable) {
                t.printStackTrace()
                Log.d("Failed", "Manufacturers Fetch failed")
            }
        })
    }

    fun getOnlineManufacturers(): MutableLiveData<List<EcManufacturer>> {
         return  this.manufacturers
    }

    fun updateProductStock(product_id: String?, product_buy_price: String?, product_sell_price: String?, product_supplier: String?, product_stock: Int, new_manufacturer_name: String?, new_category_name: String?, new_product_name: String?, product_code: String?, product_image: String?, product_weight_unit: String?, product_weight: String?): Int? {
        return repository.updateProductStock(product_id, product_code, new_category_name, "", product_buy_price, product_sell_price, product_supplier, product_image, product_stock.toString() + "", product_weight_unit, product_weight, new_manufacturer_name)
    }

    fun addManufacturer(manufacturer: EcManufacturer?): Long {
        return repository.addManufacturers(manufacturer)
    }


    fun getOfflineManufacturers(): ArrayList<HashMap<String?, String?>>? {
        return repository.getOfflineManufacturers()
    }

    //get offline product categories
    fun getOfflineProductCategories(): ArrayList<HashMap<String?, String?>>? {
        return repository.getOfflineProductCategories()
    }

    fun getOfflineProductNames(): ArrayList<HashMap<String?, String?>>? {
        return repository.getOfflineProductNames()
    }

    fun addProduct(product: EcProduct): Long {
        product.sync_status = "0"
        return repository.addProduct(product)
    }

    //get offline product categories
    fun getProductSupplier(): ArrayList<HashMap<String?, String?>>? {
        return repository.getProductSupplier()
    }

    fun getWeightUnit(): ArrayList<HashMap<String?, String?>>? {
        return repository.getWeightUnit()
    }

    fun addProductCategory(category: EcProductCategory?): Long {
        return repository.addProductCategory(category)
    }

    fun updateProductSyncStatus(product_id: String?, status: String?): Int? {
        return repository.updateProductSyncStatus(product_id, status)
    }

    fun restockProductStock(product_id: String?, product_stock: Int): Long {
        return repository.restockProductStock(product_id, product_stock)
    }

    class ViewModelFactory(private val repository: MerchantRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MerchantProductViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MerchantProductViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}
