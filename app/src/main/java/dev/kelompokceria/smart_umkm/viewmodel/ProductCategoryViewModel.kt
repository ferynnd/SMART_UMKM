package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductCategoryRespository
import dev.kelompokceria.smart_umkm.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductCategoryViewModel (application: Application) : AndroidViewModel(application)  {

    private val repository: ProductCategoryRespository
    val allProductCategory: LiveData<List<ProductCategory>>

    init {
        val productCategoryDao = AppDatabase.getInstance(application).productCategoryDao()
        repository = ProductCategoryRespository(productCategoryDao)
        // Fetch categories asynchronously and expose as LiveData
//        allProductCategory = liveData(Dispatchers.IO) {
//            val categories = repository.getAllProductCategory()
//            emit(categories)
//        }
         allProductCategory = repository.getAllProductCategory() // LiveData from Room DAO

    }




    fun addProductCategory(productCategory: ProductCategory) = viewModelScope.launch {
        repository.insert(productCategory)
    }

    fun deleteProductCategory(productCategory: ProductCategory) = viewModelScope.launch {
        repository.delete(productCategory)
    }


}