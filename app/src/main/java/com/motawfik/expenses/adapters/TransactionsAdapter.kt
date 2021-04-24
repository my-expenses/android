package com.motawfik.expenses.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.motawfik.expenses.databinding.ListItemTransactionBinding
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.Transaction
import com.motawfik.expenses.models.TransactionWithCategory

class TransactionsAdapter(
    private val clickListener: TransactionListener,
) : PagingDataAdapter<TransactionWithCategory, TransactionsAdapter.MyViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class MyViewHolder private constructor(private val binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: TransactionWithCategory?,
            clickListener: TransactionListener,
        ) {
            binding.transaction = item?.transaction
            binding.clickListener = clickListener
            binding.category = item?.category
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

class PostDiffCallback : DiffUtil.ItemCallback<TransactionWithCategory>() {
    override fun areItemsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
        return oldItem.transaction.ID == newItem.transaction.ID
    }

    override fun areContentsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
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