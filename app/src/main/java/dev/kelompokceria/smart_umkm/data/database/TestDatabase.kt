package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.kelompokceria.smart_umkm.data.Converter
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.dao.TransactionDao
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.Transaction
import dev.kelompokceria.smart_umkm.model.User


@Database(entities = [User::class, Product::class, Transaction::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class TestDatabase : RoomDatabase() {

//    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao() : TransactionDao
    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database_test"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        fun getDatabase(application: Context): AppDatabase {
            return getInstance(application)
        }
    }
}
