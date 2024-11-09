package dev.kelompokceria.smart_umkm.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardDashboardBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale


class DashboardProductAdapter(
    private val onItemClicked: (Product) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
) : ListAdapter<Product, DashboardProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // Simpan daftar produk yang dipilih
    private val selectedProducts: MutableSet<Product> = mutableSetOf()

    class ProductViewHolder(private val binding: CardDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, isSelected: Boolean) {
            product.image?.let {
                Glide.with(binding.imageProduct.context)
                    .load(it)
                    .placeholder(R.drawable.picture)
                    .into(binding.imageProduct)
            } ?: run {
                binding.imageProduct.setImageResource(R.drawable.picture)
            }

            val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvName.text = product.name
            binding.tvCategory.text = product.category.toString()
            binding.tvPrice.text = numberFormat.format(product.price)

            itemView.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.WHITE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = CardDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val isSelected = selectedProducts.contains(product)
        holder.bind(product, isSelected)

        holder.itemView.setOnClickListener {
            toggleSelection(product)
            onItemClicked(product)
        }
    }

    private fun toggleSelection(product: Product) {
        if (selectedProducts.contains(product)) {
            selectedProducts.remove(product)
        } else {
            selectedProducts.add(product)
        }
        onSelectionChanged(selectedProducts.isNotEmpty())
        notifyDataSetChanged()
    }

    // Perbarui seleksi dari ViewModel
    fun updateSelections(selections: Set<Product>) {
        selectedProducts.clear()
        selectedProducts.addAll(selections)
        onSelectionChanged(selectedProducts.isNotEmpty())
        notifyDataSetChanged()
    }

    // Metode untuk mendapatkan ID produk yang dipilih
    fun getSelectedProductIds(): List<Int> {
        return selectedProducts.map { it.id!!}
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
