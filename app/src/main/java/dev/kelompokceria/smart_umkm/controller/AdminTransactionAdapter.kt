package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.CardListTransactionBinding
import dev.kelompokceria.smart_umkm.model.Transaksi
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdminTransactionListAdapter :
    ListAdapter<Transaksi, AdminTransactionListAdapter.TransactionViewHolder>(DiffCallback()) {

    inner class TransactionViewHolder(private val binding: CardListTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaksi) {
            binding.textViewId.text = transaction.transaction_id
            // Format input tetap sama karena data dari transaction.transactionTime dalam format "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Format output sesuai yang diinginkan "dd MMM yy" dengan Locale.ENGLISH
            val outputFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH) // Langsung set Locale di konstruktor

            try {
                // Parse tanggal dari string input
                val date = inputFormat.parse(transaction.transactionTime)
                // Format tanggal ke output yang diinginkan
                date?.let {
                    binding.textViewTime.text = outputFormat.format(it).uppercase() // Ubah ke uppercase agar tampil: 06 JAN 24
                } ?: run {
                    binding.textViewTime.text = "Invalid date"
                }
            } catch (e: Exception) {
                binding.textViewTime.text = "Invalid date"
            }

            binding.tvheadertotal.text = transaction.transactionTotal
            binding.textViewFullTime.text = transaction.transactionTime
            binding.textViewUser.text = transaction.transactionUser
            binding.textViewTotal.text = transaction.transactionTotal
            binding.textViewCashback.text = transaction.transactionCashback
//             val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            // Mengolah produk
            val products = transaction.transactionProduct.joinToString("\n") {
                "${it.name} - ${it.price} - qty x ${it.quantity}"
            }
            binding.textViewProduct.text = products
            binding.btnPayAllBills.setOnClickListener {
            binding.groupProfile.visibility = if (binding.groupProfile.visibility == View.GONE) {
                View.VISIBLE // Show
            } else {
                View.GONE // Hide
            }
        }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = CardListTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaksi>() {
        override fun areItemsTheSame(oldItem: Transaksi, newItem: Transaksi): Boolean {
            return oldItem.transaction_id == newItem.transaction_id
        }

        override fun areContentsTheSame(oldItem: Transaksi, newItem: Transaksi): Boolean {
            return oldItem == newItem
        }
    }
}
