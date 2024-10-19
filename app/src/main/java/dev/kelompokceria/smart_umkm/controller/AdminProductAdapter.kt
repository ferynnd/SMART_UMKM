package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.model.Product

class AdminProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    // ViewHolder untuk item produk
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName  ) // Pastikan ID ini sesuai dengan layout item
        val productPrice: TextView = itemView.findViewById(R.id.productPrice) // Pastikan ID ini sesuai dengan layout item
        val productRole: TextView = itemView.findViewById(R.id.productCategory)
    }

    // Menginflate layout untuk setiap item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_product_layout, parent, false) // Ganti dengan layout item yang sesuai
        return ProductViewHolder(view)
    }

    // Mengatur data pada ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.productName.text = currentProduct.name
        holder.productPrice.text = currentProduct.price.toString() // Sesuaikan format jika perlu
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return productList.size
    }
}
