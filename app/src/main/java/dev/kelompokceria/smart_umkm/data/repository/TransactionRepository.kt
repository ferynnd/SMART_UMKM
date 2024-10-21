package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.dao.TransactionDao
import dev.kelompokceria.smart_umkm.model.Transaksi
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<Transaksi>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaksi) {
        transactionDao.insert(transaction)
    }


}