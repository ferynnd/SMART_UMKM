package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.kelompokceria.smart_umkm.data.Converter
import dev.kelompokceria.smart_umkm.data.dao.ProductCategoryDao
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.dao.TransactionDao
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.model.Transaction
import dev.kelompokceria.smart_umkm.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [User::class, Product::class, Transaction::class, ProductCategory::class], version = 3, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao() : TransactionDao
    abstract fun productCategoryDao() : ProductCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartumkm_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context)) // Tambahkan callback untuk populasi data
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

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.userDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao) {
            // Tambahkan data default User
            val defaultUser = User(
                name = "admin",
                email = "admin@example.com",
                phone = "081111111",
                username = "admin",
                password = "admin123",
                role = UserRole.ADMIN,
                id = 1,
                image = "hahaha"
            )
            userDao.insert(defaultUser)
        }
    }
}
