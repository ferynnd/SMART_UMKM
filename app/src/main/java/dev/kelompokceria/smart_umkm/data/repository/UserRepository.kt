package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
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

    suspend fun getUserLogin( userName : String , userPassword : String) : User? {
        return userDao.getUserLogin(userName,userPassword)
    }

    suspend fun userDel( userUsername : String) {
        userDao.delUser(userUsername)
    }

    suspend fun userUpdate( userEmail: String, userPhone :String, userPassword : String, userRole: UserRole, user : String) {
        userDao.userUpdate( userEmail, userPhone, userPassword, userRole, user)
    }

    suspend fun userSearch( userSeach : String) : List<User> {
        return userDao.userSearch(userSeach)
    }
}