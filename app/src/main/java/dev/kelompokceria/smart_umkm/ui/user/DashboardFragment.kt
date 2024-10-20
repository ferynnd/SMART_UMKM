package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
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


class DashboardFragment : Fragment(), DashboardProductAdapter.QuantityChangeListener {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView

    private var totalPrice: Int = 0
    private var totalItems: Int = 0

    // Map untuk menyimpan nama produk dan informasi kuantitas & harga
    private val selectedProducts: MutableMap<String, Pair<Int, Int>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList?.let {
                recyclerView.adapter = DashboardProductAdapter(it, this)
            }
        }

        productViewModel.selectedProducts.observe(viewLifecycleOwner) { selectedProducts ->
            // Perbarui UI setiap kali ada perubahan pada selectedProducts di ViewModel
            updateCheckoutUI(selectedProducts)
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

//        binding.btnCheckout.setOnClickListener {
//            val bundle = Bundle()
//            val productList = ArrayList<Triple<String, Int, Int>>()
//
//            // Masukkan item yang dipilih ke dalam bundle
//            selectedProducts.forEach { (productName, details) ->
//                val (quantity, price) = details
//                productList.add(Triple(productName, quantity, price))
//            }
//
//            bundle.putSerializable("selectedProducts", productList)
//
//            // Buat instance baru dari TransaksiFragment
//            val transaksiFragment = TransaksiFragment()
//            transaksiFragment.arguments = bundle
//
//            // Pindah ke fragment transaksi
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.nav_host_fragment_user, transaksiFragment)
//                .addToBackStack(null)
//                .commit()
//        }

        return binding.root
    }

//    override fun onQuantityChanged(price: Int, items: Int) {
//        totalPrice += price
//        totalItems = if (items > 0) totalItems + 1 else totalItems - 1
//        updateCheckoutUI()
//    }


     override fun addToSelectedProducts(productName: String, quantity: Int, price: Int) {
        productViewModel.addToSelectedProducts(productName, quantity, price)
        updateTotals()
    }

    override fun removeFromSelectedProducts(productName: String, price: Int) {
        productViewModel.removeFromSelectedProducts(productName, price)
        updateTotals()
    }

    override fun onQuantityChanged(price: Int, quantityChange: Int) {
        totalPrice += price * quantityChange
        updateTotals()
    }

    private fun updateTotals() {
        val selectedProducts = productViewModel.selectedProducts.value ?: mutableMapOf()
        totalItems = selectedProducts.values.sumOf { it.first }
        totalPrice = selectedProducts.entries.sumOf { it.value.first * it.value.second }

        updateCheckoutUI(selectedProducts)
    }

    private fun updateCheckoutUI(selectedProducts: Map<String, Pair<Int, Int>>) {
        binding.totalPrice.text = "Rp. $totalPrice"
        binding.totalItem.text = "$totalItems ITEM SELECTED"

        if (totalItems > 0) {
            binding.layoutCheckout.visibility = View.VISIBLE
        } else {
            binding.layoutCheckout.visibility = View.GONE
            binding.totalPrice.text = ""
            binding.totalItem.text = ""
        }
    }



}
