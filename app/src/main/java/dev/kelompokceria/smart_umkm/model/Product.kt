package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    val name: String,
    val image: Int,
    val deskripsi: String,
    val price: Boolean,
)
