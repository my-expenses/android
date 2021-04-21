package com.motawfik.expenses.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.motawfik.expenses.databinding.ListItemTransactionBinding
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction

class TransactionsAdapter(
    private val clickListener: TransactionListener,
    private val categories: LiveData<List<Category>>,
) : PagingDataAdapter<Transaction, TransactionsAdapter.MyViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener, categories)
    }

    class MyViewHolder private constructor(private val binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Transaction?,
            clickListener: TransactionListener,
            categories: LiveData<List<Category>>
        ) {
            binding.transaction = item
            binding.clickListener = clickListener
            binding.category = categories.value?.find { it.ID == item?.categoryID }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTransactionBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

}


class TransactionListener(
    val clickListener: (transaction: Transaction) -> Unit,
    val deleteListener: (transactionID: Int) -> Unit) {
    fun onClick(transaction: Transaction) {
        clickListener(transaction)
    }

    fun onDelete(transactionID: Int) {
        deleteListener(transactionID)
    }
}