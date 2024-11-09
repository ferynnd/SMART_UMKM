package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.CardListTransactionBinding
import dev.kelompokceria.smart_umkm.model.Transaksi

class AdminTransactionListAdapter :
    ListAdapter<Transaksi, AdminTransactionListAdapter.TransactionViewHolder>(DiffCallback()) {

    inner class TransactionViewHolder(private val binding: CardListTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaksi) {
            binding.textViewId.text = transaction.transaction_id
            binding.textViewTime.text = transaction.transactionTime
            binding.textViewUser.text = transaction.transactionUser
            binding.textViewTotal.text = transaction.transactionTotal
            binding.textViewCashback.text = transaction.transactionCashback

            // Mengolah produk
            val products = transaction.transactionProduct.joinToString("\n") {
                "${it.name} - Qty: ${it.quantity}"
            }
            binding.textViewProduct.text = products
            binding.Expand.setOnClickListener {
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
