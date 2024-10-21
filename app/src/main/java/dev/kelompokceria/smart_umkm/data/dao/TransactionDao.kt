package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.Transaksi
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaksi)

    @Query("SELECT * FROM transaction_table")
    fun getAllTransactions(): Flow<List<Transaksi>>

}