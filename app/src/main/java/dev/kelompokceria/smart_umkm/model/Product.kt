package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Ubah "_id" menjadi "id" agar lebih standar
    val name: String,
    val imageUrl: String, // Menggunakan URL untuk gambar, karena lebih umum dalam aplikasi e-commerce
    val description: String, // Mengubah nama "deskripsi" menjadi "description" agar lebih konsisten dengan standar umum
    val price: Double,
    val category: String // Menambahkan kategori sebagai String untuk menyimpan kategori produk
)
