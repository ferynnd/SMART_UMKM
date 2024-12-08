package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.api.ProductApiResponse
import dev.kelompokceria.smart_umkm.data.api.ProductApiService
import dev.kelompokceria.smart_umkm.data.api.UpdateProductResponse
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductRepository
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

    private lateinit var  productApiService : ProductApiService

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    // LiveData untuk menyimpan posisi yang dipilih
    private val _selectedPositions = MutableLiveData<Set<Product>>(emptySet())
    val selectedPositions: LiveData<Set<Product>> get() = _selectedPositions

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        refreshProducts()
        refreshDeleteProducts()
        products
        getAllProduct()
    }

     private fun getAllProduct() {
        viewModelScope.launch {
            _products.value = repository.getAllProducts()
        }
    }

    fun refreshProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil produk dari API
                val apiProducts = repository.getProductsFromApi()

                // Ambil produk yang ada di Room
                val existingProducts = repository.getAllProducts()

                // Tambahkan produk baru yang tidak ada di database
                val newProducts = apiProducts.filter { apiProduct ->
                    existingProducts.none { existingProduct ->
                        existingProduct.id == apiProduct.id
                    }
                }
                // Simpan produk baru ke database
                repository.insertAll(newProducts)

                val updatedProducts = repository.getAllProducts()
                _products.postValue(updatedProducts) // Gunakan postValue untuk mengupdate LiveData dari background thread
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }

    fun refreshDeleteProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil produk dari API
                val apiProducts = repository.getProductsFromApi()

                // Ambil produk yang ada di Room
                val existingProducts = repository.getAllProducts()

                // Tambahkan produk baru yang tidak ada di database
                val recentProducts = existingProducts.filter { roomProducts ->
                    apiProducts.none { apiProducts ->
                        apiProducts.id == roomProducts.id
                    }
                }
                // Simpan produk baru ke database
                repository.deleteAll(recentProducts)

                val updatedProducts = repository.getAllProducts()
                _products.postValue(updatedProducts) // Gunakan postValue untuk mengupdate LiveData dari background thread
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }


     // Fungsi untuk membuat produk
    suspend fun createProduct(product: Product, imagePart: MultipartBody.Part, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.createProduct(product, imagePart)
                onSuccess() // Panggil callback onSuccess jika berhasil
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error") // Panggil callback onError jika terjadi kesalahan
            }
        }
    }

    suspend fun updateProduct(id : Int,product: Product, imagePart: MultipartBody.Part? = null,method : String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Panggil metode repository untuk membuat produk
                  repository.updateProduct(id, product, imagePart, method)
                  onSuccess() // Panggil callback onSuccess jika berhasil
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error") // Panggil callback onError jika terjadi kesalahan
            }
        }
    }

    private val _product = MutableLiveData<UpdateProductResponse>()
    val product: LiveData<UpdateProductResponse> get() = _product

        // ViewModel
        fun fetchProductById(id: Int) {
            viewModelScope.launch {
                try {
                    val response = repository.getProductById(id)
                    _product.postValue(response)
//                    _product.value = response
                } catch (e: Exception) {
                    _product.value = UpdateProductResponse(status = false, message = e.message ?: "An error occurred", data = null)
                }
            }
        }

    suspend fun deleteProduct(product: Product, id : Int) {
        repository.deleteProduct(product, id)
    }


//    // Fungsi untuk menambahkan produk baru ke database
//    fun addProduct(product: Product) = viewModelScope.launch {
//        repository.insert(product) // Memastikan ini memanggil fungsi insert yang tepat
//    }
//
//    // Fungsi untuk menghapus produk
//    fun deleteProduct(product: Product) = viewModelScope.launch {
//        repository.delete(product)
//    }
//
//    // Fungsi untuk memperbarui produk
//    fun updateProduct(product: Product) = viewModelScope.launch {
//        repository.update(product)
//    }
//
//    // Fungsi untuk mendapatkan produk berdasarkan ID
//    fun getProductById(id: Int): LiveData<Product?> {
//        return repository.getProductById(id) // Mengambil produk dari repository
//    }
//
//    fun getProductsByIds(ids: List<Int>): LiveData<List<Product>> {
//    if (ids.isEmpty()) {
//        return MutableLiveData(emptyList()) // Mengembalikan LiveData kosong jika ID kosong
//        }
//        return repository.getProductsByIds(ids)
//    }
//
//
//    // Fungsi untuk mencari produk berdasarkan nama
//    fun productSearch(query: String) {
//        viewModelScope.launch {
//            val result = repository.productSearch(query)
//            _filteredProducts.postValue(result)
//        }
//    }
//
//     // Tambahkan atau hapus posisi dari daftar seleksi
    fun toggleSelection(position: Product) {
        val currentSelections = _selectedPositions.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelections.contains(position)) {
            currentSelections.remove(position)
        } else {
            currentSelections.add(position)
        }
        _selectedPositions.value = currentSelections
    }

    // Set ulang semua seleksi
    fun clearSelections() {
        _selectedPositions.value = emptySet()
    }

}
