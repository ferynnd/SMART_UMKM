package dev.kelompokceria.smart_umkm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_category_table")
data class ProductCategory (
    val name: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0

)