package dev.kelompokceria.smart_umkm.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository (private val productDao: ProductDao) {

    suspend fun insert(product: Product) {
        return withContext(Dispatchers.IO) {
            productDao.insert(product)
        }
    }

    suspend fun delete(loker: Product) {
        productDao.delete(loker)
    }

    suspend fun update(loker: Product   ) {
        productDao.update(loker)
    }

    suspend fun getAllLoker(): List<Product> {
        return productDao.getAllProduct()
    }

}