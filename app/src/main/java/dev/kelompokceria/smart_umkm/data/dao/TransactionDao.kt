package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.kelompokceria.smart_umkm.model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    suspend fun getTransactionById(id: String): Transaction

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Delete
    suspend fun deleteAll(transactions: List<Transaction>)

//    @Query("UPDATE product_table SET name = :name, price = :price, description = :description, category = :category, image = :image WHERE id = :id")
//    suspend fun updateProductById(
//        id: Int,
//        name: String,
//        price: Int,
//        description: String,
//        category: String,
//        image: String
//    )

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transaction_table ORDER BY id ASC")
    fun getAllTransactions(): List<Transaction>

//    @Query("SELECT * FROM product_table WHERE id = :id")
//    fun getProductById(id: Int): LiveData<Product?>
//

}