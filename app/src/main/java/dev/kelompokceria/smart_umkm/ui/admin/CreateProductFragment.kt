package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateProductBinding
import dev.kelompokceria.smart_umkm.model.Category
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class CreateProductFragment : Fragment() {

    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel
    private var productId: Int? = null // Menyimpan ID produk jika ada

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

        // Ambil ID produk dari argumen
        productId = arguments?.getInt("productId")

        // Jika ID produk ada, ambil produk dari ViewModel
        productId?.let { id ->
            productViewModel.getProductById(id).observe(viewLifecycleOwner) { product ->
                product?.let {
                    // Isi form dengan data produk yang diterima
                    binding.edName.setText(it.name)
                    binding.edPrice.setText(it.price.toString())
                    binding.edDescription.setText(it.description)

                    // Set selected item di spinner sesuai dengan kategori produk yang diterima
                    val categoryIndex = Category.values().indexOf(it.category)
                    if (categoryIndex >= 0) {
                        binding.spinnerCategory.setSelection(categoryIndex)
                    }

                    binding.edImage.setText(it.imageUrl)

                    // Sesuaikan teks tombol menjadi "Update" jika produk sedang diedit
                    binding.btnCreate.text = "Update"
                } ?: run {
                    Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                    navigateToProductList()
                }
            }
        }

        // Inisialisasi spinner kategori
        setupCategorySpinner()

        binding.btnCreate.setOnClickListener {
            if (validateInputs()) {
                if (productId != null) {
                    updateProduct() // Jika produk ada, update
                } else {
                    saveProduct() // Jika produk baru, simpan
                }
            } else {
                Toast.makeText(requireContext(), "Silahkan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategorySpinner() {
        // Mendapatkan daftar nama kategori dari enum
        val categories = Category.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun validateInputs(): Boolean {
        val name = binding.edName.text?.toString()?.trim()
        val price = binding.edPrice.text?.toString()?.trim()
        val description = binding.edDescription.text?.toString()?.trim()
        val category = binding.spinnerCategory.selectedItem?.toString()?.trim()

        return !name.isNullOrEmpty() &&
                !price.isNullOrEmpty() &&
                !description.isNullOrEmpty() &&
                !category.isNullOrEmpty() &&
                (price.toDoubleOrNull() ?: 0.0) > 0
    }

    private fun saveProduct() {
        val name = binding.edName.text?.toString()?.trim() ?: ""
        val priceString = binding.edPrice.text?.toString()
        val price = priceString?.toDoubleOrNull()
        val description = binding.edDescription.text?.toString()?.trim() ?: ""
        val categoryString = binding.spinnerCategory.selectedItem?.toString()?.trim() ?: ""
        val imageUrl = binding.edImage.text?.toString()?.trim() ?: ""

        // Pastikan kategori valid sebelum konversi
        val categoryEnum = try {
            Category.valueOf(categoryString.uppercase()) // Konversi String ke enum
        } catch (e: IllegalArgumentException) {
            null // Atau tangani kesalahan sesuai kebutuhan
        }

        if (categoryEnum == null) {
            Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
            return // Menghentikan eksekusi jika kategori tidak valid
        }

        val newProduct = Product(
            id = 0, // ID is auto-generated
            name = name,
            price = price ?: 0.0,
            description = description,
            category = categoryEnum, // Masukkan enum sebagai kategori
            imageUrl = imageUrl
        )

        lifecycleScope.launch {
            productViewModel.addProduct(newProduct)
            Toast.makeText(requireContext(), "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            clearFields()
            navigateToProductList()
        }
    }

    private fun updateProduct() {
        val updatedProduct = Product(
            id = productId!!, // Gunakan ID yang ada
            name = binding.edName.text?.toString()?.trim() ?: "",
            price = binding.edPrice.text?.toString()?.toDoubleOrNull() ?: 0.0,
            description = binding.edDescription.text?.toString()?.trim() ?: "",
            category = try {
                Category.valueOf(binding.spinnerCategory.selectedItem.toString().uppercase()) // Konversi String ke enum
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
                return // Menghentikan eksekusi jika kategori tidak valid
            },
            imageUrl = binding.edImage.text?.toString()?.trim() ?: ""
        )

        lifecycleScope.launch {
            productViewModel.updateProduct(updatedProduct)
            Toast.makeText(requireContext(), "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
            navigateToProductList()
        }
    }

    private fun clearFields() {
        binding.edName.text?.clear()
        binding.edPrice.text?.clear()
        binding.edDescription.text?.clear()
        binding.edImage.text?.clear()
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
