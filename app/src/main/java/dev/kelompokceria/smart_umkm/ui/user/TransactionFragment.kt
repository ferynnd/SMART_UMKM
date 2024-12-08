//package dev.kelompokceria.smart_umkm.ui.user
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.material.card.MaterialCardView
//import dev.kelompokceria.smart_umkm.R
//import dev.kelompokceria.smart_umkm.controller.TransactionAdapter
//import dev.kelompokceria.smart_umkm.databinding.FragmentTransactionBinding
//import dev.kelompokceria.smart_umkm.model.TransactionProduct
//import dev.kelompokceria.smart_umkm.model.Transaction
//import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
//import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import dev.kelompokceria.smart_umkm.data.helper.Constant
//import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
//import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
//import java.text.NumberFormat
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class TransactionFragment : Fragment() {
//
//    private lateinit var binding: FragmentTransactionBinding
//    private lateinit var productViewModel: ProductViewModel
//    private lateinit var transactionViewModel: TransactionViewModel
//    private lateinit var userViewModel: UserViewModel
//    private lateinit var selectedProductIds: List<Int>
//    private val productQuantities = mutableMapOf<Int, Int>()
//    private var totalHargaSemuaProduk = 0
//    private lateinit var userTransaction : String
//      private lateinit var sharedPref: PreferenceHelper
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//         userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
//        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
//        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
//        val ids = arguments?.getIntArray("KEY_SELECTED_IDS")
//        selectedProductIds = ids?.toList() ?: emptyList()
//         sharedPref = PreferenceHelper(requireContext())
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentTransactionBinding.inflate(inflater, container, false)
//        setupRecyclerView()
//        setupTransactionDetails()
//        hideBottomNavigationView()
//        setupUserObservers()
//
//        binding.btnBack.setOnClickListener{
//            val checkoutFragment = DashboardFragment()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.nav_host_fragment_user, checkoutFragment)
//                .commit()
//        }
//
//        binding.btnCashback.setOnClickListener {
//            updateTotalHargaSemuaProduk()
//        }
//
//            binding.btnOrder.setOnClickListener {
//                    if (validasiCashback()) {
//                        saveTransaction()
//                        val bundle = Bundle().apply {
//                            putString("KEY_USERNAME", userTransaction)
//                        }
//                        val dashboard = DashboardFragment()
//                        dashboard.arguments = bundle
//                        parentFragmentManager.beginTransaction()
//                            .replace(R.id.nav_host_fragment_user, dashboard)
//                            .addToBackStack(null)
//                            .commit()
//                    }
//            }
//
//
//        return binding.root
//    }
//
//    private fun validasiCashback(): Boolean {
//
//        val cash = binding.tvcashback.text.toString()
//        if (cash.contains("-")) {
//            Toast.makeText(requireContext(), "Nominal Tidak Boleh Minus", Toast.LENGTH_LONG).show()
//            return false
//        }
//
//        val cashInput = binding.edCashback.text?.toString()
//
//
//        if (cashInput.isNullOrEmpty()) {
//            binding.edCashback.error = "Nominal harus diisi"
//            return false
//        }
//
//        val cashValue = cashInput?.toIntOrNull()
//        if (cashValue == null) {
//            binding.edCashback.error = "Nominal harus berupa angka"
//            return false
//        }
//
//        // Validasi jika nominal kurang dari total harga
//        if (cashValue < totalHargaSemuaProduk) {
//            binding.edCashback.error = "Nominal tidak valid atau kurang dari total harga"
//            return false
//        }
//
//        return true // Validasi berhasil
//    }
//
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        showBottomNavigationView()
//    }
//
//    private fun getCurrentDateTime(): String {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val date = Date()
//        return dateFormat.format(date)
//    }
//
//    private fun setupRecyclerView() {
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = TransactionAdapter { product, newQuantity ->
//                // Update jumlah untuk produk tertentu
//                product.id?.let { productId ->
//                    productQuantities[productId] = newQuantity
//                    updateTotalHargaSemuaProduk()
//                }
//            }
//        }
//    }
//
//    private fun updateTotalHargaSemuaProduk() {
//        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//        // Hitung total harga berdasarkan semua produk
//        totalHargaSemuaProduk = productQuantities.entries.sumOf { entry ->
//            val productId = entry.key
//            val quantity = entry.value
//            val product = (binding.recyclerView.adapter as TransactionAdapter).currentList.find { it.id == productId }
//            product?.price?.toInt()?.times(quantity) ?: 0
//        }
//
//        // Perbarui total harga di UI
//        binding.tvtotal.text = numberFormat.format(totalHargaSemuaProduk)
//
//        // Hitung cashback
//        val cashInput = binding.edCashback.text.toString()
//        val totalUang = if (cashInput.isNotEmpty()) cashInput.toInt() else 0
//        val cashback = totalUang - totalHargaSemuaProduk
//        binding.tvcashback.text = numberFormat.format(cashback)
//    }
//
//    private fun setupTransactionDetails() {
//        if (selectedProductIds.isEmpty()) {
//            Toast.makeText(requireContext(), "Tidak ada produk yang dipilih", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        productViewModel.getProductsByIds(selectedProductIds).observe(viewLifecycleOwner) { products ->
//            if (products.isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "Tidak ada produk yang ditemukan", Toast.LENGTH_SHORT).show()
//            } else {
//                products.forEach { product ->
//                    product.id?.let { productId ->
//                        productQuantities[productId] = 1
//                    }
//                }
//                (binding.recyclerView.adapter as TransactionAdapter).submitList(products)
//                updateTotalHargaSemuaProduk()
//            }
//        }
//    }
//
//    private fun setupUserObservers() {
//        val username = sharedPref.getString(Constant.PREF_USER_NAME)
//        if (username != null) {
//            userViewModel.getUserByUsername(username)
//
//            userViewModel.user.observe(viewLifecycleOwner) { user ->
//                if (user != null) {
//                    userTransaction = user.name
//                } else {
//                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } else {
//            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//    private fun saveTransaction() {
//        // Konversi productQuantities ke daftar TransactionProduct
//        val transactionProducts = productQuantities.mapNotNull { entry ->
//            val product = (binding.recyclerView.adapter as TransactionAdapter).currentList.find { it.id == entry.key }
//            product?.let {
//                TransactionProduct(
//                    name = it.name ?: "Unknown",
//                    price = it.price.toString(),
//                    quantity = entry.value
//                )
//            }
//        }
//
//        // Simpan data transaksi
//        val dataTransaction = Transaction(
//            time = getCurrentDateTime(),
//            user = userTransaction,
//            products = transactionProducts, // Simpan sebagai JSON
//            total = binding.tvtotal.text.toString(),
//            cashback = binding.tvcashback.text.toString(),
//            id = "TRX${System.currentTimeMillis()}" // ID unik untuk transaksi
//        )
//
//        // Simpan ke database menggunakan Coroutine
//        CoroutineScope(Dispatchers.IO).launch {
//            transactionViewModel.insert(dataTransaction)
//            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(requireContext(), "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun hideBottomNavigationView() {
//        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
//        bottomNavigationView?.visibility = View.GONE
//    }
//
//    private fun showBottomNavigationView() {
//        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavUser)
//        bottomNavigationView?.visibility = View.VISIBLE
//    }
//}
