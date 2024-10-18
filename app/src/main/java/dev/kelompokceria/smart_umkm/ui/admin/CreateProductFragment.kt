package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class CreateProductFragment : Fragment() {

    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProductBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreate.setOnClickListener {
            if (validateInputs()) {
                saveProduct() // Save product if inputs are valid
            } else {
                Toast.makeText(requireContext(), "Silahkan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.edName.text?.toString()?.trim()
        val price = binding.edPrice.text?.toString()?.trim()
        val description = binding.edDescription.text?.toString()?.trim()
        val category = binding.edCategory.text?.toString()?.trim() // Get input category

        return !name.isNullOrEmpty() &&
                !price.isNullOrEmpty() &&
                !description.isNullOrEmpty() &&
                !category.isNullOrEmpty() && // Ensure category is provided
                (price.toDoubleOrNull() ?: 0.0) > 0 // Ensure price is valid and greater than zero
    }

    private fun saveProduct() {
        // Get user input
        val name = binding.edName.text?.toString()?.trim() ?: ""
        val priceString = binding.edPrice.text?.toString() // This can be null
        val price = priceString?.toDoubleOrNull() // Convert only if not null
        val description = binding.edDescription.text?.toString()?.trim() ?: ""
        val category = binding.edCategory.text?.toString()?.trim() ?: "" // Get input category

        // Create Product object
        val newProduct = Product(
            id = 0, // ID is auto-generated
            name = name,
            imageUrl = binding.edImage.text?.toString()?.trim() ?: "", // Get image URL
            description = description,
            price = price ?: 0.0, // Use 0.0 if conversion fails
            category = category // Pass the category
        )

        // Save product to the database
        lifecycleScope.launch {
            productViewModel.addProduct(newProduct)
            Toast.makeText(requireContext(), "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()

            // Reset input fields
            binding.edName.text?.clear()
            binding.edPrice.text?.clear()
            binding.edDescription.text?.clear()
            binding.edCategory.text?.clear() // Clear category input
            binding.edImage.text?.clear() // Clear image URL field

            navigateToProductList() // Navigate to the product list
        }
    }

    private fun navigateToProductList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListProductFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

