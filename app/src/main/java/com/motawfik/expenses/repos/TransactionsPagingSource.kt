package com.motawfik.expenses.repos

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.network.TransactionsApiService
import java.text.SimpleDateFormat
import java.util.*

class TransactionsPagingSource(
    private val backend: TransactionsApiService,
    private val selectedMonth: Date,
) : PagingSource<Int, Transaction>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        try {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .format(selectedMonth)

            // Start refresh at page 0 if undefined.
            val page = params.key ?: 1
            val response = backend.getTransactions(
                page, PAGE_SIZE, listOf("created_at"), listOf("true"), formattedDate
            )
            return LoadResult.Page(
                data = response.transactions,
                prevKey = null, // Only paging forward.
                nextKey = if (response.transactions.size < PAGE_SIZE) null else page + 1
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val PAGE_SIZE = 4
    }
}