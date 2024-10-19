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
    private val productList: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // ViewHolder untuk item produk
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditProduct)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteProduct)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "Rp. ${product.price}" // Sesuaikan format jika perlu
            productCategory.text = product.category.name // Konversi enum Category menjadi String

            // Set listener untuk tombol edit dan delete
            btnEdit.setOnClickListener { onEditClick(product) }
            btnDelete.setOnClickListener { onDeleteClick(product) }
        }
    }

    // Menginflate layout untuk setiap item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_product_layout, parent, false) // Ganti dengan layout item yang sesuai
        return ProductViewHolder(view)
    }

    // Mengatur data pada ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.bind(currentProduct) // Panggil fungsi bind untuk setiap produk
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return productList.size
    }
}
