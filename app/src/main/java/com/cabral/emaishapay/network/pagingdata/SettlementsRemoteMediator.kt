package com.cabral.emaishapay.network.pagingdata

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.cabral.emaishapay.network.api_helpers.EmaishaShopAPIService
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.db.entities.MerchantOrder
import com.cabral.emaishapay.network.db.entities.RemoteKeys
import com.cabral.emaishapay.network.db.entities.Transactions
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class SettlementsRemoteMediator(
        private val wallet_id: Int,
        private val service: EmaishaShopAPIService,
        private val emaishapayDb: EmaishapayDb
) : RemoteMediator<Int, Transactions>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }



    override suspend fun load(loadType: LoadType, state: PagingState<Int, Transactions>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        //val apiQuery = query + IN_QUALIFIER

        try {
            val apiResponse = service.getpagedSettlements(wallet_id, page, state.config.pageSize)

            val settlements = apiResponse
            val endOfPaginationReached = settlements.isEmpty()
            emaishapayDb.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    emaishapayDb.remoteKeysDao()?.clearOrderRemoteKeys()
                    emaishapayDb.transactionsDao()?.clearTransactions()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1)
                val nextKey = if (endOfPaginationReached) null else page.plus(1)
                val keys = settlements.map {
                    RemoteKeys(id = it.id.toString(), prevKey = prevKey, nextKey = nextKey)
                }
                emaishapayDb.remoteKeysDao()?.insertAll(keys)
                emaishapayDb.transactionsDao()?.insertAll(settlements)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Transactions>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { order ->
                    // Get the remote keys of the last item retrieved
                    emaishapayDb.remoteKeysDao()?.remoteKeysTransactionsId(order.id.toString())
                }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Transactions>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { repo ->
                    // Get the remote keys of the first items retrieved
                    emaishapayDb.remoteKeysDao()?.remoteKeysTransactionsId(repo.id.toString())
                }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
            state: PagingState<Int, Transactions>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                emaishapayDb.remoteKeysDao()?.remoteKeysTransactionsId(repoId.toString())
            }
        }
    }
}


}