package dev.kelompokceria.smart_umkm.data.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.model.Product

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): LiveData<List<Product>> {
    }

    suspend fun insert(product: Product) {
    }

    suspend fun delete(product: Product) {
    }

    suspend fun update(product: Product) {
    }
    }
}
