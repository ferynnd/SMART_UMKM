package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.DashboardProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentDashboardBinding
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {


    private lateinit var binding: FragmentDashboardBinding

    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container,false)

        recyclerView = binding.recyclerView

        recyclerView.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)

        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList?.let {
                recyclerView.adapter = DashboardProductAdapter(it)
            }
        }

        binding.productSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                if (searchText != null) {
                    lifecycleScope.launch {
                        productViewModel.productSearch(searchText)
                    }
                }
                return true
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}