package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.ProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.User
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
        productAdapter = ProductAdapter(emptyList(), { product ->
            onEditClick(product)
        }, { product ->
            onDeleteClick(product)
        })

        // Setup RecyclerView
        binding.recy.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // Observasi LiveData untuk produk
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                productAdapter.updateProducts(it) // Update data di adapter
            }
        }

        // Navigasi ke CreateProductFragment
        binding.btnAddProduct.setOnClickListener {
            val createProductFragment = CreateProductFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, createProductFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.editTextSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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

    private fun onEditClick(product: Product) {
        // Navigasi ke CreateProductFragment dengan ID produk
        val bundle = Bundle().apply {
            putInt("productId", product.id ?: 0)
        }
        val createProductFragment = CreateProductFragment()
        createProductFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, createProductFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onDeleteClick(product: Product) {
        // Menampilkan dialog konfirmasi sebelum menghapus produk
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus produk ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                // Jika pengguna mengkonfirmasi, hapus produk
                productViewModel.deleteProduct(product)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                // Jika pengguna membatalkan, tutup dialog
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Hapus Produk")
        alert.show()
    }
}
