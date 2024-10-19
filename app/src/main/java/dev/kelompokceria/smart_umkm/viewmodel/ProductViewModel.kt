package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductRepository
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    private val _allProduct = MutableLiveData<List<Product>>()
    val allProduct : LiveData<List<Product>> get() = _allProduct


    // LiveData untuk mendapatkan semua produk
    val allProducts: LiveData<List<Product>>

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts() // Mengambil LiveData dari repository
    }

    // Fungsi untuk menambahkan produk baru ke database
    fun addProduct(product: Product) = viewModelScope.launch {
        repository.insert(product) // Memastikan ini memanggil fungsi insert yang tepat
    }

    // Fungsi untuk menghapus produk
    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }

    // Fungsi untuk memperbarui produk
    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.update(product)
    }

    // Fungsi untuk mendapatkan produk berdasarkan ID
    fun getProductById(id: Int): LiveData<Product?> {
        return repository.getProductById(id) // Mengambil produk dari repository
    }

     suspend fun productSearch(product: String) {
         viewModelScope.launch {
            _allProduct.postValue(repository.productSearch(product))
        }
    }

}
