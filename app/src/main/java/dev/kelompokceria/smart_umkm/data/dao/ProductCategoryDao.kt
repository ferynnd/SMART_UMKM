package dev.kelompokceria.smart_umkm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.kelompokceria.smart_umkm.model.ProductCategory

@Dao
interface ProductCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg productCategory: ProductCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productCategory: List<ProductCategory>)

    @Delete
    suspend fun deleteAll(productCategory: List<ProductCategory>)

    @Delete
    suspend fun delete(productCategory: ProductCategory)

    @Update
    suspend fun update(productCategory: ProductCategory)

    @Query("SELECT * FROM product_category_table ORDER BY id DESC")
    suspend fun getAllProductCategory(): List<ProductCategory>

}