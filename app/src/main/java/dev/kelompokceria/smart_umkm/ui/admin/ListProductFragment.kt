package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.ProductAdapter
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.databinding.FragmentListProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListProductFragment : Fragment() {

    private lateinit var binding: FragmentListProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    private lateinit var  networkStatusViewModel: NetworkStatusViewModel


   private val groupedData = mutableListOf<Any>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        networkStatusViewModel = ViewModelProvider(this).get(NetworkStatusViewModel::class.java)
         activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)?.visibility = View.VISIBLE
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
        }, { product, id ->
            onDeleteClick(product, id)
        })


        networkStatusViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected) {
                binding.btnAddCategory.setOnClickListener {
                    val categoryFragment = ListCategoryProductFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_admin, categoryFragment)
                        .addToBackStack(null)
                        .commit()
                }
                binding.btnAddProduct.setOnClickListener {
                    val createProductFragment = CreateProductFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_admin, createProductFragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                Toast.makeText(requireContext(), "Network is not connected", Toast.LENGTH_LONG).show()
            }
        }


        // Setup RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }


        binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    productViewModel.refreshProducts()
                    productViewModel.refreshDeleteProducts()
                } catch (e: Exception) {
                    Log.e("ProductList", "Error fetching product data", e)
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.products.observe(viewLifecycleOwner) { products ->
                  products?.let {
                      lifecycleScope.launch(Dispatchers.Main) {
                          if(products.isNotEmpty()){
                              binding.linear.visibility = View.GONE
                              productAdapter.submitList(products)
                              setProduct(products)
                          }else{
                              binding.linear.visibility = View.VISIBLE
                              productAdapter.submitList(emptyList())
                              productAdapter.notifyDataSetChanged()
                          }
                      }
                  }
            }

        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    lifecycleScope.launch {
                        newText?.let {
                            productViewModel.searchProduct(it)
                            productViewModel.products.observe(viewLifecycleOwner) { products ->
                                products?.let {
                                    productAdapter.submitList(products)
                                    setProduct(products)
                                }
                            }
                        }
                    }

                    return true
                }
        })



        binding.btnSwitch.setOnClickListener {
                if (binding.btnAddProduct.visibility == View.GONE && binding.btnAddCategory.visibility == View.GONE) {
                    binding.btnAddProduct.visibility = View.VISIBLE
                    binding.btnAddCategory.visibility = View.VISIBLE
                } else {
                    binding.btnAddProduct.visibility = View.GONE
                    binding.btnAddCategory.visibility = View.GONE
                }
        }

        return binding.root
    }



    private fun onEditClick(product: Product) {
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

    private fun onDeleteClick(product: Product, id : Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus produk ${product.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {

                        if (product.id == null) {
                            Toast.makeText(requireContext(), "Product ID is null", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        productViewModel.deleteProduct(product, id)
                        withContext(Dispatchers.Main) {
                             productViewModel.products.observe(viewLifecycleOwner) { productList ->
                                productList?.let {
                                    productAdapter.submitList(productList)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Tampilkan pesan error di thread utama
                        withContext(Dispatchers.Main) {
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
//
//
    private fun setProduct(newProducts: List<Product>) {
        groupedData.clear()
        val sortedProducts = newProducts.sortedBy { it.category }

        if (sortedProducts.isNotEmpty()) {
            var currentCategory = sortedProducts[0].category // Kategori awal
            if (currentCategory != null) {
                groupedData.add(currentCategory)
            } // Tambahkan kategori ke daftar
            for (product in sortedProducts) {
                if (product.category != currentCategory) {
                    currentCategory = product.category // Perbarui kategori
                    if (currentCategory != null) {
                        groupedData.add(currentCategory)
                    }
                }
                groupedData.add(product)
            }
        }
        productAdapter.submitList(groupedData)
    }



}
