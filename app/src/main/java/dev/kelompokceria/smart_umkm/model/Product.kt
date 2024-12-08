package dev.kelompokceria.smart_umkm.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "product_table")
data class Product(
    val image : String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val category: String? = null,
    @SerializedName("created_at") val createAt : String? = null,
    @SerializedName("updated_at") val updateAt : String? = null,
    @PrimaryKey val id: Int? = null

)