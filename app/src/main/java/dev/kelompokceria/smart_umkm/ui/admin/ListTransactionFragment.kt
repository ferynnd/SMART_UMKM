package dev.kelompokceria.smart_umkm.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.TransactionProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListTransactionBinding
import dev.kelompokceria.smart_umkm.databinding.FragmentTransactionBinding
import dev.kelompokceria.smart_umkm.model.Transaksi
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel


private lateinit var binding : FragmentListTransactionBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionProductAdapter: TransactionProductAdapter

    private var transakList: MutableList<Transaksi> = mutableListOf()
class ListTransactionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListTransactionBinding.inflate(inflater,container,false)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        transactionProductAdapter = TransactionProductAdapter(transakList)

        recyclerView.adapter = transactionProductAdapter

        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transList ->
            transList?.let {
                transactionProductAdapter.update(it)
            }
        }

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }




    override fun onDestroyView() {
        super.onDestroyView()
    }


}