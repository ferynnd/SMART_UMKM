package dev.kelompokceria.smart_umkm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.ProductCategory

@Dao
interface ProductCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg productCategory: ProductCategory)

    @Delete
    suspend fun delete(productCategory: ProductCategory)

    @Query("SELECT * FROM product_category_table ORDER BY id DESC")
    fun getAllProductCategory(): LiveData<List<ProductCategory>>

}