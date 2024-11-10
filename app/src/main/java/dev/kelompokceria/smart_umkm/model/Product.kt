package dev.kelompokceria.smart_umkm.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @ColumnInfo(name = "product_image") val image : String = "" ,
    @ColumnInfo(name = "name_product") val name: String,
    @ColumnInfo(name = "description_product") val description: String,
    @ColumnInfo(name = "price_product") val price: Double,
    @ColumnInfo(name = "category_product") val category: String,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_product") val id: Int? = null
)