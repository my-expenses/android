package com.motawfik.expenses.transactions.repos

import androidx.paging.*
import androidx.room.withTransaction
import com.motawfik.expenses.transactions.models.Transaction
import com.motawfik.expenses.network.TransactionsApiService
import com.motawfik.expenses.repos.TransactionsDatabase
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalPagingApi
class TransactionsRemoteMediator(
    private val backend: TransactionsApiService,
    private val database: TransactionsDatabase,
    private val selectedMonth: Date,
) : RemoteMediator<Int, Transaction>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Transaction>
    ): MediatorResult {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .format(selectedMonth)

        return try {
            // The network load method takes an optional page parameter
            // For REFRESH let the page number = 1 to get the first page
            currentPage = when (loadType) {
                LoadType.REFRESH -> 1
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    currentPage + 1 // increment current page by 1
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            val response = backend.getTransactions(
                currentPage, PAGE_SIZE, listOf("date"), listOf("true"), formattedDate
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.transactionDao().deleteAll()
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                database.transactionDao().insertAll(response.transactions)
            }

            return MediatorResult.Success(
                endOfPaginationReached = response.numberOfRecords < PAGE_SIZE
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        private var currentPage = 1
    }
}