package dev.kelompokceria.smart_umkm.data.api

import dev.kelompokceria.smart_umkm.model.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionApiService {
    @GET("transaction")
    suspend fun getTransactionList(): TransactionApiResponse

    @GET("transaction/{transactionId}")
    suspend fun getTransactionById(
        @Path("transactionId") transactionId: Int
    ): UpdateTransactionResponse

//    @POST("transaction")
//    suspend fun createTransaction(
//        @Body transaction: Transaction
//    ): UpdateTransactionResponse
//
    @POST("transaction") // Adjust the endpoint as necessary
    fun createTransaction(@Body transaction: Transaction): Call<UpdateTransactionResponse>

//    @PUT("transaction/{transactionId}")
//    suspend fun updateTransaction(
//        @Path("transactionId") transactionId: Int,
//        @Body transaction: Transaction
//    ): Transaction
//
    @DELETE("transaction/{transactionId}")
    suspend fun deleteTransaction(
        @Path("transactionId") transactionId: Int
    ): TransactionApiResponse


}