package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.ItemCartBinding

class TransactionProductAdapter ( private val productList: List<Pair<String, Pair<Int, Int>>>
) : RecyclerView.Adapter<TransactionProductAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {

        val view = holder.binding
        val (productName, details) = productList[position]
        val (quantity, price) = details

        view.NamaProduk.text = productName
        holder.binding.HargaProduk.text = "Rp. $price"
        holder.binding.JumlahProduk.text = quantity.toString()

        holder.binding.btnTambah.setOnClickListener {
            // Logika untuk menambah item (Opsional)
        }

        holder.binding.btnKurang.setOnClickListener {
            // Logika untuk mengurangi item (Opsional)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
