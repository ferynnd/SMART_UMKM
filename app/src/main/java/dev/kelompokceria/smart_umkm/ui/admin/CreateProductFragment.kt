package dev.kelompokceria.smart_umkm.ui.admin

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateProductBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.viewmodel.ProductCategoryViewModel
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreateProductFragment : Fragment() {

    private lateinit var binding: FragmentCreateProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productCategoryViewModel: ProductCategoryViewModel
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private var imageUri: Uri? = null
    private var imagePart: MultipartBody.Part? = null
    private var productId : Int? = null

    private var isClicked = true

    private var imageUpdate : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateProductBinding.inflate(inflater, container, false)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        productCategoryViewModel = ViewModelProvider(this)[ProductCategoryViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        isClicked = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getInt("productId")

        if (productId != null) {
            productViewModel.fetchProductById(productId!!)

            productViewModel.product.observe(viewLifecycleOwner) { response ->
                response.data?.let { product ->
                    // Set UI elements with product data
                    binding.edName.setText(product.name)
                    binding.edPrice.setText(product.price.toString())
                    binding.edDescription.setText(product.description)

                    imageUpdate = product.image

                    product.image?.let { imageUrl ->
                        Glide.with(this).load(imageUrl).into(binding.ImagePreview)
                    }

                    // Set spinner sesuai kategori produk
                    productCategoryViewModel.productCategory.observe(viewLifecycleOwner) { categories ->
                        val categoryNames = categories.map { it.name }
                        val categoryIndex = categoryNames.indexOf(product.category)
                        if (categoryIndex >= 0) {
                            binding.spinnerCategory.setSelection(categoryIndex)
                        }
                    }

                    // Ubah teks tombol ke "UPDATE" dan tambahkan logika update
                    binding.btnCreate.setOnClickListener {
                        updateProduct(productId!!)
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
                    navigateToProductList()
                }

            }

        }

         binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        if (isClicked){
            binding.btnCreate.setText("SAVE")
        } else {
            binding.btnCreate.setText("LOADING")
        }

         binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnCreate.setOnClickListener {
            if (validateInputs()) {
                isClicked = false
                uploadProduct()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        setupCategorySpinner()
        hideBottomNavigationView()

    }

    private fun setupCategorySpinner() {
        productCategoryViewModel.productCategory.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }



    private fun updateProduct(id: Int) {
        val name = binding.edName.text.toString().trim()
        val price = binding.edPrice.text.toString().toIntOrNull()
        val description = binding.edDescription.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString().trim()

        // Validate input
        if (name.isNotEmpty() && price != null && description.isNotEmpty() && category.isNotEmpty()) {
            val product = Product(
                name = name,
                price = price,
                description = description,
                category = category,
                image = imageUpdate// Save URI or URL of the image
            )

            lifecycleScope.launch(Dispatchers.IO) {
                // Check if imagePart is not null before updating
                if (imagePart != null) {
                    productViewModel.updateProduct(id, product, imagePart,"PUT",
                        onSuccess = {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Product successfully updated", Toast.LENGTH_SHORT).show()
                                clearFields()
                                navigateToProductList()
                            }
                        },
                        onError = { errorMessage ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to update product: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                } else {
                    // If no imagePart, call updateProduct without it
                    productViewModel.updateProduct(id, product, method = "PUT",
                        onSuccess = {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Product successfully updated", Toast.LENGTH_SHORT).show()
                                clearFields()
                                navigateToProductList()
                            }
                        },
                        onError = { errorMessage ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to update product: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadProduct() {
        val name = binding.edName.text.toString().trim()
        val price = binding.edPrice.text.toString().toIntOrNull()
        val description = binding.edDescription.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString().trim()

        // Validasi input
        if (name.isNotEmpty() && price != null && description.isNotEmpty() && category.isNotEmpty() && imagePart != null) {
            val product = Product(
                name = name,
                price = price,
                description = description,
                category = category,
                image = imageUri.toString() // Simpan URI atau URL gambar
            )

            lifecycleScope.launch(Dispatchers.IO) {
                productViewModel.createProduct(product, imagePart!!,
                    onSuccess = {
                        Toast.makeText(context, "Product successfully created", Toast.LENGTH_SHORT).show()
                        clearFields()
                        navigateToProductList()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, "Failed to create product: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                )
            }
        } else {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }


     private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                imagePart = uriToMultipart(it)
                Glide.with(this).load(it).into(binding.ImagePreview)
            }
        }

   @SuppressLint("Recycle")
   private fun uriToMultipart(uri: Uri): MultipartBody.Part? {
        return try {
            val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r") ?: return null
            val file = File(requireContext().cacheDir, "temp_image.jpg")

            // Menyalin file dari URI ke file sementara
            FileOutputStream(file).use { outputStream ->
                val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
                inputStream.copyTo(outputStream)
            }

            // Membuat RequestBody dari file
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            // Mengembalikan MultipartBody.Part
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("CreateProductFragment", "Error converting URI to Multipart: ${e.message}")
            null
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.edName.text?.toString()?.trim()
        val price = binding.edPrice.text?.toString()?.replace(",", ".")?.toIntOrNull()
        val description = binding.edDescription.text?.toString()?.trim()
        val category = binding.spinnerCategory.selectedItem?.toString()?.trim()

        return !name.isNullOrEmpty() && price != null && price > 0 &&
                !description.isNullOrEmpty() && !category.isNullOrEmpty() && imageUri != null
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

    private fun hideBottomNavigationView() {
        activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)?.visibility = View.VISIBLE
    }
}
