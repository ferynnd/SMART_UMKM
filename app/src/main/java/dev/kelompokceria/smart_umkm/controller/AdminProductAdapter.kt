package dev.kelompokceria.smart_umkm.controller

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardProductLayoutBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productList: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder untuk item produk
    inner class ProductViewHolder(var view: CardProductLayoutBinding) : RecyclerView.ViewHolder(view.root) {

        init {
            view.btnEdit.setOnClickListener {
                onEditClick(productList[adapterPosition]) // Menangani klik edit
            }

            view.btnDel.setOnClickListener {
                onDeleteClick(productList[adapterPosition]) // Menangani klik delete
            }
        }
    }

    // Menginflate layout untuk setiap item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = CardProductLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    // Mengatur data pada ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        val view = holder.view
        view.viewName.text = currentProduct.name
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        view.viewPrice.text = numberFormat.format(currentProduct.price)
        view.viewCate.text = currentProduct.category.name
        view.viewDesk.text = currentProduct.description

         currentProduct.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.imageView.setImageBitmap(bitmap)
        } ?: run {
            // Default image if no image is provided
            view.imageView.setImageResource(R.drawable.picture)
        }

        view.Expand.setOnClickListener {
                if (view.viewDesk.visibility == View.GONE) {
                    view.viewDesk.visibility = View.VISIBLE  // Tampilkan
                } else {
                    view.viewDesk.visibility = View.GONE  // Sembunyikan
                }
        }
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return productList.size
    }

    // Fungsi untuk memperbarui produk di adapter
    fun updateProducts(products: List<Product>) {
        this.productList = products
        notifyDataSetChanged() // Memperbarui tampilan
    }
}
