package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.dao.TransactionDao
import dev.kelompokceria.smart_umkm.model.Transaksi
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun insert(transaction: Transaksi) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun getAllTransactions(): List<Transaksi> {
        return transactionDao.getAllTransactions()
    }

}