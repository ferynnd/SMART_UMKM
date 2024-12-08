package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table")
    fun getAllTransactions(): List<Transaction>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    suspend fun getTransactionById(id: String): Transaction

}