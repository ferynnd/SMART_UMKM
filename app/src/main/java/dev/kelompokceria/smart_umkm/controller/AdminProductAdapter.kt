package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.model.Product

class ProductAdapter(
    private var productList: MutableList<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditProduct)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteProduct)

        init {
            // Cek apakah adapterPosition valid sebelum melakukan tindakan
            btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(productList[position])
                }
            }
            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(productList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_product_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.productName.text = currentProduct.name
        holder.productPrice.text = currentProduct.price.toString()
        holder.productCategory.text = currentProduct.category.name // Menangani enum dengan nama
    }

    override fun getItemCount(): Int = productList.size

    // Metode untuk memperbarui produk dengan lebih efisien
    fun updateProducts(products: List<Product>) {
        productList.clear() // Kosongkan daftar lama
        productList.addAll(products) // Tambahkan daftar produk baru
        notifyDataSetChanged() // Notifikasi bahwa data telah berubah
    }
}
