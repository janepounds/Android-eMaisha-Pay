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

package com.cabral.emaishapay.network.pagingdata

import androidx.paging.*
import com.cabral.emaishapay.network.api_helpers.EmaishaShopAPIService
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.db.daos.*
import com.cabral.emaishapay.network.db.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Repository class that works with local and remote data sources.
 */
class MerchantRepository(private val wallet_id: Int, private val database: EmaishapayDb) {

    /**
     * fetch merchant orders, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    private val mEcProductsDao: EcProductsDao? = database.ecProductsDao()
    private val mEcManufacturerDao: EcManufacturerDao? = database.ecManufacturerDao()
    private val mEcProductCategoryDao: EcProductCategoryDao? = database.ecProductCategoryDao()
    private val mEcSupplierDao: EcSupplierDao? = database.supplierDao()
    private val mEcProductWeightDao: EcProductWeightDao? = database.ecProductWeightDao()

    fun getMerchantOrdersResultStream(query: String): Flow<PagingData<MerchantOrder>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.merchantOrderDao()?.getOrderList() else database.merchantOrderDao()?.searchOders(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = MerchantOrderRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, MerchantOrder>
        ).flow
    }
    fun getMerchantProductsResultStream(query: String): Flow<PagingData<EcProduct>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.merchantProductDao()?.getProducts() else database.merchantProductDao()?.searchProducts(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = MerchantProductRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, EcProduct>
        ).flow
    }

    fun getTransactionsResultStream(query: String): Flow<PagingData<Transactions>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.transactionsDao()?.getTransactionList() else database.transactionsDao()?.searchTransactions(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = TransactionsRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, Transactions>
        ).flow
    }
    fun getSettlementsResultStream(query: String): Flow<PagingData<Transactions>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.transactionsDao()?.getTransactionList() else database.transactionsDao()?.searchTransactions(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = SettlementsRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, Transactions>
        ).flow
    }

    fun deleteProductStock(product: EcProduct?) {
        mEcProductsDao?.deleteProduct(product)
    }

    fun updateProductStock(product_id: String?, product_code: String?, product_category: String?, product_description: String?, product_buy_price: String?, product_sell_price: String?, product_supplier: String?, product_image: String?, product_stock: String?, product_weight_unit: String?, product_weight: String?, manufacturer: String?): Int? {
        return mEcProductsDao?.updateProductStock(product_id, product_code, product_category, product_description, product_buy_price, product_sell_price, product_supplier, product_image, product_stock, product_weight_unit, product_weight, manufacturer)
    }

    fun updateProductSyncStatus(product_id: String?, status: String?): Int? {
        return mEcProductsDao?.updateProductSyncStatus(product_id, status)
    }
    fun getOfflineManufacturers(): ArrayList<HashMap<String?, String?>>? {
        val manufacturers = ArrayList<HashMap<String?, String?>>()
        if (mEcManufacturerDao != null) {
            for (manufacturer in mEcManufacturerDao.getOfflineManufacturers()) {
                val map: HashMap<String?, String?> = HashMap()
                map["manufacturer"] = manufacturer.manufacturer_name
                manufacturers.add(map)
            }
        }
        return manufacturers
    }

    fun getOfflineProductCategories(): ArrayList<HashMap<String?, String?>>? {
        val categories = ArrayList<HashMap<String?, String?>>()
        if (mEcProductCategoryDao != null) {
            for (category in mEcProductCategoryDao.getOfflineProductCategories()) {
                val map: HashMap<String?, String?> = HashMap()
                map["category_name"] = category.category_name
                categories.add(map)
            }
        }
        return categories
    }


    //get offline product names
    fun getOfflineProductNames(): ArrayList<HashMap<String?, String?>>? {
        val productnames = ArrayList<HashMap<String?, String?>>()
        for ((_, _, product_name) in mEcProductsDao!!.offlineProductNames) {
            val map: HashMap<String?, String?> = HashMap()
            map["product_name"] = product_name
            productnames.add(map)
        }
        return productnames
    }

    //**********ADD PRODUCT NAME *******************//
    fun addProduct(product: EcProduct?): Long {
        return mEcProductsDao!!.addProduct(product)
    }

    //**********ADD PRODUCT CATEGORY *******************//
    fun addProductCategory(productCategory: EcProductCategory?): Long {
        return mEcProductCategoryDao!!.addProductCategory(productCategory)
    }

    //**********ADD MANUFACTURERS *******************//
    fun addManufacturers(manufacturer: EcManufacturer?) : Long {
        return mEcManufacturerDao!!.addManufacturers(manufacturer)
    }


    //get product weight
    fun getWeightUnit(): ArrayList<HashMap<String?, String?>>? {
        val productWeights = ArrayList<HashMap<String?, String?>>()
        if (mEcProductWeightDao != null) {
            for (productWeight in mEcProductWeightDao.getWeightUnit()) {
                val map: HashMap<String?, String?> = HashMap()
                map["weight_unit"] = productWeight.weight_unit
                productWeights.add(map)
            }
        }
        return productWeights
    }


    //get product supplier
    fun getProductSupplier(): ArrayList<HashMap<String?, String?>>? {
        val suppliers = ArrayList<HashMap<String?, String?>>()
        if (mEcSupplierDao != null) {
            for (supplier in mEcSupplierDao.getProductSupplier()) {
                val map: HashMap<String?, String?> = HashMap()
                map["suppliers_name"] = supplier.suppliers_name
                suppliers.add(map)
            }
        }
        return suppliers
    }

    fun restockProductStock(id: String?, product_stock: Int): Long {
        return mEcProductsDao!!.restockProductStock(id, product_stock).toLong()
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20

    }
}
