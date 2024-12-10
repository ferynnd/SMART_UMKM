package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.api.UpdateTransactionResponse
import dev.kelompokceria.smart_umkm.data.dao.TransactionDao
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ApiTransaction = RetrofitHelper.transactionApiService
class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getAllTransactions()
        }
    }

    suspend fun insertAll(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    suspend fun deleteAll(transactions: List<Transaction>) {
        transactionDao.deleteAll(transactions)
    }

    suspend fun getTransactionsFromApi(): List<Transaction> {
        val response = ApiTransaction.getTransactionList()
        return response.data
    }

    suspend fun createTransaction(transaction: Transaction) {
        ApiTransaction.createTransaction(transaction)
    }

    suspend fun getTransactionById(id: Int): UpdateTransactionResponse {
       return try {
           val response = ApiTransaction.getTransactionById(id)
           response
       } catch (e: Exception){
           UpdateTransactionResponse(status = false, message = e.message ?: "An error occurred", data = null)
       }
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction, id : Int) {
        transactionDao.delete(transaction)
        ApiTransaction.deleteTransaction(id)
    }


}