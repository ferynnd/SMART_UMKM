package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.ProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.widget.Toast
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel

class ListProductFragment : Fragment() {

    private lateinit var binding: FragmentListProductBinding
    private lateinit var productViewModel: ProductViewModel
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListProductBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.recy.layoutManager = LinearLayoutManager(requireContext())

        // Observe the product list from the ViewModel
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                productList.clear()
                productList.addAll(it)
                // Jika adapter belum ada, buat adapter baru
                binding.recy.adapter = ProductAdapter(productList, ::onEditProduct, ::onDeleteProduct)
            }
        }

        // Navigate to CreateProductFragment
        binding.btnAddProduct.setOnClickListener {
            val createProductFragment = CreateProductFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, createProductFragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    // Handle product editing
    private fun onEditProduct(product: Product) {
        val editProductFragment = CreateProductFragment().apply {
            arguments = Bundle().apply {
                putParcelable("product", product) // Kirim data produk untuk diedit
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, editProductFragment)
            .addToBackStack(null)
            .commit()
    }

    // Handle product deletion
    private fun onDeleteProduct(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Penghapusan")
            .setMessage("Apakah Anda yakin ingin menghapus produk ini?")
            .setPositiveButton("Ya") { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    productViewModel.deleteProduct(product)
                    Toast.makeText(requireContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
