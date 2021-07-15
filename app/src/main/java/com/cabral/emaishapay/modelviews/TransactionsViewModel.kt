package com.cabral.emaishapay.modelviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cabral.emaishapay.network.db.entities.Transactions
import com.cabral.emaishapay.network.pagingdata.MerchantRepository
import kotlinx.coroutines.flow.Flow

class TransactionsViewModel(private val repository: MerchantRepository) : ViewModel()  {
    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<Transactions>>? = null

    fun searchTransactions(queryString: String): Flow<PagingData<Transactions>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Transactions>> = repository.getTransactionsResultStream(queryString)
                .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }


    fun searchSettlements(queryString: String): Flow<PagingData<Transactions>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Transactions>> = repository.getSettlementsResultStream(queryString)
                .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }


    class ViewModelFactory(private val repository: MerchantRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}