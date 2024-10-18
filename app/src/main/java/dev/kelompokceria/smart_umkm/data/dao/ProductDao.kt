package dev.kelompokceria.smart_umkm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.kelompokceria.smart_umkm.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product_table")
    fun getAllProduct() : List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Update
    suspend fun update(product: Product)

}