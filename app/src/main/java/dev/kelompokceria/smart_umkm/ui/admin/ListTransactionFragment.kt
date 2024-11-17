package dev.kelompokceria.smart_umkm.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.controller.AdminTransactionListAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListTransactionBinding
import dev.kelompokceria.smart_umkm.databinding.FragmentTransactionBinding
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch


class ListTransactionFragment : Fragment() {

    private lateinit var binding: FragmentListTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var adapter: AdminTransactionListAdapter

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
                    if (transactions.isNotEmpty()){
                        binding.linear.visibility = View.GONE
                        adapter.submitList(it)
                    } else {
                        binding.linear.visibility = View.VISIBLE
                        adapter.submitList(emptyList())
                        adapter.notifyDataSetChanged() // Memaksa pembaruan adapter
                    }
                }
            }
        }

    }
}