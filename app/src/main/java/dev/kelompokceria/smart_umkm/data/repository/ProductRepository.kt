package dev.kelompokceria.smart_umkm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.kelompokceria.smart_umkm.data.api.ProductApiResponse
import dev.kelompokceria.smart_umkm.data.api.UpdateProductResponse
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val ApiProduct = RetrofitHelper.productApiService

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getAllProducts(): List<Product> {
          return withContext(Dispatchers.IO) {
            productDao.getAllProducts()
        }
    }

    suspend fun insertAll(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun deleteAll(products: List<Product>) {
        productDao.deleteAll(products)
    }

    suspend fun getProductsFromApi(): List<Product> {
        val response = ApiProduct.getProductList()
        return response.data
    }

    suspend fun createProduct(product: Product, imagePart: MultipartBody.Part) {
        ApiProduct.createProduct(
            product.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            product.description!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            product.category!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            imagePart
        )

    }
    suspend fun updateProduct(id : Int,product: Product, imagePart: MultipartBody.Part? = null, methode : String ) {

        val requestMethode = methode.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiProduct.updateProduct(
            id,
            product.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            product.description!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            product.category!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            imagePart, // Kirim gambar sebagai MultipartBody.Part
            requestMethode
        )
        productDao.updateProductById(id, product.name, product.price!!, product.description, product.category, product.image!!)
    }

    suspend fun getProductById(id: Int): UpdateProductResponse {
        return try {
            val response = ApiProduct.getProductById(id)
            response // Mengembalikan response dari API
        } catch (e: Exception) {
            UpdateProductResponse(status = false, message = e.message ?: "An error occurred", data = null)
        }
    }

    suspend fun deleteProduct(product: Product, id : Int) {
        productDao.delete(product)
        ApiProduct.deleteProduct(id)
    }


    suspend fun insert(product: Product) {
        productDao.insert(product)
    }


}