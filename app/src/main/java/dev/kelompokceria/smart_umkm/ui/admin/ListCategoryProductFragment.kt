package dev.kelompokceria.smart_umkm.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.databinding.FragmentListCategoryProductBinding
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.viewmodel.ProductCategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListCategoryProductFragment : Fragment() {

    private lateinit var binding: FragmentListCategoryProductBinding
    private lateinit var productCategoryViewModel: ProductCategoryViewModel
    private lateinit var categoryAdapter: ProductCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productCategoryViewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCategoryProductBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        categoryAdapter = ProductCategoryAdapter { product ->
            onDeleteClick(product)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        // Observe LiveData from ViewModel
        productCategoryViewModel.allProductCategory.observe(viewLifecycleOwner) { categories ->
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

        // Add category on button click
        binding.btnAddCategory.setOnClickListener {
            val categoryName = binding.edCategory.text.toString().uppercase()
            if (categoryName.isNotBlank()) {
                val category = ProductCategory(name = categoryName)

                lifecycleScope.launch(Dispatchers.IO) {
                    productCategoryViewModel.addProductCategory(category)
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                        binding.edCategory.text?.clear()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun onDeleteClick(product: ProductCategory) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus category ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    try {
                        productCategoryViewModel.deleteProductCategory(product)
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
}
