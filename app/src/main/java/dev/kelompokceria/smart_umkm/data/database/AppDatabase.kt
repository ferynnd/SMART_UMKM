package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.Product

@Database(entities = [User::class, Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartumkm_database"
                )
                    .fallbackToDestructiveMigration()
                    // Hindari pemanggilan ini pada aplikasi produksi
                    // .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
