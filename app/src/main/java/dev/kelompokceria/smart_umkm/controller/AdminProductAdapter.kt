package dev.kelompokceria.smart_umkm.controller

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardProductLayoutBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // ViewHolder for product item
    inner class ProductViewHolder(var view: CardProductLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {
        init {
            view.btnEdit.setOnClickListener {
                onEditClick(getItem(adapterPosition)) // Handle edit click
            }

            view.btnDel.setOnClickListener {
                onDeleteClick(getItem(adapterPosition)) // Handle delete click
            }
        }
    }

    // Inflate layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = CardProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        val view = holder.view
        view.viewName.text = currentProduct.name
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        view.viewPrice.text = numberFormat.format(currentProduct.price)
        view.viewCate.text = currentProduct.category
        view.viewDesk.text = currentProduct.description

        currentProduct.image?.let {
            Glide.with(view.imageView.context)
                .load(it)
                .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                .into(view.imageView)
        } ?: run {
            view.imageView.setImageResource(R.drawable.picture) // Default image
        }

        view.Expand.setOnClickListener {
            if (view.viewDesk.visibility == View.GONE) {
                view.viewDesk.visibility = View.VISIBLE  // Show
            } else {
                view.viewDesk.visibility = View.GONE  // Hide
            }
        }
    }

    // Filter products by name
    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            currentList // Return all products if query is empty
        } else {
            currentList.filter { product ->
                product.name.contains(query, ignoreCase = true) // Filter by product name
            }
        }
        submitList(filteredList) // Update adapter with the filtered list
    }

     class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id // Compare by unique ID
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem // Compare content for equality
        }
    }


}