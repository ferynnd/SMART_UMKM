package dev.kelompokceria.smart_umkm.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @ColumnInfo(name = "user_image") val image: ByteArray? = null,
    @ColumnInfo(name = "name_product") val name: String,
    @ColumnInfo(name = "description_product") val description: String,
    @ColumnInfo(name = "price_product") val price: Double,
    @ColumnInfo(name = "category_product") val category: Category,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_product") val id: Int? = null
) : Parcelable {

    // Implementasi Parcelable
    constructor(parcel: Parcel) : this(
        image = parcel.createByteArray(),
        name = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        price = parcel.readDouble(),
        category = parcel.readSerializable() as? Category ?: Category.MAKANAN, // Default value jika null
        id = parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(image) // Menulis image ke parcel
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeDouble(price)
        parcel.writeSerializable(category)
        parcel.writeValue(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

enum class Category {
    MAKANAN,
    MINUMAN;

    companion object {
        // Menambahkan metode untuk mendapatkan enum dari string
        fun fromString(value: String): Category? {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}