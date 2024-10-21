package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.ItemCartBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.Transaksi

class TransactionProductAdapter (private var transaksiList: List<Transaksi>
) : RecyclerView.Adapter<TransactionProductAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {

        val view = holder.binding
        val transaksi = transaksiList[position]

        view.namaProduk.text = transaksi.transactionProduct
//        view.namaProduk.text = transaksi.transactionProduct.toString()
        view.namaUser.text = transaksi.transactionUser
        view.Total.text = "Rp. ${transaksi.transactionTotal}"
        view.cashback.text = "Rp. ${transaksi.transactionCashback}"

    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }

    fun update(upList: List<Transaksi>) {
        transaksiList =upList
        notifyDataSetChanged() // Memperbarui seluruh daftar
    }
}
