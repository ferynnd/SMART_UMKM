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
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.viewmodel.ProductCategoryViewModel
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CreateProductFragment : Fragment() {

    private lateinit var binding: FragmentCreateProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productCategoryViewModel : ProductCategoryViewModel
    private var productId : Int? = null
    private var productImage : String? = null
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateProductBinding.inflate(inflater, container, false)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        productCategoryViewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Ambil ID produk dari argumen
        productId = arguments?.getInt("productId")
        productImage = arguments?.getString("productImage")

        // Jika ID produk ada, ambil produk dari ViewModel
        productId?.let { id ->
            productViewModel.getProductById(id).observe(viewLifecycleOwner) { product ->
                product?.let {
                    // Isi form dengan data produk yang diterima
                    binding.edName.setText(it.name)
                    binding.edPrice.setText(it.price.toString())
                    binding.edDescription.setText(it.description)

                    productImage?.let {
                        Glide.with(this).load(File(it)).into(binding.ImagePreview)
                    }

                    // Set the spinner's selection to match the category
                    productCategoryViewModel.allProductCategory.observe(viewLifecycleOwner) { categories ->
                        val categoryNames = categories.map { it.name }

                        // Find the index of the category in the dynamically populated spinner
                        val categoryIndex = categoryNames.indexOf(it.category)
                        if (categoryIndex >= 0) {
                            binding.spinnerCategory.setSelection(categoryIndex)
                        }
                    }

                    // Sesuaikan teks tombol menjadi "Update" jika produk sedang diedit
                    binding.btnCreate.text = "UPDATE"
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
    // Observe LiveData from ViewModel to get the category names
    productCategoryViewModel.allProductCategory.observe(viewLifecycleOwner) { categories ->
        // Extract the names from the ProductCategory list
        val categoryNames = categories.map { it.name }

        // Set up the spinner adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }
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


        val imagePath = imageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmap?.let { saveImageToLocalStorage(it) }
        } ?: ""


        val newProduct = Product(
            image = imagePath,
            name = name,
            price = price ?: 0.0,
            description = description,
            category = categoryString
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

         val updatedImagePath = imageUri?.let { uri ->
            uriToBitmap(uri)?.let { bitmap ->
                saveImageToLocalStorage(bitmap)
            }
        } ?: productImage ?: ""

        val updatedProduct = Product(
            image = updatedImagePath,
            id = productId!!, // Gunakan ID yang ada
            name = binding.edName.text?.toString()?.trim() ?: "",
            price = binding.edPrice.text?.toString()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0,
            description = binding.edDescription.text?.toString()?.trim() ?: "",
            category = binding.spinnerCategory.selectedItem?.toString()?.trim() ?: ""
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

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

     private fun saveImageToLocalStorage(imageBitmap: Bitmap): String {
        val filename = "image_product_${System.currentTimeMillis()}.jpg"
        val file = File(context?.filesDir, filename)
        val outputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
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