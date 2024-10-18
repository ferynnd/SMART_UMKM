package dev.kelompokceria.smart_umkm.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productDao: ProductDao) {

    // Fungsi untuk mendapatkan semua produk
    fun getAllProducts(): LiveData<List<Product>> {
        return productDao.getAllProducts()
    }

    // Fungsi untuk memasukkan produk baru
    suspend fun insert(product: Product) {
        withContext(Dispatchers.IO) {
            productDao.insert(product)
        }
    }

    // Fungsi untuk menghapus produk
    suspend fun delete(product: Product) {
        withContext(Dispatchers.IO) {
            productDao.delete(product)
        }
    }

    // Fungsi untuk memperbarui produk
    suspend fun update(product: Product) {
        withContext(Dispatchers.IO) {
            productDao.update(product)
        }
    }
}

