package com.motawfik.expenses.transactions.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.motawfik.expenses.R
import com.motawfik.expenses.databinding.TransactionsLoadStateFooterViewItemBinding

class TransactionsLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<TransactionsLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: TransactionsLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): TransactionsLoadStateViewHolder {
        return TransactionsLoadStateViewHolder.create(parent, retry)
    }
}

class TransactionsLoadStateViewHolder(
    private val binding: TransactionsLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): TransactionsLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.transactions_load_state_footer_view_item, parent, false)
            val binding = TransactionsLoadStateFooterViewItemBinding.bind(view)
            return TransactionsLoadStateViewHolder(binding, retry)
        }
    }
}