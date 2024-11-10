package dev.kelompokceria.smart_umkm.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.CardCategoryBinding
import dev.kelompokceria.smart_umkm.model.ProductCategory

class ProductCategoryAdapter(
    private val onDeleteClick: (ProductCategory) -> Unit
    )    :
    ListAdapter<ProductCategory, ProductCategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: CardCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ProductCategory) {
            binding.textViewName.text = category.name
            binding.buttonDelete.setOnClickListener {
                onDeleteClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductCategory>() {
        override fun areItemsTheSame(oldItem: ProductCategory, newItem: ProductCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductCategory, newItem: ProductCategory): Boolean {
            return oldItem == newItem
        }
    }
}
