package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.kelompokceria.smart_umkm.data.dao.ProductDao
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.User

@Database( entities = [User::class,Product::class] , version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserDao() : UserDao
    abstract fun ProductDao() : ProductDao

    companion object {
        @Volatile private var INSTANCE : AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "smartumkm_database")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance(){
            INSTANCE = null
        }

    }
}