package dev.kelompokceria.smart_umkm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import dev.kelompokceria.smart_umkm.model.Product

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product_table ORDER BY id_product ASC")
    fun getAllProducts(): LiveData<List<Product>>

    // Menambahkan metode untuk mendapatkan produk berdasarkan ID
    @Query("SELECT * FROM product_table WHERE id_product = :id LIMIT 1")
    fun getProductById(id: Int): LiveData<Product?>
}
