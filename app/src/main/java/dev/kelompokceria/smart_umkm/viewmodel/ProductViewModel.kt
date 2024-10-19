package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts()
    }

    fun addProduct(product: Product) = viewModelScope.launch {
        repository.insert(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }

    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.update(product)
    }

    // Fungsi untuk mendapatkan produk berdasarkan ID
    fun getProductById(id: Int): LiveData<Product?> {
        return repository.getProductById(id) // Pastikan repository memiliki fungsi ini
    }
}
