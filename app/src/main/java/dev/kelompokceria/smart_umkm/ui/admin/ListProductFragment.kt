package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.ProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel

class ListProductFragment : Fragment() {

    private lateinit var binding: FragmentListProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListProductBinding.inflate(inflater, container, false)

        recyclerView = binding.recy
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList?.let {
                recyclerView.adapter = ProductAdapter(it)
            }
        }

        // Mengganti fragment menggunakan FragmentTransaction
        binding.btnAddProduct.setOnClickListener {
            val createProductFragment = CreateProductFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, createProductFragment)
                .addToBackStack(null)  // Menambahkan ke backstack untuk kembali ke fragment sebelumnya
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
