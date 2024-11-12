package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.DashboardProductAdapter
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.FragmentDashboardBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var dashboardProductAdapter: DashboardProductAdapter

    private lateinit var sharedPref: PreferenceHelper

    private val groupedData = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        sharedPref = PreferenceHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupProductObservers()
        setupUserObservers()
        setupSearch()
        setupCheckoutButton()

        return binding.root
    }

    private fun setupRecyclerView() {
        dashboardProductAdapter = DashboardProductAdapter(
            { product -> productViewModel.toggleSelection(product) },
            { isVisible -> toggleCheckoutButton(isVisible)}
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        // Logika untuk menentukan span size berdasarkan posisi atau tipe item
                        return when (dashboardProductAdapter.getItemViewType(position)) {
                            DashboardProductAdapter.TYPE_VIEW.TYPE_HEADER.ordinal -> 2  // Header mengambil 2 span
                            DashboardProductAdapter.TYPE_VIEW.TYPE_CONTENT.ordinal -> 1  // Item produk mengambil 1 span
                            else -> 1  // Default ke 1 span
                        }
                    }
                }
            }

            adapter = dashboardProductAdapter
        }

    }

    private fun setupProductObservers() {
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            setProduct(products)
        }

        productViewModel.selectedPositions.observe(viewLifecycleOwner) { selectedProducts ->
            dashboardProductAdapter.updateSelections(selectedProducts)
        }
    }

    private fun setProduct(newProducts: List<Product>) {
        groupedData.clear()
        val sortedProducts = newProducts.sortedBy { it.category }

        if (sortedProducts.isNotEmpty()) {
            var currentCategory = sortedProducts[0].category
            groupedData.add(currentCategory) // Tambahkan kategori awal sebagai header

            for (product in sortedProducts) {
                if (product.category != currentCategory) {
                    currentCategory = product.category
                    groupedData.add(currentCategory) // Tambahkan kategori baru
                }
                groupedData.add(product) // Tambahkan produk
            }
        }

        // Kirim data yang sudah dikelompokkan ke adapter
        dashboardProductAdapter.submitList(groupedData)
    }

    private fun setupUserObservers() {
        val username = sharedPref.getString(Constant.PREF_USER_NAME)
        if (username != null) {
            userViewModel.getUserByUsername(username)

            userViewModel.user.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.tvName.text = user.name
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearch() {
        binding.productSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                searchText?.let {
                    lifecycleScope.launch {
                        productViewModel.productSearch(it)
                    }
                }
                return true
            }
        })
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            val selectedIds = dashboardProductAdapter.getSelectedProductIds()
            val bundle = Bundle().apply {
                putIntArray("KEY_SELECTED_IDS", selectedIds.toIntArray())
            }
            val checkoutFragment = TransactionFragment()
            checkoutFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_user, checkoutFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun toggleCheckoutButton(isVisible: Boolean) {
        binding.layoutCheckout.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavUser)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}
