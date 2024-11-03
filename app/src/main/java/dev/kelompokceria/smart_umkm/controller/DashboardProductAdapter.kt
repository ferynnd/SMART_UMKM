package dev.kelompokceria.smart_umkm.controller

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardDashboardBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale

class DashboardProductAdapter(
        private var productList: List<Product>,
        private val quantityChangeListener: QuantityChangeListener
        ) : RecyclerView.Adapter<DashboardProductAdapter.ProductViewHolder>() {

        class ProductViewHolder( var view: CardDashboardBinding) : RecyclerView.ViewHolder(view.root)


    interface QuantityChangeListener {
        fun onQuantityChanged(price: Int, quantityChange: Int)
        fun removeFromSelectedProducts(productName: String, price: Int)
        fun addToSelectedProducts(productName: String, quantity: Int, price: Int)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = CardDashboardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = productList.get(position)
        val view = holder.view

        product.image?.let {
            Glide.with(view.imageProduct.context)
                .load(it)
                .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                .into(view.imageProduct)
        } ?: run {
            view.imageProduct.setImageResource(R.drawable.picture) // Default image
        }
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        view.tvName.text = product.name
        view.tvPrice.text = numberFormat.format(product.price)

        view.btnADD.setOnClickListener {
            view.btnADD.visibility = View.GONE
            view.liner.visibility = View.VISIBLE
            view.tvCount.text = "1"

            // Tambahkan produk ke dalam daftar
            quantityChangeListener.addToSelectedProducts(product.name, 1, product.price.toInt())
        }

        view.btnPlus.setOnClickListener {
            val currentQuantity = view.tvCount.text.toString().toInt()
            view.tvCount.text = (currentQuantity + 1).toString()
            quantityChangeListener.onQuantityChanged(product.price.toInt(), 1)
            quantityChangeListener.addToSelectedProducts(product.name, 1, product.price.toInt())
        }

        view.btnMinn.setOnClickListener {
            val currentQuantity = view.tvCount.text.toString().toInt()
            if (currentQuantity > 1) {
                view.tvCount.text = (currentQuantity - 1).toString()
                quantityChangeListener.onQuantityChanged(-product.price.toInt(), -1)
                quantityChangeListener.removeFromSelectedProducts(product.name, product.price.toInt())
            } else {
                // Jika jumlah menjadi nol, kembali ke tombol Add dan hapus produk dari daftar yang dipilih
                view.btnADD.visibility = View.VISIBLE
                view.liner.visibility = View.GONE
                quantityChangeListener.removeFromSelectedProducts(product.name, product.price.toInt())
            }
        }




    }


}