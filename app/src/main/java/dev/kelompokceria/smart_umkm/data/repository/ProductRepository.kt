package dev.kelompokceria.smart_umkm.data.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.model.Product

class ProductRepository(private val productDao: ProductDao) {

    // Mengambil semua produk dari database sebagai LiveData
    fun getAllProducts(): LiveData<List<Product>> {
        return productDao.getAllProducts() // Pastikan ada fungsi di DAO yang mengembalikan LiveData
    }

    // Menambahkan metode untuk mendapatkan produk berdasarkan ID
    fun getProductById(id: Int): LiveData<Product?> {
        return productDao.getProductById(id) // Mengambil produk berdasarkan ID dari DAO
    }

    suspend fun insert(product: Product) {
        productDao.insert(product) // Pastikan ada fungsi insert di DAO
    }

    suspend fun delete(product: Product) {
        productDao.delete(product) // Pastikan ada fungsi delete di DAO
    }

    suspend fun update(product: Product) {
        productDao.update(product) // Pastikan ada fungsi update di DAO
    }

    suspend fun productSearch(product: String) : List<Product> {
        return productDao.productSearch(product)
    }
}