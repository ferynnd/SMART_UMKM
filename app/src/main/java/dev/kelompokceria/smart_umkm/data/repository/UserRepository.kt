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

    suspend fun updateUser(user: User) {
        return withContext(Dispatchers.IO){
            userDao.updateUser(user)
        }
    }

    suspend fun getAllUser() : List<User> {
        return userDao.getAllUser()
    }

    suspend fun delete(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun getUserLogin( userName : String , userPassword : String) : User? {
        return userDao.getUserLogin(userName,userPassword)
    }

    suspend fun userSearch( userSeach : String) : List<User> {
        return userDao.userSearch(userSeach)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}