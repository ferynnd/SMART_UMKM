package dev.kelompokceria.smart_umkm.data.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.model.Product

class ProductRepository(private val productDao: ProductDao) {

    // Mendapatkan semua produk
    fun getAllProducts(): LiveData<List<Product>> {
        return productDao.getAllProducts() // Pastikan ini sesuai dengan DAO Anda
    }

    // Menambahkan produk
    suspend fun insert(product: Product) {
        productDao.insert(product) // Pastikan ada metode insert di DAO
    }

    // Menghapus produk
    suspend fun delete(product: Product) {
        productDao.delete(product) // Pastikan ada metode delete di DAO
    }

    // Memperbarui produk
    suspend fun update(product: Product) {
        productDao.update(product) // Pastikan ada metode update di DAO
    }

    // Mendapatkan produk berdasarkan ID
    fun getProductById(id: Int): LiveData<Product?> {
        return productDao.getProductById(id) // Memanggil metode dari DAO
    }
}
