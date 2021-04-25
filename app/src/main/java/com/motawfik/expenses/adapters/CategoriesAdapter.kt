package com.motawfik.expenses.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.motawfik.expenses.databinding.ListItemCategoryBinding
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.CategoryWithGroupedTransactions

class CategoriesAdapter(
    private val clickListener: CategoryListener,
) :
    ListAdapter<CategoryWithGroupedTransactions, CategoriesAdapter.MyViewHolder>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }


    class MyViewHolder private constructor(private val binding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: CategoryWithGroupedTransactions,
            clickListener: CategoryListener,
        ) {
            // if the category is null, then bind it with `Uncategorized` else bind it with the category
            binding.category = item.category ?: Category(0, 0, "Uncategorized")
            binding.clickListener = clickListener
            binding.groupedTransaction = item.groupedTransaction
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCategoryBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }
}


class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryWithGroupedTransactions>() {
    override fun areItemsTheSame(oldItem: CategoryWithGroupedTransactions, newItem: CategoryWithGroupedTransactions): Boolean {
        return oldItem.category?.ID == newItem.category?.ID
    }

    override fun areContentsTheSame(oldItem: CategoryWithGroupedTransactions, newItem: CategoryWithGroupedTransactions): Boolean {
        return oldItem == newItem
    }

}

class CategoryListener(
    val clickListener: (category: Category) -> Unit,
    val deleteListener: (categoryID: Int) -> Unit
) {
    fun onClick(category: Category) {
        clickListener(category)
    }
    fun onDelete(categoryID: Int) {
        deleteListener(categoryID)
    }
}