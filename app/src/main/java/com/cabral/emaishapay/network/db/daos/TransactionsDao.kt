package com.cabral.emaishapay.network.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cabral.emaishapay.network.db.entities.MerchantOrder
import com.cabral.emaishapay.network.db.entities.Transactions
import com.cabral.emaishapay.network.db.entities.UserTransactions

@Dao
interface TransactionsDao {
    @Query("SELECT * FROM UserTransactions ORDER BY id DESC")
    fun getTransactionList(): PagingSource<Int, UserTransactions>

    @Query("SELECT Transactions.* FROM Transactions JOIN TransactionsFts ON (TransactionsFts.rowid=Transactions.id) WHERE TransactionsFts MATCH :searchKey ORDER BY Transactions.id DESC")
    fun searchTransactions(searchKey : String): PagingSource<Int, Transactions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactionList: List<Transactions>)


    @Query("DELETE  FROM Transactions")
    suspend fun clearTransactions()

    @Query("SELECT * FROM UserTransactions ORDER BY id DESC")
    fun getUserTransactionList(): PagingSource<Int, UserTransactions>

}