package dev.kelompokceria.smart_umkm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.ProductCategoryDao
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

    private val ApiProduct = RetrofitHelper.productCategoryApiService

class ProductCategoryRespository(private val productCategoryDao: ProductCategoryDao) {


     suspend fun getAllProductCategory(): List<ProductCategory> {
          return withContext(Dispatchers.IO) {
            productCategoryDao.getAllProductCategory()
        }
    }

    suspend fun insertAll(productCategory: List<ProductCategory>) {
        productCategoryDao.insertAll(productCategory)
    }
    suspend fun deleteAll(productCategory: List<ProductCategory>) {
        productCategoryDao.deleteAll(productCategory)
    }

    suspend fun getProductCategoryFromApi(): List<ProductCategory> {
        val response = ApiProduct.getCategoryList()
        return response.data
    }
    suspend fun createProductCategory(productCategory: ProductCategory) {
        ApiProduct.createCategory(productCategory)
    }

    suspend fun deleteProductCategory(productCategory: ProductCategory, id : Int) {
        productCategoryDao.delete(productCategory)
        ApiProduct.deleteCategory(id)
    }


}