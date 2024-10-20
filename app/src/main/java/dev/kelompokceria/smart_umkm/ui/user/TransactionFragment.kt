package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.TransactionProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentTransactionBinding
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
//
//        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
//
//        productViewModel.selectedProducts.observe(viewLifecycleOwner) { selectedProducts ->
//            displaySelectedProducts(selectedProducts)
//        }

        hideBottomNavigationView()

        // Dapatkan shared ViewModel
        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

        // Pantau produk yang dipilih dan tampilkan di RecyclerView
        productViewModel.selectedProducts.observe(viewLifecycleOwner) { selectedProducts ->
            val productList = selectedProducts.map { it.key to it.value }
            binding.recyclerView.adapter = TransactionProductAdapter(productList)
            updateTotals(selectedProducts)
        }


        return binding.root
    }

     private fun updateTotals(selectedProducts: Map<String, Pair<Int, Int>>) {
        var subtotal = 0
        selectedProducts.forEach { (_, details) ->
            val (quantity, price) = details
            subtotal += quantity * price
        }
        binding.tvTotalPrice.text = "Rp. $subtotal"
        binding.tvTotalPayment.text = "Rp. $subtotal"
    }



    private fun displaySelectedProducts(selectedProducts: Map<String, Pair<Int, Int>>) {
        // Tampilkan produk yang dipilih di UI Transaksi
        val productDetails = StringBuilder()
        var totalPrice = 0
        selectedProducts.forEach { (productName, details) ->
            val (quantity, price) = details
            productDetails.append("$productName x$quantity = Rp. ${quantity * price}\n")
            totalPrice += quantity * price
        }
//
//        binding.selectedProductsTextView.text = productDetails.toString()
//        binding.totalPriceTextView.text = "Total: Rp. $totalPrice"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
    }

     // Fungsi untuk menyembunyikan BottomNavigationView
    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavUser)
        bottomNavigationView?.visibility = View.GONE
    }

    // Fungsi untuk menampilkan BottomNavigationView kembali
    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavUser)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}