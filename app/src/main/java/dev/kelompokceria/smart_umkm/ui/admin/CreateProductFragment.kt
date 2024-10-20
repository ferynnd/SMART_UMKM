package dev.kelompokceria.smart_umkm.ui.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateProductBinding
import dev.kelompokceria.smart_umkm.model.Category
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CreateProductFragment : Fragment() {

    private var _binding: FragmentCreateProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var productViewModel: ProductViewModel
    private var productId: Int? = null // Menyimpan ID produk jika ada
    private var imageUri: Uri? = null

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
                    // Sesuaikan teks tombol menjadi "Update" jika produk sedang diedit
                    binding.btnCreate.text = "Update"
                } ?: run {
                    Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                    navigateToProductList()
                }
            }
        }

        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*") // Memilih gambar dari galeri
        }

        hideBottomNavigationView()
        // Inisialisasi spinner kategori
        setupCategorySpinner()

        binding.btnCreate.setOnClickListener {
            if (validateInputs()) {
                binding.btnCreate.isEnabled = false // Nonaktifkan tombol untuk mencegah pengulangan
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

    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavAdmin)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
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
        val price = binding.edPrice.text?.toString()?.replace(",", ".")?.toDoubleOrNull() // Memastikan format harga valid
        val description = binding.edDescription.text?.toString()?.trim()
        val category = binding.spinnerCategory.selectedItem?.toString()?.trim()

        return !name.isNullOrEmpty() &&
                price != null && price > 0 &&
                !description.isNullOrEmpty() &&
                !category.isNullOrEmpty()
    }

    private fun saveProduct() {
        val name = binding.edName.text?.toString()?.trim() ?: ""
        val priceString = binding.edPrice.text?.toString()?.replace(",", ".")
        val price = priceString?.toDoubleOrNull()
        val description = binding.edDescription.text?.toString()?.trim() ?: ""
        val categoryString = binding.spinnerCategory.selectedItem?.toString()?.trim() ?: ""

        // Pastikan kategori valid sebelum konversi
        val categoryEnum = try {
            Category.valueOf(categoryString.uppercase()) // Konversi String ke enum
        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
            binding.btnCreate.isEnabled = true // Aktifkan kembali tombol jika ada kesalahan
            return // Menghentikan eksekusi jika kategori tidak valid
        }

        val imageBytes = imageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmap?.let { bitmapToByteArray(scaleBitmap(it, 1024, 1024)) } // Lakukan scaling gambar
        }

        val newProduct = Product(
            image = imageBytes,
            name = name,
            price = price ?: 0.0,
            description = description,
            category = categoryEnum
        )

        lifecycleScope.launch {
            try {
                productViewModel.addProduct(newProduct)
                Toast.makeText(requireContext(), "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                clearFields()
                navigateToProductList()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnCreate.isEnabled = true // Aktifkan kembali tombol setelah proses selesai
            }
        }
    }

    private fun updateProduct() {
        // Cek jika ada gambar baru, jika tidak, tetap gunakan gambar lama
        val imageBytes = imageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmap?.let { bitmapToByteArray(scaleBitmap(it, 1024, 1024)) }
        } ?: productViewModel.getProductById(productId!!).value?.image // Jika gambar baru tidak ada, gunakan gambar lama


        val updatedProduct = Product(
            image = imageBytes,
            id = productId!!, // Gunakan ID yang ada
            name = binding.edName.text?.toString()?.trim() ?: "",
            price = binding.edPrice.text?.toString()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0,
            description = binding.edDescription.text?.toString()?.trim() ?: "",
            category = try {
                Category.valueOf(binding.spinnerCategory.selectedItem.toString().uppercase()) // Konversi String ke enum
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
                binding.btnCreate.isEnabled = true // Aktifkan kembali tombol jika ada kesalahan
                return
            }
        )

        lifecycleScope.launch {
            try {
                productViewModel.updateProduct(updatedProduct)
                Toast.makeText(requireContext(), "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
                navigateToProductList()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnCreate.isEnabled = true // Aktifkan kembali tombol setelah proses selesai
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.ImagePreview) // Preview image
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = if (bitmap.width > bitmap.height) maxWidth else (maxHeight * aspectRatio).toInt()
        val height = if (bitmap.height > bitmap.width) maxHeight else (maxWidth / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun clearFields() {
        binding.edName.text?.clear()
        binding.edPrice.text?.clear()
        binding.edDescription.text?.clear()
    }

    private fun navigateToProductList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListProductFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
    }
}