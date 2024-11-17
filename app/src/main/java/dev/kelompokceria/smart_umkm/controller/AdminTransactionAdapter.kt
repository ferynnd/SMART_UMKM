package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.databinding.CardCategoryLayoutBinding
import dev.kelompokceria.smart_umkm.databinding.CardListTransactionBinding
import dev.kelompokceria.smart_umkm.model.Transaksi
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdminTransactionListAdapter :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

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

    inner class CatergoryViewHolder(val binding: CardCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root)

     enum class TYPE_VIEW { HEADER , CONTENT }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Transaksi -> TYPE_VIEW.CONTENT.ordinal
              is String -> TYPE_VIEW.HEADER.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_VIEW.CONTENT.ordinal -> {
                val binding = CardListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TransactionViewHolder(binding)
            }
            TYPE_VIEW.HEADER.ordinal -> {
                val binding = CardCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CatergoryViewHolder(binding)
            }
             else -> throw IllegalArgumentException("Unknown viewType: $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         val item = getItem(position)
        when(holder) {
               is TransactionViewHolder -> {
                   val transaksi = getItem(position) as Transaksi
                   holder.bind(transaksi)
               }
               is CatergoryViewHolder -> {
                   holder.binding.textViewHeader.text = (item as String)
               }
           }

    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Transaksi -> {
                    if (newItem is Transaksi) {
                        (oldItem.transaction_id) == (newItem.transaction_id)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Transaksi) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            } // Compare by unique ID
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when ( oldItem) {
                is Transaksi -> {
                    if (newItem is Transaksi) {
                        (oldItem) == (newItem)
                    } else {
                        false
                    }
                }
                else -> {
                    if (newItem is Transaksi) {
                        false
                    } else {
                        (oldItem) == (newItem)
                    }
                }
            } // Compare by unique ID
        }
    }

}
