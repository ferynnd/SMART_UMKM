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
    suspend fun insert(vararg product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Delete
    suspend fun deleteAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Query("UPDATE product_table SET name = :name, price = :price, description = :description, category = :category, image = :image WHERE id = :id")
    suspend fun updateProductById(
        id: Int,
        name: String,
        price: Int,
        description: String,
        category: String,
        image: String
    )

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product_table ORDER BY id ASC")
    fun getAllProducts(): List<Product>

    @Query("SELECT * FROM product_table WHERE id = :id")
    fun getProductById(id: Int): LiveData<Product?>

}