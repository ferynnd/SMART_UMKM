package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductRepository
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    // LiveData untuk mendapatkan semua produk
    val allProducts: LiveData<List<Product>>

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts() // Mengambil LiveData dari repository
    }

    fun addProduct(product: Product) = viewModelScope.launch {
        repository.insert(product)
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
}
