package dev.kelompokceria.smart_umkm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.kelompokceria.smart_umkm.model.User

@Database( entities = [User::class] , version = 1)
abstract class AppDatabase : RoomDatabase() {

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