package dev.kelompokceria.smart_umkm.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.databinding.FragmentListCategoryProductBinding
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.viewmodel.ProductCategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListCategoryProductFragment : Fragment() {

    private lateinit var binding: FragmentListCategoryProductBinding
    private lateinit var productCategoryViewModel: ProductCategoryViewModel
    private lateinit var categoryAdapter: ProductCategoryAdapter
    private lateinit var networkStatusViewModel: NetworkStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productCategoryViewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)
        networkStatusViewModel = ViewModelProvider(this).get(NetworkStatusViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCategoryProductBinding.inflate(inflater, container, false)

        hideBottomNavigationView()
        // Set up RecyclerView
        categoryAdapter = ProductCategoryAdapter { product, id ->
            onDeleteClick(product, id)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    productCategoryViewModel.refreshProductCategory()
                    productCategoryViewModel.refreshDeleteProductCategory()
                } catch (e: Exception) {
                    Log.e("ProductList", "Error fetching product data", e)
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }

        // Observe LiveData from ViewModel
        productCategoryViewModel.productCategory.observe(viewLifecycleOwner) { categories ->
            categories.let {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (it.isNotEmpty()) {
                        binding.linear.visibility = View.GONE
                        categoryAdapter.submitList(it)
                    } else {
                        binding.linear.visibility = View.VISIBLE
                        categoryAdapter.submitList(emptyList())
                        categoryAdapter.notifyDataSetChanged() // Memaksa pembaruan adapter
                    }
                }
            }
        }

        networkStatusViewModel.networkStatus.observe(viewLifecycleOwner) {  isConnected ->
            if(isConnected){
                // Add category on button click
                    binding.btnAddCategory.setOnClickListener {
                        val categoryName = binding.edCategory.text.toString().uppercase()
                        if (categoryName.isNotBlank()) {
                            val category = ProductCategory(name = categoryName)

                            lifecycleScope.launch(Dispatchers.IO) {
                                productCategoryViewModel.createProductCategory(category)
                                launch(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                                    binding.edCategory.text?.clear()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Network is not connected", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    private fun onDeleteClick(product: ProductCategory, id: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus category ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    try {
                        productCategoryViewModel.deleteProductCategory(product, id)
                        launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}
