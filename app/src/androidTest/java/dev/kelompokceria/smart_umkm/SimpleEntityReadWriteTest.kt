package dev.kelompokceria.smart_umkm
//
//import android.content.Context
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import dev.kelompokceria.smart_umkm.data.dao.UserDao
//import dev.kelompokceria.smart_umkm.data.database.TestDatabase
//import dev.kelompokceria.smart_umkm.model.User
//import dev.kelompokceria.smart_umkm.model.UserRole
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.IOException
//
//
//@RunWith(AndroidJUnit4::class)
//class SimpleEntityReadWriteTest {
//    private lateinit var userDao: UserDao
//    private lateinit var db: TestDatabase
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//                context, TestDatabase::class.java).build()
//        userDao = db.getUserDao()
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    @Throws(Exception::class)
//    suspend fun writeUserAndReadInList() {
//
//        val user = User(
//            image = null, // Set a valid ByteArray if needed
//            name = "george",
//            email = "george@example.com",
//            phone = "123456789",
//            username = "george123",
//            password = "securepassword",
//            role = UserRole.USER
//        )
//        userDao.addUser(user)
//        val retrievedUser = userDao.getUserByUsername("george123")
//        println("DATA YANG DI TAMBAH = ${retrievedUser}")
//
////
////        userDao.addUser(user)
////        val retrievedUser = userDao.getUserByUsername("george123")
////
////        assertThat(retrievedUser?.copy(id = 0), equalTo(user.copy(id = 0)))
//
////        CoroutineScope(Dispatchers.IO).launch{
////            userDao.addUser(user)
////             val retrievedUser = userDao.getUserByUsername("george123")
////
////            assertThat(retrievedUser?.copy(id = 0), equalTo(user.copy(id = 0))) // Compare without the ID
////        }
//
//    }

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.data.database.TestDatabase
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var database: TestDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context,
            TestDatabase::class.java
        ).build()
        userDao = database.getUserDao()
    }

    @After
    @Throws(IOException::class)
    fun dbClose() = database.close()

   val user = User(
            image = null, // Set a valid ByteArray if needed
            name = "george",
            email = "george@example.com",
            phone = "123456789",
            username = "george123",
            password = "securepassword",
            role = UserRole.USER
        )
    @Test
    @Throws(Exception::class)
    fun insertAndRetrieve() = runBlocking {
//        user.forEach { userDao.addUser(it) }
        userDao.addUser(user)
        val allLoker = userDao.getAllUser()
        assert(allLoker.size == 1)
        allLoker.forEach { loker ->
            println("Loker: $loker")
        }
    }

}