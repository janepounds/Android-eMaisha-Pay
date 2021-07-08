package com.cabral.emaishapay.network.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cabral.emaishapay.network.db.entities.EcProduct

@Dao
interface MerchantProductDao {
    @Query("SELECT * FROM  EcProduct ORDER BY product_id DESC")
    fun getProducts(): PagingSource<Int, EcProduct>

    @Query("SELECT EcProduct.* FROM EcProduct JOIN EcProductFts ON (EcProductFts.rowid=EcProduct.id) WHERE EcProductFts MATCH :searchKey ORDER BY EcProduct.product_id DESC")
    fun searchProducts(searchKey : String): PagingSource<Int, EcProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productList: List<EcProduct>)


    @Query("DELETE  FROM EcProduct")
    suspend fun clear()
}