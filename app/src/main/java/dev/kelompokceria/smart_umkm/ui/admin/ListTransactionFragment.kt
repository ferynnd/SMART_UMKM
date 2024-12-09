package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.controller.AdminTransactionListAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListTransactionBinding
import dev.kelompokceria.smart_umkm.model.Transaction
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class ListTransactionFragment : Fragment() {

    private lateinit var binding: FragmentListTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var adapter: AdminTransactionListAdapter

    private val groupedData = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchTransactions()

         binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    transactionViewModel.refreshTransaction()
                    transactionViewModel.refreshDeleteTransaction()
                } catch (e: Exception) {
                    Log.e("TransactionList", "Error fetching transaction data", e)
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminTransactionListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListTransactionFragment.adapter
        }
    }

    private fun fetchTransactions() {
        transactionViewModel.trans.observe(viewLifecycleOwner) { transactions ->
            transactions.let {
                lifecycleScope.launch {
                    if (transactions.isNotEmpty()) {
                        binding.linear.visibility = View.GONE
                         adapter.submitList(it)
                            setTransaction(it)

                    } else {
                        binding.linear.visibility = View.VISIBLE
                        adapter.submitList(emptyList())
                        adapter.notifyDataSetChanged() // Memaksa pembaruan adapter
                    }
                }
            }

        }


    }

    private fun setTransaction(newProducts: List<Transaction>) {
        groupedData.clear()

        // Format tanggal untuk parsing dan pengelompokan
        val formatTanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatBulan = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        // Urutkan transaksi berdasarkan waktu
        val sortedTransactions = newProducts.sortedByDescending { it.time }

        // Kelompokkan transaksi berdasarkan bulan
        val groupedByMonth = sortedTransactions.groupBy { transaksi ->
            val date = formatTanggal.parse(transaksi.time)
            date?.let { formatBulan.format(it) } ?: "Unknown" // Handle null values
        }

        // Bangun data untuk adapter
        groupedByMonth.forEach { (bulan, transaksiList) ->
            groupedData.add(bulan) // Tambahkan header bulan
            groupedData.addAll(transaksiList) // Tambahkan transaksi di bulan itu
        }

        // Kirim data yang sudah dikelompokkan ke adapter
        adapter.submitList(groupedData)
    }
}