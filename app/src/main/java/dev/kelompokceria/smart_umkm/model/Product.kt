package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @ColumnInfo(name = "product_name") val name : String,
    @ColumnInfo(name = "product_image") val image : Int,
    @ColumnInfo(name = "product_price") val price : String,
    @ColumnInfo(name = "product_description") val description :String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "product_id" )val id : Int? = null,
)
