package dev.kelompokceria.smart_umkm.controller

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardCategoryLayoutBinding
import dev.kelompokceria.smart_umkm.databinding.CardDashboardBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale

class DashboardProductAdapter(
    private val onItemClicked: (Product) -> Unit,
    private val onSelectionChanged: (Boolean) -> Unit
//    private val setBackgroundColor: (Int) -> Unit

) : ListAdapter<Any, RecyclerView.ViewHolder>(DashboardDiffCallback()) {


    // Simpan daftar produk yang dipilih
    private val selectedProducts: MutableSet<Product> = mutableSetOf()

    // ViewHolder untuk item produk
    inner class ProductViewHolder( val binding: CardDashboardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, isSelected: Boolean) {
            product.image.let {
                Glide.with(binding.imageProduct.context)
                    .load(it)
                    .placeholder(R.drawable.picture)
                    .into(binding.imageProduct)
            } ?: run {
                binding.imageProduct.setImageResource(R.drawable.picture)
            }

            val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvName.text = product.name
            binding.tvCategory.text = product.category
            binding.tvPrice.text = numberFormat.format(product.price)

        }
    }

    // ViewHolder untuk header kategori
    inner class HeaderViewHolder(val binding : CardCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    enum class TYPE_VIEW { TYPE_HEADER , TYPE_CONTENT }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Product -> TYPE_VIEW.TYPE_CONTENT.ordinal
            is String -> TYPE_VIEW.TYPE_HEADER.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_VIEW.TYPE_CONTENT.ordinal -> {
                val binding = CardDashboardBinding.inflate(LayoutInflater.from(parent.context), parent,false)
                return ProductViewHolder(binding)
            }
            TYPE_VIEW.TYPE_HEADER.ordinal -> {
                val binding = CardCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return HeaderViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")

        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder) {
            is ProductViewHolder -> {
                val product = getItem(position) as Product

                holder.bind(product,true)

                // Klik item untuk mengubah seleksi
                holder.itemView.setOnClickListener {
                    toggleSelection(product)
                    onItemClicked(product)
                    notifyItemChanged(position) // Perbarui tampilan item
                }

                val isSelected = selectedProducts.contains(product) // Periksa apakah produk dipilih
                val view = holder.itemView as MaterialCardView

                view.setCardBackgroundColor(
                    if (isSelected) {
                        // Warna saat dipilih
                        ContextCompat.getColor(view.context, R.color.gray)
                    } else {
                        // Warna saat tidak dipilih
                        ContextCompat.getColor(view.context, R.color.white)
                    }
                )

            }
           is HeaderViewHolder -> {
                holder.binding.textViewHeader.text = (item as String)
            }
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
        return selectedProducts.map { it.id!! }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when (oldItem) {
                is Product -> {
                    if(newItem is Product) {
                        (oldItem.id) == (newItem.id)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Product) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Product -> {
                    if (newItem is Product) {
                        (oldItem) == (newItem)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Product) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            }
        }
    }
}
