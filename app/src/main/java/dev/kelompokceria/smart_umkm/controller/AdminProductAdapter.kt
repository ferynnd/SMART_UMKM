package dev.kelompokceria.smart_umkm.controller

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.databinding.CardCategoryLayoutBinding
import dev.kelompokceria.smart_umkm.databinding.CardProductLayoutBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.ProductCategory
import java.text.NumberFormat
import java.util.Locale


class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(ProductDiffCallback()) {


    // ViewHolder for product item
    inner class ProductViewHolder(var view: CardProductLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {

         fun bind(product: Product) {
            view.viewName.text = product.name
            view.viewPrice.text = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(product.price)
            view.viewCate.text = product.category
            view.viewDesk.text = product.description

            // Load image using Glide
            Glide.with(view.imageView.context)
                .load(product.image)
                .placeholder(R.drawable.picture)
                .into(view.imageView)

             getViewModel(view.root.context).networkStatus.observe(view.root.context as LifecycleOwner) { isConnected ->
                    if (isConnected) {
                        view.btnEdit.setOnClickListener { onEditClick(product) }
                        view.btnDel.setOnClickListener { onDeleteClick(product)  }
                    } else {
                        Toast.makeText(view.root.context, "Network is not connected", Toast.LENGTH_SHORT).show()
                    }
                }

            view.Expand.setOnClickListener {
                view.viewDesk.visibility = if (view.viewDesk.visibility == View.GONE) View.VISIBLE else View.GONE
            }
         }
    }

    inner class CategoryViewHolder(var view: CardCategoryLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root)

    // Inflate layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_VIEW.CONTENT.ordinal -> {
                val binding = CardProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ProductViewHolder(binding)
            }
            TYPE_VIEW.HEADER.ordinal -> {
                val binding = CardCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CategoryViewHolder(binding)
            }
             else -> throw IllegalArgumentException("Unknown viewType: $viewType")

        }
    }

    enum class TYPE_VIEW { HEADER , CONTENT }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Product -> TYPE_VIEW.CONTENT.ordinal
              is String -> TYPE_VIEW.HEADER.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
               when(holder) {
                   is ProductViewHolder -> {
                        val product = item as Product
                       holder.bind(product)
                   }
                   is CategoryViewHolder -> {
                       holder.view.textViewHeader.text = (item as String)
                   }
               }

    }

    private fun getViewModel(context: Context): NetworkStatusViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner)[NetworkStatusViewModel::class.java]
    }



     class ProductDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Product -> {
                    if (newItem is Product) {
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
            } // Compare by unique ID
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
            } // Compare by unique ID
        }
    }


}