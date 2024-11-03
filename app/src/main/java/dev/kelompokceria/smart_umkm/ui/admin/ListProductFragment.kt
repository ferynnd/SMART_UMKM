package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.ProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class ListProductFragment : Fragment() {

    private lateinit var binding: FragmentListProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

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

        // Inisialisasi adapter dengan list kosong dan callback untuk edit dan hapus
        productAdapter = ProductAdapter({ product ->
            onEditClick(product)
        }, { product ->
            onDeleteClick(product)
        })

        // Setup RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // Observasi LiveData untuk produk
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                productAdapter.submitList(it)
            }
        }

        binding.btnAddProduct.setOnClickListener {
            val createProductFragment = CreateProductFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, createProductFragment)
                .addToBackStack(null)
                .commit()
        }

        // Setup SearchView
        setupSearchView()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView1.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                if (searchText.isNullOrEmpty()) {
                    // Jika pencarian dibatalkan atau teks kosong, tampilkan semua produk
                    productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                        products?.let {
                            productAdapter.submitList(it) // Tampilkan semua produk
                        }
                    }
                } else {
                    // Panggil metode filter dari adapter
                    productAdapter.filter(searchText)
                }
                return true
            }
        })
    }

    private fun onEditClick(product: Product) {
        val bundle = Bundle().apply {
            putInt("productId", product.id ?: 0)
            putString("productImage", product.image)
        }
        val createProductFragment = CreateProductFragment()
        createProductFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, createProductFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onDeleteClick(product: Product) {

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus produk ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                 lifecycleScope.launch {
                    try {
                        productViewModel.deleteProduct(product)
                        Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Hapus Produk")
        alert.show()
    }
}
