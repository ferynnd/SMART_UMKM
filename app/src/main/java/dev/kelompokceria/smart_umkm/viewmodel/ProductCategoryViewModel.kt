package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductCategoryRespository
import dev.kelompokceria.smart_umkm.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

    private lateinit var productCategoryRespository: ProductCategoryRespository

class ProductCategoryViewModel (application: Application) : AndroidViewModel(application)  {

    private val repository: ProductCategoryRespository

    private val _productCategory = MutableLiveData<List<ProductCategory>>()
    val productCategory: LiveData<List<ProductCategory>> get() = _productCategory

    init {
        val productCategoryDao = AppDatabase.getInstance(application).productCategoryDao()
        repository = ProductCategoryRespository(productCategoryDao)
        refreshProductCategory()
        refreshDeleteProductCategory()
        getAllProductCategory()
    }


    private fun getAllProductCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            _productCategory.postValue(repository.getAllProductCategory())
        }
    }

    suspend fun createProductCategory(productCategory: ProductCategory) {
        viewModelScope.launch {
            repository.createProductCategory(productCategory)
        }
    }

    suspend fun deleteProductCategory(productCategory: ProductCategory, id : Int) {
        viewModelScope.launch {
            repository.deleteProductCategory(productCategory, id)
        }
    }

    fun refreshProductCategory() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val apiProductCategory = repository.getProductCategoryFromApi()
                val existingProductCategory = repository.getAllProductCategory()

                val newProductCategory = apiProductCategory.filter { apiProductCategory ->
                    existingProductCategory.none { existingProductCategory ->
                        existingProductCategory.id == apiProductCategory.id
                    }
                }
                repository.insertAll(newProductCategory)

                val updatedProductCategory = repository.getAllProductCategory()
                _productCategory.postValue(updatedProductCategory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshDeleteProductCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiProductCategory = repository.getProductCategoryFromApi()
                val existingProductCategory = repository.getAllProductCategory()

                val deletedProductCategory = existingProductCategory.filter { roomProductCategory ->
                        apiProductCategory.none { apiProductCategory ->
                            apiProductCategory.id == roomProductCategory.id
                        }
                    }
                repository.deleteAll(deletedProductCategory)

                val updatedProductCategory = repository.getAllProductCategory()
                _productCategory.postValue(updatedProductCategory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





}