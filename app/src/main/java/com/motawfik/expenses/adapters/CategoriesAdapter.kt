package com.motawfik.expenses.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.motawfik.expenses.databinding.ListItemCategoryBinding
import com.motawfik.expenses.models.Category
import com.motawfik.expenses.models.GroupedTransaction

class CategoriesAdapter(
    private val clickListener: CategoryListener,
    private val groupedTransaction: Pair<List<Category>?, List<GroupedTransaction>?>
) :
    ListAdapter<Category, CategoriesAdapter.MyViewHolder>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("GROUPED_T", groupedTransaction.toString())
        holder.bind(item, clickListener, groupedTransaction.second)
    }


    class MyViewHolder private constructor(private val binding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Category,
            clickListener: CategoryListener,
            groupedTransaction: List<GroupedTransaction>?,
        ) {
            binding.category = item
            binding.clickListener = clickListener
            binding.groupedTransaction = groupedTransaction?.find {
                it.categoryID == item.ID
            }
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


class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}

class CategoryListener(
    val deleteListener: (categoryID: Int) -> Unit
) {
    fun onDelete(categoryID: Int) {
        deleteListener(categoryID)
    }
}