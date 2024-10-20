package dev.kelompokceria.smart_umkm.controller

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardDashboardBinding
import dev.kelompokceria.smart_umkm.model.Product
import java.text.NumberFormat
import java.util.Locale

class DashboardProductAdapter(
        private var productList: List<Product>
        ) : RecyclerView.Adapter<DashboardProductAdapter.ProductViewHolder>() {

        class ProductViewHolder( var view: CardDashboardBinding) : RecyclerView.ViewHolder(view.root)

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
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.imageProduct.setImageBitmap(bitmap)
        } ?: run {
            // Default image if no image is provided
            view.imageProduct.setImageResource(R.drawable.picture)
        }
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        view.tvName.text = product.name
        view.tvPrice.text = numberFormat.format(product.price)

        view.btnADD.setOnClickListener {
            view.btnADD.visibility = View.GONE
            view.liner.visibility = View.VISIBLE
            view.tvCount.text = "1" // Mulai dari 1
        }

        view.btnPlus.setOnClickListener {
            val currentQuantity = view.tvCount.text.toString().toInt()
            view.tvCount.text = (currentQuantity + 1).toString()
        }

        view.btnMinn.setOnClickListener {
            val currentQuantity = view.tvCount.text.toString().toInt()
            if (currentQuantity > 1) {
                view.tvCount.text = (currentQuantity - 1).toString()
            } else {
                // Kembali ke tombol Add jika kurang dari 1
                view.btnADD.visibility = View.VISIBLE
                view.liner.visibility = View.GONE
            }
        }


    }


}