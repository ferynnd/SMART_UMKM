package dev.kelompokceria.smart_umkm.data.api

import com.google.gson.Gson
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Deferred
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductApiService {

    @GET("product")
    suspend fun getProductList(): ProductApiResponse

    @Multipart
    @POST("product")
    suspend fun createProduct(
        @Part("name") productTitle: RequestBody,
        @Part("price") productPrice: RequestBody,
        @Part("description") productDescription: RequestBody,
        @Part("category") productCategory: RequestBody,
        @Part image: MultipartBody.Part // Gambar sebagai Multipart
    ): UpdateProductResponse


    @DELETE("product/{productId}")
    suspend fun deleteProduct(
        @Path("productId") productId: Int
    ): ProductApiResponse

    @Multipart
    @POST("product/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: Int,
        @Part("name") productTitle: RequestBody,
        @Part("price") productPrice: RequestBody,
        @Part("description") productDescription: RequestBody,
        @Part("category") productCategory: RequestBody,
        @Part image: MultipartBody.Part? = null,
        @Part("_method") method: RequestBody
    ): UpdateProductResponse

    @GET("product/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: Int
    ): UpdateProductResponse



}