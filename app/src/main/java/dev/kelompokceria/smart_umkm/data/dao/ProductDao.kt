package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product_table")
    fun getAllProduct() : List<Product>

    @Insert
    fun addProduct(vararg product: Product)

}