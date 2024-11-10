package dev.kelompokceria.smart_umkm.data.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductCategoryDao
import dev.kelompokceria.smart_umkm.model.ProductCategory

class ProductCategoryRespository(private val productCategoryDao: ProductCategoryDao) {

    fun getAllProductCategory(): LiveData<List<ProductCategory>>{
        return productCategoryDao.getAllProductCategory()
    }

    suspend fun insert(productCategory: ProductCategory) {
        productCategoryDao.insert(productCategory)
    }

    suspend fun delete(productCategory: ProductCategory) {
        productCategoryDao.delete(productCategory)
    }


}