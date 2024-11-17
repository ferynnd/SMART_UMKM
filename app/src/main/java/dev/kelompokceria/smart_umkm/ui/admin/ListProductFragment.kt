package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.os.Handler
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListProductFragment : Fragment() {

    private lateinit var binding: FragmentListProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter


   private val groupedData = mutableListOf<Any>()


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

        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            products?.let {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (it.isNotEmpty()) {
                        binding.linear.visibility = View.GONE
                        productAdapter.submitList(it)
                        setProduct(it)
                    } else {
//                        Handler().postDelayed({})
                        binding.linear.visibility = View.VISIBLE
                        productAdapter.submitList(emptyList())
                        productAdapter.notifyDataSetChanged() // Memaksa pembaruan adapter
                    }
                }
            }
        }



        binding.btnSwitch.setOnClickListener {
                if (binding.btnAddProduct.visibility == View.GONE && binding.btnAddCategory.visibility == View.GONE) {
                    binding.btnAddProduct.visibility = View.VISIBLE
                    binding.btnAddCategory.visibility = View.VISIBLE
                } else {
                    binding.btnAddProduct.visibility = View.GONE
                    binding.btnAddCategory.visibility = View.GONE
                }
        }

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
                val filteredProducts = productViewModel.allProducts.value?.filter {
                    it.name.contains(searchText ?: "", ignoreCase = true)
                } ?: emptyList()

                setProduct(filteredProducts)
                return true
            }

//            override fun onQueryTextChange(searchText: String?): Boolean {
//                if (searchText.isNullOrEmpty()) {
//                    // Jika pencarian dibatalkan atau teks kosong, tampilkan semua produk
//                    productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
//                        products?.let {
//                            productAdapter.submitList(it) // Tampilkan semua produk
//                        }
//                    }
//                } else {
//                    // Panggil metode filter dari adapter
////                    productAdapter.filter(searchText)
//                }
//                return true
//            }
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
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        // Hapus produk dari ViewModel
                        productViewModel.deleteProduct(product)
                        withContext(Dispatchers.Main) {
                             productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
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


    private fun setProduct(newProducts: List<Product>) {
        groupedData.clear()
        // Sortir produk berdasarkan kategori
        val sortedProducts = newProducts.sortedBy { it.category }

        if (sortedProducts.isNotEmpty()) {
            var currentCategory = sortedProducts[0].category // Kategori awal
            groupedData.add(currentCategory) // Tambahkan kategori ke daftar
            for (product in sortedProducts) {
                if (product.category != currentCategory) {
                    currentCategory = product.category // Perbarui kategori
                    groupedData.add(currentCategory) // Tambahkan kategori baru
                }
                groupedData.add(product) // Tambahkan produk
            }
        }
        // Gunakan groupedData untuk di-render di RecyclerView
        productAdapter.submitList(groupedData)
    }

}
