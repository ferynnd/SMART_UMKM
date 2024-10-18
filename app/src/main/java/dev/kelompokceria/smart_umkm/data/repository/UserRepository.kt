package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun addUser(user: User){
        return withContext(Dispatchers.IO) {
            userDao.addUser(user)
        }
    }

    suspend fun getAllUser() : List<User> {
        return userDao.getAllUser()
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }
}