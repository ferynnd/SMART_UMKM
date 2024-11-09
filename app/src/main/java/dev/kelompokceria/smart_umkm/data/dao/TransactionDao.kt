package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.Transaksi
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table")
    fun getAllTransactions(): List<Transaksi>

    @Insert
    suspend fun insertTransaction(transaksi: Transaksi)

    @Query("SELECT * FROM transaction_table WHERE transaction_id = :id")
    suspend fun getTransactionById(id: String): Transaksi

}