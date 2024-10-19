package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                    .addCallback(DatabaseCallback(context)) // Tambahkan callback
                    .build()
                    .also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Tambahkan data default di sini menggunakan coroutine
            INSTANCE?.let { database ->
                GlobalScope.launch(Dispatchers.IO) {
                    populateDatabase(database.userDao())
                }
            }
        }

        private suspend fun populateDatabase(userDao: UserDao) {
            // Tambah data default User
            val defaultUser = User(
                name = "admin",
                email = "admin@example.com",
                phone = "081111111",
                username = "admin",
                password = "password123",
                role = UserRole.ADMIN,
                id = 1,
            )
            userDao.addUser(defaultUser)
        }
    }
}
